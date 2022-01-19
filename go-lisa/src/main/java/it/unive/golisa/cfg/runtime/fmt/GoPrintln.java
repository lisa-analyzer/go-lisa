package it.unive.golisa.cfg.runtime.fmt;

import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.heap.HeapDomain;
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
import it.unive.lisa.type.Untyped;

public class GoPrintln extends NativeCFG {

	public GoPrintln(CodeLocation location, CompilationUnit fmtUnit) {
		super(new CFGDescriptor(location, fmtUnit, false, fmtUnit.getName() + ".Println", Untyped.INSTANCE,
				new Parameter(location, "this", Untyped.INSTANCE)),
				Println.class);
	}

	public static class Println extends UnaryExpression implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static Println build(CFG cfg, CodeLocation location, Expression... params) {
			return new Println(cfg, location, params[0]);
		}

		public Println(CFG cfg, CodeLocation location, Expression arg) {
			super(cfg, location, "Println", Untyped.INSTANCE, arg);
		}

		@Override
		protected <A extends AbstractState<A, H, V>,
				H extends HeapDomain<H>,
				V extends ValueDomain<V>> AnalysisState<A, H, V> unarySemantics(
						InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> state,
						SymbolicExpression expr)
						throws SemanticException {
			return state.smallStepSemantics(expr, original);
		}
	}
}
