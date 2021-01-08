/*
 * Saturday, September 14, 2002
 */
package org.translet.builder;

import java.io.IOException;
import javax.xml.parsers.*;
import org.w3c.dom.*;

import org.translet.helper.Logger;
import org.translet.helper.DomUtils;
import org.translet.rules.RuleMonitor;
import org.translet.processor.LocationPathHandler;
import org.translet.processor.PredicateExpr;
import org.translet.processor.PredicateContext;
/**
 * Default implementation of the <tt>LocationPathHandler</tt>
 * that creates the nodes belonging to the XML document.
 */
public class NodeCreator implements LocationPathHandler
{
	private static final Logger log =
		Logger.getInstance(NodeCreator.class.getName());

	private Document doc;
	private RuleMonitor rules;

	private Node activeNode;
	private short nodeType = Node.TEXT_NODE;

    private PredicateContextImpl predCtxt;
    private PredicateExpr activePredicate;

	/*-- last reported states --*/
    private PredicateExpr lastPredicate;
	private String    lastPath;


	/**
	 * Uses a new DOM document.
	 */
	public NodeCreator()
	{
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder        = factory.newDocumentBuilder();
			this.doc                       = builder.newDocument();
		}
		catch(ParserConfigurationException pce)
		{
			log.error("Unable to create a new document.",pce);
			throw new RuntimeException(pce.getMessage());
		}
	}

	public NodeCreator(Document doc)
	{
		if(null==doc)
            throw new NullPointerException("Valid document expected.");

        this.doc = doc;
	}

	public void startXPath()
	{
		log.debug("---startXPath----");
		//activeNode = doc.getDocumentElement();
		activeNode = doc;
	}

	public void endXPath()
	{
		log.debug("---endXPath----");
		//try {
		//    log.debug(DomUtils.serialize(doc.getDocumentElement()));
		//} catch(IOException ioe) {
		//	log.info("Unable to serialize doc",ioe);
		//}
	}

	public void startPredicate()
	{
		log.debug("---startPredicate----");
	}

	public void endPredicate()
	{
		log.debug("---endPredicate----");
	}

	public void attributeInPredicate(String attrName)
	{
		log.debug("---attributeInPredicate: "+attrName);
	}

	public void startLocationPath()
	{
		log.debug("---startLocationPath---");
	}

	public void endLocationPath()
	{
		log.debug("---endLocationPath---");

		//Element e  = setElement(step);
		//activeNode = e;
	}

	public void locationStep(String step)
	{
		log.debug("---locationStep: "+step);

		Element e  = setElement(step);
		activeNode = e;

		this.lastPath = step;
	}

	public void axes(short axescode)
	{
		log.debug("---axes: "+axescode);
	}

	public void predicate(PredicateExpr predicate)
	{
		this.activePredicate = predicate;

        log.debug("---predicate: "+predicate);

        if(null==predCtxt) predCtxt = new PredicateContextImpl();

        predCtxt.initialize(activeNode,activeNode.getNodeName());

        if(!predicate.initialize(predCtxt,rules))
			throw new IllegalStateException("Predicate can't be evaluated");

		//FIXME! Better contract between the predicate and
        // its clients about the value it returns.
        activeNode = (Node) predicate.evaluate();
	}

	public void attribute(String attrName)
	{
		log.debug("---attribute: "+attrName);
		Attr a = doc.createAttribute(attrName);

		if(activeNode.getNodeType() == Node.ELEMENT_NODE)
			((Element)activeNode).setAttributeNode(a);
		else
			throw new IllegalStateException("Attributes are allowed only for element nodes.");

		activeNode = a;
	}

	public void data(String value)
	{
		Node data;

		if(activeNode.getNodeType()==Node.ATTRIBUTE_NODE)
		{
			((Attr)activeNode).setValue(value);
		}
		else
		{
            if(!(activeNode instanceof Element))
                throw new IllegalStateException("The parent for text is not element. "+activeNode.getClass());

			if(Node.CDATA_SECTION_NODE == nodeType)
				data = doc.createCDATASection(value);
			else if(Node.TEXT_NODE == nodeType)
				data = doc.createTextNode(value);
			else
				throw new RuntimeException("Node type for data not recognized :"+nodeType);

            activeNode.appendChild(data);
		}
	}

	private Element setElement(String name)
	{
		Element e = (Element)DomUtils.getFirstChildWithTag(activeNode,name);
		if(null==e)
		{
			e = doc.createElement(name);
			activeNode.appendChild(e);
		}
		return e;
	}

    public Node createNode(String nodeName, String namespaceURI, short nodeType)
    {
        if(namespaceURI!=null && namespaceURI.length()!=0)
            throw new UnsupportedOperationException("Namespaces not supported yet.");

        switch(nodeType)
        {
            case Node.ATTRIBUTE_NODE:
                return doc.createAttribute(nodeName);

            case Node.ELEMENT_NODE:
                return doc.createElement(nodeName);

            case Node.TEXT_NODE:
                return doc.createTextNode("");

            default:
                throw new IllegalArgumentException("Nodetype not recognized: "+nodeType);
        }
    }

    public Document getDocument() { return this.doc; }
} // Class End

class PredicateContextImpl implements PredicateContext
{
    private Node    parentNode;
    private String  nodeName;

    void initialize(Node parentNode, String nodeName)
    {
        this.parentNode = parentNode;
        this.nodeName   = nodeName;
    }

    public Node   getParentNode() { return this.parentNode; }
    public String getContextNodeName() { return this.nodeName; }
    public short  getAxes()     { throw new UnsupportedOperationException(); }
    public String getNamespaceURI() { throw new UnsupportedOperationException(); }
}