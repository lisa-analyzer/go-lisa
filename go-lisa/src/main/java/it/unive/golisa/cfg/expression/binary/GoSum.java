package it.unive.golisa.cfg.call.binary;

import java.util.Collection;

import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.HeapDomain;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.ValueDomain;
import it.unive.lisa.analysis.callgraph.CallGraph;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.statement.NativeCall;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.BinaryOperator;

/**
 * A Go numerical sum function call (e1 + e2).
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoSum extends NativeCall {

	/**
	 * Builds a Go sum expression. The location where 
	 * this expression appears is unknown 
	 * (i.e. no source file/line/column is available).
	 * 
	 * @param cfg	the cfg that this expression belongs to
	 * @param exp1	left-hand side operand
	 * @param exp2 	right-hand side operand 
	 */
	public GoSum(CFG cfg, Expression exp1, Expression exp2) {
		super(cfg, null, -1, -1, "+", exp1, exp2);
	}

	@Override
	public <H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<H, V> callSemantics(
			AnalysisState<H, V> computedState, CallGraph callGraph, Collection<SymbolicExpression>[] params)
					throws SemanticException {
		
		AnalysisState<H, V> result = null;
		for (SymbolicExpression expr1 : params[0])
			for (SymbolicExpression expr2 : params[1]) {
				AnalysisState<H, V> tmp = new AnalysisState<H, V>(computedState.getState(),
						new BinaryExpression(getStaticType(), expr1, expr2, BinaryOperator.NUMERIC_ADD));
				if (result == null)
					result = tmp;
				else
					result = result.lub(tmp);
			}
		return result;
	}
}
