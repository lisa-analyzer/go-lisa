package it.unive.golisa.cfg.runtime.os.file.function;

import it.unive.golisa.cfg.runtime.os.type.File;
import it.unive.golisa.cfg.runtime.os.type.FileMode;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoPointerType;
import it.unive.golisa.cfg.type.composite.GoTupleType;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.lattices.ExpressionSet;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.CodeUnit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.NativeCFG;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.NaryExpression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;

/**
 * func OpenFile(name string, flag int, perm FileMode) (*File, error).
 * 
 * @link https://pkg.go.dev/os#OpenFile
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class OpenFile extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param osUnit   the unit to which this native cfg belongs to
	 */
	public OpenFile(CodeLocation location, CodeUnit osUnit) {
		super(new CodeMemberDescriptor(location, osUnit, false, "OpenFile",
				GoTupleType.getTupleTypeOf(location, GoPointerType.lookup(File.getFileType(osUnit.getProgram())),
						GoErrorType.INSTANCE),
				new Parameter(location, "name", GoStringType.INSTANCE),
				new Parameter(location, "flag", GoIntType.INSTANCE),
				new Parameter(location, "perm", FileMode.getFileModeType(osUnit.getProgram()))),
				OpenFileImpl.class);
	}

	/**
	 * The OpenFile implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class OpenFileImpl extends NaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
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
		public static OpenFileImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new OpenFileImpl(cfg, location, params);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param params   the parameters
		 */
		public OpenFileImpl(CFG cfg, CodeLocation location, Expression... params) {
			super(cfg, location, "OpenFileImpl",
					GoTupleType.getTupleTypeOf(location, GoPointerType.lookup(File.getFileType(null)),
							GoErrorType.INSTANCE),
					params);
		}

		@Override
		public <A extends AbstractState<A>> AnalysisState<A> forwardSemanticsAux(
				InterproceduralAnalysis<A> interprocedural, AnalysisState<A> state,
				ExpressionSet[] params, StatementStore<A> expressions)
				throws SemanticException {
			return state.top();
		}

		@Override
		protected int compareSameClassAndParams(Statement o) {
			return 0; // nothing else to compare
		}
	}
}
