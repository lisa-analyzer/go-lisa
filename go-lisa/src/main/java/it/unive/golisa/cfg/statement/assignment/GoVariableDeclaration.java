package it.unive.golisa.cfg.statement.assignment;

import it.unive.golisa.cfg.type.untyped.GoUntypedFloat;
import it.unive.golisa.cfg.type.untyped.GoUntypedInt;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.caches.Caches;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Variable;
import it.unive.lisa.symbolic.value.operator.binary.TypeConv;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeTokenType;
import it.unive.lisa.util.collections.externalSet.ExternalSet;

/**
 * Go variable declaration class (e.g., var x int = 5).
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoVariableDeclaration extends it.unive.lisa.program.cfg.statement.BinaryExpression {

	private final Type type;

	/**
	 * Builds a Go variable declaration with initialization, assigning
	 * {@code expression} to {@code target}, happening at the given location in
	 * the program.
	 * 
	 * @param cfg        the cfg that this declaration belongs to
	 * @param sourceFile the source file where this declaration happens. If
	 *                       unknown, use {@code null}
	 * @param line       the line number where this declaration happens in the
	 *                       source file. If unknown, use {@code -1}
	 * @param col        the column where this statement happens in the source
	 *                       file. If unknown, use {@code -1}
	 * @param var        the declared variable
	 * @param expression the expression to assign to {@code var}
	 */
	public GoVariableDeclaration(CFG cfg, SourceCodeLocation location, Type type, VariableRef var,
			Expression expression) {
		super(cfg, location, ":=", var, expression);
		this.type = type;
	}

	@Override
	public String toString() {
		return "var " + getLeft() + " " + type + " = " + getRight();
	}

	public Type getDeclaredType() {
		return type;
	}

	@Override
	protected <A extends AbstractState<A, H, V>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>> AnalysisState<A, H, V> binarySemantics(
					InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> state,
					SymbolicExpression left,
					SymbolicExpression right, StatementStore<A, H, V> expressions) throws SemanticException {

		// e.g., _ = f(), we just return right state
		if (GoLangUtils.refersToBlankIdentifier(getLeft()))
			return state;
		
		ExternalSet<Type> idType = Caches.types().mkSingletonSet(type);
		Variable id = new Variable(idType, ((VariableRef) getLeft()).getName(), getLeft().getLocation());

		AnalysisState<A, H, V> result = state.bottom();
		for (Type rightType : right.getTypes()) {
			AnalysisState<A, H, V> tmp = state.bottom();
			if (rightType instanceof GoUntypedInt || rightType instanceof GoUntypedFloat) {
				Constant typeCast = new Constant(new TypeTokenType(idType), type, getRight().getLocation());
				tmp = state.assign(id, new BinaryExpression(idType, right, typeCast, TypeConv.INSTANCE,
						getRight().getLocation()), this);
			} else if (rightType.canBeAssignedTo(type))
				tmp = state.assign(id, right, this);

			result = result.lub(tmp);

		}

		if (!getRight().getMetaVariables().isEmpty())
			result = result.forgetIdentifiers(getRight().getMetaVariables());
		if (!getLeft().getMetaVariables().isEmpty())
			result = result.forgetIdentifiers(getLeft().getMetaVariables());

		return result;
	}
}
