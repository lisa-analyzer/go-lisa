package it.unive.golisa.cfg.runtime.path.filepath.function;

import it.unive.golisa.cfg.type.GoStringType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
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

/**
 * 
 * func Dir(path string) string
 * 
 * @link https://pkg.go.dev/path/filepath#Dir
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class Dir extends NativeCFG {

	public Dir(CodeLocation location, CompilationUnit pathfileUnit) {
		super(new CFGDescriptor(location, pathfileUnit, false, "Dir", 
				GoStringType.INSTANCE,
				new Parameter(location, "pathfileUnit", GoStringType.INSTANCE)),
				DirImpl.class);
	}

	public static class DirImpl extends UnaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static DirImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new DirImpl(cfg, location, params[0]);
		}

		public DirImpl(CFG cfg, CodeLocation location, Expression e) {
			super(cfg, location, "DirImpl", GoStringType.INSTANCE, e);
		}

		@Override
		protected <A extends AbstractState<A, H, V>,
				H extends HeapDomain<H>,
				V extends ValueDomain<V>> AnalysisState<A, H, V> unarySemantics(
						InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> state,
						SymbolicExpression expr, StatementStore<A, H, V> expressions)
						throws SemanticException {
			return state.top();
		}
	}
}