package it.unive.golisa.cfg.runtime.tendermint.core.abci.method;

import it.unive.golisa.cfg.runtime.tendermint.core.abci.type.BaseApplication;
import it.unive.golisa.cfg.runtime.tendermint.core.abci.type.RequestDeliverTx;
import it.unive.golisa.cfg.runtime.tendermint.core.abci.type.ResponseDeliverTx;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.caches.Caches;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CFGDescriptor;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.NativeCFG;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.BinaryExpression;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.PushAny;

/**
 * func (BaseApplication) DeliverTx(req RequestDeliverTx) ResponseDeliverTx
 * 
 * https://pkg.go.dev/github.com/tendermint/tendermint/abci/types#BaseApplication.DeliverTx
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class DeliverTx extends NativeCFG {

	public DeliverTx(CodeLocation location, CompilationUnit abciUnit) {
		super(new CFGDescriptor(location, abciUnit, true, "DeliverTx", ResponseDeliverTx.INSTANCE,
				new Parameter(location, "this", BaseApplication.INSTANCE),
				new Parameter(location, "req", RequestDeliverTx.INSTANCE)),
				DeliverTxImpl.class);
	}

	public static class DeliverTxImpl extends BinaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static DeliverTxImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new DeliverTxImpl(cfg, location, params[0], params[1]);
		}

		public DeliverTxImpl(CFG cfg, CodeLocation location, Expression expr, Expression expr2) {
			super(cfg, location, "DeliverTxImpl", ResponseDeliverTx.INSTANCE, expr, expr2);
		}

		@Override
		protected <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> binarySemantics(
				InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> state, SymbolicExpression left,
				SymbolicExpression right, StatementStore<A, H, V> expressions) throws SemanticException {
			return  state.smallStepSemantics(new PushAny(Caches.types().mkSingletonSet(ResponseDeliverTx.INSTANCE), getLocation()), original);

		}

	}
}
