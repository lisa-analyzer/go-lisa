package it.unive.golisa.cfg.expression.unary;

import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
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
import it.unive.lisa.symbolic.heap.AccessChild;
import it.unive.lisa.symbolic.value.UnaryExpression;
import it.unive.lisa.symbolic.value.UnaryOperator;
import it.unive.lisa.symbolic.value.Variable;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import it.unive.lisa.util.collections.externalSet.ExternalSet;

public class GoLength extends UnaryNativeCall {
	
	public GoLength(CFG cfg, String sourceFile, int line, int col, Expression exp) {
		super(cfg, new SourceCodeLocation(sourceFile, line, col), "len", exp);
	}

	@Override
	protected <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> unarySemantics(
			AnalysisState<A, H, V> entryState, CallGraph callGraph, AnalysisState<A, H, V> exprState,
			SymbolicExpression expr) throws SemanticException {
	
		ExternalSet<Type> intType = Caches.types().mkSingletonSet(GoIntType.INSTANCE);

		if (expr.getDynamicType().isArrayType() || expr.getDynamicType() instanceof GoSliceType) {
			// When expr is an array or a slice, we access the len property
			ExternalSet<Type> untypedType = Caches.types().mkSingletonSet(Untyped.INSTANCE);
			AccessChild access = new AccessChild(intType, expr, new Variable(untypedType, "len"));
			return exprState.smallStepSemantics(access, this);
		}
				
		if (!expr.getDynamicType().isStringType() && !expr.getDynamicType().isUntyped())
			return entryState.bottom();
		
		
		return exprState.smallStepSemantics(new UnaryExpression(intType, expr, UnaryOperator.STRING_LENGTH), this);
	}
}
