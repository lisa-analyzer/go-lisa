package it.unive.golisa.cfg.runtime.io.ioutil.function;

import it.unive.golisa.cfg.runtime.io.type.Reader;
import it.unive.golisa.cfg.type.GoNilType;
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
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.PushAny;

/**
 * func NopCloser(r io.Reader) io.ReadCloser
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class NopCloser extends NativeCFG {

	public NopCloser(CodeLocation location, CompilationUnit ioUnit) {
		super(new CFGDescriptor(location, ioUnit, false, "NopCloser", Reader.INSTANCE,
				new Parameter(location, "r", Reader.INSTANCE)),
				NopCloserImpl.class);
	}

	public static class NopCloserImpl extends UnaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static NopCloserImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new NopCloserImpl(cfg, location, params[0]);
		}

		public NopCloserImpl(CFG cfg, CodeLocation location, Expression expr) {
			super(cfg, location, "NopCloserImpl", Reader.INSTANCE, expr);
		}

		@Override
		protected <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> unarySemantics(
				InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> state, SymbolicExpression expr,
				StatementStore<A, H, V> expressions) throws SemanticException {
			AnalysisState<A, H, V> readerValue =  state.smallStepSemantics(new PushAny(Caches.types().mkSingletonSet(Reader.INSTANCE), getLocation()), original);
			AnalysisState<A, H, V> nilValue = state.smallStepSemantics(new Constant(GoNilType.INSTANCE, "nil", getLocation()), original);
			return readerValue.lub(nilValue);
		}
	}
}
