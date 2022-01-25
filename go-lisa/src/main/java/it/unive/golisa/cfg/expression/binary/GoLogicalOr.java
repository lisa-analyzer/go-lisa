package it.unive.golisa.cfg.expression.binary;

import it.unive.golisa.cfg.type.GoBoolType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticDomain.Satisfiability;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.caches.Caches;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.operator.binary.LogicalOr;

/**
 * A Go Boolean logical or function call (e1 || e2). The static type of this
 * expression is definitely {@link GoBoolType} and both operands must be
 * instances of {@link GoBoolType}. The semantics of a Go logical or implements
 * a short-circuit logics.
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoLogicalOr extends it.unive.lisa.program.cfg.statement.BinaryExpression {

	/**
	 * Builds a Go or expression at a given location in the program.
	 * 
	 * @param cfg        the cfg that this expression belongs to
	 * @param sourceFile the source file where this expression happens. If
	 *                       unknown, use {@code null}
	 * @param line       the line number where this expression happens in the
	 *                       source file. If unknown, use {@code -1}
	 * @param col        the column where this expression happens in the source
	 *                       file. If unknown, use {@code -1}
	 * @param exp1       left-hand side operand
	 * @param exp2       right-hand side operand
	 */
	public GoLogicalOr(CFG cfg, SourceCodeLocation location, Expression exp1, Expression exp2) {
		super(cfg, location, "||", GoBoolType.INSTANCE, exp1, exp2);
	}

	@Override
	protected <A extends AbstractState<A, H, V>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>> AnalysisState<A, H, V> binarySemantics(
					InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> state,
					SymbolicExpression left,
					SymbolicExpression right, StatementStore<A, H, V> expressions) throws SemanticException {
		// FIXME: need to check which state needs to be returned (left/right)
		if (!left.getDynamicType().isBooleanType() && !left.getDynamicType().isUntyped())
			return state.bottom();
		if (!right.getDynamicType().isBooleanType() && !right.getDynamicType().isUntyped())
			return state.bottom();

		if (state.satisfies(left, this) == Satisfiability.SATISFIED)
			return state;
		else if (state.satisfies(left, this) == Satisfiability.NOT_SATISFIED)
			return state
					.smallStepSemantics(new BinaryExpression(Caches.types().mkSingletonSet(GoBoolType.INSTANCE),
							left, right, LogicalOr.INSTANCE, getLocation()), this);
		else if (state.satisfies(left, this) == Satisfiability.UNKNOWN)
			return state.lub(state
					.smallStepSemantics(new BinaryExpression(Caches.types().mkSingletonSet(GoBoolType.INSTANCE),
							left, right, LogicalOr.INSTANCE, getLocation()), this));
		else
			return state.bottom();
	}
}