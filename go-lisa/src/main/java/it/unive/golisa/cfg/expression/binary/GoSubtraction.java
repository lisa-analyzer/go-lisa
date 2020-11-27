package it.unive.golisa.cfg.expression.binary;

import java.util.Collection;

import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.HeapDomain;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.ValueDomain;
import it.unive.lisa.analysis.callgraph.CallGraph;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.NativeCall;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.BinaryOperator;
import it.unive.lisa.cfg.statement.Expression;

/**
 * A Go numerical subtraction function call (e1 - e2).
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoSubtraction extends NativeCall {

	/**
	 * Builds a Go subtraction expression. 
	 * The location where this expression appears is unknown 
	 * (i.e. no source file/line/column is available).
	 * 
	 * @param cfg	the cfg that this expression belongs to
	 * @param exp1	left-hand side operand
	 * @param exp2 	right-hand side operand 
	 */
	public GoSubtraction(CFG cfg, Expression exp1, Expression exp2) {
		super(cfg, null, -1, -1, "-", exp1, exp2);
	}
	
	@Override
	public <H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<H, V> callSemantics(
			AnalysisState<H, V> computedState, CallGraph callGraph, Collection<SymbolicExpression>[] params)
			throws SemanticException {
		
		AnalysisState<H, V> result = null;
		for (SymbolicExpression expr1 : params[0])
			for (SymbolicExpression expr2 : params[1]) {
				// TODO should be runtime type
				AnalysisState<H, V> tmp = new AnalysisState<>(computedState.getState(),
						new BinaryExpression(getStaticType(), expr1, expr2, BinaryOperator.NUMERIC_SUB));
				if (result == null)
					result = tmp;
				else
					result = result.lub(tmp);
			}
		return result;
	}
}
