package it.unive.lisa.symbolic.value.operator.quaternary;

import java.util.Set;

import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.Operator;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;

/**
 * A ternary {@link Operator} that can be applied to four
 * {@link SymbolicExpression}.
 * 
 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
 */
public interface QuaternaryOperator extends Operator {
	
	/**
	 * Computes the runtime types of this expression (i.e., of the result of
	 * this expression) assuming that the arguments of this expression have the
	 * given types.
	 * 
	 * @param types  the type system knowing about the types of the current
	 *                   program
	 * @param s1   the set of types of the first argument of this
	 *                   expression
	 * @param s2 the set of types of the second argument of this expression
	 * @param s3  the set of types of the third argument of this
	 *                   expression
	 * @param s4 the set of types of the fourth argument of this
	 *                   expression
	 * 
	 * @return the runtime types of this expression
	 */
	Set<Type> typeInference(
			TypeSystem types,
			Set<Type> s1,
			Set<Type> s2,
			Set<Type> s3,
			Set<Type> s4);

}
