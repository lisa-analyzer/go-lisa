package it.unive.golisa.cfg.runtime.os.file.function;

import it.unive.golisa.cfg.runtime.os.type.File;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoPointerType;
import it.unive.golisa.cfg.type.composite.GoTypesTuple;
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
import it.unive.lisa.program.cfg.statement.BinaryExpression;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.UnaryExpression;
import it.unive.lisa.symbolic.SymbolicExpression;

/**
 * func CreateTemp(dir, pattern string) (*File, error)
 * 
 * @link https://pkg.go.dev/os#CreateTemp
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class CreateTemp extends NativeCFG {

	public CreateTemp(CodeLocation location, CompilationUnit osUnit) {
		super(new CFGDescriptor(location, osUnit, false, "Create", GoTypesTuple.getTupleTypeOf(location, new GoPointerType(File.INSTANCE), GoErrorType.INSTANCE),
				new Parameter(location, "dir", GoStringType.INSTANCE),
				new Parameter(location, "pattern", GoStringType.INSTANCE)),
				CreateTempImpl.class);
	}

	public static class CreateTempImpl extends BinaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static CreateTempImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new CreateTempImpl(cfg, location, params[0], params[1]);
		}

		public CreateTempImpl(CFG cfg, CodeLocation location, Expression expr, Expression expr2) {
			super(cfg, location, "CreateTempImpl", GoTypesTuple.getTupleTypeOf(location, new GoPointerType(File.INSTANCE), GoErrorType.INSTANCE), expr, expr2);
		}

		@Override
		protected <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> binarySemantics(
				InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> state, SymbolicExpression left,
				SymbolicExpression right, StatementStore<A, H, V> expressions) throws SemanticException {
			return state.top();
		}

	}
}
