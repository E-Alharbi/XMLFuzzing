package org.translet.processor;

import java.util.NoSuchElementException;

import org.translet.helper.Logger;

/**
 * Iterator across a specified range of the tokens
 * generated from the xpath expressions.
 *
 * @author Karthikeyan M.
 * @version $Revision: 1.4 $
 */
public class TokenIteratorImpl implements TokenIterator
{
	private static final boolean debug = true;
	private static final Logger log =
		Logger.getInstance(TokenIteratorImpl.class.getName());

	/** The tokens created out of the xpath expression. */
	private Object[] tokens;

	/** The current number of token being processed.*/
	private int      cursorPos;

	// once set, can't be changed.
	private final int      offset;
	private final int      length;

	/** Indicates the start of the marking for creating
	snapshot */
	private int      markPos;

	/**
	 * @param offset The offset using a zero based array.
	 * @param length Number of tokens to be processed.
	 */
	public TokenIteratorImpl(Object[] tokens, int offset, int length)
	{
		if(tokens.length<offset+length) {
			throw new IllegalArgumentException("Tokens size("+tokens.length
				+") less than offset("+offset+")+length("+length+").");
		}

		this.tokens = tokens;
		this.offset = offset;
		this.length = length;
		this.cursorPos = offset;
	}

	/**
	 * Returns true if the token is the same as the char specified.
	public boolean tokenIs(final char c)
	{
		return c == current().charAt(0);
	}
	 */

	/**
	 * Moves the current token cursor to the next token
	 */
	public boolean hasNext()
	{
		return cursorPos < offset + length;
	}

	/**
	 * Returns true if the next token is the char specified.
	 * Doesn't increment the cursor.
	 */
	public boolean nextTokenIs(char c)
	{
		try {
			return hasNext() && (c == tokenAt(cursorPos).charAt(0));
		} catch(NoSuchElementException e) {
			return false;
		}
	}

	public String peekNext()
	{
		return tokenAt(cursorPos);
	}

	public String next()
	{
		return tokenAt(cursorPos++);
	}

	public String previous()
	{
		return tokenAt(--cursorPos);
	}

	private String tokenAt(int position)
	{
		// If there is no next token and we are not at the
		// end of the array, then there is something wrong.
		if(position<offset || position>=offset+length)
			throw new NoSuchElementException("No such token available. Position:"+position);

		try {
			return tokens[position].toString();
		} catch(NullPointerException ne) {
			System.out.println("offset: "+offset+", curr: "+position+", length: "+length);
			throw ne;
		}
	}

	public void markForSnapshot()
	{
		if(markPos!=0)
			throw new IllegalStateException("Snapshot is already initialized.");

		markPos = cursorPos;
	}

	public TokenIterator createSnapshot()
	{
		TokenIterator snapshot =
			new TokenIteratorImpl(tokens,markPos,cursorPos-markPos);

		markPos = 0;

		return snapshot;
	}

	public boolean hasPrevious()
	{
		return cursorPos > offset;
	}

	public void rewind()
	{
		cursorPos = offset;
		markPos = cursorPos;
	}
} // Class End.
