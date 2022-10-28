package it.unive.golisa.cfg.runtime.os.type;

import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.ClassUnit;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Program;

/**
 * A File type.
 * 
 * @link https://pkg.go.dev/os#File
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class File extends GoStructType {

	/**
	 * Unique instance of File type.
	 */
	private static File INSTANCE;

	private File(CompilationUnit unit) {
		super("File", unit);
	}

	public static File getFileType(Program program) {
		if (INSTANCE == null) {
			ClassUnit fileUnit = new ClassUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, program, "File", false);
			INSTANCE = new File(fileUnit);
		}

		return INSTANCE;
	}

	@Override
	public String toString() {
		return "io.File";
	}

	@Override
	public boolean equals(Object other) {
		return this == other;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}
}
