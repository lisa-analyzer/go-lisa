package it.unive.golisa.cfg.runtime.shim.method;

import java.util.Collections;
import java.util.Set;

import it.unive.golisa.cfg.expression.literal.GoTupleExpression;
import it.unive.golisa.cfg.runtime.shim.type.ChaincodeStub;
import it.unive.golisa.cfg.type.GoNilType;
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
import it.unive.lisa.symbolic.heap.AccessChild;
import it.unive.lisa.symbolic.heap.HeapDereference;
import it.unive.lisa.symbolic.heap.HeapReference;
import it.unive.lisa.symbolic.heap.MemoryAllocation;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.TernaryExpression;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.symbolic.value.operator.ternary.TernaryOperator;
import it.unive.lisa.type.ReferenceType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;
import it.unive.lisa.type.Untyped;

/**
 * func (s *ChaincodeStub) GetPrivateDataHash(collection string, key string) ([]byte, error).
 * https://pkg.go.dev/github.com/hyperledger/fabric-chaincode-go/shim#ChaincodeStub.GetPrivateDataHashHash
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class GetPrivateDataHash extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param shimUnit the unit to which this native cfg belongs to
	 */
	public GetPrivateDataHash(CodeLocation location, CompilationUnit shimUnit) {
		super(new CodeMemberDescriptor(location, shimUnit, true, "GetPrivateDataHash",
				GoTupleType.getTupleTypeOf(location, GoSliceType.lookup(GoUInt8Type.INSTANCE),
						GoErrorType.INSTANCE),
				new Parameter(location, "s", ChaincodeStub.getChaincodeStubType(shimUnit.getProgram())),
				new Parameter(location, "collection", GoStringType.INSTANCE),
				new Parameter(location, "key", GoStringType.INSTANCE)),
				GetPrivateDataHashImpl.class);
	}

	/**
	 * The {@link GetPrivateDataHashHash} implementation.
	 * 
	 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
	 */
	public static class GetPrivateDataHashImpl extends it.unive.lisa.program.cfg.statement.TernaryExpression
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
		public static GetPrivateDataHashImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new GetPrivateDataHashImpl(cfg, location, params[0], params[1], params[2]);
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
		public GetPrivateDataHashImpl(CFG cfg, CodeLocation location, Expression left, Expression middle, Expression right) {
			super(cfg, location, "GetPrivateDataHashImpl", GoTupleType.getTupleTypeOf(location,
					GoSliceType.lookup(GoSliceType.lookup(GoUInt8Type.INSTANCE)), GoErrorType.INSTANCE), left, middle, right);
		}


		@Override
		public <A extends AbstractState<A>> AnalysisState<A> fwdTernarySemantics(
				InterproceduralAnalysis<A> interprocedural, AnalysisState<A> state, SymbolicExpression left,
				SymbolicExpression middle, SymbolicExpression right, StatementStore<A> expressions)
				throws SemanticException {


			AccessChild collectionChild = new AccessChild(left.getStaticType(), left, middle, getLocation());
			
			Type  sliceBytes= GoSliceType.getSliceOfBytes();
			AccessChild keyChild = new AccessChild(sliceBytes, collectionChild, right, getLocation());
			
			
			GoTupleType tupleType = GoTupleType.getTupleTypeOf(original.getLocation(), sliceBytes, GoErrorType.INSTANCE);

			Annotations annots = new Annotations();
			if (original instanceof ResolvedCall)
				for (CodeMember target : ((ResolvedCall) original).getTargets())
					for (Annotation ann : target.getDescriptor().getAnnotations())
						annots.addAnnotation(ann);
			
			
			AnalysisState<A> pState = state.smallStepSemantics(keyChild, original);
			
			ExpressionSet computeExprs = pState.getComputedExpressions();
			AnalysisState<A> ret = state.bottom();
			
			for(SymbolicExpression exp : pState.getState().rewrite(computeExprs, original, state.getState())) {
				if(exp instanceof Identifier) {
					Identifier v = (Identifier) exp;
					for (Annotation ann : annots)
						v.addAnnotation(ann);
				}
				ret = ret.lub(GoTupleExpression.allocateTupleExpression(pState, 
					annots, 
					this,
					getLocation(), 
					tupleType,
					exp,
					new Constant(GoNilType.INSTANCE, "nil", getLocation())));
			}			
			

			return ret;
		}
	}

}
