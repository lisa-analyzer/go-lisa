package it.unive.golisa.cfg.runtime.url;

import it.unive.golisa.cfg.type.GoStringType;
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
import it.unive.lisa.program.cfg.statement.call.UnaryNativeCall;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.PushAny;

public class UrlPathEscape extends NativeCFG {

	public UrlPathEscape(CodeLocation location, CompilationUnit urlUnit) {
		super(new CFGDescriptor(location, urlUnit, false, "PathEscape", GoStringType.INSTANCE,
				new Parameter(location, "this", GoStringType.INSTANCE)),
				PathEscape.class);
	}
	
	public static class PathEscape extends UnaryNativeCall implements PluggableStatement {
		
		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}
		
		public static PathEscape build(CFG cfg, CodeLocation location, Expression... params) {
			return new PathEscape(cfg, location, params[0]);
		}
		
		public PathEscape(CFG cfg, CodeLocation location, Expression exp) {
			super(cfg, location, "PathEscape", GoStringType.INSTANCE, exp);
		}

		@Override
		protected <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> unarySemantics(
				AnalysisState<A, H, V> entryState, InterproceduralAnalysis<A, H, V> interprocedural,
				AnalysisState<A, H, V> exprState, SymbolicExpression expr) throws SemanticException {
			// TODO to implement path escape method from url package
			return entryState.smallStepSemantics(new PushAny(getRuntimeTypes(), getLocation()), original);
		}
	}
}
