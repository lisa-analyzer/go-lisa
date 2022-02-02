package it.unive.golisa.cfg.runtime.io.ioutil.function;

import it.unive.golisa.cfg.runtime.io.type.Reader;
import it.unive.golisa.cfg.type.GoNilType;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.lattices.ExpressionSet;
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
import it.unive.lisa.program.cfg.statement.NaryExpression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.type.common.UInt8;

/**
 * func WriteFile(filename string, data []byte, perm fs.FileMode) error
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class WriteFile extends NativeCFG {

	public WriteFile(CodeLocation location, CompilationUnit ioUnit) {
		super(new CFGDescriptor(location, ioUnit, false, "WriteFile", GoErrorType.INSTANCE,
				new Parameter(location, "filename", GoStringType.INSTANCE),
				new Parameter(location, "data", new GoSliceType(UInt8.INSTANCE)),
				new Parameter(location, "perm", Reader.INSTANCE)),
				WriteFileImpl.class);
	}

	public static class WriteFileImpl extends NaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static WriteFileImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new WriteFileImpl(cfg, location, params);
		}

		public WriteFileImpl(CFG cfg, CodeLocation location, Expression... params) {
			super(cfg, location, "WriteFileImpl", GoErrorType.INSTANCE, params);
		}

		@Override
		public <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> expressionSemantics(
				InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> state,
				ExpressionSet<SymbolicExpression>[] params, StatementStore<A, H, V> expressions)
				throws SemanticException {
			AnalysisState<A, H, V> errorValue = state.smallStepSemantics(new PushAny(Caches.types().mkSingletonSet(GoErrorType.INSTANCE), getLocation()), original);
			AnalysisState<A, H, V> nilValue = state.smallStepSemantics(new Constant(GoNilType.INSTANCE, "nil", getLocation()), original);
			return errorValue.lub(nilValue);
		}
	}
}
