package it.unive.golisa.cfg.runtime.shim.method;

import java.util.Collections;
import java.util.Set;

import it.unive.golisa.analysis.ni.IntegrityNIDomain;
import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.cfg.expression.literal.GoTupleExpression;
import it.unive.golisa.cfg.runtime.shim.type.ChaincodeStub;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoMapType;
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
import it.unive.lisa.program.cfg.statement.UnaryExpression;
import it.unive.lisa.program.cfg.statement.call.ResolvedCall;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.HeapDereference;
import it.unive.lisa.symbolic.heap.HeapReference;
import it.unive.lisa.symbolic.heap.MemoryAllocation;
import it.unive.lisa.symbolic.value.operator.unary.UnaryOperator;
import it.unive.lisa.type.ReferenceType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;
import it.unive.lisa.type.Untyped;

/**
 * func (s *ChaincodeStub) GetTransient() (map[string][]byte, error)
 * 
 * 
 * @link https://pkg.go.dev/github.com/hyperledger/fabric-chaincode-go/shim#ChaincodeStub.GetTransient
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class GetTransient extends NativeCFG {

	private final static Annotations anns = new Annotations(TaintDomain.CLEAN_ANNOTATION,
			IntegrityNIDomain.HIGH_ANNOTATION);

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param shimUnit the unit to which this native cfg belongs to
	 */
	public GetTransient(CodeLocation location, CompilationUnit shimUnit) {
		super(new CodeMemberDescriptor(location, shimUnit, true, "GetTransient",
				GoTupleType.getTupleTypeOf(location, GoMapType.lookup(GoStringType.INSTANCE, GoUInt8Type.INSTANCE),
						GoErrorType.INSTANCE), anns,
				new Parameter(location, "s", ChaincodeStub.getChaincodeStubType(shimUnit.getProgram()))),
				GetTransientImpl.class);
	}

	/**
	 * The {@link GetTransient} implementation.
	 * 
	 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
	 */
	public static class GetTransientImpl extends UnaryExpression implements PluggableStatement {

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
		public static GetTransientImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new GetTransientImpl(cfg, location, params);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param params   the parameters
		 */
		public GetTransientImpl(CFG cfg, CodeLocation location, Expression... params) {
			super(cfg, location, "GetTransientImpl", GoTupleType.getTupleTypeOf(location, GoMapType.lookup(GoStringType.INSTANCE, GoUInt8Type.INSTANCE),
					GoErrorType.INSTANCE), params[0]);
		}

		@Override
		public <A extends AbstractState<A>> AnalysisState<A> fwdUnarySemantics(
				InterproceduralAnalysis<A> interprocedural, AnalysisState<A> state, SymbolicExpression expr,
				StatementStore<A> expressions) throws SemanticException {
			
			Type sliceOfSliceOfBytes = GoSliceType.getSliceOfSliceOfBytes();
			Type mapsType = GoMapType.lookup(GoStringType.INSTANCE, sliceOfSliceOfBytes);
			GoTupleType tupleType = GoTupleType.getTupleTypeOf(original.getLocation(), mapsType,	GoErrorType.INSTANCE);

			// Allocates the new heap allocation
			MemoryAllocation created = new MemoryAllocation(tupleType, getLocation(), anns, true);
			
			if (original instanceof ResolvedCall)
				for (CodeMember target : ((ResolvedCall) original).getTargets())
					for (Annotation ann : target.getDescriptor().getAnnotations())
						created.getAnnotations().addAnnotation(ann);
			
			HeapReference ref = new HeapReference(new ReferenceType(tupleType), created, getLocation());
			HeapDereference deref = new HeapDereference(tupleType, ref, getLocation());
			AnalysisState<A> result = state.bottom();
			
			// Retrieves all the identifiers reachable from expr
			ExpressionSet reachableIds = state.getState().reachableFrom(expr, this, state.getState());
			for (SymbolicExpression id : reachableIds) {
				if (id instanceof AllocationSite)
					id = new HeapReference(new ReferenceType(id.getStaticType()), id, getLocation());
				HeapDereference derefId = new HeapDereference(Untyped.INSTANCE, id, expr.getCodeLocation());
				it.unive.lisa.symbolic.value.UnaryExpression lExp = new it.unive.lisa.symbolic.value.UnaryExpression(GoSliceType.getSliceOfBytes(), derefId,
						GetTransientOperatorFirstParameter.INSTANCE, getLocation());
				it.unive.lisa.symbolic.value.UnaryExpression rExp = new it.unive.lisa.symbolic.value.UnaryExpression(GoErrorType.INSTANCE, derefId,
						GetTransientSecondParameter.INSTANCE, getLocation());
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
	 * The GetTransient operator returning the first parameter of the tuple
	 * expression result.
	 * 
	 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
	 */
	public static class GetTransientOperatorFirstParameter implements UnaryOperator {

		/**
		 * The singleton instance of this class.
		 */
		public static final GetTransientOperatorFirstParameter INSTANCE = new GetTransientOperatorFirstParameter();

		/**
		 * Builds the operator. This constructor is visible to allow
		 * subclassing: instances of this class should be unique, and the
		 * singleton can be retrieved through field {@link #INSTANCE}.
		 */
		protected GetTransientOperatorFirstParameter() {
		}

		@Override
		public String toString() {
			return "GetTransientOperator_1";
		}

		@Override
		public Set<Type> typeInference(TypeSystem types, Set<Type> argument) {
			Type sliceOfSliceOfBytes = GoSliceType.getSliceOfSliceOfBytes();
			Type mapsType = GoMapType.lookup(GoStringType.INSTANCE, sliceOfSliceOfBytes);
			return Collections.singleton(mapsType);
		}
	}

	/**
	 * The GetTransient operator returning the second parameter of the tuple
	 * expression result.
	 * 
	 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
	 */
	public static class GetTransientSecondParameter implements UnaryOperator {

		/**
		 * The singleton instance of this class.
		 */
		public static final GetTransientSecondParameter INSTANCE = new GetTransientSecondParameter();

		/**
		 * Builds the operator. This constructor is visible to allow
		 * subclassing: instances of this class should be unique, and the
		 * singleton can be retrieved through field {@link #INSTANCE}.
		 */
		protected GetTransientSecondParameter() {
		}

		@Override
		public String toString() {
			return "GetTransientOperator_2";
		}

		@Override
		public Set<Type> typeInference(TypeSystem types, Set<Type> argument) {
			return Collections.singleton(GoErrorType.INSTANCE);
		}
	}
}
