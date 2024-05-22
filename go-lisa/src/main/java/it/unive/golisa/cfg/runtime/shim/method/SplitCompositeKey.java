package it.unive.golisa.cfg.runtime.shim.method;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import it.unive.golisa.cfg.expression.literal.GoTupleExpression;
import it.unive.golisa.cfg.runtime.shim.type.ChaincodeStub;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.composite.GoTupleType;
import it.unive.golisa.cfg.type.numeric.floating.GoFloat64Type;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.CompilationUnit;
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
import it.unive.lisa.symbolic.value.UnaryExpression;
import it.unive.lisa.symbolic.value.operator.unary.UnaryOperator;
import it.unive.lisa.type.ReferenceType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;
import it.unive.lisa.type.Untyped;

/**
 * func (s *ChaincodeStub) SplitCompositeKey(compositeKey string) (string,
 * []string, error).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class SplitCompositeKey extends NativeCFG {
	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param shimUnit the unit to which this native cfg belongs to
	 */
	public SplitCompositeKey(CodeLocation location, CompilationUnit shimUnit) {
		super(new CodeMemberDescriptor(location, shimUnit, true, "SplitCompositeKey",
				GoTupleType.getTupleTypeOf(location, GoStringType.INSTANCE, GoSliceType.getSliceOfStrings(),
						GoErrorType.INSTANCE),
				new Parameter(location, "this", ChaincodeStub.getChaincodeStubType(shimUnit.getProgram())),
				new Parameter(location, "compositeKey", GoStringType.INSTANCE)),
				SplitCompositeKeyImpl.class);
	}

	/**
	 * The {@link SplitCompositeKey} implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class SplitCompositeKeyImpl extends it.unive.lisa.program.cfg.statement.BinaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		@Override
		protected int compareSameClassAndParams(Statement o) {
			return 0; // nothing else to compare
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
		public static SplitCompositeKeyImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new SplitCompositeKeyImpl(cfg, location, params[0], params[1]);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param left     the left expression
		 * @param right    the right expression
		 */
		public SplitCompositeKeyImpl(CFG cfg, CodeLocation location, Expression left, Expression right) {
			super(cfg, location, "SplitCompositeKeyImpl",
					GoTupleType.getTupleTypeOf(location, GoStringType.INSTANCE, GoSliceType.getSliceOfBytes(),
							GoErrorType.INSTANCE),
					left, right);
		}

		@Override
		public <A extends AbstractState<A>> AnalysisState<A> fwdBinarySemantics(
				InterproceduralAnalysis<A> interprocedural, AnalysisState<A> state,
				SymbolicExpression left, SymbolicExpression right, StatementStore<A> expressions)
				throws SemanticException {
			Type sliceOfStrings = GoSliceType.getSliceOfStrings();
			GoTupleType tupleType = GoTupleType.getTupleTypeOf(getLocation(),
					GoStringType.INSTANCE, new ReferenceType(sliceOfStrings), GoErrorType.INSTANCE);

			// Allocates the new heap allocation
			MemoryAllocation created = new MemoryAllocation(sliceOfStrings, left.getCodeLocation(), new Annotations(),
					true);

			HeapReference ref = new HeapReference(new ReferenceType(sliceOfStrings), created, left.getCodeLocation());
			HeapDereference deref = new HeapDereference(sliceOfStrings, ref, left.getCodeLocation());
			AnalysisState<A> asg = state.bottom();

			// Retrieves all the identifiers reachable from expr
			Collection<SymbolicExpression> reachableIds = state.getState().reachableFrom(left, this,
					state.getState()).elements;
			for (SymbolicExpression id : reachableIds) {
				HeapDereference derefId = new HeapDereference(Untyped.INSTANCE, id, left.getCodeLocation());
				UnaryExpression unary = new UnaryExpression(sliceOfStrings, derefId,
						SplitCompositeKeyOperatorSecondParameter.INSTANCE,
						getLocation());
				asg = asg.lub(state.assign(deref, unary, original));
			}

			UnaryExpression lExp = new UnaryExpression(GoFloat64Type.INSTANCE, right,
					SplitCompositeKeyOperatorFirstParameter.INSTANCE, getLocation());
			UnaryExpression rExp = new UnaryExpression(GoErrorType.INSTANCE, right,
					SplitCompositeKeyOperatorThirdParameter.INSTANCE, getLocation());

			return GoTupleExpression.allocateTupleExpression(asg, new Annotations(), this, getLocation(), tupleType,
					lExp,
					ref,
					rExp);
		}
	}

	/**
	 * The SplitCompositeKey operator returning the first parameter of the tuple
	 * expression result.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class SplitCompositeKeyOperatorFirstParameter implements UnaryOperator {

		/**
		 * The singleton instance of this class.
		 */
		public static final SplitCompositeKeyOperatorFirstParameter INSTANCE = new SplitCompositeKeyOperatorFirstParameter();

		/**
		 * Builds the operator. This constructor is visible to allow
		 * subclassing: instances of this class should be unique, and the
		 * singleton can be retrieved through field {@link #INSTANCE}.
		 */
		protected SplitCompositeKeyOperatorFirstParameter() {
		}

		@Override
		public String toString() {
			return "SplitCompositeKeyOperator_1";
		}

		@Override
		public Set<Type> typeInference(TypeSystem types, Set<Type> argument) {
			return Collections.singleton(GoStringType.INSTANCE);
		}
	}

	/**
	 * The SplitCompositeKey operator returning the second parameter of the
	 * tuple expression result.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class SplitCompositeKeyOperatorSecondParameter implements UnaryOperator {

		/**
		 * The singleton instance of this class.
		 */
		public static final SplitCompositeKeyOperatorSecondParameter INSTANCE = new SplitCompositeKeyOperatorSecondParameter();

		/**
		 * Builds the operator. This constructor is visible to allow
		 * subclassing: instances of this class should be unique, and the
		 * singleton can be retrieved through field {@link #INSTANCE}.
		 */
		protected SplitCompositeKeyOperatorSecondParameter() {
		}

		@Override
		public String toString() {
			return "SplitCompositeKeyOperator_2";
		}

		@Override
		public Set<Type> typeInference(TypeSystem types, Set<Type> argument) {
			return Collections.singleton(GoSliceType.getSliceOfStrings());
		}
	}

	/**
	 * The SplitCompositeKey operator returning the third parameter of the tuple
	 * expression result.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class SplitCompositeKeyOperatorThirdParameter implements UnaryOperator {

		/**
		 * The singleton instance of this class.
		 */
		public static final SplitCompositeKeyOperatorThirdParameter INSTANCE = new SplitCompositeKeyOperatorThirdParameter();

		/**
		 * Builds the operator. This constructor is visible to allow
		 * subclassing: instances of this class should be unique, and the
		 * singleton can be retrieved through field {@link #INSTANCE}.
		 */
		protected SplitCompositeKeyOperatorThirdParameter() {
		}

		@Override
		public String toString() {
			return "SplitCompositeKeyOperator_3";
		}

		@Override
		public Set<Type> typeInference(TypeSystem types, Set<Type> argument) {
			return Collections.singleton(GoErrorType.INSTANCE);
		}
	}
}
