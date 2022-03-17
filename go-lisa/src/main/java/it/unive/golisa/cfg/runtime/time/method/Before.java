package it.unive.golisa.cfg.runtime.time.method;

import it.unive.golisa.cfg.runtime.time.type.Duration;
import it.unive.golisa.cfg.runtime.time.type.Time;
import it.unive.golisa.cfg.type.GoBoolType;
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

public class Before extends NativeCFG {

	public Before(CodeLocation location, CompilationUnit timeUnit) {
		super(new CFGDescriptor(location, timeUnit, true, "Before",
				GoBoolType.INSTANCE,
				new Parameter(location, "this", Time.INSTANCE),
				new Parameter(location, "u", Time.INSTANCE)),
				BeforeImpl.class);
	}

	public static class BeforeImpl extends BinaryExpression
	implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static BeforeImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new BeforeImpl(cfg, location, params[0], params[1]);
		}

		public BeforeImpl(CFG cfg, CodeLocation location, Expression e1, Expression e2) {
			super(cfg, location, "BeforeImpl", Duration.INSTANCE, e1, e2);
		}

		@Override
		protected <A extends AbstractState<A, H, V, T>, H extends HeapDomain<H>, V extends ValueDomain<V>, T extends TypeDomain<T>> AnalysisState<A, H, V, T> binarySemantics(
				InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
				SymbolicExpression left, SymbolicExpression right, StatementStore<A, H, V, T> expressions)
						throws SemanticException {
			return state.smallStepSemantics(
					new PushAny(GoBoolType.INSTANCE, getLocation()), original);
		}
	}
}
