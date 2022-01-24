package it.unive.golisa.cfg.expression.literal;

import it.unive.golisa.cfg.statement.assignment.GoShortVariableDeclaration.NumericalTyper;
import it.unive.golisa.cfg.type.composite.GoTypesTuple;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.lattices.ExpressionSet;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.caches.Caches;
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

public class GoExpressionsTuple extends NaryExpression {

	public GoExpressionsTuple(CFG cfg, CodeLocation location, Expression... expressions) {
		super(cfg, location, "(tuple)", expressions);
	}

	@Override
	public <A extends AbstractState<A, H, V>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>> AnalysisState<A, H, V> expressionSemantics(
					InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> state,
					ExpressionSet<SymbolicExpression>[] params) throws SemanticException {

		// Length of the expression tuple
		int len = getSubExpressions().length;
		Parameter[] types = new Parameter[len];

		for (int i = 0; i < types.length; i++) {
			Expression p = getSubExpressions()[i];
			types[i] = new Parameter(p.getLocation(), "_", p.getDynamicType());
		}

		GoTypesTuple tupleType = new GoTypesTuple(types);

		HeapAllocation created = new HeapAllocation(Caches.types().mkSingletonSet(tupleType), getLocation());

		// Allocates the new heap allocation
		AnalysisState<A, H, V> containerState = state.smallStepSemantics(created, this);
		ExpressionSet<SymbolicExpression> containerExps = containerState.getComputedExpressions();

		AnalysisState<A, H, V> result = state.bottom();

		for (SymbolicExpression containerExp : containerExps) {
			HeapReference reference = new HeapReference(Caches.types().mkSingletonSet(getStaticType()), containerExp,
					getLocation());
			HeapDereference dereference = new HeapDereference(Caches.types().mkSingletonSet(getStaticType()), reference,
					getLocation());

			AnalysisState<A, H, V> tmp = containerState;
			for (int i = 0; i < len; i++) {
				AccessChild access = new AccessChild(Caches.types().mkSingletonSet(tupleType.getTypeAt(i)), dereference,
						new Constant(GoIntType.INSTANCE, i, getLocation()), getLocation());
				AnalysisState<A, H, V> accessState = tmp.smallStepSemantics(access, this);

				for (SymbolicExpression index : accessState.getComputedExpressions())
					for (SymbolicExpression v : params[i])
						tmp = tmp.assign(index, NumericalTyper.type(v), this);
			}

			result = result.lub(tmp.smallStepSemantics(reference, this));
		}

		return result;
	}
}
