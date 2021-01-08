package org.translet.processor;

import java.io.PrintStream;
import java.io.PrintWriter;
/**
 * Represents an exceptional condition during the
 * processing of the xpath expression. This is also
 * a wrapper exception, which means, other exceptions
 * can be wrapped in this exception and later unwrapped.
 *
 * @author Karthikeyan M.
 * @version $Revision: 1.2 $
 *
 */
public class ProcessException extends Exception
{
	private Throwable rootcause;

	public ProcessException(String state)
	{
		super(state);
	}

	public ProcessException(Throwable t)
	{
		super(t.getMessage());
		this.rootcause = t;
	}

	public void printStackTrace()
	{
		super.printStackTrace();
		if(null!=rootcause)
		{
			System.err.println("\nRoot Cause: \n");
			rootcause.printStackTrace();
		}
	}

	public void printStackTrace(PrintStream s)
	{
		super.printStackTrace(s);
		if(null!=rootcause)
		{
			s.print("\nRoot Cause: \n");
			rootcause.printStackTrace(s);
		}
	}

	public void printStackTrace(PrintWriter s)
	{
		super.printStackTrace(s);
		if(null!=rootcause)
		{
			s.print("\nRoot Cause: \n");
			rootcause.printStackTrace(s);
		}
	}

} // Class End
