package it.unive.golisa.cfg.runtime.shim.method;

import java.util.Collections;
import java.util.Set;

import it.unive.golisa.analysis.ni.IntegrityNIDomain;
import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.cfg.expression.literal.GoTupleExpression;
import it.unive.golisa.cfg.runtime.shim.type.ChaincodeStub;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.composite.GoTupleType;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt8Type;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.lattices.ExpressionSet;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.annotations.Annotations;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.NativeCFG;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.NaryExpression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.heap.HeapDereference;
import it.unive.lisa.symbolic.heap.HeapReference;
import it.unive.lisa.symbolic.heap.MemoryAllocation;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.type.ReferenceType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;
import it.unive.lisa.type.Untyped;

/**
 * func (s *ChaincodeStub) GetArgsSlice() ([]byte, error)
 * 
 * @link https://pkg.go.dev/github.com/hyperledger/fabric-chaincode-go/shim#ChaincodeStub.GetArgsSlice
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class GetArgsSlice extends NativeCFG {

	private final static Annotations anns = new Annotations(TaintDomain.CLEAN_ANNOTATION,
			IntegrityNIDomain.HIGH_ANNOTATION);

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param shimUnit the unit to which this native cfg belongs to
	 */
	public GetArgsSlice(CodeLocation location, CompilationUnit shimUnit) {
		super(new CodeMemberDescriptor(location, shimUnit, true, "GetArgsSlice",
				GoTupleType.getTupleTypeOf(location, GoSliceType.lookup(GoUInt8Type.INSTANCE),
						GoErrorType.INSTANCE), anns,
				new Parameter(location, "s", ChaincodeStub.getChaincodeStubType(shimUnit.getProgram()))),
				GetArgsSliceImpl.class);
	}

	/**
	 * The {@link GetArgsSlice} implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class GetArgsSliceImpl extends NaryExpression
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
		public static GetArgsSliceImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new GetArgsSliceImpl(cfg, location, params);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param params   the parameters
		 */
		public GetArgsSliceImpl(CFG cfg, CodeLocation location, Expression... params) {
			super(cfg, location, "GetArgsSliceImpl", GoTupleType.getTupleTypeOf(location, GoSliceType.lookup(GoUInt8Type.INSTANCE),
					GoErrorType.INSTANCE), params);
		}

		@Override
		public <A extends AbstractState<A>> AnalysisState<A> forwardSemanticsAux(
				InterproceduralAnalysis<A> interprocedural, AnalysisState<A> state,
				ExpressionSet[] params, StatementStore<A> expressions)
				throws SemanticException {
			
			Type sliceType = GoSliceType.lookup(GoUInt8Type.INSTANCE);
			GoTupleType tupleType = GoTupleType.getTupleTypeOf(getLocation(),
					new ReferenceType(sliceType), GoErrorType.INSTANCE);

			// Allocates the new heap allocation
			MemoryAllocation created = new MemoryAllocation(sliceType, original.getLocation(), new Annotations(), true);
			HeapReference ref = new HeapReference(new ReferenceType(sliceType), created, original.getLocation());
			HeapDereference deref = new HeapDereference(sliceType, ref, original.getLocation());
			AnalysisState<A> asg = state.bottom();
			
			asg = asg.lub(state.assign(deref, new PushAny(sliceType, getLocation()), original));

			BinaryExpression rExp = new BinaryExpression(GoErrorType.INSTANCE,
					new Constant(Untyped.INSTANCE, 1, getLocation()), new PushAny(sliceType, getLocation()),
					new BinaryOperator() {
						
						@Override
						public Set<Type> typeInference(TypeSystem types, Set<Type> left, Set<Type> right) {
							return Collections.singleton(GoErrorType.INSTANCE);
						}
					}, getLocation());

			return GoTupleExpression.allocateTupleExpression(asg, new Annotations(), this, getLocation(), tupleType,
					ref,
					rExp);

		}
	}
}
