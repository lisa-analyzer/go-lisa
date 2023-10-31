package it.unive.golisa.cfg.runtime.strconv;

import it.unive.golisa.cfg.expression.literal.GoTupleExpression;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoTupleType;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.CodeUnit;
import it.unive.lisa.program.annotations.Annotations;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.NativeCFG;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.UnaryExpression;
import it.unive.lisa.symbolic.value.operator.unary.UnaryOperator;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;
import java.util.Collections;
import java.util.Set;

/**
 * func Atoi(s string) (int, error).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class Atoi extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location    the location where this native cfg is defined
	 * @param strconvUnit the unit to which this native cfg belongs to
	 */
	public Atoi(CodeLocation location, CodeUnit strconvUnit) {
		super(new CodeMemberDescriptor(location, strconvUnit, false, "Atoi",
				GoTupleType.getTupleTypeOf(location, GoIntType.INSTANCE, GoErrorType.INSTANCE),
				new Parameter(location, "this", GoStringType.INSTANCE)),
				AtoiImpl.class);
	}

	/**
	 * The Atoi implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class AtoiImpl extends it.unive.lisa.program.cfg.statement.UnaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param params   the parameters
		 * 
		 * @return the pluggable statement
		 */
		public static AtoiImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new AtoiImpl(cfg, location, params[0]);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param expr     the expression
		 */
		public AtoiImpl(CFG cfg, CodeLocation location, Expression expr) {
			super(cfg, location, "Atoi", GoTupleType.getTupleTypeOf(location, GoIntType.INSTANCE, GoErrorType.INSTANCE),
					expr);
		}

		@Override
		public <A extends AbstractState<A>> AnalysisState<A> fwdUnarySemantics(
				InterproceduralAnalysis<A> interprocedural, AnalysisState<A> state,
				SymbolicExpression expr, StatementStore<A> expressions) throws SemanticException {

			UnaryExpression lExp = new UnaryExpression(GoIntType.INSTANCE, expr, AtoiOperatorFirstParameter.INSTANCE,
					getLocation());
			UnaryExpression rExp = new UnaryExpression(GoErrorType.INSTANCE, expr, AtoiOperatorSecondParameter.INSTANCE,
					getLocation());

			GoTupleType tupleType = GoTupleType.getTupleTypeOf(getLocation(), GoIntType.INSTANCE, GoErrorType.INSTANCE);

			return GoTupleExpression.allocateTupleExpression(state, new Annotations(), original, getLocation(),
					tupleType,
					lExp,
					rExp);
		}
	}

	/**
	 * The Atoi operator returning the first parameter of the tuple expression
	 * result.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class AtoiOperatorFirstParameter implements UnaryOperator {

		/**
		 * The singleton instance of this class.
		 */
		public static final AtoiOperatorFirstParameter INSTANCE = new AtoiOperatorFirstParameter();

		/**
		 * Builds the operator. This constructor is visible to allow
		 * subclassing: instances of this class should be unique, and the
		 * singleton can be retrieved through field {@link #INSTANCE}.
		 */
		protected AtoiOperatorFirstParameter() {
		}

		@Override
		public String toString() {
			return "AtoiOperator_1";
		}

		@Override
		public Set<Type> typeInference(TypeSystem types, Set<Type> argument) {
			return Collections.singleton(GoIntType.INSTANCE);
		}
	}

	/**
	 * The Atoi operator returning the second parameter of the tuple expression
	 * result.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class AtoiOperatorSecondParameter implements UnaryOperator {

		/**
		 * The singleton instance of this class.
		 */
		public static final AtoiOperatorSecondParameter INSTANCE = new AtoiOperatorSecondParameter();

		/**
		 * Builds the operator. This constructor is visible to allow
		 * subclassing: instances of this class should be unique, and the
		 * singleton can be retrieved through field {@link #INSTANCE}.
		 */
		protected AtoiOperatorSecondParameter() {
		}

		@Override
		public String toString() {
			return "AtoiOperator_2";
		}

		@Override
		public Set<Type> typeInference(TypeSystem types, Set<Type> argument) {
			return Collections.singleton(GoErrorType.INSTANCE);
		}
	}
}
