package org.translet.processor;

/**
 * This class represents the xpath expression.
 * <p>
 * Known Restrictions:
 * <ul>

 * <li> Predicate limitation, attribute before number required:
 * /a[@b=5] is OK,while /a[5=@b] is not OK.

 * </ul>
 *
 * @author Karthikeyan Mariappan.
 */
public class XPathExpr
{
	private String expr;
	private String data;

    public XPathExpr()
    {
        this(null,null);
    }

	public XPathExpr(String expression, String data)
	{
		this.expr = expression;
		this.data = data;
	}

	public String toString() { return this.expr; }
	public String getData() { return this.data; }
    public String getExpression() { return this.expr; }

	//public void setExpression(String expr) { this.expr=expr; }
	//public void setData(String data)       { this.data=data; }
	public void set(String expression, String data)
	{
		this.expr = expression;
		this.data = data;
	}
} // Class End.
