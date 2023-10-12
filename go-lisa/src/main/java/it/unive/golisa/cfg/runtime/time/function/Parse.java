package it.unive.golisa.cfg.runtime.time.function;

import java.util.Collections;
import java.util.Set;

import it.unive.golisa.cfg.expression.literal.GoTupleExpression;
import it.unive.golisa.cfg.runtime.time.type.Time;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoTupleType;
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
import it.unive.lisa.symbolic.heap.HeapDereference;
import it.unive.lisa.symbolic.heap.HeapReference;
import it.unive.lisa.symbolic.heap.MemoryAllocation;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.type.ReferenceType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;
import it.unive.lisa.type.Untyped;

/**
 * func Parse(layout, value string) (Time, error).
 * 
 * @link https://pkg.go.dev/time#Parse
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class Parse extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param timeUnit the unit to which this native cfg belongs to
	 */
	public Parse(CodeLocation location, CodeUnit timeUnit) {
		super(new CodeMemberDescriptor(location, timeUnit, false, "Parse",
				GoTupleType.lookup(new Parameter(location, "_", Time.getTimeType(timeUnit.getProgram())),
						new Parameter(location, "_", GoErrorType.INSTANCE)),
				new Parameter(location, "layout", GoStringType.INSTANCE),
				new Parameter(location, "value", GoStringType.INSTANCE)),
				ParseImpl.class);
	}

	/**
	 * The {@link Parse} implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class ParseImpl extends it.unive.lisa.program.cfg.statement.BinaryExpression
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
		public static ParseImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new ParseImpl(cfg, location, params[0], params[1]);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param left     the left-hand side of this expression
		 * @param right    the right-hand side of this expression
		 */
		public ParseImpl(CFG cfg, CodeLocation location, Expression left, Expression right) {
			super(cfg, location, "ParseImpl", GoTupleType.lookup(new Parameter(location, "_", Time.getTimeType(null)),
					new Parameter(location, "_", GoErrorType.INSTANCE)), left, right);
		}

		@Override
		public <A extends AbstractState<A>> AnalysisState<A> fwdBinarySemantics(
						InterproceduralAnalysis<A> interprocedural, AnalysisState<A> state,
						SymbolicExpression left, SymbolicExpression right, StatementStore<A> expressions)
						throws SemanticException {
			
			Type timeType = Time.getTimeType(getProgram());
			GoTupleType tupleType = GoTupleType.getTupleTypeOf(getLocation(), 
					new ReferenceType(timeType), GoErrorType.INSTANCE);

			// Allocates the new heap allocation
			MemoryAllocation created = new MemoryAllocation(timeType, left.getCodeLocation(), new Annotations(), true);
			HeapReference ref = new HeapReference(new ReferenceType(timeType), created, left.getCodeLocation());
			HeapDereference deref = new HeapDereference(timeType, ref, left.getCodeLocation());

			BinaryExpression rExp = new BinaryExpression(Untyped.INSTANCE, left, right, ParseOperatorFirstParameter.INSTANCE, getLocation());
			BinaryExpression lExp = new BinaryExpression(GoErrorType.INSTANCE, left, right, ParseOperatorFirstParameter.INSTANCE, getLocation());
			state = state.assign(deref, rExp, original);
			
			return GoTupleExpression.allocateTupleExpression(state, new Annotations(), this, getLocation(), tupleType, 
					ref,
					lExp
					);
		}
	}
	
	public static class ParseOperatorFirstParameter implements BinaryOperator {

		/**
		 * The singleton instance of this class.
		 */
		public static final ParseOperatorFirstParameter INSTANCE = new ParseOperatorFirstParameter();

		/**
		 * Builds the operator. This constructor is visible to allow subclassing:
		 * instances of this class should be unique, and the singleton can be
		 * retrieved through field {@link #INSTANCE}.
		 */
		protected ParseOperatorFirstParameter() {
		}

		@Override
		public String toString() {
			return "ParseOperator_first";
		}	

		@Override
		public Set<Type> typeInference(TypeSystem types, Set<Type> left, Set<Type> right) {
			return Collections.singleton(Untyped.INSTANCE);
		}
	}
	
	public static class ParseOperatorSecondParameter implements BinaryOperator {

		/**
		 * The singleton instance of this class.
		 */
		public static final ParseOperatorSecondParameter INSTANCE = new ParseOperatorSecondParameter();

		/**
		 * Builds the operator. This constructor is visible to allow subclassing:
		 * instances of this class should be unique, and the singleton can be
		 * retrieved through field {@link #INSTANCE}.
		 */
		protected ParseOperatorSecondParameter() {
		}

		@Override
		public String toString() {
			return "ParseOperator_second";
		}	

		@Override
		public Set<Type> typeInference(TypeSystem types, Set<Type> left, Set<Type> right) {
			return Collections.singleton(GoErrorType.INSTANCE);
		}
	}
}
