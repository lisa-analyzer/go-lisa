package it.unive.golisa.cfg.expression.binary;

import it.unive.golisa.cfg.type.composite.GoInterfaceType;
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
import it.unive.lisa.program.cfg.statement.UnaryExpression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;

/**
 * A Go type assertion (e.g., x.(string)).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoTypeAssertion extends UnaryExpression {

	private final Type type;

	/**
	 * Builds the type assertion expression.
	 *
	 * @param cfg      the {@link CFG} where this expression lies
	 * @param location the location where this expression is defined
	 * @param exp      the expression
	 * @param type     the type to assert
	 */
	public GoTypeAssertion(CFG cfg, SourceCodeLocation location, Expression exp, Type type) {
		super(cfg, location, ".(" + type + ")", exp);
		this.type = type;
	}

	@Override
	protected <A extends AbstractState<A, H, V, T>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>,
			T extends TypeDomain<T>> AnalysisState<A, H, V, T> unarySemantics(
					InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
					SymbolicExpression expr, StatementStore<A, H, V, T> expressions) throws SemanticException {
		// A type assertion provides access to an interface value's underlying
		// concrete value,
		// hence we need to check if the static type of the arguments is an
		// interface
		Type argStaticType = getSubExpressions()[0].getStaticType();
		if (argStaticType instanceof GoInterfaceType || argStaticType instanceof Untyped)
			for (Type exprType : expr.getRuntimeTypes())
				if (exprType.canBeAssignedTo(type))
					return state;

		return state.bottom();
	}
}
