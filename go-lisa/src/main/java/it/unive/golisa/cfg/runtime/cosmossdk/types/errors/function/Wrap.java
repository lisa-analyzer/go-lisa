package it.unive.golisa.cfg.runtime.cosmossdk.types.errors.function;

import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
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
 * func Wrap(err error, description string) error
 * 
 * @link https://pkg.go.dev/github.com/cosmos/cosmos-sdk/errors#Wrap
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class Wrap extends NativeCFG {

	public Wrap(CodeLocation location, CompilationUnit errorsUnit) {
		super(new CFGDescriptor(location, errorsUnit, false, "Wrap", GoErrorType.INSTANCE,
				new Parameter(location, "err", GoErrorType.INSTANCE),
				new Parameter(location, "description", GoStringType.INSTANCE)),
				WrapImpl.class);
	}

	public static class WrapImpl extends BinaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static WrapImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new WrapImpl(cfg, location, params[0], params[1]);
		}

		public WrapImpl(CFG cfg, CodeLocation location, Expression expr1, Expression expr2) {
			super(cfg, location, "WrapImpl", GoErrorType.INSTANCE, expr1, expr2);
		}

		@Override
		protected <A extends AbstractState<A, H, V, T>,
				H extends HeapDomain<H>,
				V extends ValueDomain<V>,
				T extends TypeDomain<T>> AnalysisState<A, H, V, T> binarySemantics(
						InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
						SymbolicExpression left, SymbolicExpression right, StatementStore<A, H, V, T> expressions)
						throws SemanticException {
			return state.smallStepSemantics(new PushAny(GoErrorType.INSTANCE, getLocation()), original);
		}

	}
}