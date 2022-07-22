package it.unive.golisa.cfg.expression.unary;

import it.unive.golisa.cfg.expression.binary.GoLess;
import it.unive.golisa.cfg.type.GoBoolType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.UnaryExpression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.type.Type;
import it.unive.lisa.util.collections.externalSet.ExternalSet;

/**
 * A Go range expression, tracking the beginning of a range statement.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoRange extends UnaryExpression {

	private final Statement idxInit;
	private final Statement idxPost;

	private ExternalSet<Type> collectionTypes;

	/**
	 * Builds a range expression.
	 * 
	 * @param cfg      the {@link CFG} where this expression lies
	 * @param location the location where this expression is defined
	 * @param exp      the expression
	 * @param idxPost
	 * @param idxInit
	 */
	public GoRange(CFG cfg, SourceCodeLocation location, Expression exp, Statement idxInit, Statement idxPost) {
		super(cfg, location, "range", GoBoolType.INSTANCE, exp);
		this.idxInit = idxInit;
		this.idxPost = idxPost;
	}

	@Override
	protected <A extends AbstractState<A, H, V, T>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>,
			T extends TypeDomain<T>> AnalysisState<A, H, V, T> unarySemantics(
					InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
					SymbolicExpression expr, StatementStore<A, H, V, T> expressions) throws SemanticException {

		collectionTypes = expressions.getState(((GoLess) getSubExpression()).getRight())
				.getDomainInstance(TypeDomain.class).getInferredRuntimeTypes();

		return state.smallStepSemantics(expr, this);
	}

	public Statement getIdxInit() {
		return idxInit;
	}

	public Statement getIdxPost() {
		return idxPost;
	}

	public ExternalSet<Type> getCollectionTypes() {
		return collectionTypes;
	}

}
