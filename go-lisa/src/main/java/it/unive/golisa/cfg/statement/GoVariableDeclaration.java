package it.unive.golisa.cfg.statement;

import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.HeapDomain;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.impl.types.TypeEnvironment;
import it.unive.lisa.callgraph.CallGraph;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Assignment;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.NullConstant;
import it.unive.lisa.type.Type;

/**
 * Go variable declaration class (e.g., var x int = 5).
 * TODO: at the moment, we skip the variable type.
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoVariableDeclaration extends Assignment {

	private final Type type;

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
	public GoVariableDeclaration(CFG cfg, Type type, Expression var, Expression expression) {
		super(cfg, var, expression);
		this.type = type;
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
	public GoVariableDeclaration(CFG cfg, String sourceFile, int line, int col, Type type, Expression var, Expression expression) {
		super(cfg, sourceFile, line, col, var, expression);
		this.type = type;
	}

	@Override
	public <A extends AbstractState<A, H, TypeEnvironment>,
	H extends HeapDomain<H>> AnalysisState<A, H, TypeEnvironment> typeInference(
			AnalysisState<A, H, TypeEnvironment> entryState, CallGraph callGraph,
			StatementStore<A, H, TypeEnvironment> expressions) throws SemanticException {
		AnalysisState<A, H, TypeEnvironment> right = getRight().typeInference(entryState, callGraph, expressions);
		AnalysisState<A, H, TypeEnvironment> left = getLeft().typeInference(right, callGraph, expressions);
		expressions.put(getRight(), right);
		expressions.put(getLeft(), left);


		AnalysisState<A, H, TypeEnvironment> result = null;
		for (SymbolicExpression expr1 : left.getComputedExpressions())
			for (SymbolicExpression expr2 : right.getComputedExpressions()) {
				Type rightType = expr2.getDynamicType();
				AnalysisState<A, H, TypeEnvironment> tmp = null;

				if (rightType instanceof GoType) {
					if (!((GoType) rightType).canBeAssignedTo(type))
						tmp = entryState.bottom();
					else {
						// TODO: this is a work-around for the type conversion
						tmp = left.assign((Identifier) expr1, new Constant(type, 0));
					}
				} else {
					tmp = left.assign((Identifier) expr1, NullConstant.INSTANCE);
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

		setRuntimeTypes(result.getState().getValueState().getLastComputedTypes().getRuntimeTypes());
		return result;
	}
}
