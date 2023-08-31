package it.unive.golisa.cfg.expression.unary;

import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.AccessChild;
import it.unive.lisa.symbolic.heap.HeapDereference;
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.symbolic.value.UnaryExpression;
import it.unive.lisa.symbolic.value.Variable;
import it.unive.lisa.symbolic.value.operator.unary.StringLength;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;
import it.unive.lisa.type.Untyped;

/**
 * A Go len expression (e.g., len(x)).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoLength extends it.unive.lisa.program.cfg.statement.UnaryExpression {

	/**
	 * Builds the len expression.
	 * 
	 * @param cfg      the {@link CFG} where this expression lies
	 * @param location the location where this expression is defined
	 * @param exp      the expression
	 */
	public GoLength(CFG cfg, SourceCodeLocation location, Expression exp) {
		super(cfg, location, "len", exp);
	}

	@Override
	public <A extends AbstractState<A, H, V, T>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>,
			T extends TypeDomain<T>> AnalysisState<A, H, V, T> unarySemantics(
					InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
					SymbolicExpression expr, StatementStore<A, H, V, T> expressions) throws SemanticException {

		TypeSystem typeSys = getProgram().getTypes();
		AnalysisState<A, H, V, T> result = state.bottom();
		for (Type type : expr.getRuntimeTypes(typeSys)) {
			if (type.isPointerType() && (type.asPointerType().getInnerType().isArrayType()
					|| type.asPointerType().getInnerType() instanceof GoSliceType)) {
				// When expr is an array or a slice, we access the len property
				AnalysisState<A, H, V, T> rec = state.smallStepSemantics(expr, this);
				AnalysisState<A, H, V, T> partialResult = state.bottom();

				for (SymbolicExpression recExpr : rec.getComputedExpressions()) {
					HeapDereference deref = new HeapDereference(type, recExpr, getLocation());
					AnalysisState<A, H, V, T> refState = state.smallStepSemantics(deref, this);

					for (SymbolicExpression l : refState.getComputedExpressions()) {
						AnalysisState<A, H, V, T> tmp = rec.smallStepSemantics(new AccessChild(GoIntType.INSTANCE, l,
								new Variable(Untyped.INSTANCE, "len", getLocation()), getLocation()), this);
						partialResult = partialResult.lub(tmp);
					}
				}
				result = result.lub(partialResult);
			} else if (type.isArrayType() || type instanceof GoSliceType) {
				// When expr is an array or a slice, we access the len property
//				AnalysisState<A, H, V, T> rec = state.smallStepSemantics(expr, this);
//				AnalysisState<A, H, V, T> partialResult = state.bottom();
//
//				for (SymbolicExpression recExpr : rec.getComputedExpressions()) {
//					AnalysisState<A, H, V, T> tmp = rec.smallStepSemantics(new AccessChild(GoIntType.INSTANCE, recExpr,
//							new Variable(Untyped.INSTANCE, "len", getLocation()), getLocation()), this);
//					partialResult = partialResult.lub(tmp);
//				}
				// FIXME we get here when rec is a parameter of an entrypoint,
				// and len is not defined yet..
				result = result.lub(state.smallStepSemantics(new PushAny(GoIntType.INSTANCE, getLocation()), this));
			} else if (type.isStringType())
				return result.lub(state.smallStepSemantics(
						new UnaryExpression(GoIntType.INSTANCE, expr, StringLength.INSTANCE, getLocation()), this));
			else
				result = result.lub(state.smallStepSemantics(expr, this));
		}
		return result;
	}
}
