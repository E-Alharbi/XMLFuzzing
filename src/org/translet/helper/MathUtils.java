package org.translet.helper;

import java.util.*;

public class MathUtils
{
	private static Map operations;

	static {
		operations = new HashMap();
		operations.put("+",new PlusOperation());

		Object eqOp = new EqualityOperation();

		operations.put("=",eqOp);
		operations.put("==",eqOp);
	}
	/**
	 * Takes two strings and an operator that acts on the
	 * two strings and evaluates the operation.
	 *
	 * <p>Ex:
	 * <ul>
	 * <li>2 div 2
	 * <li> 3 * 5
	 * <li> a = b
	 * </ul>
	 *
	 * @throws NumberFormatException if the left or right value
	 * can't be converted into a number (int/double).
	 */
	public static Object evaluateOperation(String sleft,
						String operator,
						String sright) throws NumberFormatException
	{
		MathOperation op = getOperation(operator);
		if(null==op)
			throw new UnsupportedOperationException("Operator: "+operator);

		return op.evaluate(sleft,operator,sright);
	}

	private static MathOperation getOperation(String operatorStr)
	{
		return (MathOperation)operations.get(operatorStr);
	}
}

/**
 * Interface for operations.
 */
interface MathOperation
{
	public Object evaluate(String sleft,String operator,String sright);
}

class PlusOperation implements MathOperation
{
	public Object evaluate(String sleft,String operator,String sright)
	{
		double leftVal  = 0;
		double rightVal = 0;

		//try {
			leftVal = Double.parseDouble(sleft);
		//} catch(NumberFormatException nfe) { }

		//try {
			rightVal = Double.parseDouble(sright);
		//} catch(NumberFormatException nfe) { }

		return new Double(leftVal + rightVal);
	}
}

class EqualityOperation implements MathOperation
{
	public Object evaluate(String sleft,String operator,String sright)
	{
		//check string equality.
		boolean areEqual = sleft.equals(sright);

		// If they are equal, then there is no issue. If not,
		// there is a chance that they may be mathematically
		// still equal (10==10.000)
		if(!areEqual)
		{
			double leftVal  = 0;
			double rightVal = 0;

			//try {
				leftVal = Double.parseDouble(sleft);
			//} catch(NumberFormatException nfe) { }

			//try {
				rightVal = Double.parseDouble(sright);
			//} catch(NumberFormatException nfe) { }

			areEqual = (leftVal==rightVal);
		}

		return new Boolean(areEqual);
	}
}
