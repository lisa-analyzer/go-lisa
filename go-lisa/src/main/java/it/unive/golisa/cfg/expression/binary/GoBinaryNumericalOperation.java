package it.unive.golisa.cfg.expression.binary;

import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;

/**
 * Class for computing the result type of a numerical expression.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public interface GoBinaryNumericalOperation {

	/**
	 * Yields the result type of a binary numerical operation between
	 * {@code left} and {@code right}.
	 * 
	 * @param left  the left type
	 * @param right the right type
	 * 
	 * @return the result type of a binary numerical operation between
	 *             {@code left} and {@code right}
	 */
	public default Type resultType(Type left, Type right) {
		
		if(left == null || right == null )
			return Untyped.INSTANCE;
		
		if (!left.isNumericType() && !right.isNumericType())
			// if none have numeric types in them, we cannot really compute the
			// result
			return Untyped.INSTANCE;

		if (left.isUntyped() && right.isUntyped())
			return left;
		else if (left.isUntyped())
			return right;
		else if (right.isUntyped())
			return left;
		else if (left.canBeAssignedTo(right))
			return right;
		else if (right.canBeAssignedTo(left))
			return left;
		else
			return Untyped.INSTANCE;
	}
}
