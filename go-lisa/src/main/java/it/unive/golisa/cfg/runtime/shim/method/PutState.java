package it.unive.golisa.cfg.runtime.shim.method;

import it.unive.golisa.cfg.runtime.shim.type.ChaincodeStub;
import it.unive.golisa.cfg.type.GoNilType;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.lattices.ExpressionSet;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.caches.Caches;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CFGDescriptor;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.NativeCFG;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.NaryExpression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.PushAny;

/**
 * func (*ChaincodeStub) PutState(key string, value []byte) error
 * https://pkg.go.dev/github.com/hyperledger/fabric-chaincode-go/shim#ChaincodeStub.PutState
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class PutState extends NativeCFG {

	public PutState(CodeLocation location, CompilationUnit shimUnit) {
		super(new CFGDescriptor(location, shimUnit, false, "PutState", GoErrorType.INSTANCE,
				new Parameter(location, "this", ChaincodeStub.INSTANCE),
				new Parameter(location, "key", GoStringType.INSTANCE),
				new Parameter(location, "value", GoSliceType.getSliceOfBytes())),
				PutStateImpl.class);
	}

	public static class PutStateImpl extends NaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static PutStateImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new PutStateImpl(cfg, location, params);
		}

		public PutStateImpl(CFG cfg, CodeLocation location, Expression... params) {
			super(cfg, location, "PutStateImpl", GoErrorType.INSTANCE, params);
		}

		@Override
		public <A extends AbstractState<A, H, V>,
				H extends HeapDomain<H>,
				V extends ValueDomain<V>> AnalysisState<A, H, V> expressionSemantics(
						InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> state,
						ExpressionSet<SymbolicExpression>[] params, StatementStore<A, H, V> expressions)
						throws SemanticException {
			AnalysisState<A, H, V> errorValue = state.smallStepSemantics(new PushAny(Caches.types().mkSingletonSet(GoErrorType.INSTANCE), getLocation()), original);
			AnalysisState<A, H, V> nilValue = state.smallStepSemantics(new Constant(GoNilType.INSTANCE, "nil", getLocation()), original);
			return errorValue.lub(nilValue);
		}
	}
}
