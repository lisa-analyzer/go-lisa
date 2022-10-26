package it.unive.golisa.cfg.expression.unary;

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

	private Statement idxRange;
	private Statement valRange;

	private ExternalSet<Type> collectionTypes;

	/**
	 * Builds a range expression.
	 * 
	 * @param cfg      the {@link CFG} where this expression lies
	 * @param location the location where this expression is defined
	 * @param exp      the expression
	 * @param valRange the init statement index
	 * @param idxInit  the post statement index
	 */
	public GoRange(CFG cfg, SourceCodeLocation location, Expression exp) {
		super(cfg, location, "range", GoBoolType.INSTANCE, exp);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <A extends AbstractState<A, H, V, T>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>,
			T extends TypeDomain<T>> AnalysisState<A, H, V, T> unarySemantics(
					InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
					SymbolicExpression expr, StatementStore<A, H, V, T> expressions) throws SemanticException {

		collectionTypes = expressions.getState(getSubExpression())
				.getDomainInstance(TypeDomain.class).getInferredRuntimeTypes();

		return state.smallStepSemantics(expr, this);
	}

	public Statement getIdxRange() {
		return idxRange;
	}

	public Statement getValRange() {
		return valRange;
	}

	public void setIdxRange(Statement idxRange) {
		this.idxRange = idxRange;
	}

	public void setValRange(Statement valRange) {
		this.valRange = valRange;
	}

	/**
	 * Yields the types collected by this range clause.
	 * 
	 * @return the types collected by this range clause
	 */
	public ExternalSet<Type> getCollectionTypes() {
		return collectionTypes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((collectionTypes == null) ? 0 : collectionTypes.hashCode());
		result = prime * result + ((idxRange == null) ? 0 : idxRange.hashCode());
		result = prime * result + ((valRange == null) ? 0 : valRange.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		GoRange other = (GoRange) obj;
		if (collectionTypes == null) {
			if (other.collectionTypes != null)
				return false;
		} else if (!collectionTypes.equals(other.collectionTypes))
			return false;
		if (idxRange == null) {
			if (other.idxRange != null)
				return false;
		} else if (!idxRange.equals(other.idxRange))
			return false;
		if (valRange == null) {
			if (other.valRange != null)
				return false;
		} else if (!valRange.equals(other.valRange))
			return false;
		return true;
	}

}