package it.unive.golisa.cfg.statement.assignment;

import it.unive.golisa.cfg.statement.block.BlockInfo;
import it.unive.golisa.cfg.statement.block.OpenBlock;
import it.unive.golisa.cli.GoSyntaxException;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.program.cfg.statement.BinaryExpression;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.Identifier;
import java.util.List;

public class GoAssignment extends BinaryExpression {

	private final List<BlockInfo> blocksToDeclaration;

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
		super(cfg, location, target, expression);
		this.blocksToDeclaration = BlockInfo.getListOfBlocksBeforeDeclaration(listBlock, getLeft());
		this.containingBlock = containingBlock;
	}

	@Override
	public final String toString() {
		return getLeft() + " = " + getRight();
	}

	/**
	 * Semantics of an assignment ({@code left = right}) is evaluated as
	 * follows:
	 * <ol>
	 * <li>the semantic of the {@code right} is evaluated using the given
	 * {@code entryState}, returning a new analysis state
	 * {@code as_r = <state_r, expr_r>}</li>
	 * <li>the semantic of the {@code left} is evaluated using {@code as_r},
	 * returning a new analysis state {@code as_l = <state_l, expr_l>}</li>
	 * <li>the final post-state is evaluated through
	 * {@link AnalysisState#assign(Identifier, SymbolicExpression, ProgramPoint)},
	 * using {@code expr_l} as {@code id} and {@code expr_r} as
	 * {@code value}</li>
	 * </ol>
	 * This means that all side effects from {@code right} are evaluated before
	 * the ones from {@code left}.<br>
	 * <br>
	 * {@inheritDoc}
	 */
	@Override
	public final <A extends AbstractState<A, H, V>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>> AnalysisState<A, H, V> semantics(
					AnalysisState<A, H, V> entryState, InterproceduralAnalysis<A, H, V> interprocedural,
					StatementStore<A, H, V> expressions)
					throws SemanticException {
		
		// TODO: this check should be moved in the front-end
		if (blocksToDeclaration.get(blocksToDeclaration.size() - 1).isConstantDeclaration(getLeft()))
			throw new GoSyntaxException("Cannot assign a value to '" + getLeft() + "' at " + getLeft().getLocation()
					+ ", because it is declared as 'const'");

		AnalysisState<A, H, V> right = getRight().semantics(entryState, interprocedural, expressions);
		AnalysisState<A, H, V> left = getLeft().semantics(right, interprocedural, expressions);
		expressions.put(getRight(), right);
		expressions.put(getLeft(), left);

		AnalysisState<A, H, V> result = right.bottom();
		for (SymbolicExpression expr1 : left.getComputedExpressions())
			for (SymbolicExpression expr2 : right.getComputedExpressions()) {
				AnalysisState<A, H, V> tmp = assignScopedId(left, expr1, expr2);
				tmp = tmp.assign(expr1, expr2, this);
				result = result.lub(tmp);
			}

		if (!getRight().getMetaVariables().isEmpty())
			result = result.forgetIdentifiers(getRight().getMetaVariables());
		if (!getLeft().getMetaVariables().isEmpty())
			result = result.forgetIdentifiers(getLeft().getMetaVariables());

		return result;
	}

	private <A extends AbstractState<A, H, V>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>> AnalysisState<A, H, V> assignScopedId(AnalysisState<A, H, V> entryState,
					SymbolicExpression expr1, SymbolicExpression expr2) throws SemanticException {

		// if the assignment occurs in the same block in which
		// the variable is declared, no assignment on scoped ids
		// needs to be performed
		if (blocksToDeclaration.get(0).getOpen() != containingBlock)
			return entryState;

		AnalysisState<A, H, V> tmp = entryState;

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
}