package it.unive.golisa.cfg.statement.assignment;

import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.caches.Caches;
import it.unive.lisa.callgraph.CallGraph;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.BinaryExpression;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryOperator;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Variable;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeTokenType;
import it.unive.lisa.util.collections.externalSet.ExternalSet;

/**
 * Go variable declaration class (e.g., var x int = 5).
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoVariableDeclaration extends BinaryExpression {

	private final Type type;

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
	public GoVariableDeclaration(CFG cfg, SourceCodeLocation location, Type type, VariableRef var, Expression expression) {
		super(cfg, location, var, expression);
		this.type = type;
	}

	@Override
	public String toString() {
		return "var " + getLeft() + " " + type + " = " + getRight();
	}

	@Override
	public <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> semantics(
			AnalysisState<A, H, V> entryState, CallGraph callGraph, StatementStore<A, H, V> expressions)
					throws SemanticException {
		AnalysisState<A, H, V> right = getRight().semantics(entryState, callGraph, expressions);
		expressions.put(getRight(), right);
		expressions.put(getLeft(), right);

		ExternalSet<Type> idType = Caches.types().mkSingletonSet(type);
		Variable id = new Variable(idType, ((VariableRef) getLeft()).getName());

		AnalysisState<A, H, V> result = entryState.bottom();
		for (SymbolicExpression rightExp : right.getComputedExpressions()) {
			Constant typeCast = new Constant(new TypeTokenType(idType), type);
			it.unive.lisa.symbolic.value.BinaryExpression rightConverted = 
					new it.unive.lisa.symbolic.value.BinaryExpression(idType, rightExp, typeCast, BinaryOperator.TYPE_CONV);				

			AnalysisState<A, H, V> tmp = right.assign(id, rightConverted, this);
			result = result.lub(tmp);
		}

		if (!getRight().getMetaVariables().isEmpty())
			result = result.forgetIdentifiers(getRight().getMetaVariables());
		if (!getLeft().getMetaVariables().isEmpty())
			result = result.forgetIdentifiers(getLeft().getMetaVariables());
		return result;
	}
}
