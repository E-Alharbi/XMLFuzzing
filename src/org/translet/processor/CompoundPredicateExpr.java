package org.translet.processor;

import org.w3c.dom.Node;

import org.translet.rules.RuleMonitor;
import org.translet.helper.MathUtils;

/**
 * Represents a compound expression involving xpath expressions.
 * This is a composite of <tt>PredicateExpr</tt>. A compound
 * expression can be made up of two other PredicateExpr (either or
 * both of which can also be another <tt>CompoundPredicateExpr</tt>).
 *
 * @author Karthikeyan M.
 */

public class CompoundPredicateExpr implements PredicateExpr
{
	private PredicateExpr       left;
	private String              operator;
	private PredicateExpr       right;
    private PredicateContext    ctxt;
	private RuleMonitor         rules;

	public CompoundPredicateExpr(PredicateExpr left,
	                             String operator,
	                             PredicateExpr right)
	{
		if(null==left || null==operator || null==right)
			throw new NullPointerException("Expr or operator is null.");

		this.left      = left;
		this.operator  = operator;
		this.right     = right;
	}

	public boolean initialize(PredicateContext ctxt, RuleMonitor rules)
	{

        if( !left.initialize(ctxt,rules) ||
            !right.initialize(ctxt,rules) )
		{
			return false;
		}

        this.ctxt  = ctxt;
		this.rules = rules;

		return ( rules!=null ?
			     rules.isOperationAllowed(operator) :
			     true );
	}

	public Object evaluate()
	{
		Object leftVal  = left.evaluate();
		Object rightVal = right.evaluate();

		if(null==leftVal || null==rightVal)
			return null;

		if(leftVal instanceof Node || rightVal instanceof Node)
			throw new IllegalStateException("Unexpected values upon evaluating the predicate: "+toString());

		String sleft  = leftVal.toString();
		String sright = rightVal.toString();

		return MathUtils.evaluateOperation(sleft,operator,sright);
	}

	public String toString()
	{
		return left.toString() + operator + right.toString();
	}
}
