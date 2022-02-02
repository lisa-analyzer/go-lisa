package it.unive.golisa.cfg.runtime.os.function;

import it.unive.golisa.cfg.type.GoBoolType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
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
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.UnaryExpression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.PushAny;

/**
 * 
 * func IsNotExist(err error) bool
 * 
 * @link https://pkg.go.dev/os#IsNotExist
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class IsNotExist extends NativeCFG {

	public IsNotExist(CodeLocation location, CompilationUnit osUnit) {
		super(new CFGDescriptor(location, osUnit, false, "IsNotExist", 
				GoBoolType.INSTANCE,
				new Parameter(location, "err", GoErrorType.INSTANCE)),
				IsNotExistImpl.class);
	}

	public static class IsNotExistImpl extends UnaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static IsNotExistImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new IsNotExistImpl(cfg, location, params[0]);
		}

		public IsNotExistImpl(CFG cfg, CodeLocation location, Expression e) {
			super(cfg, location, "IsNotExistImpl", GoBoolType.INSTANCE, e);
		}

		@Override
		protected <A extends AbstractState<A, H, V>,
				H extends HeapDomain<H>,
				V extends ValueDomain<V>> AnalysisState<A, H, V> unarySemantics(
						InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> state,
						SymbolicExpression expr, StatementStore<A, H, V> expressions)
						throws SemanticException {
			return state.smallStepSemantics(new PushAny(Caches.types().mkSingletonSet(GoBoolType.INSTANCE), getLocation()), original);
		}
	}
}