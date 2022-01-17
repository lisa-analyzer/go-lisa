package it.unive.golisa.cfg.expression.unary;

import it.unive.golisa.cfg.expression.literal.GoInteger;
import it.unive.golisa.cfg.type.untyped.GoUntypedInt;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.caches.Caches;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.UnaryExpression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.operator.binary.NumericNonOverflowingAdd;

/**
 * Go unary plus native function class (e.g., +(5 - 3), +5).
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoPlus extends UnaryExpression {

	public GoPlus(CFG cfg, SourceCodeLocation location, Expression exp) {
		super(cfg, location, "+", exp);
	}

	@Override
	protected <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> unarySemantics(
			InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> state, SymbolicExpression expr)
			throws SemanticException {
		if (!expr.getDynamicType().isNumericType() && !expr.getDynamicType().isUntyped())
			return state.bottom();

		Constant zero = new Constant(GoUntypedInt.INSTANCE,
				new GoInteger(getCFG(), (SourceCodeLocation) getLocation(), 0), getLocation());

		return state.smallStepSemantics(
				new BinaryExpression(Caches.types().mkSingletonSet(zero.getDynamicType()), zero, expr,
						NumericNonOverflowingAdd.INSTANCE, getLocation()),
				this);
	}

}
