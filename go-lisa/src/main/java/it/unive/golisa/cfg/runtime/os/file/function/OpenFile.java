package it.unive.golisa.cfg.runtime.os.file.function;

import it.unive.golisa.cfg.runtime.os.type.File;
import it.unive.golisa.cfg.runtime.os.type.FileMode;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoPointerType;
import it.unive.golisa.cfg.type.composite.GoTypesTuple;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.lattices.ExpressionSet;
import it.unive.lisa.analysis.value.ValueDomain;
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

/**
 * func OpenFile(name string, flag int, perm FileMode) (*File, error)
 * 
 * @link https://pkg.go.dev/os#OpenFile
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class OpenFile extends NativeCFG {

	public OpenFile(CodeLocation location, CompilationUnit osUnit) {
		super(new CFGDescriptor(location, osUnit, false, "OpenFile", GoTypesTuple.getTupleTypeOf(location, new GoPointerType(File.INSTANCE), GoErrorType.INSTANCE),
				new Parameter(location, "name", GoStringType.INSTANCE),
				new Parameter(location, "flag", GoIntType.INSTANCE),
				new Parameter(location, "perm", FileMode.INSTANCE)),
				OpenFileImpl.class);
	}

	public static class OpenFileImpl extends NaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static OpenFileImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new OpenFileImpl(cfg, location, params);
		}

		public OpenFileImpl(CFG cfg, CodeLocation location, Expression... params) {
			super(cfg, location, "OpenFileImpl", GoTypesTuple.getTupleTypeOf(location, new GoPointerType(File.INSTANCE), GoErrorType.INSTANCE), params);
		}

		@Override
		public <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> expressionSemantics(
				InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> state,
				ExpressionSet<SymbolicExpression>[] params, StatementStore<A, H, V> expressions)
				throws SemanticException {
			return state.top();
		}

	}
}