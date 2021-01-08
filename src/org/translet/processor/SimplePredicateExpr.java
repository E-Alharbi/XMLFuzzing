package org.translet.processor;

import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.translet.rules.RuleMonitor;

import org.translet.helper.DomUtils;
import org.translet.helper.Logger;

/**
 * Represents a simple expression in the predicate.
 * A simple expression is the smallest unit of the
 * expression. It is the expression that is found
 * on one side of a single complete expression.
 *<p>
 * Ex: In case of the predicate in the xpath
 * <tt>"/a/b[./c/text()=./d/text()]/e"</tt>
 * <tt>./c/text()</tt> is a simple xpath expression,
 * so is the expression on the RHS of the equation.
 *
 * @author Karthikeyan M.
 */

public class SimplePredicateExpr implements PredicateExpr
{
    private static final Logger log =
        Logger.getInstance(SimplePredicateExpr.class.getName());

	private PredicateContext    ctxt;
	private RuleMonitor         rules;
	private TokenIterator       iter;
	private LocationPathHandler handler;

	/**
	 * The tokens from the start to end position
	 * are the only tokens that should be processed
	 * by this simple expression.
	 */
    public SimplePredicateExpr(TokenIterator iter, LocationPathHandler handler)
	{
		this.iter    = iter;
        this.handler = handler;
	}

	public boolean initialize(PredicateContext ctxt, RuleMonitor rules)
	{
		this.ctxt  = ctxt;
		this.rules = rules;

		return true;
	}

	/**
	 * Returns the name of the attribute
	 */
	public Object evaluate()
	{
		// Restriction: /a[@b=5] is OK,while /a[5=@b] is not OK.
		while(iter.hasNext())
		{
            String val = iter.next();

            log.debug("Next Token is: '"+val+"'");

			if("@".equals(val))
			{
                iter.rewind();
				// Obtain the value of the attribute.
				//Node
                log.debug("Returning attr value.");
                return ((Element)ctxt.getParentNode()).getAttribute(val);
				//return val;
			}
			else
			{
                int intg = 0;
                try {
                    // This is the case when the value inside the simple predicate
                    // is just an integer.
                    intg = Integer.parseInt(val);
                } catch(NumberFormatException nfe) {

                    log.info("Error",nfe);

                    return val;
                }


				iter.rewind();

                Node parentNode = ctxt.getParentNode().getParentNode();
                Node n = DomUtils.getFirstChildWithIndex(parentNode,
                                                ctxt.getContextNodeName(),intg);

                if(n!=null) return n;

                if(intg>1)
                {
                    Node m = DomUtils.getFirstChildWithIndex(parentNode,
                                                ctxt.getContextNodeName(),intg-1);
                    if(null==m)
                        throw new IllegalStateException("Node at "+intg+" can't be created, when node at "+(intg-1)+" doesn't exist.");
                }

                n = handler.createNode(ctxt.getContextNodeName(),
                                                  null,
                                                  Node.ELEMENT_NODE);

                parentNode.appendChild(n);

                return n;
            }
		}

		throw new IllegalStateException();
	}

	private String getAttrValue(String name)
	{
        return ((Element)ctxt.getParentNode()).getAttribute(name);
	}

	public String toString()
	{
		StringBuffer buff = new StringBuffer("^");
		while(iter.hasNext())
		{
			buff.append(iter.next());
		}
		buff.append("$");
		iter.rewind();
		return buff.toString();
	}
}
