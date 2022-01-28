package it.unive.golisa.cfg.runtime.io.type;

import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.SourceCodeLocation;

/**
 * A I/O Reader
 * 
 * @link https://pkg.go.dev/io#Reader
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class Reader extends GoStructType {

	public static final Reader INSTANCE = new Reader();

	private Reader() {
		this("Reader", buildReaderUnit());
	}

	private Reader(String name, CompilationUnit unit) {
		super(name, unit);
	}

	private static CompilationUnit buildReaderUnit() {
		SourceCodeLocation unknownLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
		CompilationUnit randUnit = new CompilationUnit(unknownLocation, "Reader", false);
		return randUnit;
	}

	public static void registerMethods() {
		SourceCodeLocation runtimeLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);

		//TODO: add methods
		
	}

	@Override
	public String toString() {
		return "io.Reader";
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
