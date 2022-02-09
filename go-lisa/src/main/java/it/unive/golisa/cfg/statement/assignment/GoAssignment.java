package it.unive.golisa.cfg.statement.assignment;

import it.unive.golisa.cfg.statement.block.BlockInfo;
import it.unive.golisa.cfg.statement.block.OpenBlock;
import it.unive.golisa.frontend.GoSyntaxException;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.BinaryExpression;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.symbolic.SymbolicExpression;
import java.util.List;

public class GoAssignment extends BinaryExpression {

	/**
	 * The chain of blocks (starting from the block containing this assignment)
	 * to the block defining the assigned variable
	 */
	private final List<BlockInfo> blocksToDeclaration;

	/**
	 * The open block related to the block that contains this assignment
	 */
	private final OpenBlock containingBlock;

	/**
	 * Builds the assignment, assigning {@code expression} to {@code target},
	 * happening at the given location in the program.
	 * 
	 * @param cfg        the cfg that this statement belongs to
	 * @param location   the location where this statement is defined within the
	 *                       source file. If unknown, use {@code null}
	 * @param target     the target of the assignment
	 * @param expression the expression to assign to {@code target}
	 */
	public GoAssignment(CFG cfg, CodeLocation location, Expression target, Expression expression,
			List<BlockInfo> listBlock, OpenBlock containingBlock) {
		super(cfg, location, "=", target, expression);
		this.blocksToDeclaration = BlockInfo.getListOfBlocksBeforeDeclaration(listBlock, getLeft());
		this.containingBlock = containingBlock;
	}

	@Override
	public final String toString() {
		return getLeft() + " = " + getRight();
	}

	private <A extends AbstractState<A, H, V, T>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>,
			T extends TypeDomain<T>> AnalysisState<A, H, V, T> assignScopedId(AnalysisState<A, H, V, T> entryState,
					SymbolicExpression expr1, SymbolicExpression expr2) throws SemanticException {

		// if the assignment occurs in the same block in which
		// the variable is declared, no assignment on scoped ids
		// needs to be performed
		if (blocksToDeclaration.isEmpty() || blocksToDeclaration.get(0).getOpen() != containingBlock)
			return entryState;

		AnalysisState<A, H, V, T> tmp = entryState;

		// removes the block where the declaration occurs
		List<BlockInfo> blocksBeforeDecl = blocksToDeclaration.subList(0, blocksToDeclaration.size() - 1);

		for (int i = 0; i < blocksBeforeDecl.size(); i++) {
			SymbolicExpression idToAssign = expr1;

			for (int j = blocksBeforeDecl.size() - 1 - i; j >= 0; j--)
				idToAssign = idToAssign.pushScope(new ScopeToken(blocksBeforeDecl.get(j).getOpen()));
			tmp = tmp.assign(idToAssign, expr2, this);
		}

		return tmp;

	}

	@Override
	protected <A extends AbstractState<A, H, V, T>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>,
			T extends TypeDomain<T>> AnalysisState<A, H, V, T> binarySemantics(
					InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
					SymbolicExpression left, SymbolicExpression right, StatementStore<A, H, V, T> expressions)
					throws SemanticException {
		// TODO: this check should be moved in the front-end
		if (!blocksToDeclaration.isEmpty()
				&& blocksToDeclaration.get(blocksToDeclaration.size() - 1).isConstantDeclaration(getLeft()))
			throw new GoSyntaxException("Cannot assign a value to '" + getLeft() + "' at " + getLeft().getLocation()
					+ ", because it is declared as 'const'");

		AnalysisState<A, H, V, T> result = assignScopedId(state, left, right);
		result = result.assign(left, right, this);

		if (!getRight().getMetaVariables().isEmpty())
			result = result.forgetIdentifiers(getRight().getMetaVariables());
		if (!getLeft().getMetaVariables().isEmpty())
			result = result.forgetIdentifiers(getLeft().getMetaVariables());

		return result;
	}
}