package it.unive.golisa.cfg.runtime.tendermint.core.abci.method;

import it.unive.golisa.cfg.runtime.tendermint.core.abci.type.BaseApplication;
import it.unive.golisa.cfg.runtime.tendermint.core.abci.type.RequestEndBlock;
import it.unive.golisa.cfg.runtime.tendermint.core.abci.type.ResponseEndBlock;
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
 * func (BaseApplication) EndBlock(req RequestEndBlock) ResponseEndBlock
 * https://pkg.go.dev/github.com/tendermint/tendermint/abci/types#BaseApplication.EndBlock
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class EndBlock extends NativeCFG {

	public EndBlock(CodeLocation location, CompilationUnit abciUnit) {
		super(new CFGDescriptor(location, abciUnit, true, "EndBlock", ResponseEndBlock.INSTANCE,
				new Parameter(location, "this", BaseApplication.INSTANCE),
				new Parameter(location, "req", RequestEndBlock.INSTANCE)),
				EndBlockImpl.class);
	}

	public static class EndBlockImpl extends BinaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static EndBlockImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new EndBlockImpl(cfg, location, params[0], params[1]);
		}

		public EndBlockImpl(CFG cfg, CodeLocation location, Expression expr, Expression expr2) {
			super(cfg, location, "EndBlockImpl", ResponseEndBlock.INSTANCE, expr, expr2);
		}

		@Override
		protected <A extends AbstractState<A, H, V, T>,
				H extends HeapDomain<H>,
				V extends ValueDomain<V>,
				T extends TypeDomain<T>> AnalysisState<A, H, V, T> binarySemantics(
						InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
						SymbolicExpression left, SymbolicExpression right, StatementStore<A, H, V, T> expressions)
						throws SemanticException {
			return state.smallStepSemantics(new PushAny(ResponseEndBlock.INSTANCE, getLocation()), original);
		}
	}
}
