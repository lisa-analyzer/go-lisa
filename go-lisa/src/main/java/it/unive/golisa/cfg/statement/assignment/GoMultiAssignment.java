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
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
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
import it.unive.lisa.type.Untyped;
import it.unive.lisa.util.datastructures.graph.GraphVisitor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 * A Go multi-assignment.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoMultiAssignment extends Expression {
	// FIXME this should be an instance of NaryExpression to make it work
	// correctly with lisa

	/**
	 * The identifiers to assign.
	 */
	protected final Expression[] ids;

	/**
	 * The expressions to assign.
	 */
	protected final Expression e;

	/**
	 * The chain of blocks (starting from the block containing this assignment)
	 * to the block defining the assigned variables.
	 */
	protected Map<VariableRef, List<BlockInfo>> blocksToDeclaration;

	private final OpenBlock containingBlock;

	/**
	 * Builds a multi-assignment.
	 * 
	 * @param cfg             the cfg that this statement belongs to
	 * @param location        the location where this statement is defined
	 *                            within the source file
	 * @param ids             identifiers to assign
	 * @param e               expression to assign
	 * @param listBlock       list of block informations
	 * @param containingBlock the block to which this assignment belongs to
	 */
	public GoMultiAssignment(CFG cfg, SourceCodeLocation location, Expression[] ids, Expression e,
			List<BlockInfo> listBlock, OpenBlock containingBlock) {
		super(cfg, location);
		this.ids = ids;
		this.e = e;
		this.blocksToDeclaration = new HashMap<>();

		for (Expression id : ids)
			if (id instanceof VariableRef)
				blocksToDeclaration.put((VariableRef) id, BlockInfo.getListOfBlocksBeforeDeclaration(listBlock, id));
		this.containingBlock = containingBlock;

		this.e.setParentStatement(this);
		for (Expression id : ids)
			id.setParentStatement(this);
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

	private <A extends AbstractState<A, H, V, T>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>,
			T extends TypeDomain<T>> AnalysisState<A, H, V, T> assignScopedId(AnalysisState<A, H, V, T> rightState,
					SymbolicExpression expr1, SymbolicExpression expr2, List<BlockInfo> blockInfo, Expression at)
					throws SemanticException {
		// if the assignment occurs in the same block in which
		// the variable is declared, no assignment on scoped ids
		// needs to be performed
		if (blockInfo == null || blockInfo.isEmpty() || blockInfo.get(0).getOpen() != containingBlock)
			return rightState;

		AnalysisState<A, H, V, T> tmp = rightState;

		// removes the block where the declaration occurs
		List<BlockInfo> blocksBeforeDecl = blockInfo.subList(0, blocksToDeclaration.get(at).size() - 1);

		for (int i = 1; i < blocksBeforeDecl.size(); i++) {
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

	@Override
	public <A extends AbstractState<A, H, V, T>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>,
			T extends TypeDomain<T>> AnalysisState<A, H, V, T> semantics(
					AnalysisState<A, H, V, T> entryState, InterproceduralAnalysis<A, H, V, T> interprocedural,
					StatementStore<A, H, V, T> expressions) throws SemanticException {
		AnalysisState<A, H, V, T> rightState = e.semantics(entryState, interprocedural, expressions);
		expressions.put(e, rightState);

		AnalysisState<A, H, V, T> result = rightState;

		for (int i = 0; i < ids.length; i++) {

			if (GoLangUtils.refersToBlankIdentifier(ids[i]))
				continue;

			List<BlockInfo> blockInfo = blocksToDeclaration.get(ids[i]);
			AnalysisState<A, H, V, T> idState = ids[i].semantics(rightState, interprocedural, expressions);

			AnalysisState<A, H, V, T> tmp2 = rightState.bottom();
			for (SymbolicExpression retExp : rightState.getComputedExpressions()) {
				HeapDereference dereference = new HeapDereference(getStaticType(),
						retExp, getLocation());
				AccessChild access = new AccessChild(Untyped.INSTANCE, dereference,
						new Constant(GoIntType.INSTANCE, i, getLocation()), getLocation());
				AnalysisState<A, H, V, T> accessState = rightState.smallStepSemantics(access, this);

				AnalysisState<A, H, V, T> tmp = rightState.bottom();
				for (SymbolicExpression accessExp : accessState.getComputedExpressions()) {
					for (SymbolicExpression idExp : idState.getComputedExpressions()) {
						AnalysisState<A, H, V, T> assign = assignScopedId(rightState, idExp,
								NumericalTyper.type(accessExp), blockInfo, ids[i]);
						tmp = tmp.lub(assign);
					}
				}
				tmp2 = tmp.lub(tmp2);
			}

			result = tmp2;
		}

		AnalysisState<A, H, V, T> finalResult = result;
		for (int i = 0; i < ids.length; i++) {
			if (ids[i] instanceof VariableRef && GoLangUtils.refersToBlankIdentifier((VariableRef) ids[i]))
				continue;

			AnalysisState<A, H, V, T> idState = ids[i].semantics(result, interprocedural, expressions);
			expressions.put(ids[i], idState);

			if (GoLangUtils.refersToBlankIdentifier(ids[i]))
				continue;

			AnalysisState<A, H, V, T> partialResult = entryState.bottom();

			for (SymbolicExpression retExp : rightState.getComputedExpressions()) {
				HeapDereference dereference = new HeapDereference(getStaticType(),
						retExp, getLocation());
				AccessChild access = new AccessChild(Untyped.INSTANCE, dereference,
						new Constant(GoIntType.INSTANCE, i, getLocation()), getLocation());
				AnalysisState<A, H, V, T> accessState = result.smallStepSemantics(access, this);

				for (SymbolicExpression accessExp : accessState.getComputedExpressions())
					for (SymbolicExpression idExp : idState.getComputedExpressions()) {
						AnalysisState<A, H, V,
								T> assign = finalResult.assign(idExp, NumericalTyper.type(accessExp), this);
						partialResult = partialResult.lub(assign);
					}
				finalResult = partialResult;
			}
		}

		return finalResult;
	}
}
