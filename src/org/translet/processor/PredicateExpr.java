package org.translet.processor;

import org.w3c.dom.Node;

import org.translet.rules.RuleMonitor;

/**
 * Composite interface that represents the expression
 * in the predicate
 *
 * @author Karthikeyan M.
 *
 */
public interface PredicateExpr
{
	// This interface is the implementation of the component
	// interface of the composite design pattern by GOF.

	/**
	 * Initializes and validates (recursively, if required)
	 * the expression and returns the status about if the
	 * expression can be evaluated.
	 *
	 * <p> The values passed on the contextNode and the rules
	 * are also the values that are to be used in evaluating
	 * the expression.
	 *
	 * <p> The expression is expected to return at the first
	 * place where the initialization fails. In other words,
	 * if the initialization succeeds, all the expressions
	 * were initialized, but, if it fails, not all expressions
	 * might have been evaluated.
	 *
	 * @param context The context for this expression.
	 * @param rules Required to make decisions when the expression
	 * is ambiguous. null, if there are no rules required.
	 *
	 * @return true if the expression can be evaluated within the
	 * context of the rules specified.
	 *
	 */
	public boolean initialize(PredicateContext context,RuleMonitor rules);

	/**
	 * Evaluates the expression and returns the value.
	 *
	 * @return The value for this expression.
	 */
	public Object evaluate();

} // Class End
