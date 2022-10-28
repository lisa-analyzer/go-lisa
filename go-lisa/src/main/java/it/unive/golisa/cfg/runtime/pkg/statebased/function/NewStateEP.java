package it.unive.golisa.cfg.runtime.pkg.statebased.function;

import it.unive.golisa.analysis.taint.Clean;
import it.unive.golisa.cfg.runtime.pkg.statebased.type.KeyEndorsementPolicy;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.composite.GoTupleType;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt8Type;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
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
import it.unive.lisa.type.Untyped;

/**
 * func NewStateEP(policy []byte) (KeyEndorsementPolicy, error).
 * 
 * @link https://pkg.go.dev/github.com/hyperledger/fabric-chaincode-go/pkg/statebased#NewStateEP
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class NewStateEP extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location          the location where this native cfg is defined
	 * @param statebasedPackage the unit to which this native cfg belongs to
	 */
	public NewStateEP(CodeLocation location, CodeUnit statebasedPackage) {
		super(new CodeMemberDescriptor(location, statebasedPackage, false, "NewStateEP",
				GoTupleType.getTupleTypeOf(location, KeyEndorsementPolicy.getKeyEndorsementPolicyType(statebasedPackage.getProgram()), GoErrorType.INSTANCE),
				new Parameter(location, "policy", GoSliceType.lookup(GoUInt8Type.INSTANCE))),
				NewStateEPImpl.class);
	}

	/**
	 * The NewStateEP implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class NewStateEPImpl extends UnaryExpression
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
		public static NewStateEPImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new NewStateEPImpl(cfg, location, params[0]);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param expr     the expression
		 */
		public NewStateEPImpl(CFG cfg, CodeLocation location, Expression expr) {
			super(cfg, location, "NewStateEPImpl",
					GoTupleType.getTupleTypeOf(location, KeyEndorsementPolicy.getKeyEndorsementPolicyType(null),
							GoErrorType.INSTANCE),
					expr);
		}

		@Override
		protected <A extends AbstractState<A, H, V, T>,
				H extends HeapDomain<H>,
				V extends ValueDomain<V>,
				T extends TypeDomain<T>> AnalysisState<A, H, V, T> unarySemantics(
						InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
						SymbolicExpression expr, StatementStore<A, H, V, T> expressions) throws SemanticException {
			return state.smallStepSemantics(new Clean(Untyped.INSTANCE, getLocation()), original);
		}
	}
}
