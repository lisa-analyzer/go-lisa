package it.unive.golisa.cfg.runtime.shim.method;

import it.unive.golisa.cfg.runtime.shim.type.ChaincodeStub;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
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
import it.unive.lisa.type.Untyped;

/**
 * func (*ChaincodeStub) PutState(key string, value []byte) error.
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class PutState extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param shimUnit the unit to which this native cfg belongs to
	 */
	public PutState(CodeLocation location, CompilationUnit shimUnit) {
		super(new CodeMemberDescriptor(location, shimUnit, true, "PutState", GoErrorType.INSTANCE,
				new Parameter(location, "this", ChaincodeStub.getChaincodeStubType(shimUnit.getProgram())),
				new Parameter(location, "key", GoStringType.INSTANCE),
				new Parameter(location, "value", Untyped.INSTANCE)),
				PutStateImpl.class);
	}

	/**
	 * The {@link PutState} implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class PutStateImpl extends NaryExpression
			implements PluggableStatement {

		@SuppressWarnings("unused")
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
		public static PutStateImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new PutStateImpl(cfg, location, params);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param params   the parameters
		 */
		public PutStateImpl(CFG cfg, CodeLocation location, Expression... params) {
			super(cfg, location, "PutStateImpl", GoErrorType.INSTANCE, params);
		}

		@Override
		public <A extends AbstractState<A>> AnalysisState<A> forwardSemanticsAux(
				InterproceduralAnalysis<A> interprocedural, AnalysisState<A> state, ExpressionSet[] params,
				StatementStore<A> expressions) throws SemanticException {
			return state.smallStepSemantics(new PushAny(GoErrorType.INSTANCE, getLocation()), this);
		}
	}
}
