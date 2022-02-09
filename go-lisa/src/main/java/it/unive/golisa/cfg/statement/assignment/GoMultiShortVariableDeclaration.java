package it.unive.golisa.cfg.statement.assignment;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import it.unive.golisa.analysis.taint.Clean;
import it.unive.golisa.cfg.statement.assignment.GoShortVariableDeclaration.NumericalTyper;
import it.unive.golisa.cfg.statement.block.BlockInfo;
import it.unive.golisa.cfg.statement.block.OpenBlock;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.golisa.golang.util.GoLangUtils;
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
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.AccessChild;
import it.unive.lisa.symbolic.heap.HeapDereference;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;

public class GoMultiShortVariableDeclaration extends GoMultiAssignment {

	public GoMultiShortVariableDeclaration(CFG cfg, String filePath, int line, int col, Expression[] ids,
			Expression e, List<BlockInfo> listBlock, OpenBlock containingBlock) {
		super(cfg, filePath, line, col, ids, e, listBlock, containingBlock);
	}

	@Override
	public String toString() {
		return StringUtils.join(ids, ", ") + " := " + e.toString();
	}

	private boolean isClean(ExpressionSet<SymbolicExpression> computedExpressions) {
		return computedExpressions.size() == 1 && computedExpressions.iterator().next() instanceof Clean;
	}

	@Override
	public <A extends AbstractState<A, H, V, T>,
	H extends HeapDomain<H>,
	V extends ValueDomain<V>, T extends TypeDomain<T>> AnalysisState<A, H, V, T> semantics(
			AnalysisState<A, H, V, T> entryState, InterproceduralAnalysis<A, H, V, T> interprocedural,
			StatementStore<A, H, V, T> expressions) throws SemanticException {
		AnalysisState<A, H, V, T> rightState = e.semantics(entryState, interprocedural, expressions);
		expressions.put(e, rightState);

		// if the right state is top,
		// we put all the variables to top
		if (rightState.isTop() 
				|| isClean(rightState.getComputedExpressions()) || rightState.getComputedExpressions().size() > 1) {
			AnalysisState<A, H, V, T> result = entryState;

			for (int i = 0; i < ids.length; i++) {
				if (GoLangUtils.refersToBlankIdentifier((VariableRef) ids[i]))
					continue;

				AnalysisState<A, H, V, T> idState = ids[i].semantics(result, interprocedural, expressions);
				expressions.put(ids[i], idState);

				AnalysisState<A, H, V, T> tmp = result;

				for (SymbolicExpression id : idState.getComputedExpressions()) {
					if (isClean(rightState.getComputedExpressions())) {
						AnalysisState<A, H, V, T> tmp2 = rightState.bottom();
						for (Type type : id.getRuntimeTypes()) {
							AnalysisState<A, H, V, T> assign = tmp.assign((Identifier) id, new Clean(type, getLocation()), this);
							if (!assign.getState().getHeapState().isTop() || !assign.getState().getValueState().isTop())
								tmp2 = tmp2.lub(assign);
						}

						tmp = tmp2;
					} else if (rightState.isTop()) {
						AnalysisState<A, H, V, T> tmp2 = rightState.bottom();
						for (Type type : id.getRuntimeTypes())
							tmp2 = tmp2.lub(tmp.assign((Identifier) id, new PushAny(type, getLocation()), this));
						tmp = tmp2;
					} else {
						AnalysisState<A, H, V, T> tmp2 = rightState.bottom();
						for (SymbolicExpression s : rightState.getComputedExpressions())
							tmp2 = tmp2.lub(tmp.assign((Identifier) id, s, this));
						tmp = tmp2;
					}
				}

				result = tmp;
			}

			return result;
		}

		AnalysisState<A, H, V, T> result = rightState;

		for (int i = 0; i < ids.length; i++) {
			if (GoLangUtils.refersToBlankIdentifier((VariableRef) ids[i]))
				continue;

			AnalysisState<A, H, V, T> idState = ids[i].semantics(result, interprocedural, expressions);
			expressions.put(ids[i], idState);

			AnalysisState<A, H, V, T> tmp2 = rightState.bottom();
			for (SymbolicExpression retExp : rightState.getComputedExpressions()) {
				HeapDereference dereference = new HeapDereference(getStaticType(),
						retExp, getLocation());
				AccessChild access = new AccessChild(Untyped.INSTANCE, dereference,
						new Constant(GoIntType.INSTANCE, i, getLocation()), getLocation());
				AnalysisState<A, H, V, T> accessState = result.smallStepSemantics(access, this);

				AnalysisState<A, H, V, T> tmp = rightState.bottom();
				for (SymbolicExpression accessExp : accessState.getComputedExpressions()) {
					for (SymbolicExpression idExp : idState.getComputedExpressions()) {
						AnalysisState<A, H, V, T> assign = result.assign(idExp, NumericalTyper.type(accessExp), this);
						tmp = tmp.lub(assign);
					}
				}

				tmp2 = tmp.lub(tmp2);
			}

			result = tmp2;
		}

		return result;
	}
}
