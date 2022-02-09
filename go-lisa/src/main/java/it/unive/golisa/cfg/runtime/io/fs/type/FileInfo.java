package it.unive.golisa.cfg.runtime.io.fs.type;

import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.SourceCodeLocation;

/**
 * A File Info
 * 
 * @link https://pkg.go.dev/io/fs#FileInfo
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class FileInfo extends GoStructType {

	public static final FileInfo INSTANCE = new FileInfo();

	private FileInfo() {
		this("FileInfo", buildFileUnit());
	}

	private FileInfo(String name, CompilationUnit unit) {
		super(name, unit);
	}

	private static CompilationUnit buildFileUnit() {
		SourceCodeLocation unknownLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
		CompilationUnit randUnit = new CompilationUnit(unknownLocation, "FileInfo", false);
		return randUnit;
	}

	public static void registerMethods() {
		// TODO
	}

	@Override
	public String toString() {
		return "fs.FileInfo";
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
