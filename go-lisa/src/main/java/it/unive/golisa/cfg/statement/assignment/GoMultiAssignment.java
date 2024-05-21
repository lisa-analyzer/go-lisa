package it.unive.golisa.cfg.statement.assignment;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import it.unive.golisa.cfg.statement.block.BlockInfo;
import it.unive.golisa.cfg.statement.block.OpenBlock;
import it.unive.golisa.cfg.type.composite.GoTupleType;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.heap.pointbased.AllocationSite;
import it.unive.lisa.analysis.lattices.ExpressionSet;
import it.unive.lisa.analysis.type.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.NaryExpression;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.AccessChild;
import it.unive.lisa.symbolic.heap.HeapDereference;
import it.unive.lisa.symbolic.heap.HeapReference;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.type.ReferenceType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;

/**
 * A Go multi-assignment.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoMultiAssignment extends NaryExpression {

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
	private Map<VariableRef, List<BlockInfo>> blocksToDeclaration;

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
		super(cfg, location, ":=",
				GoTupleType.getTupleTypeOf(location, Arrays.stream(ids).map(i -> i.getStaticType()).toArray(Type[]::new)), 
				join(ids, e));
		this.ids = ids;
		this.e = e;
		this.blocksToDeclaration = new HashMap<>();

		for (Expression id : ids)
			if (id instanceof VariableRef)
				blocksToDeclaration.put((VariableRef) id, BlockInfo.getListOfBlocksBeforeDeclaration(listBlock, id));
		this.containingBlock = containingBlock;
	}
	
	private static Expression[] join(Expression[] ids, Expression e) {
		Expression[] res = new Expression[ids.length + 1];
		System.arraycopy(ids, 0, res, 0, ids.length);
		res[ids.length] = e;
		return res;
	}
	
	@Override
	public String toString() {
		return StringUtils.join(ids, ", ") + " := " + e.toString();
	}

	private <A extends AbstractState<A>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>,
			T extends TypeDomain<T>> AnalysisState<A> assignScopedId(AnalysisState<A> rightState,
					SymbolicExpression expr1, SymbolicExpression expr2, List<BlockInfo> blockInfo, Expression at)
					throws SemanticException {
		// if the assignment occurs in the same block in which
		// the variable is declared, no assignment on scoped ids
		// needs to be performed
		if (blockInfo == null || blockInfo.isEmpty() || blockInfo.get(0).getOpen() != containingBlock)
			return rightState;

		AnalysisState<A> tmp = rightState;

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
	public <A extends AbstractState<A>> AnalysisState<A> forwardSemantics(
			AnalysisState<A> entryState, InterproceduralAnalysis<A> interprocedural,
			StatementStore<A> expressions) throws SemanticException {
		AnalysisState<A> rightState = e.forwardSemantics(entryState, interprocedural, expressions);
		expressions.put(e, rightState);

		AnalysisState<A> result = rightState;

		for (int i = 0; i < ids.length; i++) {
			if (GoLangUtils.refersToBlankIdentifier(ids[i]))
				continue;

			List<BlockInfo> blockInfo = blocksToDeclaration.get(ids[i]);
			AnalysisState<A> idState = ids[i].forwardSemantics(rightState, interprocedural, expressions);
			AnalysisState<A> tmp = rightState.bottom();
			for (SymbolicExpression retExp : rightState.getComputedExpressions()) {
				HeapDereference deref = new HeapDereference(getStaticType(),
						retExp, getLocation());
				AccessChild access;
				if (retExp.getStaticType() instanceof GoTupleType)
					access = new AccessChild(((GoTupleType) retExp.getStaticType()).getTypeAt(i), deref,
							new Constant(GoIntType.INSTANCE, i, getLocation()), getLocation());
				else
					access = new AccessChild(Untyped.INSTANCE, deref,
							new Constant(GoIntType.INSTANCE, i, getLocation()), getLocation());

				for (SymbolicExpression idExp : idState.getComputedExpressions()) {
					AnalysisState<A> assign = assignScopedId(rightState, idExp,
							access, blockInfo, ids[i]);
					tmp = tmp.lub(assign);
				}
			}

			result = tmp;
		}

		AnalysisState<A> finalResult = result;
		for (int i = 0; i < ids.length; i++) {
			if (GoLangUtils.refersToBlankIdentifier(ids[i]))
				continue;

			AnalysisState<A> idState = ids[i].forwardSemantics(result, interprocedural, expressions);
			expressions.put(ids[i], idState);

			if (GoLangUtils.refersToBlankIdentifier(ids[i]))
				continue;

			AnalysisState<A> partialResult = entryState.bottom();
			for (SymbolicExpression retExp : rightState.getComputedExpressions()) {
				
				boolean allAllocationSites = true;
				for (SymbolicExpression e : rightState.getState().rewrite(retExp, this, rightState.getState()))
					if (!(e instanceof AllocationSite)) {
						allAllocationSites = false;
						break;
					}

				SymbolicExpression rec = retExp;
				if (!allAllocationSites)
					rec = new HeapDereference(getStaticType(), retExp, getLocation());
				
				AccessChild access;
				Type rightExpType = retExp.getStaticType();
				if (rightExpType instanceof ReferenceType
						&& ((ReferenceType) rightExpType).getInnerType() instanceof GoTupleType) {
					Type typeAtPos = ((GoTupleType) ((ReferenceType) rightExpType).getInnerType()).getTypeAt(i);
					access = new AccessChild(typeAtPos, rec,
							new Constant(GoIntType.INSTANCE, i, getLocation()), getLocation());
				} else
					access = new AccessChild(Untyped.INSTANCE, rec,
							new Constant(GoIntType.INSTANCE, i, getLocation()), getLocation());
				for (SymbolicExpression idExp : idState.getComputedExpressions()) {
					AnalysisState<A> assign;
					if (rightExpType instanceof ReferenceType
							&& ((ReferenceType) rightExpType).getInnerType() instanceof GoTupleType) {
						Type typeAtPos = ((GoTupleType) ((ReferenceType) rightExpType).getInnerType()).getTypeAt(i);

						if (typeAtPos instanceof ReferenceType) {
							HeapReference ref = new HeapReference(new ReferenceType(typeAtPos), access, getLocation());
							assign = finalResult.assign(idExp, ref, this);
						} else
							assign = finalResult.assign(idExp, access, this);
					} else
						assign = finalResult.assign(idExp, access, this);

					partialResult = partialResult.lub(assign);
				}

				finalResult = partialResult;
			}
		}

		return finalResult;
	}

	/**
	 * Yields the assigned identifiers.
	 * 
	 * @return the assigned identifiers
	 */
	public Expression[] getIds() {
		return ids;
	}

	/**
	 * Yields the expression to assign.
	 * 
	 * @return the expression to assign
	 */
	public Expression getExpressionToAssign() {
		return e;
	}

	@Override
	protected int compareSameClassAndParams(
			Statement o) {
		return 0; // nothing else to compare
	}

	@Override
	public <A extends AbstractState<A>> AnalysisState<A> forwardSemanticsAux(
			InterproceduralAnalysis<A> interprocedural,
			AnalysisState<A> state,
			ExpressionSet[] params,
			StatementStore<A> expressions)
			throws SemanticException {
		throw new UnsupportedOperationException("This method should never be called!");
	}
}
