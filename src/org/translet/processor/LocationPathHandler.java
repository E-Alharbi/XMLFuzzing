package org.translet.processor;

import org.w3c.dom.Node;
import org.w3c.dom.Document;
/**
 * Handler for the enents generated while parsing
 * an xpath.
 *<P> Please read the xpath specification available at
 * <a href="http://www.w3.org/TR/xpath">http://www.w3.org/TR/xpath</a>
 *
 * @author Karthikeyan Mariappan.
 */
public interface LocationPathHandler
{
	/** Constant indicating an ancestor axes */
	public static final short AXES_ANCESTOR            = 0;

	/** Constant indicating an ancestor-or-self axes */
	public static final short AXES_ANCESTOR_OR_SELF    = 1;

	/** Constant indicating an attribute axes */
	public static final short AXES_ATTRIBUTE           = 2;

	/** Constant indicating child axes */
	public static final short AXES_CHILD               = 3;

	/** Constant indicating descendent axes */
	public static final short AXES_DESCENDENT          = 4;

	/** Constant indicating descendent-or-self axes */
	public static final short AXES_DESCENDENT_OR_SELF  = 5;

	/** Constant indicating following axes */
	public static final short AXES_FOLLOWING           = 6;

	/** Constant indicating following-sibling axes */
	public static final short AXES_FOLLOWING_SIBLING   = 7;

	/** Constant indicating namespace axes */
	public static final short AXES_NAMESPACE           = 8;

	/** Constant indicating parent axes */
	public static final short AXES_PARENT              = 9;

	/** Constant indicating preceding axes */
	public static final short AXES_PRECEDING           = 10;

	/** Constant indicating preceding-sibling axes */
	public static final short AXES_PRECEDING_SIBLING   = 11;

	/** Constant indicating self axes */
	public static final short AXES_SELF                = 12;

	/** Constant indicating that the axes found was not recognized. */
	public static final short AXES_UNKNOWN             = -1;

	/**
	 * Indicates the beginning of an xpath expression.
	 * Typically called before any other events are
	 * called.
	 */
	public void startXPath();

	/**
	 * Indicates the closing of an xpath expression.
	 * Typically called at the end of an expression
	 * processing.
	 */
	public void endXPath();

	/**
	 * Indicates the beginning of a predicate in an expression.
	 */
	public void startPredicate();

	/**
	 * Indicates the closing of a predicate.
	 */
	public void endPredicate();

	/**
	 * Indicates the beginning of a LocationPath in an expression.
	 */
	public void startLocationPath();

	/**
	 * Indicates the closing of a LocationPath.
	 */
	public void endLocationPath();

	/**
	 * Indicates that an attribute was found with in the
	 * predicate
	 */
	public void attributeInPredicate(String attrName);

	/**
	 * Indicates that a location step was found.
	 * @param step the name of the location step.
	 */
	public void locationStep(String step);

	/**
	 * Indicates that an axes was encountered.
	 * @param axesname the name of the axis.
	 */
	public void axes(short axescode);

	/**
	 * Indicates a predicate in the location path.
	 * @param PredicateExpr encapsulates the elements of the predicate.
	 */
	public void predicate(PredicateExpr predicate);

	/**
	 * Indicates an attribute in the location path.
	 * @param name of the attribute.
	 */
	public void attribute(String attrName);

	/**
	 * Sets the data.
	 */
	public void data(String value);

    /**
     * Creates a node of the specified type.
     *
     * @param nodeName - name for the node. Ignored if not
     * appropriate (like in text nodes).
     * @param namespaceURI - namespace for the node. If invalid
     * then, a no-namespace node will be created.
     * @param name nodeType - a node type as specified in the
     * <tt>org.w3c.dom.Node</tt> interface.
     *
     * @return node of the specified type.
     *
     * @throws IllegalArgumentException if the nodeType is
     * doesn't correspond to the values in the <tt>Node</tt>
     * interface.
     */
    public Node createNode(String nodeName, String namespaceURI, short nodeType);

    /**
     * Returns the dom document.
     */
    public Document getDocument();
}
// Class End


