package it.unive.golisa.cfg.statement;

import it.unive.golisa.cfg.type.numeric.floating.GoFloat32Type;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.golisa.cfg.type.untyped.GoUntypedFloat;
import it.unive.golisa.cfg.type.untyped.GoUntypedInt;
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
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.ValueIdentifier;
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
	 * assigning {@code expression} to {@code var} 
	 * without make explicit the location (i.e. no source
	 * file/line/column is available).
	 * 
	 * @param cfg        the cfg that this declaration belongs to
	 * @param var     the declared variable
	 * @param expression the expression to assign to {@code var}
	 */
	public GoVariableDeclaration(CFG cfg, Type type, VariableRef var, Expression expression) {
		this(cfg, null, -1, -1, type, var, expression);
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
	public GoVariableDeclaration(CFG cfg, String sourceFile, int line, int col, Type type, VariableRef var, Expression expression) {
		super(cfg, new SourceCodeLocation(sourceFile, line, col), var, expression);
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

		Identifier id = new ValueIdentifier(((VariableRef) getLeft()).getRuntimeTypes(), ((VariableRef) getLeft()).getName());

		AnalysisState<A, H, V> result = null;
		for (SymbolicExpression expr2 : right.getComputedExpressions()) {
			AnalysisState<A, H, V> tmp = null;

			if (expr2.getDynamicType() instanceof GoUntypedInt) {
				ExternalSet<Type> intType = Caches.types().mkSingletonSet(GoIntType.INSTANCE);
				Constant typeCast = new Constant(new TypeTokenType(intType), GoIntType.INSTANCE);
				it.unive.lisa.symbolic.value.BinaryExpression rhsCasted = 
						new it.unive.lisa.symbolic.value.BinaryExpression(intType, expr2, typeCast, BinaryOperator.TYPE_CONV);				
				tmp = right.assign(id, rhsCasted, this);

			} else if (expr2.getDynamicType() instanceof GoUntypedFloat) {
				ExternalSet<Type> floatType = Caches.types().mkSingletonSet(GoFloat32Type.INSTANCE);
				Constant typeCast = new Constant(new TypeTokenType(floatType), GoFloat32Type.INSTANCE);
				it.unive.lisa.symbolic.value.BinaryExpression rhsCasted = 
						new it.unive.lisa.symbolic.value.BinaryExpression(floatType, expr2, typeCast, BinaryOperator.TYPE_CONV);

				tmp = right.assign((Identifier) id, rhsCasted, this);
			} else { 
				if (expr2.getDynamicType().canBeAssignedTo(type))
					tmp = right.assign((Identifier) id, expr2, this);
				else 
					tmp = entryState.bottom();
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
