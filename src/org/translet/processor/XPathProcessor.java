package org.translet.processor;

//import org.translet.builder.LocationPathHandler;

/**
 * Handles the processing of an xpath expression. Generates
 * events based on the xpath nodes encountered in the
 * document.
 *
 * @author Karthikeyan Mariappan.
 * @version $Revision: 1.3 $
 */

public interface XPathProcessor
{
	/**
	 * Processes the specified XPath and calls the
	 * registered call back methods.
	 *
	 * @param xpath The xpath to be processed.
	 */
	public void processXPath(XPathExpr xpath)
	throws ProcessException;

	/**
	 * Registers the handler that processes the
	 * events generated while parsing the xpath.
	 *
	 * @param handler to processes the xpath events.
	 */
	public void setHandler(LocationPathHandler handler);

    /**
     * Returns the current instance of handler that
     * is registered to handle events.
     *
     * @return handler to processes the xpath events.
     */
    public LocationPathHandler getHandler();
}