package org.translet.helper;

import java.util.Calendar;

/**
 * Class used for logging debug messages.
 * Typical Usage:
 *<blockquote>
 *<pre>
 * class MyClass
 * {
 *     private static final Logger log =
 *        Logger.getInstance(MyClass.class.getName());
 *
 *     public void doSomething(String param1)
 *     {
 *        log.debug("The value of param1:"+param1);
 *        .
 *        List nodes;
 *        .
 *        .
 *        .
 *        // Logging messages that are expensive to calculate.
 *        if(log.isDebugEnabled())
 *        {
 *           for(Iterator it=nodes.iterator();it.hasNext();)
 *           {
 *              log.debug("Node Name:"+node.getNodeName());
 *           }
 *        }
 *        .
 *        .
 *        .
 *      }
 *
 * } //Class End
 *</pre>
 *</blockquote>
 *
 *
 * @author Karthikeyan M.
 * @version $Revision: 1.2 $
 */
public class Logger
{
	public static final short DEBUG = 1;
	public static final short INFO  = 2;
	public static final short WARN  = 3;
	public static final short ERROR = 4;

	//--------------------- initialization -----------------//
	// A Local strategy for debug is being used.
	// should be easy to change this to use something
	// like Log4J
	private static final short  LOGLEVEL;

	private static final String LOGFILE;

	private static final boolean STANDALONE = true;

	static
	{
		String property = System.getProperty("org.translet.log.level");
        LOGFILE         = System.getProperty("org.translet.log.file");

		if(null==property || 0==property.length())
            LOGLEVEL = WARN;
		else
            LOGLEVEL = Short.valueOf(property.trim()).shortValue();
	}

	//--------------- instance methods ----------------//
	private String classname;

	/** Not accessible to public */
	private Logger(String classname)
	{
		this.classname = classname;
	}

	public static Logger getInstance(String classname)
	{
		int index = classname.indexOf(".translet.");
		if(index>0)
			return new Logger(classname.substring(index+10));
		else
			return new Logger(classname);
	}

	/**
	 * Used to determine, if the debug is currently on or not.
	 * Useful when a costly debugging message is to be created.
	 */
	public boolean isDebugEnabled()
	{
        return DEBUG == LOGLEVEL;
	}

	public void debug(Object message)
	{
		dologging(message,null,DEBUG,"DEBUG");
	}

	public void debug(Object message, Throwable t)
	{
		dologging(message,t,DEBUG,"DEBUG");
	}

	public void info(Object message)
	{
		dologging(message,null,INFO,"INFO");
	}

	public void info(Object message, Throwable t)
	{
		dologging(message,t,INFO,"INFO");
	}

	public void warn(Object message)
	{
		dologging(message,null,WARN,"WARN");
	}

	public void warn(Object message, Throwable t)
	{
		dologging(message,t,WARN,"WARN");
	}

	public void error(Object message)
	{
		dologging(message,null,ERROR,"ERROR");
	}

	public void error(Object message, Throwable t)
	{
		dologging(message,t,ERROR,"ERROR");
	}

	private void dologging(Object message, Throwable t, short level, String ctxt)
	{
        if(level>=LOGLEVEL)
		{
			Calendar c = Calendar.getInstance();
            if(STANDALONE)
			{
				System.out.println("["+c.get(c.HOUR_OF_DAY)+":"+c.get(c.MINUTE)+":"+
					c.get(c.SECOND)+","+c.get(c.MILLISECOND)+"] ["+ctxt+"] ["+classname+"] "
					+message);
				if(null!=t) t.printStackTrace();
			}
			else
			{
			}
		}
	}

}
// Class End
