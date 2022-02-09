package it.unive.golisa.cfg.runtime.shim.method;

import it.unive.golisa.cfg.runtime.io.type.Reader;
import it.unive.golisa.cfg.runtime.shim.type.ChaincodeStub;
import it.unive.golisa.cfg.type.GoNilType;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.lattices.ExpressionSet;
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
import it.unive.lisa.program.cfg.statement.NaryExpression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.PushAny;

/**
 * func (*ChaincodeStub) DelPrivateData(collection string, key string) error
 * https://pkg.go.dev/github.com/hyperledger/fabric-chaincode-go/shim#ChaincodeStub.DelPrivateData
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class DelPrivateData extends NativeCFG {

	public DelPrivateData(CodeLocation location, CompilationUnit shimUnit) {
		super(new CFGDescriptor(location, shimUnit, false, "DelPrivateData", GoErrorType.INSTANCE,
				new Parameter(location, "this", ChaincodeStub.INSTANCE),
				new Parameter(location, "collection", GoStringType.INSTANCE),
				new Parameter(location, "key", GoStringType.INSTANCE)),
				DelPrivateDataImpl.class);
	}

	public static class DelPrivateDataImpl extends NaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static DelPrivateDataImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new DelPrivateDataImpl(cfg, location, params[0], params[1]);
		}

		public DelPrivateDataImpl(CFG cfg, CodeLocation location, Expression... params) {
			super(cfg, location, "DelPrivateDataImpl", GoErrorType.INSTANCE, params);
		}

		@Override
		public <A extends AbstractState<A, H, V, T>, H extends HeapDomain<H>, V extends ValueDomain<V>, T extends TypeDomain<T>> AnalysisState<A, H, V, T> expressionSemantics(
				InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
				ExpressionSet<SymbolicExpression>[] params, StatementStore<A, H, V, T> expressions)
				throws SemanticException {
			AnalysisState<A, H, V, T> readerValue =  state.smallStepSemantics(new PushAny(Reader.INSTANCE, getLocation()), original);
			AnalysisState<A, H, V, T> nilValue = state.smallStepSemantics(new Constant(GoNilType.INSTANCE, "nil", getLocation()), original);
			return readerValue.lub(nilValue);
		}

	}
}
