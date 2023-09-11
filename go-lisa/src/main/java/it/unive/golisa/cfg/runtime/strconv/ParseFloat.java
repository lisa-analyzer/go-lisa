package it.unive.golisa.cfg.runtime.strconv;

import java.util.Collections;
import java.util.Set;

import it.unive.golisa.cfg.expression.literal.GoTupleExpression;
import it.unive.golisa.cfg.runtime.fmt.Sprintf;
import it.unive.golisa.cfg.runtime.fmt.Sprintf.SprintfImpl;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoTupleType;
import it.unive.golisa.cfg.type.numeric.floating.GoFloat64Type;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
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
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;

/**
 * func ParseFloat(s string, bitSize int) (float64, error)
 *
 * @link https://pkg.go.dev/strconv#ParseFloat
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class ParseFloat extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param strconvUnit  the unit to which this native cfg belongs to
	 */
	public ParseFloat(CodeLocation location, CodeUnit strconvUnit) {
		super(new CodeMemberDescriptor(location, strconvUnit, false, "ParseFloat", 
				GoTupleType.getTupleTypeOf(location, GoFloat64Type.INSTANCE, GoErrorType.INSTANCE),
				new Parameter(location, "s", GoStringType.INSTANCE),
				new Parameter(location, "bitSize", GoIntType.INSTANCE)),
				SprintfImpl.class);
	}

	/**
	 * The {@link Sprintf} implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class ParseFloatImpl extends it.unive.lisa.program.cfg.statement.BinaryExpression implements PluggableStatement {

		@SuppressWarnings("unused")
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
		public static ParseFloatImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new ParseFloatImpl(cfg, location, params[0], params[1]);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param arg      the expression
		 */
		public ParseFloatImpl(CFG cfg, CodeLocation location, Expression left, Expression right) {
			super(cfg, location, "ParseFloat", GoTupleType.getTupleTypeOf(location, GoFloat64Type.INSTANCE, GoErrorType.INSTANCE), left, right);
		}

		@Override
		public <A extends AbstractState<A, H, V, T>, H extends HeapDomain<H>, V extends ValueDomain<V>, T extends TypeDomain<T>> AnalysisState<A, H, V, T> binarySemantics(
				InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
				SymbolicExpression left, SymbolicExpression right, StatementStore<A, H, V, T> expressions)
						throws SemanticException {
			BinaryExpression lExp = new BinaryExpression(GoFloat64Type.INSTANCE, left, right, ParseFloatOperatorFirstParameter.INSTANCE, getLocation());
			BinaryExpression rExp = new BinaryExpression(GoErrorType.INSTANCE, left, right, ParseFloatOperatorSecondParameter.INSTANCE, getLocation());
			return GoTupleExpression.allocateTupleExpression(state, new Annotations(), this, getLocation(), GoTupleType.getTupleTypeOf(getLocation(), GoFloat64Type.INSTANCE, GoErrorType.INSTANCE), 
					lExp,
					rExp);
		}
	}

	public static class ParseFloatOperatorFirstParameter implements BinaryOperator {

		/**
		 * The singleton instance of this class.
		 */
		public static final ParseFloatOperatorFirstParameter INSTANCE = new ParseFloatOperatorFirstParameter();

		/**
		 * Builds the operator. This constructor is visible to allow subclassing:
		 * instances of this class should be unique, and the singleton can be
		 * retrieved through field {@link #INSTANCE}.
		 */
		protected ParseFloatOperatorFirstParameter() {
		}

		@Override
		public String toString() {
			return "ParseFloat_first";
		}

		@Override
		public Set<Type> typeInference(TypeSystem types, Set<Type> left, Set<Type> right) {
			return Collections.singleton(GoFloat64Type.INSTANCE);
		}
	}

	public static class ParseFloatOperatorSecondParameter implements BinaryOperator {

		/**
		 * The singleton instance of this class.
		 */
		public static final ParseFloatOperatorSecondParameter INSTANCE = new ParseFloatOperatorSecondParameter();

		/**
		 * Builds the operator. This constructor is visible to allow subclassing:
		 * instances of this class should be unique, and the singleton can be
		 * retrieved through field {@link #INSTANCE}.
		 */
		protected ParseFloatOperatorSecondParameter() {
		}

		@Override
		public String toString() {
			return "ParseFloat_second";
		}

		@Override
		public Set<Type> typeInference(TypeSystem types, Set<Type> left, Set<Type> right) {
			return Collections.singleton(GoErrorType.INSTANCE);
		}
	}
}
