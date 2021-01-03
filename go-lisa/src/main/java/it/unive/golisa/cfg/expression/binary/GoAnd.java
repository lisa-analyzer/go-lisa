package it.unive.golisa.cfg.expression.binary;

import java.util.Collection;

import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.HeapDomain;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.ValueDomain;
import it.unive.lisa.analysis.impl.types.TypeEnvironment;
import it.unive.lisa.callgraph.CallGraph;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.statement.NativeCall;
import it.unive.lisa.symbolic.SymbolicExpression;

/**
 * A Go and function function call.
 * e1 & e2 copies a bit to the result if it exists in both operands.
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoAnd extends NativeCall {
	
	/**
	 * Builds a Go and expression. 
	 * The location where this expression appears is unknown 
	 * (i.e. no source file/line/column is available).
	 * 
	 * @param cfg	the cfg that this expression belongs to
	 * @param exp1	left-hand side operand
	 * @param exp2 	right-hand side operand 
	 */
	public GoAnd(CFG cfg, Expression exp1, Expression exp2) {
		super(cfg, null, -1, -1, "&", exp1, exp2);
	}
	
	/**
	 * Builds a Go and expression at a given location in the program.
	 * 
	 * @param cfg           the cfg that this expression belongs to
	 * @param sourceFile    the source file where this expression happens. If
	 *                      unknown, use {@code null}
	 * @param line          the line number where this expression happens in the
	 *                      source file. If unknown, use {@code -1}
	 * @param col           the column where this expression happens in the source
	 *                      file. If unknown, use {@code -1}
	 * @param exp1		    left-hand side operand
	 * @param exp2		    right-hand side operand
	 */
	public GoAnd(CFG cfg, String sourceFile, int line, int col, Expression exp1, Expression exp2) {
		super(cfg, sourceFile, line, col, "&", exp1, exp2);
	}

	@Override
	public <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> callSemantics(
			AnalysisState<A, H, V> entryState, CallGraph callGraph, AnalysisState<A, H, V>[] computedStates,
			Collection<SymbolicExpression>[] params) throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <A extends AbstractState<A, H, TypeEnvironment>, H extends HeapDomain<H>> AnalysisState<A, H, TypeEnvironment> callTypeInference(
			AnalysisState<A, H, TypeEnvironment> entryState, CallGraph callGraph,
			AnalysisState<A, H, TypeEnvironment>[] computedStates, Collection<SymbolicExpression>[] params)
			throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}

}
