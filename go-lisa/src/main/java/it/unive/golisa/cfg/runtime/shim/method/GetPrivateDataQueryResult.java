package it.unive.golisa.cfg.runtime.shim.method;

import java.util.Collections;
import java.util.Set;

import it.unive.golisa.cfg.expression.literal.GoTupleExpression;
import it.unive.golisa.cfg.runtime.shim.type.ChaincodeStub;
import it.unive.golisa.cfg.runtime.shim.type.StateQueryIteratorInterface;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.composite.GoTupleType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.pointbased.AllocationSite;
import it.unive.lisa.analysis.lattices.ExpressionSet;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.annotations.Annotation;
import it.unive.lisa.program.annotations.Annotations;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.CodeMember;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.NativeCFG;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.call.ResolvedCall;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.HeapDereference;
import it.unive.lisa.symbolic.heap.HeapReference;
import it.unive.lisa.symbolic.heap.MemoryAllocation;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.TernaryExpression;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.symbolic.value.operator.ternary.TernaryOperator;
import it.unive.lisa.type.ReferenceType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;
import it.unive.lisa.type.Untyped;

/**
 * func (s *ChaincodeStub) GetPrivateDataQueryResult(collection, query string) (StateQueryIteratorInterface, error).
 *
 * @link https://pkg.go.dev/github.com/hyperledger/fabric-chaincode-go/shim#ChaincodeStub.GetPrivateDataQueryResult
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class GetPrivateDataQueryResult extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param shimUnit the unit to which this native cfg belongs to
	 */
	public GetPrivateDataQueryResult(CodeLocation location, CompilationUnit shimUnit) {
		super(new CodeMemberDescriptor(location, shimUnit, true, "GetPrivateDataQueryResult",
				GoTupleType.getTupleTypeOf(location, StateQueryIteratorInterface.getStateQueryIteratorInterfaceType(shimUnit.getProgram()),
						GoErrorType.INSTANCE),
				new Parameter(location, "s", ChaincodeStub.getChaincodeStubType(shimUnit.getProgram())),
				new Parameter(location, "collection", GoStringType.INSTANCE),
				new Parameter(location, "query", GoStringType.INSTANCE)),
				GetPrivateDataQueryResultImpl.class);
	}

	/**
	 * The {@link GetPrivateDataQueryResult} implementation.
	 * 
	 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
	 */
	public static class GetPrivateDataQueryResultImpl extends it.unive.lisa.program.cfg.statement.TernaryExpression
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
		public static GetPrivateDataQueryResultImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new GetPrivateDataQueryResultImpl(cfg, location, params[0], params[1], params[2]);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param left     the left argument
		 * @param middle   the middle argument
		 * @param right    the right argument
		 */
		public GetPrivateDataQueryResultImpl(CFG cfg, CodeLocation location, Expression left, Expression middle, Expression right) {
			super(cfg, location, "GetPrivateDataQueryResultImpl", GoTupleType.getTupleTypeOf(location,
					StateQueryIteratorInterface.getStateQueryIteratorInterfaceType(left.getProgram()), GoErrorType.INSTANCE), left, middle, right);
		}


		@Override
		public <A extends AbstractState<A>> AnalysisState<A> fwdTernarySemantics(
				InterproceduralAnalysis<A> interprocedural, AnalysisState<A> state, SymbolicExpression left,
				SymbolicExpression middle, SymbolicExpression right, StatementStore<A> expressions)
				throws SemanticException {
			Type iteratorType = StateQueryIteratorInterface.getStateQueryIteratorInterfaceType(getProgram());

			GoTupleType tupleType = GoTupleType.getTupleTypeOf(getLocation(), new ReferenceType(iteratorType),
					GoErrorType.INSTANCE);

			// Allocates the new heap allocation
			MemoryAllocation created = new MemoryAllocation(iteratorType, left.getCodeLocation(), new Annotations(),
					true);
			
			if (original instanceof ResolvedCall)
				for (CodeMember target : ((ResolvedCall) original).getTargets())
					for (Annotation ann : target.getDescriptor().getAnnotations())
						created.getAnnotations().addAnnotation(ann);
			
			HeapReference ref = new HeapReference(new ReferenceType(iteratorType), created, left.getCodeLocation());
			HeapDereference deref = new HeapDereference(iteratorType, ref, left.getCodeLocation());
			AnalysisState<A> result = state.bottom();

			// Retrieves all the identifiers reachable from expr
			ExpressionSet reachableIds = state.getState().reachableFrom(left, this, state.getState());
			for (SymbolicExpression id : reachableIds) {
				if (id instanceof AllocationSite)
					id = new HeapReference(new ReferenceType(id.getStaticType()), id, getLocation());
				HeapDereference derefId = new HeapDereference(Untyped.INSTANCE, id, left.getCodeLocation());
				TernaryExpression lExp = new TernaryExpression(GoSliceType.getSliceOfBytes(), derefId, middle, right,
						GetPrivateDataQueryResultOperatorFirstParameter.INSTANCE, getLocation());
				BinaryExpression rExp = new BinaryExpression(GoErrorType.INSTANCE, derefId, right,
						GetPrivateDataQueryResultOperatorThirdParameter.INSTANCE, getLocation());
				AnalysisState<A> asg = state.assign(deref, lExp, original);
				AnalysisState<A> tupleExp = GoTupleExpression.allocateTupleExpression(asg, new Annotations(), this,
						getLocation(), tupleType,
						ref,
						rExp);

				result = result.lub(tupleExp);
			}

			return result;
		}
	}

	/**
	 * The GetPrivateDataQueryResult operator returning the first parameter of the tuple
	 * expression result.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class GetPrivateDataQueryResultOperatorFirstParameter implements TernaryOperator {

		/**
		 * The singleton instance of this class.
		 */
		public static final GetPrivateDataQueryResultOperatorFirstParameter INSTANCE = new GetPrivateDataQueryResultOperatorFirstParameter();

		/**
		 * Builds the operator. This constructor is visible to allow
		 * subclassing: instances of this class should be unique, and the
		 * singleton can be retrieved through field {@link #INSTANCE}.
		 */
		protected GetPrivateDataQueryResultOperatorFirstParameter() {
		}

		@Override
		public String toString() {
			return "GetPrivateDataQueryResultOperator_1";
		}

		@Override
		public Set<Type> typeInference(TypeSystem types, Set<Type> left, Set<Type> middle, Set<Type> right) {
			return Collections.singleton(StateQueryIteratorInterface.getStateQueryIteratorInterfaceType(null));
		}
	}

	/**
	 * The GetPrivateDataQueryResult operator returning the second parameter of the tuple
	 * expression result.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class GetPrivateDataQueryResultOperatorThirdParameter implements BinaryOperator {

		/**
		 * The singleton instance of this class.
		 */
		public static final GetPrivateDataQueryResultOperatorThirdParameter INSTANCE = new GetPrivateDataQueryResultOperatorThirdParameter();

		/**
		 * Builds the operator. This constructor is visible to allow
		 * subclassing: instances of this class should be unique, and the
		 * singleton can be retrieved through field {@link #INSTANCE}.
		 */
		protected GetPrivateDataQueryResultOperatorThirdParameter() {
		}

		@Override
		public String toString() {
			return "GetPrivateDataQueryResultOperator_2";
		}

		@Override
		public Set<Type> typeInference(TypeSystem types, Set<Type> left, Set<Type> right) {
			return Collections.singleton(GoErrorType.INSTANCE);
		}
	}
}
