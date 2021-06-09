package it.unive.golisa.cfg.statement.assignment;

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
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.BinaryOperator;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeTokenType;
import it.unive.lisa.util.collections.externalSet.ExternalSet;

public class GoShortVariableDeclaration extends it.unive.lisa.program.cfg.statement.BinaryExpression {

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
	public GoShortVariableDeclaration(CFG cfg, String sourceFile, int line, int col, VariableRef var, Expression expression) {
		super(cfg, new SourceCodeLocation(sourceFile, line, col), var, expression);
	}
	
	public GoShortVariableDeclaration(CFG cfg, CodeLocation location, VariableRef var, Expression expression) {
		super(cfg, location, var, expression);
	}

	@Override
	public String toString() {
		return getLeft() + " := " + getRight();
	}

	@Override
	public final <A extends AbstractState<A, H, V>,
	H extends HeapDomain<H>,
	V extends ValueDomain<V>> AnalysisState<A, H, V> semantics(
			AnalysisState<A, H, V> entryState, InterproceduralAnalysis<A, H, V> interprocedural, StatementStore<A, H, V> expressions)
					throws SemanticException {

		AnalysisState<A, H, V> right = getRight().semantics(entryState, interprocedural, expressions);
		AnalysisState<A, H, V> left = getLeft().semantics(right, interprocedural, expressions);
		expressions.put(getRight(), right);
		expressions.put(getLeft(), left);

		AnalysisState<A, H, V> result = entryState.bottom();
		for (SymbolicExpression leftExp : left.getComputedExpressions()) 
			for (SymbolicExpression rightExp : right.getComputedExpressions()) {
				AnalysisState<A, H, V> tmp = right.assign(leftExp, NumericalTyper.type(rightExp), this);
				result = result.lub(tmp);
			}

		if (!getRight().getMetaVariables().isEmpty())
			result = result.forgetIdentifiers(getRight().getMetaVariables());
		if (!getLeft().getMetaVariables().isEmpty())
			result = result.forgetIdentifiers(getLeft().getMetaVariables());
		return result;
	}
	
	public static class NumericalTyper {
		
		public static SymbolicExpression type(SymbolicExpression exp) {
			if (exp.getDynamicType() instanceof GoUntypedInt) {
				ExternalSet<Type> intType = Caches.types().mkSingletonSet(GoIntType.INSTANCE);
				Constant typeCast = new Constant(new TypeTokenType(intType), GoIntType.INSTANCE);
				return new BinaryExpression(intType, exp, typeCast, BinaryOperator.TYPE_CONV);

			} else if (exp.getDynamicType() instanceof GoUntypedFloat) {
				ExternalSet<Type> floatType = Caches.types().mkSingletonSet(GoFloat32Type.INSTANCE);
				Constant typeCast = new Constant(new TypeTokenType(floatType), GoFloat32Type.INSTANCE);
				return new BinaryExpression(floatType, exp, typeCast, BinaryOperator.TYPE_CONV);
			} else 
				return exp;
		}
	}
}
