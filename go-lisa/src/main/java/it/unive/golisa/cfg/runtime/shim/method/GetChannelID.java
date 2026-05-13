package it.unive.golisa.cfg.runtime.shim.method;

import it.unive.golisa.cfg.runtime.shim.type.ChaincodeStub;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.lisa.analysis.AbstractDomain;
import it.unive.lisa.analysis.AbstractLattice;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.lattices.ExpressionSet;
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
import it.unive.lisa.symbolic.value.Constant;

/**
 * func (s *ChaincodeStub) GetChannelID() string.
 * 
 * @link https://pkg.go.dev/github.com/hyperledger/fabric-chaincode-go/shim#ChaincodeStub.GetChannelID
 * 
 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
 */
public class GetChannelID extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param shimUnit the unit to which this native cfg belongs to
	 */
	public GetChannelID(CodeLocation location, CompilationUnit shimUnit) {
		super(new CodeMemberDescriptor(location, shimUnit, true, "GetChannelID",
				GoStringType.INSTANCE,
				new Parameter(location, "s", ChaincodeStub.getChaincodeStubType(shimUnit.getProgram()))),
				GetChannelIDImpl.class);
	}

	/**
	 * The {@link GetChannelID} implementation.
	 * 
	 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
	 */
	public static class GetChannelIDImpl extends NaryExpression
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
		public static GetChannelIDImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new GetChannelIDImpl(cfg, location, params);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param params   the parameters
		 */
		public GetChannelIDImpl(CFG cfg, CodeLocation location, Expression... params) {
			super(cfg, location, "GetChannelIDImpl", GoStringType.INSTANCE, params);
		}

		@Override
		public <A extends AbstractLattice<A>, D extends AbstractDomain<A>> AnalysisState<A> forwardSemanticsAux(
				InterproceduralAnalysis<A, D> interprocedural, AnalysisState<A> state, ExpressionSet[] params,
				StatementStore<A> expressions) throws SemanticException {
			// https://github.com/hyperledger/fabric-chaincode-go/blob/main/shim/interfaces.go#L73C2-L74C17
			// if `channel` is empty string, the caller's channel is assumed.
			return interprocedural.getAnalysis().smallStepSemantics(state,
					new Constant(GoStringType.INSTANCE, "", getLocation()), original);

		}
	}
}
