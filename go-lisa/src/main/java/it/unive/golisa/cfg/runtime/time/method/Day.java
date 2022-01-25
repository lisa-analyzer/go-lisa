package it.unive.golisa.cfg.runtime.time.method;

import it.unive.golisa.cfg.runtime.time.type.Time;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
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
 * Day returns the day of the month specified by t. func (t Time) Day() int
 * 
 * @link https://pkg.go.dev/time#Time.Day
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class Day extends NativeCFG {

	public Day(CodeLocation location, CompilationUnit timeUnit) {
		super(new CFGDescriptor(location, timeUnit, false, timeUnit.getName() + ".Day",
				GoIntType.INSTANCE,
				new Parameter(location, "this", Time.INSTANCE)),
				DayImpl.class);
	}

	public static class DayImpl extends UnaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static DayImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new DayImpl(cfg, location, params[0]);
		}

		public DayImpl(CFG cfg, CodeLocation location, Expression e) {
			super(cfg, location, "DayImpl", it.unive.golisa.cfg.runtime.time.type.Month.INSTANCE, e);
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
