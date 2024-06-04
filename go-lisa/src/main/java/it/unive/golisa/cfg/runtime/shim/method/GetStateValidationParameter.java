package it.unive.golisa.cfg.runtime.shim.method;

import java.util.Collections;
import java.util.Set;

import it.unive.golisa.cfg.expression.literal.GoTupleExpression;
import it.unive.golisa.cfg.runtime.shim.type.ChaincodeStub;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.composite.GoTupleType;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt8Type;
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
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.type.ReferenceType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;
import it.unive.lisa.type.Untyped;

/**
 * func (s *ChaincodeStub) GetStateValidationParameter(key string) ([]byte, error).
 * https://pkg.go.dev/github.com/hyperledger/fabric-chaincode-go/shim#ChaincodeStub.GetStateValidationParameter
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GetStateValidationParameter extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param shimUnit the unit to which this native cfg belongs to
	 */
	public GetStateValidationParameter(CodeLocation location, CompilationUnit shimUnit) {
		super(new CodeMemberDescriptor(location, shimUnit, true, "GetStateValidationParameter",
				GoTupleType.getTupleTypeOf(location, GoSliceType.lookup(GoUInt8Type.INSTANCE),
						GoErrorType.INSTANCE),
				new Parameter(location, "s", ChaincodeStub.getChaincodeStubType(shimUnit.getProgram())),
				new Parameter(location, "key", GoStringType.INSTANCE)),
				GetStateValidationParameterImpl.class);
	}

	/**
	 * The {@link GetStateValidationParameterValidationParameter} implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class GetStateValidationParameterImpl extends it.unive.lisa.program.cfg.statement.BinaryExpression
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
		public static GetStateValidationParameterImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new GetStateValidationParameterImpl(cfg, location, params[0], params[1]);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param left     the left argument
		 * @param right    the right argument
		 */
		public GetStateValidationParameterImpl(CFG cfg, CodeLocation location, Expression left, Expression right) {
			super(cfg, location, "GetStateValidationParameterImpl", GoTupleType.getTupleTypeOf(location,
					GoSliceType.lookup(GoSliceType.lookup(GoUInt8Type.INSTANCE)), GoErrorType.INSTANCE), left, right);
		}

		@Override
		public <A extends AbstractState<A>> AnalysisState<A> fwdBinarySemantics(
				InterproceduralAnalysis<A> interprocedural, AnalysisState<A> state,
				SymbolicExpression left, SymbolicExpression right, StatementStore<A> expressions)
				throws SemanticException {
			Type sliceOfBytes = GoSliceType.getSliceOfBytes();

			GoTupleType tupleType = GoTupleType.getTupleTypeOf(getLocation(), new ReferenceType(sliceOfBytes),
					GoErrorType.INSTANCE);

			// Allocates the new heap allocation
			MemoryAllocation created = new MemoryAllocation(sliceOfBytes, left.getCodeLocation(), new Annotations(),
					true);
			
			if (original instanceof ResolvedCall)
				for (CodeMember target : ((ResolvedCall) original).getTargets())
					for (Annotation ann : target.getDescriptor().getAnnotations())
						created.getAnnotations().addAnnotation(ann);
			
			HeapReference ref = new HeapReference(new ReferenceType(sliceOfBytes), created, left.getCodeLocation());
			HeapDereference deref = new HeapDereference(sliceOfBytes, ref, left.getCodeLocation());
			AnalysisState<A> result = state.bottom();

			// Retrieves all the identifiers reachable from expr
			ExpressionSet reachableIds = state.getState().reachableFrom(left, this, state.getState());
			for (SymbolicExpression id : reachableIds) {
				if (id instanceof AllocationSite)
					id = new HeapReference(new ReferenceType(id.getStaticType()), id, getLocation());
				HeapDereference derefId = new HeapDereference(Untyped.INSTANCE, id, left.getCodeLocation());
				BinaryExpression lExp = new BinaryExpression(GoSliceType.getSliceOfBytes(), derefId, right,
						GetStateValidationParameterOperatorFirstParameter.INSTANCE, getLocation());
				BinaryExpression rExp = new BinaryExpression(GoErrorType.INSTANCE, derefId, right,
						GetStateValidationParameterSecondParameter.INSTANCE, getLocation());
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
	 * The GetStateValidationParameter operator returning the first parameter of the tuple
	 * expression result.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class GetStateValidationParameterOperatorFirstParameter implements BinaryOperator {

		/**
		 * The singleton instance of this class.
		 */
		public static final GetStateValidationParameterOperatorFirstParameter INSTANCE = new GetStateValidationParameterOperatorFirstParameter();

		/**
		 * Builds the operator. This constructor is visible to allow
		 * subclassing: instances of this class should be unique, and the
		 * singleton can be retrieved through field {@link #INSTANCE}.
		 */
		protected GetStateValidationParameterOperatorFirstParameter() {
		}

		@Override
		public String toString() {
			return "GetStateValidationParameterOperator_1";
		}

		@Override
		public Set<Type> typeInference(TypeSystem types, Set<Type> left, Set<Type> right) {
			return Collections.singleton(GoSliceType.getSliceOfBytes());
		}
	}

	/**
	 * The GetStateValidationParameter operator returning the second parameter of the tuple
	 * expression result.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class GetStateValidationParameterSecondParameter implements BinaryOperator {

		/**
		 * The singleton instance of this class.
		 */
		public static final GetStateValidationParameterSecondParameter INSTANCE = new GetStateValidationParameterSecondParameter();

		/**
		 * Builds the operator. This constructor is visible to allow
		 * subclassing: instances of this class should be unique, and the
		 * singleton can be retrieved through field {@link #INSTANCE}.
		 */
		protected GetStateValidationParameterSecondParameter() {
		}

		@Override
		public String toString() {
			return "GetStateValidationParameterOperator_2";
		}

		@Override
		public Set<Type> typeInference(TypeSystem types, Set<Type> left, Set<Type> right) {
			return Collections.singleton(GoErrorType.INSTANCE);
		}
	}
}
