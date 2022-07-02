package it.unive.golisa.cfg.expression;

import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.BinaryExpression;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.AccessChild;
import it.unive.lisa.symbolic.heap.HeapDereference;
import it.unive.lisa.type.Untyped;

/**
 * A Go access expression (e.g., x.y).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoCollectionAccess extends BinaryExpression {

	/**
	 * Builds the access expression.
	 * 
	 * @param cfg       the {@link CFG} where this expression lies
	 * @param location  the location where this expression is defined
	 * @param container the left-hand side of this expression
	 * @param child     the right-hand side of this expression
	 */
	public GoCollectionAccess(CFG cfg, SourceCodeLocation location, Expression container, Expression child) {
		super(cfg, location, container + "::" + child, container, child);
	}

	@Override
	protected <A extends AbstractState<A, H, V, T>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>,
			T extends TypeDomain<T>> AnalysisState<A, H, V, T> binarySemantics(
					InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
					SymbolicExpression left, SymbolicExpression right, StatementStore<A, H, V, T> expressions)
					throws SemanticException {
		if (getLeft().toString().startsWith("args"))
			return state.smallStepSemantics(left, this);

		AnalysisState<A, H, V, T> result = state.bottom();

		AnalysisState<A, H, V, T> rec = state.smallStepSemantics(left, this);
		for (SymbolicExpression expr : rec.getComputedExpressions()) {
			AnalysisState<A, H, V, T> tmp = rec.smallStepSemantics(
					new AccessChild(Untyped.INSTANCE,
							new HeapDereference(getStaticType(), expr, getLocation()), right, getLocation()),
					this);
			result = result.lub(tmp);
		}

		return result;
	}

	/**
	 * Yields the recevier of this access expression.
	 * 
	 * @return the recevier of this access expression.
	 */
	public Expression getReceiver() {
		return getLeft();
	}

	/**
	 * Yields the target of this access expression.
	 * 
	 * @return the target of this access expression.
	 */
	public Expression getTarget() {
		return getRight();
	}
}
