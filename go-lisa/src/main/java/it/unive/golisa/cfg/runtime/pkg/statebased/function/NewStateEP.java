package it.unive.golisa.cfg.runtime.pkg.statebased.function;

import it.unive.golisa.analysis.taint.Clean;
import it.unive.golisa.cfg.runtime.pkg.statebased.type.KeyEndorsementPolicy;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.composite.GoTypesTuple;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt8Type;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CFGDescriptor;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.NativeCFG;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.UnaryExpression;
import it.unive.lisa.symbolic.SymbolicExpression;

/**
 * func NewStateEP(policy []byte) (KeyEndorsementPolicy, error)
 * 
 * https://pkg.go.dev/github.com/hyperledger/fabric-chaincode-go/pkg/statebased#NewStateEP
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class NewStateEP extends NativeCFG {

	public NewStateEP(CodeLocation location, CompilationUnit statebasedPackage) {
		super(new CFGDescriptor(location, statebasedPackage, false, "NewStateEP",
				GoTypesTuple.getTupleTypeOf(location, KeyEndorsementPolicy.INSTANCE, GoErrorType.INSTANCE),
				new Parameter(location, "policy", GoSliceType.lookup(new GoSliceType(GoUInt8Type.INSTANCE)))),
				NewStateEPImpl.class);
	}

	public static class NewStateEPImpl extends UnaryExpression
	implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static NewStateEPImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new NewStateEPImpl(cfg, location, params[0]);
		}

		public NewStateEPImpl(CFG cfg, CodeLocation location, Expression e) {
			super(cfg, location, "NewStateEPImpl", GoTypesTuple.getTupleTypeOf(location, KeyEndorsementPolicy.INSTANCE, GoErrorType.INSTANCE), e);
		}

		@Override
		protected <A extends AbstractState<A, H, V, T>, H extends HeapDomain<H>, V extends ValueDomain<V>, T extends TypeDomain<T>> AnalysisState<A, H, V, T> unarySemantics(
				InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
				SymbolicExpression expr, StatementStore<A, H, V, T> expressions) throws SemanticException {
			return state.smallStepSemantics(new Clean(getLocation()), original);
		}
	}
}
