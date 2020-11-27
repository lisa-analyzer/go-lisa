package it.unive.golisa.cfg.expression.unary;

import java.util.Collection;

import it.unive.golisa.cfg.type.GoBoolType;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.HeapDomain;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.ValueDomain;
import it.unive.lisa.analysis.callgraph.CallGraph;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.statement.NativeCall;
import it.unive.lisa.symbolic.SymbolicExpression;

/**
 * Go unary not native function class (e.g., !(x > y)).
 * The static type of this expression is definite {@link GoBoolType}.
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoNot extends NativeCall {
	
	/**
	 * Builds a Go unary not expression. The location where 
	 * this expression appears is unknown 
	 * (i.e. no source file/line/column is available).
	 * 
	 * @param cfg	the cfg that this expression belongs to
	 * @param exp	operand
	 */
	public GoNot(CFG cfg, Expression exp) {
		super(cfg, null, -1, -1, "!", GoBoolType.INSTANCE, exp);
	}

	@Override
	public <H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<H, V> callSemantics(
			AnalysisState<H, V> current, CallGraph callGraph, Collection<SymbolicExpression>[] params)
			throws SemanticException {
		throw new UnsupportedOperationException("Semantics not supported yet");
	}
}
