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
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.NativeCFG;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.BinaryExpression;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.PushAny;

/**
 * func (BaseApplication) BeginBlock(req RequestBeginBlock) ResponseBeginBlock.
 * 
 * @link https://pkg.go.dev/github.com/tendermint/tendermint/abci/types#BaseApplication.BeginBlock
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class BeginBlock extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param abciUnit the unit to which this native cfg belongs to
	 */
	public BeginBlock(CodeLocation location, CompilationUnit abciUnit) {
		super(new CodeMemberDescriptor(location, abciUnit, true, "BeginBlock",
				ResponseBeginBlock.getRequestBeginBlockType(abciUnit.getProgram()),
				new Parameter(location, "this", BaseApplication.etBaseApplicationType(abciUnit.getProgram())),
				new Parameter(location, "req", RequestBeginBlock.getRequestBeginBlockType(abciUnit.getProgram()))),
				BeginBlockImpl.class);
	}

	/**
	 * The {@link BeginBlock} implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class BeginBlockImpl extends BinaryExpression
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
		public static BeginBlockImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new BeginBlockImpl(cfg, location, params[0], params[1]);
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
		public BeginBlockImpl(CFG cfg, CodeLocation location, Expression left, Expression right) {
			super(cfg, location, "BeginBlockImpl", ResponseBeginBlock.getRequestBeginBlockType(null), left, right);
		}

		@Override
		public <A extends AbstractState<A, H, V, T>,
				H extends HeapDomain<H>,
				V extends ValueDomain<V>,
				T extends TypeDomain<T>> AnalysisState<A, H, V, T> binarySemantics(
						InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
						SymbolicExpression left, SymbolicExpression right, StatementStore<A, H, V, T> expressions)
						throws SemanticException {
			return state.smallStepSemantics(
					new PushAny(ResponseBeginBlock.getRequestBeginBlockType(null), getLocation()),
					original);
		}
	}
}
