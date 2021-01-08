package org.translet.rules;

/**
 * The interface for the rules that are
 * used to make decisions when the xpath is
 * ambiguous.
 *
 * @author Karthikeyan M.
 * @version $Revision: 1.1 $
 */
public interface RuleMonitor
{
	public boolean isOperationAllowed(String operator);
}
