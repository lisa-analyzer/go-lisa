package it.unive.golisa.cfg.expression.unary;

import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoArrayType;
import it.unive.golisa.cfg.type.composite.GoMapType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt8Type;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.lattices.ExpressionSet;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.NaryExpression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.type.ReferenceType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;

/**
 * Instrumented class.
 * 
 * @author OlivieriL
 */
public class GoRangeGetNextValue extends NaryExpression {

	/**
	 * Builds the next value expression.
	 * 
	 * @param cfg       the {@link CFG} where this expression lies
	 * @param location  the location where this expression is defined
	 * @param rangeItem the subexpression
	 */
	public GoRangeGetNextValue(CFG cfg, CodeLocation location, Expression rangeItem) {
		super(cfg, location, "GoRangeGetNextValue", computeType(rangeItem.getStaticType()));
	}

	private static Type computeType(Type type) {
		if (!type.equals(Untyped.INSTANCE)) {
			if (type instanceof GoStringType)
				return GoUInt8Type.INSTANCE;
			else if (type instanceof GoArrayType)
				return ((GoArrayType) type).getContenType();
			else if (type instanceof GoSliceType)
				return ((GoSliceType) type).getContentType();
			else if (type instanceof GoMapType)
				return ((GoMapType) type).getElementType();
			else if (type instanceof ReferenceType)
				return computeType(((ReferenceType) type).getInnerType());
		}
		return Untyped.INSTANCE;
	}

	@Override
	public <A extends AbstractState<A, H, V, T>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>,
			T extends TypeDomain<T>> AnalysisState<A, H, V, T> expressionSemantics(
					InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
					ExpressionSet<SymbolicExpression>[] params, StatementStore<A, H, V, T> expressions)
					throws SemanticException {
		if (state.getComputedExpressions().size() == 1) {
			for (SymbolicExpression e : state.getComputedExpressions()) {
				Type computed = computeType(e.getDynamicType());
				if (!computed.equals(Untyped.INSTANCE))
					return state.smallStepSemantics(
							new PushAny(computed, this.getLocation()),
							this);
			}
		}

		return state;
	}

}