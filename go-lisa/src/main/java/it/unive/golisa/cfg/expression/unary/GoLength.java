package it.unive.golisa.cfg.expression.unary;

import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
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
import it.unive.lisa.program.cfg.statement.UnaryNativeCall;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.AccessChild;
import it.unive.lisa.symbolic.heap.HeapDereference;
import it.unive.lisa.symbolic.value.MemoryPointer;
import it.unive.lisa.symbolic.value.UnaryExpression;
import it.unive.lisa.symbolic.value.UnaryOperator;
import it.unive.lisa.symbolic.value.Variable;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import it.unive.lisa.util.collections.externalSet.ExternalSet;

public class GoLength extends UnaryNativeCall {
	
	public GoLength(CFG cfg, SourceCodeLocation location, Expression exp) {
		super(cfg, location, "len", exp);
	}

	@Override
	protected <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> unarySemantics(
			AnalysisState<A, H, V> entryState, InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> exprState,
			SymbolicExpression expr) throws SemanticException {
	
		ExternalSet<Type> intType = Caches.types().mkSingletonSet(GoIntType.INSTANCE);

		if (expr.getDynamicType().isArrayType() || expr.getDynamicType() instanceof GoSliceType) {
			// When expr is an array or a slice, we access the len property
			AnalysisState<A, H, V> rec = entryState.smallStepSemantics(expr, this);
			AnalysisState<A, H, V> result = entryState.bottom();
			ExternalSet<Type> untypedType = Caches.types().mkSingletonSet(Untyped.INSTANCE);

			for (SymbolicExpression recExpr : rec.getComputedExpressions()) {	
				HeapDereference deref = new HeapDereference(getRuntimeTypes(), recExpr, getLocation());
				AnalysisState<A, H, V> refState = entryState.smallStepSemantics(deref, this);

				for (SymbolicExpression l : refState.getComputedExpressions()) {
					if (!(l instanceof MemoryPointer))
						continue;

					AnalysisState<A, H, V> tmp = rec.smallStepSemantics(new AccessChild(getRuntimeTypes(), (MemoryPointer) l, new Variable(untypedType, "len", getLocation()), getLocation()), this);
					result = result.lub(tmp);
				}

			}
			return result;
		}
				
		if (!expr.getDynamicType().isStringType() && !expr.getDynamicType().isUntyped())
			return entryState.bottom();
		
		
		return exprState.smallStepSemantics(new UnaryExpression(intType, expr, UnaryOperator.STRING_LENGTH, getLocation()), this);
	}
}
