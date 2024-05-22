package it.unive.golisa.cfg.runtime.shim.method;

import it.unive.golisa.cfg.runtime.shim.type.ChaincodeStub;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.NativeCFG;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.PushAny;

/**
 * func (s *ChaincodeStub) SetPrivateDataValidationParameter(collection, key string, ep []byte) error.
 *
 * @link https://pkg.go.dev/github.com/hyperledger/fabric-chaincode-go/shim#ChaincodeStub.SetPrivateDataValidationParameter
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class SetPrivateDataValidationParameter extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param shimUnit the unit to which this native cfg belongs to
	 */
	public SetPrivateDataValidationParameter(CodeLocation location, CompilationUnit shimUnit) {
		super(new CodeMemberDescriptor(location, shimUnit, true, "SetPrivateDataValidationParameter",
						GoErrorType.INSTANCE,
				new Parameter(location, "s", ChaincodeStub.getChaincodeStubType(shimUnit.getProgram())),
				new Parameter(location, "collection", GoStringType.INSTANCE),
				new Parameter(location, "key", GoStringType.INSTANCE),
				new Parameter(location, "ep", GoSliceType.getSliceOfBytes())),
				SetPrivateDataValidationParameterImpl.class);
	}

	/**
	 * The {@link SetPrivateDataValidationParameter} implementation.
	 * 
	 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
	 */
	public static class SetPrivateDataValidationParameterImpl extends it.unive.lisa.program.cfg.statement.QuaternaryExpression
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
		public static SetPrivateDataValidationParameterImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new SetPrivateDataValidationParameterImpl(cfg, location, params[0], params[1], params[2],  params[3]);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param e1     the first argument
		 * @param e2   the second argument
		 * @param e3    the third argument
		 * @param e4    the fourth argument
		 */
		public SetPrivateDataValidationParameterImpl(CFG cfg, CodeLocation location, Expression e1, Expression e2, Expression e3, Expression e4) {
			super(cfg, location, "SetPrivateDataValidationParameterImpl", GoErrorType.INSTANCE, e1, e2, e3, e4);
		}

		@Override
		public <A extends AbstractState<A>> AnalysisState<A> fwdQuaternarySemantics(
				InterproceduralAnalysis<A> interprocedural, AnalysisState<A> state, SymbolicExpression e1,
				SymbolicExpression e2, SymbolicExpression e3, SymbolicExpression e4, StatementStore<A> expressions)
				throws SemanticException {
			return state.smallStepSemantics(new PushAny(GoErrorType.INSTANCE, getLocation()), this);
		}
	}

}
