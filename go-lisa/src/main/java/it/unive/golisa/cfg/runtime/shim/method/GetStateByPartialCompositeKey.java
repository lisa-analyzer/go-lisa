package it.unive.golisa.cfg.runtime.shim.method;

import it.unive.golisa.cfg.expression.literal.GoTupleExpression;
import it.unive.golisa.cfg.runtime.shim.type.ChaincodeStub;
import it.unive.golisa.cfg.runtime.shim.type.StateQueryIterator;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.composite.GoTupleType;
import it.unive.golisa.golang.util.GoLangUtils;
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
import it.unive.lisa.symbolic.value.TernaryExpression;
import it.unive.lisa.symbolic.value.operator.ternary.TernaryOperator;
import it.unive.lisa.type.ReferenceType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;
import it.unive.lisa.type.Untyped;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * func (s *ChaincodeStub) GetStateByPartialCompositeKey(objectType string,
 * attributes []string) (StateQueryIteratorInterface, error).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GetStateByPartialCompositeKey extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param shimUnit the unit to which this native cfg belongs to
	 */
	public GetStateByPartialCompositeKey(CodeLocation location, CompilationUnit shimUnit) {
		super(new CodeMemberDescriptor(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, shimUnit,
				true,
				"GetStateByPartialCompositeKey",
				GoTupleType.getTupleTypeOf(location, StateQueryIterator.getStateQueryIterator(shimUnit.getProgram()),
						GoErrorType.INSTANCE),
				new Parameter(location, "this", ChaincodeStub.getChaincodeStubType(shimUnit.getProgram())),
				new Parameter(location, "objectType", GoStringType.INSTANCE),
				new Parameter(location, "attributes", GoSliceType.getSliceOfStrings())),
				GetStateByPartialCompositeKeyImpl.class);
	}

	/**
	 * The {@link GetStateByPartialCompositeKey} implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class GetStateByPartialCompositeKeyImpl extends it.unive.lisa.program.cfg.statement.TernaryExpression
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
		public static GetStateByPartialCompositeKeyImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new GetStateByPartialCompositeKeyImpl(cfg, location, params[0], params[1], params[2]);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param left     the left-hand side of this expression
		 * @param middle   the middle-hand side of this expression
		 * @param right    the right-hand side of this expression
		 */
		public GetStateByPartialCompositeKeyImpl(CFG cfg, CodeLocation location, Expression left, Expression middle,
				Expression right) {
			super(cfg, location, "GetStateByPartialCompositeKeyImpl", GoTupleType
					.getTupleTypeOf(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, Untyped.INSTANCE, GoErrorType.INSTANCE),
					left, middle, right);
		}

		@Override
		public <A extends AbstractState<A>> AnalysisState<A> fwdTernarySemantics(
				InterproceduralAnalysis<A> interprocedural, AnalysisState<A> state, SymbolicExpression left,
				SymbolicExpression middle, SymbolicExpression right, StatementStore<A> expressions)
				throws SemanticException {
			Type allocType = StateQueryIterator.getStateQueryIterator(getProgram());
			GoTupleType tupleType = GoTupleType.getTupleTypeOf(getLocation(),
					new ReferenceType(allocType), GoErrorType.INSTANCE);

			// Allocates the new heap allocation
			MemoryAllocation created = new MemoryAllocation(allocType, left.getCodeLocation(), new Annotations(), true);
			HeapReference ref = new HeapReference(new ReferenceType(allocType), created, left.getCodeLocation());
			HeapDereference deref = new HeapDereference(allocType, ref, left.getCodeLocation());
			AnalysisState<A> result = state.bottom();

			// Retrieves all the identifiers reachable from expr
			Collection<SymbolicExpression> reachableIds = state.getState().reachableFrom(left, this,
					state.getState()).elements;
			Collection<SymbolicExpression> reachableIdsRight = state.getState().reachableFrom(right, this,
					state.getState()).elements;
			for (SymbolicExpression id : reachableIds) {
				for (SymbolicExpression r : reachableIdsRight) {
					HeapDereference derefId = new HeapDereference(Untyped.INSTANCE, id, left.getCodeLocation());
					TernaryExpression lExp = new TernaryExpression(new ReferenceType(allocType), derefId, middle, r,
							GetStateByPartialCompositeKeyOperatorFirstParameter.INSTANCE, getLocation());
					AnalysisState<A> asg = state.assign(deref, lExp, original);
					TernaryExpression rExp = new TernaryExpression(GoErrorType.INSTANCE, derefId, middle, r,
							GetStateByPartialCompositeKeyOperatorSecondParameter.INSTANCE, getLocation());

					result = result.lub(GoTupleExpression.allocateTupleExpression(asg, new Annotations(), this,
							getLocation(), tupleType,
							ref,
							rExp));
				}
			}

			return result;
		}
	}

	/**
	 * The GetStateByParitalCompositeKey operator returning the first parameter
	 * of the tuple expression result.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class GetStateByPartialCompositeKeyOperatorFirstParameter implements TernaryOperator {

		/**
		 * The singleton instance of this class.
		 */
		public static final GetStateByPartialCompositeKeyOperatorFirstParameter INSTANCE = new GetStateByPartialCompositeKeyOperatorFirstParameter();

		/**
		 * Builds the operator. This constructor is visible to allow
		 * subclassing: instances of this class should be unique, and the
		 * singleton can be retrieved through field {@link #INSTANCE}.
		 */
		protected GetStateByPartialCompositeKeyOperatorFirstParameter() {
		}

		@Override
		public String toString() {
			return "GetStateByPartialCompositeKeyOperator_1";
		}

		@Override
		public Set<Type> typeInference(TypeSystem types, Set<Type> left, Set<Type> middle, Set<Type> right) {
			return Collections.singleton(new ReferenceType(StateQueryIterator.getStateQueryIterator(null)));
		}
	}

	/**
	 * The GetStateByParitalCompositeKey operator returning the second parameter
	 * of the tuple expression result.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class GetStateByPartialCompositeKeyOperatorSecondParameter implements TernaryOperator {

		/**
		 * The singleton instance of this class.
		 */
		public static final GetStateByPartialCompositeKeyOperatorSecondParameter INSTANCE = new GetStateByPartialCompositeKeyOperatorSecondParameter();

		/**
		 * Builds the operator. This constructor is visible to allow
		 * subclassing: instances of this class should be unique, and the
		 * singleton can be retrieved through field {@link #INSTANCE}.
		 */
		protected GetStateByPartialCompositeKeyOperatorSecondParameter() {
		}

		@Override
		public String toString() {
			return "GetStateByPartialCompositeKeyOperator_2";
		}

		@Override
		public Set<Type> typeInference(TypeSystem types, Set<Type> left, Set<Type> middle, Set<Type> right) {
			return Collections.singleton(GoErrorType.INSTANCE);
		}
	}
}
