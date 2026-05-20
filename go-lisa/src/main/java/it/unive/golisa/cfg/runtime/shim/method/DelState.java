package it.unive.golisa.cfg.runtime.shim.method;

import it.unive.golisa.cfg.runtime.io.type.Reader;
import it.unive.golisa.cfg.runtime.shim.type.ChaincodeStub;
import it.unive.golisa.cfg.type.GoNilType;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.lisa.analysis.AbstractDomain;
import it.unive.lisa.analysis.AbstractLattice;
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
import it.unive.lisa.program.cfg.statement.BinaryExpression;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.PushAny;

/**
 * func (*ChaincodeStub) DelState(key string) error.
 * 
 * @link https://pkg.go.dev/github.com/hyperledger/fabric-chaincode-go/shim#ChaincodeStub.DelState
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class DelState extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param shimUnit the unit to which this native cfg belongs to
	 */
	public DelState(CodeLocation location, CompilationUnit shimUnit) {
		super(new CodeMemberDescriptor(location, shimUnit, true, "DelState", GoErrorType.INSTANCE,
				new Parameter(location, "this", ChaincodeStub.getChaincodeStubType(shimUnit.getProgram())),
				new Parameter(location, "key", GoStringType.INSTANCE)),
				DelStateImpl.class);
	}

	/**
	 * The {@link DelState} implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class DelStateImpl extends BinaryExpression
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
		public static DelStateImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new DelStateImpl(cfg, location, params[0], params[1]);
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
		public DelStateImpl(CFG cfg, CodeLocation location, Expression left, Expression right) {
			super(cfg, location, "DelStateImpl", GoErrorType.INSTANCE, left, right);
		}

		@Override
		public <A extends AbstractLattice<A>, D extends AbstractDomain<A>> AnalysisState<A> fwdBinarySemantics(
				InterproceduralAnalysis<A, D> interprocedural, AnalysisState<A> state, SymbolicExpression left,
				SymbolicExpression right, StatementStore<A> expressions) throws SemanticException {
			AnalysisState<
					A> readerValue = interprocedural.getAnalysis().smallStepSemantics(state,
							new PushAny(Reader.getReaderType(null), getLocation()),
							original);
			AnalysisState<A> nilValue = interprocedural.getAnalysis().smallStepSemantics(state,
					new Constant(GoNilType.INSTANCE, "nil", getLocation()), original);
			return readerValue.lub(nilValue);
		}
	}
}
