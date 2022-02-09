package it.unive.golisa.cfg.runtime.os.function;

import it.unive.golisa.cfg.runtime.io.type.Reader;
import it.unive.golisa.cfg.type.GoNilType;
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
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.UnaryExpression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.PushAny;

/**
 * 
 * func RemoveAll(path string) error
 * 
 * @link https://pkg.go.dev/os#RemoveAll
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class RemoveAll extends NativeCFG {

	public RemoveAll(CodeLocation location, CompilationUnit osUnit) {
		super(new CFGDescriptor(location, osUnit, false, "RemoveAll", 
				GoErrorType.INSTANCE,
				new Parameter(location, "path", GoStringType.INSTANCE)),
				RemoveAllImpl.class);
	}

	public static class RemoveAllImpl extends UnaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static RemoveAllImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new RemoveAllImpl(cfg, location, params[0]);
		}

		public RemoveAllImpl(CFG cfg, CodeLocation location, Expression e) {
			super(cfg, location, "RemoveAllImpl", GoErrorType.INSTANCE, e);
		}

		@Override
		protected <A extends AbstractState<A, H, V, T>, H extends HeapDomain<H>, V extends ValueDomain<V>, T extends TypeDomain<T>> AnalysisState<A, H, V, T> unarySemantics(
				InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
				SymbolicExpression expr, StatementStore<A, H, V, T> expressions) throws SemanticException {
			AnalysisState<A, H, V, T> readerValue =  state.smallStepSemantics(new PushAny(Reader.INSTANCE, getLocation()), original);
			AnalysisState<A, H, V, T> nilValue = state.smallStepSemantics(new Constant(GoNilType.INSTANCE, "nil", getLocation()), original);
			return readerValue.lub(nilValue);
		}
	}
}