package it.unive.golisa.cfg.expression.binary;

import it.unive.golisa.cfg.type.composite.GoInterfaceType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.UnaryExpression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import java.util.Set;

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
	protected int compareSameClassAndParams(Statement o) {
		return 0; // nothing else to compare
	}

	@Override
	public <A extends AbstractState<A>> AnalysisState<A> fwdUnarySemantics(InterproceduralAnalysis<A> arg0,
			AnalysisState<A> state, SymbolicExpression expr, StatementStore<A> arg3) throws SemanticException {
		// A type assertion provides access to an interface value's underlying
		// concrete value,
		// hence we need to check if the static type of the arguments is an
		// interface
		Set<Type> types = state.getState().getRuntimeTypesOf(expr, this, state.getState());
		Type argStaticType = getSubExpressions()[0].getStaticType();
		if (argStaticType instanceof GoInterfaceType || argStaticType instanceof Untyped)
			for (Type exprType : types)
				if (exprType.canBeAssignedTo(type))
					return state;

		return state.bottom();
	}
}
