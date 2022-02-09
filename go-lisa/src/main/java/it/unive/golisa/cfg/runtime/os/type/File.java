package it.unive.golisa.cfg.runtime.os.type;

import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.SourceCodeLocation;

/**
 * A File
 * 
 * @link https://pkg.go.dev/os#File
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class File extends GoStructType {

	public static final File INSTANCE = new File();

	private File() {
		this("File", buildFileUnit());
	}

	private File(String name, CompilationUnit unit) {
		super(name, unit);
	}

	private static CompilationUnit buildFileUnit() {
		SourceCodeLocation unknownLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
		CompilationUnit randUnit = new CompilationUnit(unknownLocation, "File", false);
		return randUnit;
	}

	public static void registerMethods() {
		// TODO
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
