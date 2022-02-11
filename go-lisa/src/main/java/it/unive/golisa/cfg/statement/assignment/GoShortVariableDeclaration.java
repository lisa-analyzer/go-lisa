package it.unive.golisa.cfg.statement.assignment;

import it.unive.golisa.cfg.type.numeric.floating.GoFloat32Type;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.golisa.cfg.type.untyped.GoUntypedFloat;
import it.unive.golisa.cfg.type.untyped.GoUntypedInt;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.caches.Caches;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.operator.binary.TypeConv;
import it.unive.lisa.type.TypeTokenType;

public class GoShortVariableDeclaration extends it.unive.lisa.program.cfg.statement.BinaryExpression {

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
	public GoShortVariableDeclaration(CFG cfg, String sourceFile, int line, int col, VariableRef var,
			Expression expression) {
		super(cfg, new SourceCodeLocation(sourceFile, line, col), ":=", var, expression);
	}

	public GoShortVariableDeclaration(CFG cfg, CodeLocation location, VariableRef var, Expression expression) {
		super(cfg, location, ":=", var, expression);
	}

	@Override
	public String toString() {
		return getLeft() + " := " + getRight();
	}

	public static class NumericalTyper {

		public static SymbolicExpression type(SymbolicExpression exp) {
			if (exp.getDynamicType() instanceof GoUntypedInt) {
				Constant typeCast = new Constant(new TypeTokenType(Caches.types().mkSingletonSet(GoIntType.INSTANCE)),
						GoIntType.INSTANCE, exp.getCodeLocation());
				return new BinaryExpression(GoIntType.INSTANCE, exp, typeCast, TypeConv.INSTANCE,
						exp.getCodeLocation());

			} else if (exp.getDynamicType() instanceof GoUntypedFloat) {
				Constant typeCast = new Constant(
						new TypeTokenType(Caches.types().mkSingletonSet(GoFloat32Type.INSTANCE)),
						GoFloat32Type.INSTANCE,
						exp.getCodeLocation());
				return new BinaryExpression(GoFloat32Type.INSTANCE, exp, typeCast, TypeConv.INSTANCE,
						exp.getCodeLocation());
			} else
				return exp;
		}
	}

	@Override
	protected <A extends AbstractState<A, H, V, T>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>,
			T extends TypeDomain<T>> AnalysisState<A, H, V, T> binarySemantics(
					InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
					SymbolicExpression left, SymbolicExpression right, StatementStore<A, H, V, T> expressions)
					throws SemanticException {
		// e.g., _ := f(), we just return right state
		if (GoLangUtils.refersToBlankIdentifier(getLeft()))
			return state;

		AnalysisState<A, H, V, T> result = state.assign(left, NumericalTyper.type(right), this);

		if (!getRight().getMetaVariables().isEmpty())
			result = result.forgetIdentifiers(getRight().getMetaVariables());
		if (!getLeft().getMetaVariables().isEmpty())
			result = result.forgetIdentifiers(getLeft().getMetaVariables());
		return result;
	}
}
