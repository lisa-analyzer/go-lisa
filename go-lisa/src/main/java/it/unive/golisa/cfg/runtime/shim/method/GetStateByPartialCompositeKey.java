package it.unive.golisa.cfg.runtime.shim.method;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import it.unive.golisa.cfg.expression.literal.GoTupleExpression;
import it.unive.golisa.cfg.runtime.shim.type.ChaincodeStub;
import it.unive.golisa.cfg.runtime.shim.type.StateQueryIteratorInterface;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.composite.GoTupleType;
import it.unive.golisa.checker.TaintChecker.HeapResolver;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
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
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.TernaryExpression;
import it.unive.lisa.symbolic.value.operator.ternary.TernaryOperator;
import it.unive.lisa.type.ReferenceType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;
import it.unive.lisa.type.Untyped;

/**
 * func (s *ChaincodeStub) GetStateByPartialCompositeKey(objectType string, attributes []string) (StateQueryIteratorInterface, error)
 * 
 * @see https://pkg.go.dev/github.com/hyperledger/fabric-chaincode-go/shim#ChaincodeStub.GetStateByPartialCompositeKey
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GetStateByPartialCompositeKey extends NativeCFG {

	public GetStateByPartialCompositeKey(CodeLocation location, CompilationUnit shimUnit) {
		super(new CodeMemberDescriptor(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, shimUnit,
				true,
				"GetStateByPartialCompositeKey",
				GoTupleType.getTupleTypeOf(location, StateQueryIteratorInterface.getStateQueryIteratorInterfaceType(shimUnit.getProgram()), GoErrorType.INSTANCE),
				new Parameter(location, "this", ChaincodeStub.getChaincodeStubType(shimUnit.getProgram())),
				new Parameter(location, "objectType", GoStringType.INSTANCE),
				new Parameter(location, "attributes", GoSliceType.getSliceOfStrings())), GetStateByPartialCompositeKeyImpl.class);
	}

	/**
	 * The {@link GetStateByRange} implementation.
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
		 * @param right    the right-hand side of this expression
		 */
		public GetStateByPartialCompositeKeyImpl(CFG cfg, CodeLocation location, Expression left, Expression middle, Expression right) {
			super(cfg, location, "GetStateByPartialCompositeKeyImpl", GoTupleType.getTupleTypeOf(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, Untyped.INSTANCE ,GoErrorType.INSTANCE), left, middle, right);
		}

		@Override
		public <A extends AbstractState<A, H, V, T>, H extends HeapDomain<H>, V extends ValueDomain<V>, T extends TypeDomain<T>> AnalysisState<A, H, V, T> ternarySemantics(
				InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
				SymbolicExpression left, SymbolicExpression middle, SymbolicExpression right,
				StatementStore<A, H, V, T> expressions) throws SemanticException {
			Type allocType = StateQueryIteratorInterface.getStateQueryIteratorInterfaceType(getProgram());
			GoTupleType tupleType = GoTupleType.getTupleTypeOf(getLocation(), 
					new ReferenceType(allocType), GoErrorType.INSTANCE);

			// Allocates the new heap allocation
			MemoryAllocation created = new MemoryAllocation(allocType, left.getCodeLocation(), new Annotations(), true);
			HeapReference ref = new HeapReference(new ReferenceType(allocType), created, left.getCodeLocation());
			HeapDereference deref = new HeapDereference(allocType, ref, left.getCodeLocation());
			AnalysisState<A, H, V, T> asg = state.bottom();

			// Retrieves all the identifiers reachable from expr
			Collection<SymbolicExpression> reachableIds = HeapResolver.resolve(state, left, this);
			Collection<SymbolicExpression> reachableIdsRight = HeapResolver.resolve(state, right, this);
			for (SymbolicExpression id : reachableIds) {
				for (SymbolicExpression r : reachableIdsRight) {
					// FIXME: first parameter is stub, but it is not tracked (put constant now)
					HeapDereference derefId = new HeapDereference(Untyped.INSTANCE, id, left.getCodeLocation());
					TernaryExpression lExp = new TernaryExpression(Untyped.INSTANCE, new Constant(Untyped.INSTANCE, 1, getLocation()), middle, r, GetStateByPartialCompositeKeyFirstParameter.INSTANCE, getLocation());
					asg = asg.lub(state.assign(deref, lExp, original));
				}
			}
			TernaryExpression rExp = new TernaryExpression(GoErrorType.INSTANCE, new Constant(Untyped.INSTANCE, 1, getLocation()), middle, new Constant(Untyped.INSTANCE, 1, getLocation()), GetStateByPartialCompositeKeySecondParameter.INSTANCE, getLocation());

			return GoTupleExpression.allocateTupleExpression(asg, new Annotations(), this, getLocation(), tupleType, 
					ref,
					rExp);
		}
	}

	public static class GetStateByPartialCompositeKeyFirstParameter implements TernaryOperator {

		/**
		 * The singleton instance of this class.
		 */
		public static final GetStateByPartialCompositeKeyFirstParameter INSTANCE = new GetStateByPartialCompositeKeyFirstParameter();

		/**
		 * Builds the operator. This constructor is visible to allow subclassing:
		 * instances of this class should be unique, and the singleton can be
		 * retrieved through field {@link #INSTANCE}.
		 */
		protected GetStateByPartialCompositeKeyFirstParameter() {
		}

		@Override
		public String toString() {
			return "GetStateByPartialCompositeKey_first";
		}

		@Override
		public Set<Type> typeInference(TypeSystem types, Set<Type> left, Set<Type> middle, Set<Type> right) {
			return Collections.singleton(Untyped.INSTANCE);
		}
	}

	public static class GetStateByPartialCompositeKeySecondParameter implements TernaryOperator {

		/**
		 * The singleton instance of this class.
		 */
		public static final GetStateByPartialCompositeKeySecondParameter INSTANCE = new GetStateByPartialCompositeKeySecondParameter();

		/**
		 * Builds the operator. This constructor is visible to allow subclassing:
		 * instances of this class should be unique, and the singleton can be
		 * retrieved through field {@link #INSTANCE}.
		 */
		protected GetStateByPartialCompositeKeySecondParameter() {
		}

		@Override
		public String toString() {
			return "GetStateByPartialCompositeKey_second";
		}

		@Override
		public Set<Type> typeInference(TypeSystem types, Set<Type> left, Set<Type> middle, Set<Type> right) {
			return Collections.singleton(GoErrorType.INSTANCE);
		}
	}
}
