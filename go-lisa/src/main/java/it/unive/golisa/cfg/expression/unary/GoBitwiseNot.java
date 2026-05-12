package it.unive.golisa.cfg.expression.unary;

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
import it.unive.lisa.symbolic.value.operator.unary.BitwiseNegation;

/**
 * A Go bitwise unary not expression (e.g., ^x).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoBitwiseNot extends UnaryExpression {

	/**
	 * Builds the bitwise not expression.
	 * 
	 * @param cfg      the {@link CFG} where this expression lies
	 * @param location the location where this expression is defined
	 * @param exp      the expression
	 */
	public GoBitwiseNot(CFG cfg, SourceCodeLocation location, Expression exp) {
		super(cfg, location, "^", exp.getStaticType(), exp);
	}

	@Override
	protected int compareSameClassAndParams(Statement o) {
		return 0; // nothing else to compare
	}

	@Override
	public <A extends AbstractLattice<A>, D extends AbstractDomain<A>> AnalysisState<A> fwdUnarySemantics(
			InterproceduralAnalysis<A, D> interprocedural, AnalysisState<A> state, SymbolicExpression expr,
			StatementStore<A> expressions) throws SemanticException {
		return interprocedural.getAnalysis().smallStepSemantics(state, new it.unive.lisa.symbolic.value.UnaryExpression(getStaticType(), expr, BitwiseNegation.INSTANCE, getLocation()), this);
	}

}
