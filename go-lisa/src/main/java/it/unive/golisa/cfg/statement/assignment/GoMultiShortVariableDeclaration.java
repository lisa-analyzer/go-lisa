package it.unive.golisa.cfg.statement.assignment;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import it.unive.golisa.cfg.statement.block.BlockInfo;
import it.unive.golisa.cfg.statement.block.OpenBlock;
import it.unive.golisa.golang.util.GoLangUtils;
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
import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;

/**
 * A Go multi short variable declaration statement.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoMultiShortVariableDeclaration extends GoMultiAssignment {

	/**
	 * Builds a multi-short variable declaration.
	 * 
	 * @param cfg             the cfg that this statement belongs to
	 * @param location        the location where this statement is defined
	 *                            within the source file
	 * @param ids             identifiers to assign
	 * @param e               expression to assign
	 * @param listBlock       list of block informations
	 * @param containingBlock the block to which this assignment belongs to
	 */
	public GoMultiShortVariableDeclaration(CFG cfg, SourceCodeLocation location, Expression[] ids,
			Expression e, List<BlockInfo> listBlock, OpenBlock containingBlock) {
		super(cfg, location, ids, e, listBlock, containingBlock);
	}

	@Override
	public String toString() {
		return StringUtils.join(ids, ", ") + " := " + e.toString();
	}

	@Override
	public <A extends AbstractState<A, H, V, T>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>,
			T extends TypeDomain<T>> AnalysisState<A, H, V, T> semantics(
					AnalysisState<A, H, V, T> entryState, InterproceduralAnalysis<A, H, V, T> interprocedural,
					StatementStore<A, H, V, T> expressions) throws SemanticException {
		TypeSystem types = getProgram().getTypes();

		AnalysisState<A, H, V, T> rightState = e.semantics(entryState, interprocedural, expressions);
		expressions.put(e, rightState);

		// if the right state is top,
		// we put all the variables to top
		if (rightState.isTop() || rightState.getComputedExpressions().size() > 1) {
			AnalysisState<A, H, V, T> result = entryState;

			for (int i = 0; i < ids.length; i++) {
				if (GoLangUtils.refersToBlankIdentifier((VariableRef) ids[i]))
					continue;

				AnalysisState<A, H, V, T> idState = ids[i].semantics(result, interprocedural, expressions);
				expressions.put(ids[i], idState);

				AnalysisState<A, H, V, T> tmp = result;

				for (SymbolicExpression id : idState.getComputedExpressions()) {
					if (rightState.isTop()) {
						AnalysisState<A, H, V, T> tmp2 = rightState.bottom();
						for (Type type : id.getRuntimeTypes(types))
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

		return super.semantics(entryState, interprocedural, expressions);
	}
}
