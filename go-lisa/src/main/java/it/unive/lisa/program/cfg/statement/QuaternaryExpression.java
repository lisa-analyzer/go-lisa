package it.unive.lisa.program.cfg.statement;

import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.lattices.ExpressionSet;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.evaluation.EvaluationOrder;
import it.unive.lisa.program.cfg.statement.evaluation.LeftToRightEvaluation;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;

public abstract class QuaternaryExpression extends it.unive.lisa.program.cfg.statement.NaryExpression {

	
	/**
	 * Builds the untyped expression, happening at the given location in the
	 * program. The static type of this expression is {@link Untyped}. The
	 * {@link EvaluationOrder} is {@link LeftToRightEvaluation}.
	 * 
	 * @param cfg           the cfg that this expression belongs to
	 * @param location      the location where the expression is defined within
	 *                          the program
	 * @param constructName the name of the construct represented by this
	 *                          expression
	 * @param e1         the first sub-expression of this expression
	 * @param e2         the second sub-expression of this expression
	 * @param e3         the third sub-expression of this expression
	 * @param e4         the third sub-expression of this expression
	 */
	protected QuaternaryExpression(
			CFG cfg,
			CodeLocation location,
			String constructName,
			Expression e1,
			Expression e2,
			Expression e3,
			Expression e4) {
		super(cfg, location, constructName, e1, e2, e3, e4);
	}
	/**
	 * Builds the expression, happening at the given location in the program.
	 * The {@link EvaluationOrder} is {@link LeftToRightEvaluation}.
	 * 
	 * @param cfg           the cfg that this expression belongs to
	 * @param location      the location where the expression is defined within
	 *                          the program
	 * @param constructName the name of the construct represented by this
	 *                          expression
	 * @param staticType    the static type of this expression
	 * @param e1         the first sub-expression of this expression
	 * @param e2         the second sub-expression of this expression
	 * @param e3         the third sub-expression of this expression
	 * @param e4         the third sub-expression of this expression
	 */
	protected QuaternaryExpression(
			CFG cfg,
			CodeLocation location,
			String constructName,
			Type staticType,
			Expression e1,
			Expression e2,
			Expression e3,
			Expression e4) {
		super(cfg, location, constructName, staticType, e1, e2, e3, e4);
	}

	/**
	 * Builds the untyped expression, happening at the given location in the
	 * program. The static type of this expression is {@link Untyped}.
	 * 
	 * @param cfg           the cfg that this expression belongs to
	 * @param location      the location where the expression is defined within
	 *                          the program
	 * @param constructName the name of the construct represented by this
	 *                          expression
	 * @param order         the evaluation order of the sub-expressions
	 * @param e1         the first sub-expression of this expression
	 * @param e2         the second sub-expression of this expression
	 * @param e3         the third sub-expression of this expression
	 * @param e4         the third sub-expression of this expression
	 */
	protected QuaternaryExpression(
			CFG cfg,
			CodeLocation location,
			String constructName,
			EvaluationOrder order,
			Expression e1,
			Expression e2,
			Expression e3,
			Expression e4) {
		super(cfg, location, constructName, order, e1, e2, e3, e4);
	}

	/**
	 * Builds the expression, happening at the given location in the program.
	 * 
	 * @param cfg           the cfg that this expression belongs to
	 * @param location      the location where the expression is defined within
	 *                          the program
	 * @param constructName the name of the construct represented by this
	 *                          expression
	 * @param order         the evaluation order of the sub-expressions
	 * @param staticType    the static type of this expression
	 * @param e1         the first sub-expression of this expression
	 * @param e2         the second sub-expression of this expression
	 * @param e3         the third sub-expression of this expression
	 * @param e4         the third sub-expression of this expression
	 */
	protected QuaternaryExpression(
			CFG cfg,
			CodeLocation location,
			String constructName,
			EvaluationOrder order,
			Type staticType,
			Expression e1,
			Expression e2,
			Expression e3,
			Expression e4) {
		super(cfg, location, constructName, order, staticType, e1, e2, e3, e4);
	}

	/**
	 * Yields the (first) sub-expression of this expression.
	 * 
	 * @return the (first) sub-expression
	 */
	public Expression getExpr1() {
		return getSubExpressions()[0];
	}

	/**
	 * Yields the (second) sub-expression of this expression.
	 * 
	 * @return the (second) sub-expression
	 */
	public Expression getExpr2() {
		return getSubExpressions()[1];
	}

	/**
	 * Yields the (third) sub-expression of this expression.
	 * 
	 * @return the (third) sub-expression
	 */
	public Expression getExpr3() {
		return getSubExpressions()[2];
	}
	
	/**
	 * Yields the (forth) sub-expression of this expression.
	 * 
	 * @return the (forth) sub-expression
	 */
	public Expression getExpr4() {
		return getSubExpressions()[3];
	}

	@Override
	public <A extends AbstractState<A>> AnalysisState<A> forwardSemanticsAux(
			InterproceduralAnalysis<A> interprocedural,
			AnalysisState<A> state,
			ExpressionSet[] params,
			StatementStore<A> expressions)
			throws SemanticException {
		AnalysisState<A> result = state.bottom();
		for (SymbolicExpression e1 : params[0])
			for (SymbolicExpression e2 : params[1])
				for (SymbolicExpression e3 : params[2])
					for (SymbolicExpression e4 : params[3])
					result = result.lub(fwdQuaternarySemantics(interprocedural, state, e1, e2, e3, e4, expressions));

		return result;
	}

	/**
	 * Computes the forward semantics of the expression, after the semantics of
	 * the sub-expressions have been computed. Meta variables from the
	 * sub-expressions will be forgotten after this expression returns.
	 * 
	 * @param <A>             the type of {@link AbstractState}
	 * @param interprocedural the interprocedural analysis of the program to
	 *                            analyze
	 * @param state           the state where the expression is to be evaluated
	 * @param e1            the symbolic expression representing the computed
	 *                            value of the first sub-expression of this
	 *                            expression
	 * @param e2          the symbolic expression representing the computed
	 *                            value of the second sub-expression of this
	 *                            expression
	 * @param e3           the symbolic expression representing the computed
	 *                            value of the third sub-expression of this
	 *                            expression
	 * @param e4          the symbolic expression representing the computed
	 *                            value of the third sub-expression of this
	 *                            expression
	 * @param expressions     the cache where analysis states of intermediate
	 *                            expressions are stored and that can be
	 *                            accessed to query for post-states of
	 *                            parameters expressions
	 * 
	 * @return the {@link AnalysisState} representing the abstract result of the
	 *             execution of this expression
	 * 
	 * @throws SemanticException if something goes wrong during the computation
	 */
	public abstract <A extends AbstractState<A>> AnalysisState<A> fwdQuaternarySemantics(
			InterproceduralAnalysis<A> interprocedural,
			AnalysisState<A> state,
			SymbolicExpression e1,
			SymbolicExpression e2,
			SymbolicExpression e3,
			SymbolicExpression e4,
			StatementStore<A> expressions)
			throws SemanticException;

	@Override
	public <A extends AbstractState<A>> AnalysisState<A> backwardSemanticsAux(
			InterproceduralAnalysis<A> interprocedural,
			AnalysisState<A> state,
			ExpressionSet[] params,
			StatementStore<A> expressions)
			throws SemanticException {
		AnalysisState<A> result = state.bottom();
		for (SymbolicExpression e1 : params[0])
			for (SymbolicExpression e2 : params[1])
				for (SymbolicExpression e3 : params[2])
					for (SymbolicExpression e4 : params[3])
					result = result.lub(bwdQuaternarySemantics(interprocedural, state, e1, e2, e3, e4, expressions));

		return result;
	}

	/**
	 * Computes the backward semantics of the expression, after the semantics of
	 * the sub-expressions have been computed. Meta variables from the
	 * sub-expressions will be forgotten after this expression returns. By
	 * default, this method delegates to
	 * {@link #fwdTernarySemantics(InterproceduralAnalysis, AnalysisState, SymbolicExpression, SymbolicExpression, SymbolicExpression, StatementStore)},
	 * as it is fine for most atomic statements. One should redefine this method
	 * if a statement's semantics is composed of a series of smaller operations.
	 * 
	 * @param <A>             the type of {@link AbstractState}
	 * @param interprocedural the interprocedural analysis of the program to
	 *                            analyze
	 * @param state           the state where the expression is to be evaluated
	 * @param e1            the symbolic expression representing the computed
	 *                            value of the first sub-expression of this
	 *                            expression
	 * @param e2          the symbolic expression representing the computed
	 *                            value of the second sub-expression of this
	 *                            expression
	 * @param e3           the symbolic expression representing the computed
	 *                            value of the third sub-expression of this
	 *                            expression
	 * @param e4           the symbolic expression representing the computed
	 *                            value of the forth sub-expression of this
	 *                            expression
	 * @param expressions     the cache where analysis states of intermediate
	 *                            expressions are stored and that can be
	 *                            accessed to query for post-states of
	 *                            parameters expressions
	 * 
	 * @return the {@link AnalysisState} representing the abstract result of the
	 *             execution of this expression
	 * 
	 * @throws SemanticException if something goes wrong during the computation
	 */
	public <A extends AbstractState<A>> AnalysisState<A> bwdQuaternarySemantics(
			InterproceduralAnalysis<A> interprocedural,
			AnalysisState<A> state,
			SymbolicExpression e1,
			SymbolicExpression e2,
			SymbolicExpression e3,
			SymbolicExpression e4,
			StatementStore<A> expressions)
			throws SemanticException {
		return fwdQuaternarySemantics(interprocedural, state, e1, e2, e3, e4, expressions);
	}

}
