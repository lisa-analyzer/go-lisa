package it.unive.golisa.cfg.runtime.shim.method;

import it.unive.golisa.cfg.runtime.peer.type.Response;
import it.unive.golisa.cfg.runtime.shim.type.ChaincodeStub;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.lattices.ExpressionSet;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.NativeCFG;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.NaryExpression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.value.PushAny;

/**
 * func (s *ChaincodeStub) InvokeChaincode(chaincodeName string, args [][]byte, channel string) pb.Response
 * https://pkg.go.dev/github.com/hyperledger/fabric-chaincode-go/shim#ChaincodeStub.InvokeChaincode
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class InvokeChaincode extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param shimUnit the unit to which this native cfg belongs to
	 */
	public InvokeChaincode(CodeLocation location, CompilationUnit shimUnit) {
		super(new CodeMemberDescriptor(location, shimUnit, true, "InvokeChaincode", Response.getResponseType(null),
				new Parameter(location, "this", ChaincodeStub.INSTANCE),
				new Parameter(location, "chaincodeName", GoStringType.INSTANCE),
				new Parameter(location, "args", GoSliceType.lookup(GoSliceType.getSliceOfSliceOfBytes())),
				new Parameter(location, "channel", GoStringType.INSTANCE)),
				InvokeChaincodeImpl.class);
	}

	/**
	 * The {@link InvokeChaincode} implementation.
	 * 
	 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
	 */
	public static class InvokeChaincodeImpl extends NaryExpression
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
		public static InvokeChaincodeImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new InvokeChaincodeImpl(cfg, location, params);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param params   the parameters
		 */
		public InvokeChaincodeImpl(CFG cfg, CodeLocation location, Expression... params) {
			super(cfg, location, "InvokeChaincodeImpl", Response.getResponseType(null), params);
		}

		@Override
		protected int compareSameClassAndParams(Statement o) {
			return 0;  // nothing else to compare
		}

		@Override
		public <A extends AbstractState<A>> AnalysisState<A> forwardSemanticsAux(
				InterproceduralAnalysis<A> interprocedural, AnalysisState<A> state, ExpressionSet[] params,
				StatementStore<A> expressions) throws SemanticException {
		return state.smallStepSemantics(new PushAny(Response.getResponseType(null), getLocation()),	original);
		}
	}
}