package it.unive.golisa.cfg.runtime.tendermint.core.abci.method;

import it.unive.golisa.cfg.runtime.tendermint.core.abci.type.BaseApplication;
import it.unive.golisa.cfg.runtime.tendermint.core.abci.type.RequestBeginBlock;
import it.unive.golisa.cfg.runtime.tendermint.core.abci.type.ResponseBeginBlock;
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
import it.unive.lisa.program.cfg.statement.BinaryExpression;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.PushAny;

/**
 * func (BaseApplication) BeginBlock(req RequestBeginBlock) ResponseBeginBlock
 * 
 * https://pkg.go.dev/github.com/tendermint/tendermint/abci/types#BaseApplication.BeginBlock
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class BeginBlock extends NativeCFG {

	public BeginBlock(CodeLocation location, CompilationUnit shimUnit) {
		super(new CFGDescriptor(location, shimUnit, true, "BeginBlock", ResponseBeginBlock.INSTANCE,
				new Parameter(location, "this", BaseApplication.INSTANCE),
				new Parameter(location, "req", RequestBeginBlock.INSTANCE)),
				BeginBlockImpl.class);
	}

	public static class BeginBlockImpl extends BinaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static BeginBlockImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new BeginBlockImpl(cfg, location, params[0], params[1]);
		}

		public BeginBlockImpl(CFG cfg, CodeLocation location, Expression expr, Expression expr2) {
			super(cfg, location, "BeginBlockImpl", ResponseBeginBlock.INSTANCE, expr, expr2);
		}
		@Override
		protected <A extends AbstractState<A, H, V, T>, H extends HeapDomain<H>, V extends ValueDomain<V>, T extends TypeDomain<T>> AnalysisState<A, H, V, T> binarySemantics(
				InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
				SymbolicExpression left, SymbolicExpression right, StatementStore<A, H, V, T> expressions)
				throws SemanticException {
			return state.smallStepSemantics(new PushAny(ResponseBeginBlock.INSTANCE, getLocation()), original);
		}
	}
}
