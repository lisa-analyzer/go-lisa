package it.unive.golisa.cfg.runtime.shim.function;

import it.unive.golisa.cfg.runtime.peer.type.Response;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.CodeUnit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.NativeCFG;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.UnaryExpression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.PushAny;

/**
 * Error response chaincodes. func Error(msg string) pb.Response
 * 
 * @link https://pkg.go.dev/github.com/hyperledger/fabric-chaincode-go/shim#Error
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class Error extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param shimUnit the unit to which this native cfg belongs to
	 */
	public Error(CodeLocation location, CodeUnit shimUnit) {
		super(new CodeMemberDescriptor(location, shimUnit, false, "Error",
				Response.getResponseType(shimUnit.getProgram()),
				new Parameter(location, "msg", GoStringType.INSTANCE)),
				ErrorImpl.class);
	}

	/**
	 * The Error implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class ErrorImpl extends UnaryExpression
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
		public static ErrorImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new ErrorImpl(cfg, location, params[0]);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param expr     the expression
		 */
		public ErrorImpl(CFG cfg, CodeLocation location, Expression expr) {
			super(cfg, location, "ErrorImpl", Response.getResponseType(null), expr);
		}

		@Override
		public <A extends AbstractState<A>> AnalysisState<A> fwdUnarySemantics(
				InterproceduralAnalysis<A> interprocedural, AnalysisState<A> state,
				SymbolicExpression expr, StatementStore<A> expressions) throws SemanticException {
			return state.smallStepSemantics(new PushAny(Response.getResponseType(null), getLocation()), original);
		}
	}
}