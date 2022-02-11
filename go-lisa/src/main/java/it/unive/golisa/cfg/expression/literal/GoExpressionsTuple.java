package it.unive.golisa.cfg.expression.literal;

import it.unive.golisa.cfg.statement.assignment.GoShortVariableDeclaration.NumericalTyper;
import it.unive.golisa.cfg.type.composite.GoTypesTuple;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
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
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.NaryExpression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.AccessChild;
import it.unive.lisa.symbolic.heap.HeapAllocation;
import it.unive.lisa.symbolic.heap.HeapDereference;
import it.unive.lisa.symbolic.heap.HeapReference;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.type.ReferenceType;

public class GoExpressionsTuple extends NaryExpression {

	public GoExpressionsTuple(CFG cfg, CodeLocation location, Expression... expressions) {
		super(cfg, location, "(tuple)", expressions);
	}

	@Override
	public <A extends AbstractState<A, H, V, T>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>,
			T extends TypeDomain<T>> AnalysisState<A, H, V, T> expressionSemantics(
					InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
					ExpressionSet<SymbolicExpression>[] params, StatementStore<A, H, V, T> expressions)
					throws SemanticException {
		// Length of the expression tuple
		int len = getSubExpressions().length;
		Parameter[] types = new Parameter[len];

		for (int i = 0; i < types.length; i++) {
			Expression p = getSubExpressions()[i];
			types[i] = new Parameter(p.getLocation(), "_", p.getStaticType());
		}

		GoTypesTuple tupleType = new GoTypesTuple(types);

		HeapAllocation created = new HeapAllocation(tupleType, getLocation());

		// Allocates the new heap allocation
		AnalysisState<A, H, V, T> containerState = state.smallStepSemantics(created, this);
		ExpressionSet<SymbolicExpression> containerExps = containerState.getComputedExpressions();

		AnalysisState<A, H, V, T> result = state.bottom();

		for (SymbolicExpression containerExp : containerExps) {
			HeapReference reference = new HeapReference(new ReferenceType(getStaticType()), containerExp,
					getLocation());
			HeapDereference dereference = new HeapDereference(getStaticType(), reference,
					getLocation());

			AnalysisState<A, H, V, T> tmp = containerState;
			for (int i = 0; i < len; i++) {
				AccessChild access = new AccessChild(tupleType.getTypeAt(i), dereference,
						new Constant(GoIntType.INSTANCE, i, getLocation()), getLocation());
				AnalysisState<A, H, V, T> accessState = tmp.smallStepSemantics(access, this);

				for (SymbolicExpression index : accessState.getComputedExpressions())
					for (SymbolicExpression v : params[i])
						tmp = tmp.assign(index, NumericalTyper.type(v), this);
			}

			result = result.lub(tmp.smallStepSemantics(reference, this));
		}

		return result;
	}
}
