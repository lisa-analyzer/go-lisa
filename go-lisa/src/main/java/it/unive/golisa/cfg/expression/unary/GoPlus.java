package it.unive.golisa.cfg.expression.unary;

import it.unive.golisa.cfg.expression.literal.GoInteger;
import it.unive.golisa.cfg.type.untyped.GoUntypedInt;
import it.unive.lisa.analysis.AbstractDomain;
import it.unive.lisa.analysis.AbstractLattice;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.UnaryExpression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.operator.binary.NumericNonOverflowingAdd;
import it.unive.lisa.type.Type;

/**
 * A Go unary plus expression (e.g., +x).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoPlus extends UnaryExpression {

	/**
	 * Builds the unary plus expression.
	 * 
	 * @param cfg      the {@link CFG} where this expression lies
	 * @param location the location where this expression is defined
	 * @param exp      the expression
	 */
	public GoPlus(CFG cfg, SourceCodeLocation location, Expression exp) {
		super(cfg, location, "+", exp);
	}

	@Override
	protected int compareSameClassAndParams(Statement o) {
		return 0; // nothing else to compare
	}

	@Override
	public <A extends AbstractLattice<A>, D extends AbstractDomain<A>> AnalysisState<A> fwdUnarySemantics(
			InterproceduralAnalysis<A, D> interprocedural, AnalysisState<A> state, SymbolicExpression expr,
			StatementStore<A> expressions) throws SemanticException {
		Type etype = interprocedural.getAnalysis().getDynamicTypeOf(state, expr, this);
		if (!etype.isNumericType() && !etype.isUntyped())
			return state.bottom();

		Constant zero = new Constant(GoUntypedInt.INSTANCE,
				new GoInteger(getCFG(), (SourceCodeLocation) getLocation(), 0), getLocation());
		return interprocedural.getAnalysis().smallStepSemantics(state,
				new BinaryExpression(interprocedural.getAnalysis().getDynamicTypeOf(state, zero, this), zero, expr,
						NumericNonOverflowingAdd.INSTANCE, getLocation()),
				this);
	}
}
