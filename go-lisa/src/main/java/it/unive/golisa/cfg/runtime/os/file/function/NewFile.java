package it.unive.golisa.cfg.runtime.os.file.function;

import it.unive.golisa.cfg.runtime.os.type.File;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoPointerType;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUIntPrtType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.CodeUnit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.NativeCFG;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.BinaryExpression;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.PushAny;

/**
 * func NewFile(fd uintptr, name string) *File.
 * 
 * @link https://pkg.go.dev/os#NewFile
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class NewFile extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param osUnit   the unit to which this native cfg belongs to
	 */
	public NewFile(CodeLocation location, CodeUnit osUnit) {
		super(new CodeMemberDescriptor(location, osUnit, false, "NewFile",
				GoPointerType.lookup(File.getFileType(osUnit.getProgram())),
				new Parameter(location, "fd", GoStringType.INSTANCE),
				new Parameter(location, "name", GoUIntPrtType.INSTANCE)),
				NewFileImpl.class);
	}

	/**
	 * The NewFile implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class NewFileImpl extends BinaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		@Override
		protected int compareSameClassAndParams(Statement o) {
			return 0; // nothing else to compare
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param params   the parameters
		 * 
		 * @return the pluggable statement
		 */
		public static NewFileImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new NewFileImpl(cfg, location, params[0], params[1]);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param left     the left-hand side of this pluggable statement
		 * @param right    the right-hand side of this pluggable statement
		 */
		public NewFileImpl(CFG cfg, CodeLocation location, Expression left, Expression right) {
			super(cfg, location, "NewFile", GoPointerType.lookup(File.getFileType(null)), left, right);
		}

		@Override
		public <A extends AbstractState<A>> AnalysisState<A> fwdBinarySemantics(
				InterproceduralAnalysis<A> interprocedural, AnalysisState<A> state,
				SymbolicExpression left, SymbolicExpression right, StatementStore<A> expressions)
				throws SemanticException {
			return state.smallStepSemantics(new PushAny(GoPointerType.lookup(File.getFileType(null)), getLocation()),
					original);
		}
	}
}
