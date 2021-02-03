package it.unive.golisa.cfg.statement;

import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.HeapDomain;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.ValueDomain;
import it.unive.lisa.callgraph.CallGraph;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.BinaryExpression;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.PushAny;

public class GoShortVariableDeclaration extends BinaryExpression {

	/**
	 * Builds a Go variable declaration with initialization,
	 * assigning {@code expression} to {@code var} 
	 * without make explicit the location (i.e. no source
	 * file/line/column is available).
	 * 
	 * @param cfg        the cfg that this declaration belongs to
	 * @param var     the declared variable
	 * @param expression the expression to assign to {@code var}
	 */
	public GoShortVariableDeclaration(CFG cfg, Expression var, Expression expression) {
		super(cfg, var, expression);
	}

	/**
	 * Builds a Go variable declaration with initialization,
	 * assigning {@code expression} to {@code target},
	 * happening at the given location in the program.
	 * 
	 * @param cfg        the cfg that this declaration belongs to
	 * @param sourceFile the source file where this declaration happens. If unknown,
	 *                   use {@code null}
	 * @param line       the line number where this declaration happens in the source
	 *                   file. If unknown, use {@code -1}
	 * @param col        the column where this statement happens in the source file.
	 *                   If unknown, use {@code -1}
	 * @param var	     the declared variable
	 * @param expression the expression to assign to {@code var}
	 */
	public GoShortVariableDeclaration(CFG cfg, String sourceFile, int line, int col, Expression var, Expression expression) {
		super(cfg, sourceFile, line, col, var, expression);
	}


	@Override
	public String toString() {
		return getLeft() + " = " + getRight();
	}

	@Override
	public final <A extends AbstractState<A, H, V>,
	H extends HeapDomain<H>,
	V extends ValueDomain<V>> AnalysisState<A, H, V> semantics(
			AnalysisState<A, H, V> entryState, CallGraph callGraph, StatementStore<A, H, V> expressions)
					throws SemanticException {
		AnalysisState<A, H, V> right = getRight().semantics(entryState, callGraph, expressions);
		AnalysisState<A, H, V> left = getLeft().semantics(right, callGraph, expressions);
		expressions.put(getRight(), right);
		expressions.put(getLeft(), left);

		AnalysisState<A, H, V> result = null;
		for (SymbolicExpression expr1 : left.getComputedExpressions())
			for (SymbolicExpression expr2 : right.getComputedExpressions()) {
				AnalysisState<A, H, V> tmp = null;

				if (expr1 instanceof Constant && ((Constant) expr1).getValue() instanceof VariableRef[]) {
					// TODO: multi-variable declarations assigned to top
					VariableRef[] vars = (VariableRef[]) ((Constant) expr1).getValue();
					tmp = left;
					for (VariableRef v : vars) 
						tmp = tmp.assign((Identifier) v.getVariable(), new PushAny(v.getRuntimeTypes()), this);
				} else {
					// TODO: need to check if the type of expr2 is an untyped type: in such a case
					// should be converted to a type (e.g., intuntyped -> int, floatuntyped -> float32)
					tmp = left.assign((Identifier) expr1, expr2, this);
				}
				if (result == null)
					result = tmp;
				else
					result = result.lub(tmp);
			}

		if (!getRight().getMetaVariables().isEmpty())
			result = result.forgetIdentifiers(getRight().getMetaVariables());
		if (!getLeft().getMetaVariables().isEmpty())
			result = result.forgetIdentifiers(getLeft().getMetaVariables());
		return result;
	}
}
