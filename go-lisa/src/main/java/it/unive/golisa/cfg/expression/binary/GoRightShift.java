package it.unive.golisa.cfg.expression.binary;

import it.unive.lisa.analysis.AbstractDomain;
import it.unive.lisa.analysis.AbstractLattice;
import it.unive.lisa.analysis.Analysis;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.BinaryExpression;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.type.NumericType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;

/**
 * A Go right shift expression (e.g., e1 >> e2).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoRightShift extends BinaryExpression {

	/**
	 * Builds the left shift expression.
	 *
	 * @param cfg      the {@link CFG} where this expression lies
	 * @param location the location where this expression is defined
	 * @param left     the left-hand side of this expression
	 * @param right    the right-hand side of this expression
	 */
	public GoRightShift(CFG cfg,
			CodeLocation location,
			Expression left,
			Expression right) {
		super(cfg, location, ">>", inferType(left, right), left, right);
	}

	private static Type inferType(
			Expression left,
			Expression right) {
		Type leftType = left.getStaticType();
		NumericType integerType = left.getProgram().getTypes().getIntegerType();

		if (leftType.canBeAssignedTo(integerType))
			return integerType;

		else
			return Untyped.INSTANCE;
	}

	@Override
	public <A extends AbstractLattice<A>,
			D extends AbstractDomain<A>> AnalysisState<A> fwdBinarySemantics(
					InterproceduralAnalysis<A, D> interprocedural,
					AnalysisState<A> state,
					SymbolicExpression left,
					SymbolicExpression right,
					StatementStore<A> expressions)
					throws SemanticException {
		Analysis<A, D> analysis = interprocedural.getAnalysis();
		if (analysis.getRuntimeTypesOf(state, right, this).stream().noneMatch(t -> t.isNumericType()))
			return state.bottomExecution();

		if (analysis.getRuntimeTypesOf(state, left, this).stream()
				.noneMatch(t -> t.canBeAssignedTo(getProgram().getTypes().getIntegerType())))
			return state.bottomExecution();

		return analysis.smallStepSemantics(
				state,
				new it.unive.lisa.symbolic.value.BinaryExpression(
						Untyped.INSTANCE,
						left,
						right,
						it.unive.lisa.symbolic.value.operator.binary.BitwiseShiftRight.INSTANCE,
						getLocation()),
				this);
	}

	@Override
	protected int compareSameClassAndParams(
			Statement o) {
		return 0;
	}
}
