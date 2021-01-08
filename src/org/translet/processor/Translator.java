package org.translet.processor;

import org.translet.processor.dom.DOMTranslatorResult;
import org.translet.builder.NodeCreator;

/**
 * Creates xml documents. The xpaths and it's value
 * is translated into nodes in the xml document.
 * Facade for the translet package. For most common
 * usage, this class and it's helper classes (
 *  <tt> DataIterator, XPathExpr, DOMTranslatorResult
 *  </tt>)
 * are the only APIs that needs to be known.
 * See Example below.
 *
 * <p>XPath models an XML document as a tree of nodes.
 * The basic XPath syntax is similar to filesystem
 * addressing.
 *
 * <p>
 * Since <tt>XPath</tt> to an xml document branch is
 * not usually a one-to-one mapping, certain assumptions
 * are made about the <tt>XPath</tt> to create a unique
 * branch. Also, only a part of the <tt>Xpath</tt>
 * specification is implemented, and an expception may be
 * thrown if an unsupported <tt>XPath</tt> expression is
 * encountered.
 *
 * <p> Some examples of supported xpaths are are:
 *<ul>
 *<li>"/a/b/value"</li>
 *<li>"/a/b[@id=23]/value"</li>
 *<li>"/a/b/@correct"</li>
 *</ul>
 * <p> Some examples of <b>unsupported</b> xpaths are:
 *<ul>
 *<li> "/a/b[@id=1 and @display-order=2]/value" - Can't have arbitrary
 * expressions inside predicate.
 *<li> "../b/value" - Can't go back to parent node.
 * </ul>
 *
 * <p>
 * <h3>Example Usage:</h3>
 * <pre><blockquote><code>
 * import org.w3c.dom.Document;
 * import org.translet.processor.Translator;
 * import org.translet.processor.DataIterator;
 * import org.translet.processor.XPathExpr;
 * import org.translet.processor.dom.DOMTranslatorResult;
 *
 * ....
 * ....
 *  // setup the data. List is needed to maintain the order.
 *  List xpaths = Arrays.asList(new XPathExpr[] {
 *                                   new XPathExpr("/a/b","some data"),
 *                                   new XPathExpr("/a/b/@id","1"),
 *                                   new XPathExpr("/a/b[2]","more data"),
 *                                   new XPathExpr("/a/b[2]/@id","2")
 *                                   });
 *
 *  final Iterator it     = xpaths.iterator();
 *
 *  // Create the adaptor instance.
 *  DataIterator dit      = new DataIterator() {
 *    public boolean hasNext() {
 *        return it.hasNext();
 *    }
 *    public XPathExpr nextXPath() {
 *        return (XPathExpr) it.next();
 *    }
 * };
 *
 * DOMTranslatorResult result = new DOMTranslatorResult();
 * Translator.getInstance().translate(dit,result);
 *
 * Document doc = result.getDocument(); // will give you the dom document.
 * ....
 * ....
 * </code></blockquote></pre>
 *
 * @author Karthikeyan Mariappan.
 */

public class Translator
{
    private static Translator instance;
    /** Sorry nobody else can instantiated me! */
    private Translator() { }

    /**
	 * Obtains an instance of the translator.
     *
     * @return Translator an instance of the translator.
	 */
    public static Translator getInstance()
	{
        if(null==instance) instance = new Translator();

        return instance;
    }

	/**
	 * Translates the collection of xpaths into
     * xml document.
	 *
	 * @param xIterator iterator over the xpaths
     * and it's values.
     *
     * @param result holds information about how
     * the output will be held.
	 */
	public void translate(DataIterator xIterator, TranslatorResult result)
    throws ProcessException
    {
        if(null==xIterator || null==result)
            throw new NullPointerException("Null Arguments for DataIterator/TranslatorResult");

        if(!(result instanceof DOMTranslatorResult))
            throw new IllegalArgumentException("Invalid result argument. Expected should be a type of DOMTranslatorResult");

        DOMTranslatorResult domResult   = (DOMTranslatorResult) result;
        XPathProcessor xp               = newProcessor("new",domResult);

        while(xIterator.hasNext())
        {
            XPathExpr expr = xIterator.nextXPath();
            xp.processXPath(expr);
        }

        domResult.setDocument(xp.getHandler().getDocument());
    }

    /**
     * Creates a processor that performs the translation.
     */
    private XPathProcessor newProcessor(String name, DOMTranslatorResult dr)
    {
        XPathProcessor xp = new XPathProcessorImpl(name);

        if(dr.getDocument()!=null)
            xp.setHandler(new NodeCreator(dr.getDocument()));
        else
            xp.setHandler(new NodeCreator());

        return xp;
    }
}