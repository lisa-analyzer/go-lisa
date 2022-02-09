package it.unive.golisa.cfg.runtime.os.file.function;

import it.unive.golisa.cfg.runtime.os.type.File;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoPointerType;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUIntPrtType;
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
import it.unive.lisa.symbolic.value.PushAny;

/**
 * func NewFile(fd uintptr, name string) *File
 * 
 * @link https://pkg.go.dev/os#NewFile
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class NewFile extends NativeCFG {

	public NewFile(CodeLocation location, CompilationUnit osUnit) {
		super(new CFGDescriptor(location, osUnit, false, "NewFile", new GoPointerType(File.INSTANCE),
				new Parameter(location, "fd", GoStringType.INSTANCE),
				new Parameter(location, "name", GoUIntPrtType.INSTANCE)),
				NewFileImpl.class);
	}

	public static class NewFileImpl extends BinaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static NewFileImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new NewFileImpl(cfg, location, params[0], params[1]);
		}

		public NewFileImpl(CFG cfg, CodeLocation location, Expression expr, Expression expr2) {
			super(cfg, location, "NewFile", new GoPointerType(File.INSTANCE), expr, expr2);
		}

		@Override
		protected <A extends AbstractState<A, H, V, T>, H extends HeapDomain<H>, V extends ValueDomain<V>, T extends TypeDomain<T>> AnalysisState<A, H, V, T> binarySemantics(
				InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
				SymbolicExpression left, SymbolicExpression right, StatementStore<A, H, V, T> expressions)
				throws SemanticException {
			return state.smallStepSemantics(new PushAny(new GoPointerType(File.INSTANCE), getLocation()), original);
		}
	}
}
