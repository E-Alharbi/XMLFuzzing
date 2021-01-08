/*
 * DataIterator.java
 */
package org.translet.processor;

import java.util.NoSuchElementException;

/**
 * Iterator over a collection of xpaths and
 * the corresponding value. The xpath collection
 * is an ordered collection.
 *
 * @author Karthikeyan Mariappan.
 */

public interface DataIterator
{
	/**
	 * Returns <tt>true</tt> if the iterator has more
	 * xpaths to be processed.
	 *
	 * @return true if the iterator has more elements.
	 */
	public boolean hasNext();

	/**
	 * Returns next xpath in the interation.
	 *
	 * @return XPathExpr that holds xpath statement
     * and it's value.
     *
     * @throws NoSuchElementException If there is no
     * more entry.
	 */
	public XPathExpr nextXPath();
}
