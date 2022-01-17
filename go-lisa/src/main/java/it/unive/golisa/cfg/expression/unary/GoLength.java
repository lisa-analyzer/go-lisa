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
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.AccessChild;
import it.unive.lisa.symbolic.heap.HeapDereference;
import it.unive.lisa.symbolic.value.UnaryExpression;
import it.unive.lisa.symbolic.value.Variable;
import it.unive.lisa.symbolic.value.operator.unary.StringLength;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import it.unive.lisa.util.collections.externalSet.ExternalSet;

public class GoLength extends it.unive.lisa.program.cfg.statement.UnaryExpression {

	public GoLength(CFG cfg, SourceCodeLocation location, Expression exp) {
		super(cfg, location, "len", exp);
	}

	@Override
	protected <A extends AbstractState<A, H, V>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>> AnalysisState<A, H, V> unarySemantics(
					InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> state,
					SymbolicExpression expr)
					throws SemanticException {
		ExternalSet<Type> intType = Caches.types().mkSingletonSet(GoIntType.INSTANCE);
		AnalysisState<A, H, V> result = state.bottom();

		for (Type type : expr.getTypes()) {
			if (type.isArrayType() || type instanceof GoSliceType) {
				// When expr is an array or a slice, we access the len property
				AnalysisState<A, H, V> rec = state.smallStepSemantics(expr, this);
				AnalysisState<A, H, V> partialResult = state.bottom();
				ExternalSet<Type> untypedType = Caches.types().mkSingletonSet(Untyped.INSTANCE);

				for (SymbolicExpression recExpr : rec.getComputedExpressions()) {
					HeapDereference deref = new HeapDereference(getRuntimeTypes(), recExpr, getLocation());
					AnalysisState<A, H, V> refState = state.smallStepSemantics(deref, this);

					for (SymbolicExpression l : refState.getComputedExpressions()) {
						AnalysisState<A, H, V> tmp = rec.smallStepSemantics(new AccessChild(getRuntimeTypes(), l,
								new Variable(untypedType, "len", getLocation()), getLocation()), this);
						partialResult = partialResult.lub(tmp);
					}
				}
				result = result.lub(partialResult);
			}

			if (type.isStringType())
				result = result.lub(state.smallStepSemantics(
						new UnaryExpression(intType, expr, StringLength.INSTANCE, getLocation()), this));
		}

		return result;
	}
}
