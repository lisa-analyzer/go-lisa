package it.unive.golisa.cfg.expression.binary;

import it.unive.lisa.analysis.AbstractDomain;
import it.unive.lisa.analysis.AbstractLattice;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.BinaryExpression;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.type.Type;
import java.util.Set;

/**
 * A Go bit-wise nand expression (e.g., x &^ y).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoBitwiseNAnd extends BinaryExpression implements GoBinaryNumericalOperation {

	/**
	 * Builds the bit-wise nand expression.
	 *
	 * @param cfg      the {@link CFG} where this expression lies
	 * @param location the location where this expression is defined
	 * @param left     the left-hand side of this expression
	 * @param right    the right-hand side of this expression
	 */
	public GoBitwiseNAnd(CFG cfg, SourceCodeLocation location, Expression left, Expression right) {
		super(cfg, location, "&^", left, right);
	}

	@Override
	protected int compareSameClassAndParams(Statement o) {
		return 0; // nothing else to compare
	}

	@Override
	public <A extends AbstractLattice<A>, D extends AbstractDomain<A>> AnalysisState<A> fwdBinarySemantics(
			InterproceduralAnalysis<A, D> interprocedural, AnalysisState<A> state, SymbolicExpression left,
			SymbolicExpression right, StatementStore<A> expressions) throws SemanticException {
		AnalysisState<A> result = state.bottom();

		Set<Type> ltypes = interprocedural.getAnalysis().getRuntimeTypesOf(state, left, this);
		Set<Type> rtypes = interprocedural.getAnalysis().getRuntimeTypesOf(state, right, this);

		for (Type leftType : ltypes)
			for (Type rightType : rtypes)
				if ((leftType.isUntyped() || (leftType.isNumericType() && leftType.asNumericType().isIntegral())) &&
						(rightType.isUntyped()
								|| (rightType.isNumericType() && rightType.asNumericType().isIntegral()))) {
					// TODO: LiSA has not symbolic expression handling bitwise,
					// return top at the moment
					AnalysisState<A> tmp = 
							interprocedural.getAnalysis().smallStepSemantics(state, new PushAny(resultType(leftType, rightType), getLocation()), this);
					result = result.lub(tmp);
				}
		return result;
	}

}