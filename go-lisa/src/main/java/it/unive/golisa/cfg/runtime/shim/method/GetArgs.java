package it.unive.golisa.cfg.runtime.shim.method;

import it.unive.golisa.analysis.ni.IntegrityNIDomain;
import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.cfg.runtime.shim.type.ChaincodeStub;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
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
import it.unive.lisa.symbolic.heap.AccessChild;
import it.unive.lisa.symbolic.heap.HeapDereference;
import it.unive.lisa.symbolic.heap.HeapReference;
import it.unive.lisa.symbolic.heap.MemoryAllocation;
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.symbolic.value.Variable;
import it.unive.lisa.type.ReferenceType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;

/**
 * func (s *ChaincodeStub) GetArgs() [][]byte.
 * 
 * @link https://pkg.go.dev/github.com/hyperledger/fabric-chaincode-go/shim#ChaincodeStub.GetArgs
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class GetArgs extends NativeCFG {

	private final static Annotations anns = new Annotations(TaintDomain.CLEAN_ANNOTATION,
			IntegrityNIDomain.HIGH_ANNOTATION);

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param shimUnit the unit to which this native cfg belongs to
	 */
	public GetArgs(CodeLocation location, CompilationUnit shimUnit) {
		super(new CodeMemberDescriptor(location, shimUnit, true, "GetArgs",
				GoSliceType.getSliceOfSliceOfBytes(), anns,
				new Parameter(location, "s", ChaincodeStub.getChaincodeStubType(shimUnit.getProgram()))),
				GetArgsImpl.class);
	}

	/**
	 * The {@link GetArgs} implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class GetArgsImpl extends NaryExpression
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
		public static GetArgsImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new GetArgsImpl(cfg, location, params);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param params   the parameters
		 */
		public GetArgsImpl(CFG cfg, CodeLocation location, Expression... params) {
			super(cfg, location, "GetArgsImpl", GoSliceType.getSliceOfSliceOfBytes(), params);
		}

		@Override
		public <A extends AbstractState<A>> AnalysisState<A> forwardSemanticsAux(
				InterproceduralAnalysis<A> interprocedural, AnalysisState<A> state,
				ExpressionSet[] params, StatementStore<A> expressions)
				throws SemanticException {
			Type sliceOfSliceOfBytes = GoSliceType.getSliceOfSliceOfBytes();

			// Allocates the new heap allocation
			MemoryAllocation created = new MemoryAllocation(sliceOfSliceOfBytes, getLocation(), anns, true);
			HeapReference ref = new HeapReference(new ReferenceType(sliceOfSliceOfBytes), created, getLocation());
			HeapDereference deref = new HeapDereference(sliceOfSliceOfBytes, ref, getLocation());

			// Assign the len property to this hid
			Variable len = new Variable(Untyped.INSTANCE, "len",
					getLocation());
			AccessChild lenAccess = new AccessChild(GoIntType.INSTANCE, deref,
					len, getLocation());
			AnalysisState<A> lenState = state.assign(lenAccess, new PushAny(GoIntType.INSTANCE, getLocation()), this);

			// Assign the cap property to this hid
			Variable cap = new Variable(Untyped.INSTANCE, "cap",
					getLocation());
			AccessChild capAccess = new AccessChild(GoIntType.INSTANCE, deref,
					cap, getLocation());
			AnalysisState<
					A> capState = lenState.assign(capAccess, new PushAny(GoIntType.INSTANCE, getLocation()), this);

			return capState.smallStepSemantics(ref, original);
		}
	}
}
