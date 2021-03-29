package it.unive.golisa.cfg.expression.unary;

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
import it.unive.lisa.symbolic.value.UnaryExpression;
import it.unive.lisa.symbolic.value.UnaryOperator;

/**
 * Go unary minus native function class (e.g., -(5 + 3), -5).
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoMinus extends UnaryNativeCall {

	/**
	 * Builds a Go unary minus expression. The location where 
	 * this expression appears is unknown 
	 * (i.e. no source file/line/column is available).
	 * 
	 * @param cfg	the cfg that this expression belongs to
	 * @param exp	operand
	 */
	public GoMinus(CFG cfg, Expression exp) {
		this(cfg, null, -1, -1, exp);
	}
	
	public GoMinus(CFG cfg, String sourceFile, int line, int col,  Expression exp) {
		super(cfg, new SourceCodeLocation(sourceFile, line, col),  "-", exp);
	}

	@Override
	protected <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> unarySemantics(
			AnalysisState<A, H, V> entryState, CallGraph callGraph, AnalysisState<A, H, V> exprState,
			SymbolicExpression expr) throws SemanticException {
		if (!expr.getDynamicType().isNumericType() && !expr.getDynamicType().isUntyped())
			return entryState.bottom();

		return exprState.smallStepSemantics(
				new UnaryExpression(Caches.types().mkSingletonSet(expr.getDynamicType()), expr, UnaryOperator.NUMERIC_NEG), this);
	}
}
