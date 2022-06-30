package it.unive.golisa.cfg.runtime.os.function;

import it.unive.golisa.cfg.runtime.io.type.Reader;
import it.unive.golisa.cfg.runtime.os.type.FileMode;
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
import it.unive.lisa.program.cfg.statement.BinaryExpression;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.PushAny;

/**
 * func MkdirAll(path string, perm FileMode) error.
 * 
 * @link https://pkg.go.dev/os#MkdirAll
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class MkdirAll extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param osUnit   the unit to which this native cfg belongs to
	 */
	public MkdirAll(CodeLocation location, CompilationUnit osUnit) {
		super(new CFGDescriptor(location, osUnit, false, "MkdirAll",
				GoErrorType.INSTANCE,
				new Parameter(location, "path", GoStringType.INSTANCE),
				new Parameter(location, "perm", FileMode.INSTANCE)),
				MkdirAllImpl.class);
	}

	public static class MkdirAllImpl extends BinaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static MkdirAllImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new MkdirAllImpl(cfg, location, params[0], params[1]);
		}

		public MkdirAllImpl(CFG cfg, CodeLocation location, Expression expr, Expression expr2) {
			super(cfg, location, "MkdirAllImpl", GoErrorType.INSTANCE, expr, expr2);
		}

		@Override
		protected <A extends AbstractState<A, H, V, T>,
				H extends HeapDomain<H>,
				V extends ValueDomain<V>,
				T extends TypeDomain<T>> AnalysisState<A, H, V, T> binarySemantics(
						InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
						SymbolicExpression left, SymbolicExpression right, StatementStore<A, H, V, T> expressions)
						throws SemanticException {
			AnalysisState<A, H, V,
					T> readerValue = state.smallStepSemantics(new PushAny(Reader.INSTANCE, getLocation()), original);
			AnalysisState<A, H, V, T> nilValue = state
					.smallStepSemantics(new Constant(GoNilType.INSTANCE, "nil", getLocation()), original);
			return readerValue.lub(nilValue);
		}
	}
}