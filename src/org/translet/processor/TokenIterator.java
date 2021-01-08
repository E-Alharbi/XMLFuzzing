package org.translet.processor;

/**
 * Iterator across a specified range of the tokens
 * generated from the xpath expressions.
 *
 * @author Karthikeyan M.
 * @version $Revision: 1.4 $
 */
public interface TokenIterator
{
	/**
	 * Check if previous will return valid token.
	 */
	public boolean hasPrevious();

	/**
	 * Returns the previous element in the list. This method
	 * may be called repeatedly to iterate through the list
	 * backwards, or intermixed with calls to next to go back
	 * and forth. (Note that alternating calls to next and
	 * previous will return the same element repeatedly.)
	 *
	 * @throws java.util.NoSuchElementException -  if the iteration has
	 * no previous element.
	 */
	public String previous();

	/**
	 * Returns true if the next token is the char specified.
	 * Doesn't increment the cursor.
	 */
	public boolean nextTokenIs(char c);

	/**
	 * Peeks the next token w/o incrementing cursor.
	 *
	 * @throws java.util.NoSuchElementException -  if the iteration has
	 * no previous element.
	 */
	public String peekNext();

	/**
	 * Returns the value of the token.
	 */
	public String next();

	/**
	 * Check if there is any more tokens.
	 */
	public boolean hasNext();

	/**
	 * Marks the position to start recoding. Illegal to call
	 * <tt>markForSnapshot</tt> twice before calling
	 * <tt>createSnapshot()</tt>. This means, it is not
	 * possible to create overlapping snapshots.
	 * The tokens would include that would have been
	 * returned by <tt>next()</tt> (Optional operation).
	 */
	public void markForSnapshot();

	/**
	 * Creates a snapshot since the time markForSnapshot
	 * was called including all the elements that was
	 * gotten using <tt>next()</tt>. Resets the mark.
	 */
	public TokenIterator createSnapshot();

	/**
	 * Re-initializes the iterator. Will also reset any snapshot marks.
	 */
	public void rewind();
}
