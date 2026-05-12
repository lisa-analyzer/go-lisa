package it.unive.golisa.cfg.expression.unary;

import it.unive.lisa.analysis.AbstractDomain;
import it.unive.lisa.analysis.AbstractLattice;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.UnaryExpression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.HeapReference;
import it.unive.lisa.type.ReferenceType;

/**
 * A Go dereferencing expression (e.g., &x).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoDeref extends UnaryExpression {

	/**
	 * Builds the dereferencing expression.
	 *
	 * @param cfg      the {@link CFG} where this expression lies
	 * @param location the location where this expression is defined
	 * @param exp      the expression to dereference
	 */
	public GoDeref(CFG cfg, CodeLocation location, Expression exp) {
		super(cfg, location, "&", exp);
	}

	@Override
	protected int compareSameClassAndParams(Statement o) {
		return 0; // nothing else to compare
	}

	@Override
	public <A extends AbstractLattice<A>, D extends AbstractDomain<A>> AnalysisState<A> fwdUnarySemantics(
			InterproceduralAnalysis<A, D> interprocedural, AnalysisState<A> state, SymbolicExpression expr,
			StatementStore<A> expressions) throws SemanticException {
		// TODO: check if the semantics is correct
		return interprocedural.getAnalysis().smallStepSemantics(state, new HeapReference(new ReferenceType(getStaticType()), expr, getLocation()),
				this);
	}
}
