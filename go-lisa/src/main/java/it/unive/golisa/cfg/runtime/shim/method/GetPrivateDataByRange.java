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
import it.unive.lisa.symbolic.value.QuaternaryExpression;
import it.unive.lisa.symbolic.value.TernaryExpression;
import it.unive.lisa.symbolic.value.operator.quaternary.QuaternaryOperator;
import it.unive.lisa.symbolic.value.operator.ternary.TernaryOperator;
import it.unive.lisa.type.ReferenceType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;
import it.unive.lisa.type.Untyped;

/**
 * func (s *ChaincodeStub) GetPrivateDataByRange(collection, startKey, endKey string) (StateQueryIteratorInterface, error).
 * https://pkg.go.dev/github.com/hyperledger/fabric-chaincode-go/shim#ChaincodeStub.GetPrivateDataByRange
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class GetPrivateDataByRange extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param shimUnit the unit to which this native cfg belongs to
	 */
	public GetPrivateDataByRange(CodeLocation location, CompilationUnit shimUnit) {
		super(new CodeMemberDescriptor(location, shimUnit, true, "GetPrivateDataByRange",
				GoTupleType.getTupleTypeOf(location, StateQueryIteratorInterface.getStateQueryIteratorInterfaceType(shimUnit.getProgram()),
						GoErrorType.INSTANCE),
				new Parameter(location, "s", ChaincodeStub.getChaincodeStubType(shimUnit.getProgram())),
				new Parameter(location, "collection", GoStringType.INSTANCE),
				new Parameter(location, "startKey", GoStringType.INSTANCE),
				new Parameter(location, "endKey", GoStringType.INSTANCE)),
				GetPrivateDataByRangeImpl.class);
	}

	/**
	 * The {@link GetPrivateDataByRange} implementation.
	 * 
	 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
	 */
	public static class GetPrivateDataByRangeImpl extends it.unive.lisa.program.cfg.statement.QuaternaryExpression
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
		public static GetPrivateDataByRangeImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new GetPrivateDataByRangeImpl(cfg, location, params[0], params[1], params[2], params[3]);
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
		public GetPrivateDataByRangeImpl(CFG cfg, CodeLocation location, Expression e1, Expression e2, Expression e3, Expression e4) {
			super(cfg, location, "GetPrivateDataByRangeImpl", GoTupleType.getTupleTypeOf(location,
					GoTupleType.getTupleTypeOf(location, StateQueryIteratorInterface.getStateQueryIteratorInterfaceType(null),
							GoErrorType.INSTANCE)), e1, e2, e3, e4);
		}


		@Override
		public <A extends AbstractState<A>> AnalysisState<A> fwdQuaternarySemantics(
				InterproceduralAnalysis<A> interprocedural, AnalysisState<A> state, SymbolicExpression e1,
				SymbolicExpression e2, SymbolicExpression e3, SymbolicExpression e4, StatementStore<A> expressions)
				throws SemanticException {
			Type iteratorType = StateQueryIteratorInterface.getStateQueryIteratorInterfaceType(getProgram());

			GoTupleType tupleType = GoTupleType.getTupleTypeOf(getLocation(), new ReferenceType(iteratorType),
					GoErrorType.INSTANCE);

			// Allocates the new heap allocation
			MemoryAllocation created = new MemoryAllocation(iteratorType, e1.getCodeLocation(), new Annotations(),
					true);
			
			if (original instanceof ResolvedCall)
				for (CodeMember target : ((ResolvedCall) original).getTargets())
					for (Annotation ann : target.getDescriptor().getAnnotations())
						created.getAnnotations().addAnnotation(ann);
			
			HeapReference ref = new HeapReference(new ReferenceType(iteratorType), created, e1.getCodeLocation());
			HeapDereference deref = new HeapDereference(iteratorType, ref, e1.getCodeLocation());
			AnalysisState<A> result = state.bottom();

			// Retrieves all the identifiers reachable from expr
			ExpressionSet reachableIds = state.getState().reachableFrom(e1, this, state.getState());
			for (SymbolicExpression id : reachableIds) {
				if (id instanceof AllocationSite)
					id = new HeapReference(new ReferenceType(id.getStaticType()), id, getLocation());
				HeapDereference derefId = new HeapDereference(Untyped.INSTANCE, id, e1.getCodeLocation());
				QuaternaryExpression lExp = new QuaternaryExpression(GoSliceType.getSliceOfBytes(), derefId, e2, e3, e4,
						GetPrivateDataOperatorFirstParameter.INSTANCE, getLocation());
				TernaryExpression rExp = new TernaryExpression(GoErrorType.INSTANCE, derefId, e3, e4,
						GetPrivateDataOperatorThirdParameter.INSTANCE, getLocation());
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
	 * The GetPrivateData operator returning the first parameter of the tuple
	 * expression result.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class GetPrivateDataOperatorFirstParameter implements QuaternaryOperator {

		/**
		 * The singleton instance of this class.
		 */
		public static final GetPrivateDataOperatorFirstParameter INSTANCE = new GetPrivateDataOperatorFirstParameter();

		/**
		 * Builds the operator. This constructor is visible to allow
		 * subclassing: instances of this class should be unique, and the
		 * singleton can be retrieved through field {@link #INSTANCE}.
		 */
		protected GetPrivateDataOperatorFirstParameter() {
		}

		@Override
		public String toString() {
			return "GetPrivateDataByRangeOperator_1";
		}

		@Override
		public Set<Type> typeInference(TypeSystem types, Set<Type> e1, Set<Type> e2, Set<Type> e3, Set<Type> e4) {
			return Collections.singleton(GoSliceType.getSliceOfBytes());
		}
	}

	/**
	 * The GetPrivateData operator returning the second parameter of the tuple
	 * expression result.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class GetPrivateDataOperatorThirdParameter implements TernaryOperator {

		/**
		 * The singleton instance of this class.
		 */
		public static final GetPrivateDataOperatorThirdParameter INSTANCE = new GetPrivateDataOperatorThirdParameter();

		/**
		 * Builds the operator. This constructor is visible to allow
		 * subclassing: instances of this class should be unique, and the
		 * singleton can be retrieved through field {@link #INSTANCE}.
		 */
		protected GetPrivateDataOperatorThirdParameter() {
		}

		@Override
		public String toString() {
			return "GetPrivateDataByRangeOperator_2";
		}


		@Override
		public Set<Type> typeInference(TypeSystem types, Set<Type> left, Set<Type> middle, Set<Type> right) {
			return Collections.singleton(GoErrorType.INSTANCE);
		}
	}
}
