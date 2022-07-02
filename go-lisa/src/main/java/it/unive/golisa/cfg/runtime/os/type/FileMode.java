package it.unive.golisa.cfg.runtime.os.type;

import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.SourceCodeLocation;

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
	public static final FileMode INSTANCE = new FileMode();

	private FileMode() {
		this("FileMode", buildFileModeUnit());
	}

	private FileMode(String name, CompilationUnit unit) {
		super(name, unit);
	}

	private static CompilationUnit buildFileModeUnit() {
		SourceCodeLocation unknownLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
		CompilationUnit randUnit = new CompilationUnit(unknownLocation, "FileMode", false);
		return randUnit;
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
