package it.unive.lisa.symbolic.value;

import java.util.Objects;

import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.symbolic.ExpressionVisitor;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.operator.quaternary.QuaternaryOperator;
import it.unive.lisa.type.Type;
/**
* A bynary expression that applies a {@link QuaternaryExpression} to four
* {@link SymbolicExpression}s.
* 
*/
public class QuaternaryExpression extends ValueExpression {
	

	/**
	 * The first operand of this expression
	 */
	private final SymbolicExpression e1;

	/**
	 * The second operand of this expression
	 */
	private final SymbolicExpression e2;

	/**
	 * The third operand of this expression
	 */
	private final SymbolicExpression e3;
	
	/**
	 * The forth operand of this expression
	 */
	private final SymbolicExpression e4;

	/**
	 * The operator to apply
	 */
	private final QuaternaryOperator operator;

	/**
	 * Builds the binary expression.
	 * 
	 * @param staticType the static type of this expression
	 * @param e1       the first operand of this expression
	 * @param e2     the second operand of this expression
	 * @param e3      the third operand of this expression
	 * @param e4      the fourth operand of this expression
	 * @param operator   the operator to apply
	 * @param location   the code location of the statement that has generated
	 *                       this expression
	 */
	public QuaternaryExpression(
			Type staticType,
			SymbolicExpression e1,
			SymbolicExpression e2,
			SymbolicExpression e3,
			SymbolicExpression e4,
			QuaternaryOperator operator,
			CodeLocation location) {
		super(staticType, location);
		this.e1 = e1;
		this.e2 = e2;
		this.e3 = e3;
		this.e4 = e4;
		this.operator = operator;
	}

	/**
	 * Yields the first operand of this expression.
	 * 
	 * @return the first operand
	 */
	public SymbolicExpression getSymbolicExpr1() {
		return e1;
	}

	/**
	 * Yields the second operand of this expression.
	 * 
	 * @return the second operand
	 */
	public SymbolicExpression getSymbolicExpr2() {
		return e2;
	}

	/**
	 * Yields the third operand of this expression.
	 * 
	 * @return the third operand
	 */
	public SymbolicExpression getSymbolicExpr3() {
		return e3;
	}

	/**
	 * Yields the fourth operand of this expression.
	 * 
	 * @return the fourth operand
	 */
	public SymbolicExpression getSymbolicExpr4() {
		return e4;
	}
	
	/**
	 * Yields the operator that is applied to {@link #getSymbolicExpr1()}, {@link #getSymbolicExpr2()},
	 * {@link #getSymbolicExpr3()} and {@link #getSymbolicExpr4()}.
	 * 
	 * @return the operator to apply
	 */
	public QuaternaryOperator getOperator() {
		return operator;
	}

	@Override
	public SymbolicExpression pushScope(
			ScopeToken token)
			throws SemanticException {
		QuaternaryExpression expr = new QuaternaryExpression(getStaticType(), e1.pushScope(token), e2.pushScope(token),
				e3.pushScope(token), e4.pushScope(token), operator, getCodeLocation());
		return expr;
	}

	@Override
	public SymbolicExpression popScope(
			ScopeToken token)
			throws SemanticException {
		QuaternaryExpression expr = new QuaternaryExpression(getStaticType(), e1.popScope(token), e2.popScope(token),
				e3.popScope(token), e4.popScope(token), operator, getCodeLocation());
		return expr; 
	}

	
	@Override
	public String toString() {
		return e1 + " " + operator + "(" + e2 + ", " + e3 + ", " + e4 + ")";
	}


	@Override
	public boolean mightNeedRewriting() {
		return e1.mightNeedRewriting() || e2.mightNeedRewriting() || e3.mightNeedRewriting() || e4.mightNeedRewriting();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(e1, e2, e3, e4, operator);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		QuaternaryExpression other = (QuaternaryExpression) obj;
		return Objects.equals(e1, other.e1) && Objects.equals(e2, other.e2) && Objects.equals(e3, other.e3)
				&& Objects.equals(e4, other.e4) && Objects.equals(operator, other.operator);
	}

	@Override
	public <T> T accept(ExpressionVisitor<T> visitor, Object... params) throws SemanticException {
			T t1 = this.e1.accept(visitor, params);
			T t2 = this.e2.accept(visitor, params);
			T t3 = this.e3.accept(visitor, params);
			T t4 = this.e3.accept(visitor, params);
			return visitor.visit(new PushAny(getStaticType(), getCodeLocation()), params);
	}

	
	
}
