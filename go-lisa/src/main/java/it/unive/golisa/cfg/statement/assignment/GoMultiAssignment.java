package it.unive.golisa.cfg.statement.assignment;

import it.unive.golisa.cfg.statement.assignment.GoShortVariableDeclaration.NumericalTyper;
import it.unive.golisa.cfg.statement.block.BlockInfo;
import it.unive.golisa.cfg.statement.block.OpenBlock;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.caches.Caches;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.AccessChild;
import it.unive.lisa.symbolic.heap.HeapDereference;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.util.datastructures.graph.GraphVisitor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class GoMultiAssignment extends Expression {

	protected final Expression[] ids;
	protected final Expression e;

	/**
	 * The chain of blocks (starting from the block containing this assignment)
	 * to the block defining the assigned variables
	 */
	private Map<VariableRef, List<BlockInfo>> blocksToDeclaration;

	private final OpenBlock containingBlock;

	public GoMultiAssignment(CFG cfg, String filePath, int line, int col, Expression[] ids, Expression e,
			List<BlockInfo> listBlock, OpenBlock containingBlock) {
		super(cfg, new SourceCodeLocation(filePath, line, col));
		this.ids = ids;
		this.e = e;
		this.blocksToDeclaration = new HashMap<>();

		for (Expression id : ids)
			if (id instanceof VariableRef)
				blocksToDeclaration.put((VariableRef) id, BlockInfo.getListOfBlocksBeforeDeclaration(listBlock, id));
		this.containingBlock = containingBlock;
	}

	@Override
	public int setOffset(int offset) {
		this.offset = offset;
		ids[0].setOffset(offset + 1);
		for (int i = 1; i < ids.length; i++)
			ids[i].setOffset(ids[i - 1].getOffset() + 1);
		return e.setOffset(ids[ids.length - 1].getOffset() + 1);
	}

	@Override
	public <V> boolean accept(GraphVisitor<CFG, Statement, Edge, V> visitor, V tool) {
		for (int i = 0; i < ids.length; i++)
			if (!ids[i].accept(visitor, tool))
				return false;

		if (!e.accept(visitor, tool))
			return false;

		return visitor.visit(tool, getCFG(), this);
	}

	@Override
	public String toString() {
		return StringUtils.join(ids, ", ") + " := " + e.toString();
	}

	@Override
	public <A extends AbstractState<A, H, V>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>> AnalysisState<A, H, V> semantics(
					AnalysisState<A, H, V> entryState, InterproceduralAnalysis<A, H, V> interprocedural,
					StatementStore<A, H, V> expressions)
					throws SemanticException {
		AnalysisState<A, H, V> rightState = e.semantics(entryState, interprocedural, expressions);
		expressions.put(e, rightState);

		AnalysisState<A, H, V> result = rightState;

		for (int i = 0; i < ids.length; i++) {

			if (GoLangUtils.refersToBlankIdentifier(ids[i]))
				continue;

			List<BlockInfo> blockInfo = blocksToDeclaration.get(ids[i]);
			AnalysisState<A, H, V> idState = ids[i].semantics(rightState, interprocedural, expressions);

			AnalysisState<A, H, V> tmp2 = rightState.bottom();
			for (SymbolicExpression retExp : rightState.getComputedExpressions()) {
				HeapDereference dereference = new HeapDereference(Caches.types().mkSingletonSet(getStaticType()),
						retExp, getLocation());
				AccessChild access = new AccessChild(Caches.types().mkUniversalSet(), dereference,
						new Constant(GoIntType.INSTANCE, i, getLocation()), getLocation());
				AnalysisState<A, H, V> accessState = rightState.smallStepSemantics(access, this);

				AnalysisState<A, H, V> tmp = rightState.bottom();
				for (SymbolicExpression accessExp : accessState.getComputedExpressions()) {
					for (SymbolicExpression idExp : idState.getComputedExpressions()) {
						AnalysisState<A, H, V> assign = assignScopedId(rightState, idExp,
								NumericalTyper.type(accessExp), blockInfo);
						tmp = tmp.lub(assign);
					}
				}
				tmp2 = tmp.lub(tmp2);
			}

			result = tmp2;
		}

		for (int i = 0; i < ids.length; i++) {
			if (ids[i] instanceof VariableRef && GoLangUtils.refersToBlankIdentifier((VariableRef) ids[i]))
				continue;

			AnalysisState<A, H, V> idState = ids[i].semantics(result, interprocedural, expressions);
			expressions.put(ids[i], idState);

			if (GoLangUtils.refersToBlankIdentifier(ids[i]))
				continue;

			for (SymbolicExpression retExp : rightState.getComputedExpressions()) {
				HeapDereference dereference = new HeapDereference(Caches.types().mkSingletonSet(getStaticType()),
						retExp, getLocation());
				AccessChild access = new AccessChild(Caches.types().mkUniversalSet(), dereference,
						new Constant(GoIntType.INSTANCE, i, getLocation()), getLocation());
				AnalysisState<A, H, V> accessState = result.smallStepSemantics(access, this);

				for (SymbolicExpression accessExp : accessState.getComputedExpressions()) {
					for (SymbolicExpression idExp : idState.getComputedExpressions()) {
						AnalysisState<A, H, V> assign = result.assign(idExp, NumericalTyper.type(accessExp), this);
						result = result.lub(assign);
					}
				}
			}
		}

		return result;
	}

	private <A extends AbstractState<A, H, V>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>> AnalysisState<A, H, V> assignScopedId(AnalysisState<A, H, V> entryState,
					SymbolicExpression expr1, SymbolicExpression expr2, List<BlockInfo> blockInfo)
					throws SemanticException {

		// if the assignment occurs in the same block in which
		// the variable is declared, no assignment on scoped ids
		// needs to be performed
		if (blockInfo == null || blockInfo.isEmpty() || blockInfo.get(0).getOpen() != containingBlock)
			return entryState;

		AnalysisState<A, H, V> tmp = entryState;

		// removes the block where the declaration occurs
		List<BlockInfo> blocksBeforeDecl = blockInfo.subList(0, blocksToDeclaration.size() - 1);

		for (int i = 0; i < blocksBeforeDecl.size(); i++) {
			SymbolicExpression idToAssign = expr1;

			for (int j = blocksBeforeDecl.size() - 1 - i; j >= 0; j--)
				idToAssign = idToAssign.pushScope(new ScopeToken(blocksBeforeDecl.get(j).getOpen()));
			tmp = tmp.assign(idToAssign, expr2, this);
		}

		return tmp;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((e == null) ? 0 : e.hashCode());
		result = prime * result + Arrays.hashCode(ids);
		return result;
	}
}
