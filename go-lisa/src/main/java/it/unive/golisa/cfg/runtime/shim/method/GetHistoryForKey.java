package it.unive.golisa.cfg.runtime.shim.method;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import it.unive.golisa.cfg.expression.literal.GoTupleExpression;
import it.unive.golisa.cfg.runtime.shim.type.ChaincodeStub;
import it.unive.golisa.cfg.runtime.shim.type.HistoryQueryIteratorInterface;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoTupleType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.pointbased.AllocationSite;
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
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.type.ReferenceType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;
import it.unive.lisa.type.Untyped;

/**
 * func (s *ChaincodeStub) GetHistoryForKey(key string)
 * (HistoryQueryIteratorInterface, error).
 * https://pkg.go.dev/github.com/hyperledger/fabric-chaincode-go/shim#ChaincodeStub.GetHistoryForKey
 * 
 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
 */
public class GetHistoryForKey extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param shimUnit the unit to which this native cfg belongs to
	 */
	public GetHistoryForKey(CodeLocation location, CompilationUnit shimUnit) {
		super(new CodeMemberDescriptor(location, shimUnit, true, "GetHistoryForKey",
				GoTupleType.getTupleTypeOf(location,
						HistoryQueryIteratorInterface.getHistoryQueryIteratorInterfaceType(shimUnit.getProgram()),
						GoErrorType.INSTANCE),
				new Parameter(location, "this", ChaincodeStub.getChaincodeStubType(shimUnit.getProgram())),
				new Parameter(location, "key", GoStringType.INSTANCE)),
				GetHistoryForKeyImpl.class);
	}

	/**
	 * The {@link GetHistoryForKey} implementation.
	 * 
	 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
	 */
	public static class GetHistoryForKeyImpl extends it.unive.lisa.program.cfg.statement.BinaryExpression
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
		public static GetHistoryForKeyImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new GetHistoryForKeyImpl(cfg, location, params[0], params[1]);
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
		public GetHistoryForKeyImpl(CFG cfg, CodeLocation location, Expression left, Expression right) {
			super(cfg, location, "GetHistoryForKeyImpl", GoTupleType
					.getTupleTypeOf(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, Untyped.INSTANCE, GoErrorType.INSTANCE),
					left, right);
		}

		@Override
		public <A extends AbstractState<A>> AnalysisState<A> fwdBinarySemantics(
				InterproceduralAnalysis<A> interprocedural, AnalysisState<A> state, SymbolicExpression left,
				SymbolicExpression right, StatementStore<A> expressions) throws SemanticException {
			Type allocType = HistoryQueryIteratorInterface.getHistoryQueryIteratorInterfaceType(getProgram());
			GoTupleType tupleType = GoTupleType.getTupleTypeOf(getLocation(),
					new ReferenceType(allocType), GoErrorType.INSTANCE);

			// Allocates the new heap allocation
			MemoryAllocation created = new MemoryAllocation(allocType, left.getCodeLocation(), new Annotations(), true);
			
			if (original instanceof ResolvedCall)
				for (CodeMember target : ((ResolvedCall) original).getTargets())
					for (Annotation ann : target.getDescriptor().getAnnotations())
						created.getAnnotations().addAnnotation(ann);
			
			HeapReference ref = new HeapReference(new ReferenceType(allocType), created, left.getCodeLocation());
			HeapDereference deref = new HeapDereference(allocType, ref, left.getCodeLocation());
			AnalysisState<A> asg = state.bottom();

			// Retrieves all the identifiers reachable from expr
			Collection<SymbolicExpression> reachableIds = state.getState().reachableFrom(left, this,
					state.getState()).elements;
			for (SymbolicExpression id : reachableIds) {
				if (id instanceof AllocationSite)
					id = new HeapReference(new ReferenceType(id.getStaticType()), id, getLocation());
				HeapDereference derefId = new HeapDereference(Untyped.INSTANCE, id, left.getCodeLocation());
				BinaryExpression lExp = new BinaryExpression(Untyped.INSTANCE, derefId, right,
						GetHistoryForKeyOperatorFirstParameter.INSTANCE, getLocation());
				asg = asg.lub(state.assign(deref, lExp, original));
			}

			BinaryExpression rExp = new BinaryExpression(GoErrorType.INSTANCE,
					new Constant(Untyped.INSTANCE, 1, getLocation()), right,
					GetHistoryForKeySecondParameter.INSTANCE, getLocation());

			return GoTupleExpression.allocateTupleExpression(asg, new Annotations(), this, getLocation(), tupleType,
					ref,
					rExp);
		}
	}

	/**
	 * The GetHistoryForKey operator returning the first parameter of the tuple
	 * expression result.
	 * 
	 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
	 */
	public static class GetHistoryForKeyOperatorFirstParameter implements BinaryOperator {

		/**
		 * The singleton instance of this class.
		 */
		public static final GetHistoryForKeyOperatorFirstParameter INSTANCE = new GetHistoryForKeyOperatorFirstParameter();

		/**
		 * Builds the operator. This constructor is visible to allow
		 * subclassing: instances of this class should be unique, and the
		 * singleton can be retrieved through field {@link #INSTANCE}.
		 */
		protected GetHistoryForKeyOperatorFirstParameter() {
		}

		@Override
		public String toString() {
			return "GetHistoryForKeyOperator_1";
		}

		@Override
		public Set<Type> typeInference(TypeSystem types, Set<Type> left, Set<Type> right) {
			return Collections.singleton(Untyped.INSTANCE);
		}
	}

	/**
	 * The GetHistoryForKey operator returning the second parameter of the tuple
	 * expression result.
	 * 
	 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
	 */
	public static class GetHistoryForKeySecondParameter implements BinaryOperator {

		/**
		 * The singleton instance of this class.
		 */
		public static final GetHistoryForKeySecondParameter INSTANCE = new GetHistoryForKeySecondParameter();

		/**
		 * Builds the operator. This constructor is visible to allow
		 * subclassing: instances of this class should be unique, and the
		 * singleton can be retrieved through field {@link #INSTANCE}.
		 */
		protected GetHistoryForKeySecondParameter() {
		}

		@Override
		public String toString() {
			return "GetHistoryForKeyOperator_2";
		}

		@Override
		public Set<Type> typeInference(TypeSystem types, Set<Type> left, Set<Type> right) {
			return Collections.singleton(GoErrorType.INSTANCE);
		}
	}
}
