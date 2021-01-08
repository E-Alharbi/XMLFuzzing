package org.translet.processor;

import org.w3c.dom.Node;

/**
 * Used to specify the information about the context
 * of a predicate for the <tt>PredicateExpr</tt>.
 */
public interface PredicateContext
{
	/** The parent to the current locationPath */
	public Node   getParentNode();
	/** Local name of the node referred by this locationpath */
	public String getContextNodeName();

	/** The axes of the current locationpath */
	public short  getAxes();
	/** Namespace URI of the node referred to in the locationpath */
	public String getNamespaceURI();
}