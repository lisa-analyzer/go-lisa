package it.unive.golisa.cfg.expression.unary;

import it.unive.golisa.cfg.expression.literal.GoInteger;
import it.unive.golisa.cfg.type.untyped.GoUntypedInt;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.caches.Caches;
import it.unive.lisa.callgraph.CallGraph;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.UnaryNativeCall;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.BinaryOperator;
import it.unive.lisa.symbolic.value.Constant;

/**
 * Go unary plus native function class (e.g., +(5 - 3), +5).
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoPlus extends UnaryNativeCall {

	/**
	 * Builds a Go unary plus expression. The location where 
	 * this expression appears is unknown 
	 * (i.e. no source file/line/column is available).
	 * 
	 * @param cfg	the cfg that this expression belongs to
	 * @param exp	operand
	 */
	public GoPlus(CFG cfg, Expression exp) {
		this(cfg, null, -1, -1, exp);
	}
	
	public GoPlus(CFG cfg, String sourceFile, int line, int col, Expression exp) {
		super(cfg, new SourceCodeLocation(sourceFile, line, col), "+", exp);
	}

	@Override
	protected <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> unarySemantics(
			AnalysisState<A, H, V> entryState, CallGraph callGraph, AnalysisState<A, H, V> exprState,
			SymbolicExpression expr) throws SemanticException {
		if (!expr.getDynamicType().isNumericType() && !expr.getDynamicType().isUntyped())
			return entryState.bottom();

		Constant zero = new Constant(GoUntypedInt.INSTANCE, new GoInteger(getCFG(), 0));

		return entryState.smallStepSemantics(
				new BinaryExpression(Caches.types().mkSingletonSet(zero.getDynamicType()), zero, expr, BinaryOperator.NUMERIC_ADD), this);
	}

}
