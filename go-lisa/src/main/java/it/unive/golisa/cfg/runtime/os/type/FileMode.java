package it.unive.golisa.cfg.runtime.os.type;

import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.ClassUnit;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Program;

/**
 * A FileMode type.
 * 
 * @link https://pkg.go.dev/os#FileMode
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class FileMode extends GoStructType {

	/**
	 * Unique instance of FileMode type.
	 */
	private static FileMode INSTANCE;

	private FileMode(CompilationUnit unit) {
		super("FileMode", unit);
	}

	/**
	 * Yields the {@link FileMode} type.
	 * 
	 * @param program the program to which this type belongs
	 * 
	 * @return the {@link FileMode} type
	 */
	public static FileMode getFileModeType(Program program) {
		if (INSTANCE == null) {
			ClassUnit fileModeUnit = new ClassUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, program, "FileMode",
					false);
			INSTANCE = new FileMode(fileModeUnit);
		}

		return INSTANCE;
	}

	@Override
	public String toString() {
		return "io.FileMode";
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
