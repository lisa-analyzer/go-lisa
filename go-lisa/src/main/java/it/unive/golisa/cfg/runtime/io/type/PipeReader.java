package it.unive.golisa.cfg.runtime.io.type;

import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.SourceCodeLocation;

/**
 * A PipeReader type.
 * 
 * @link https://pkg.go.dev/io#PipeReader
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class PipeReader extends GoStructType {

	/**
	 * Unique instance of PipeReader.
	 */
	public static final PipeReader INSTANCE = new PipeReader();

	private PipeReader() {
		this("PipeReader", buildPipeReaderUnit());
	}

	private PipeReader(String name, CompilationUnit unit) {
		super(name, unit);
	}

	private static CompilationUnit buildPipeReaderUnit() {
		SourceCodeLocation unknownLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
		CompilationUnit randUnit = new CompilationUnit(unknownLocation, "PipeReader", false);
		return randUnit;
	}

	/**
	 * Registers methods of PipeReader.
	 */
	public static void registerMethods() {
		SourceCodeLocation runtimeLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);

		// TODO: add methods

	}

	@Override
	public String toString() {
		return "io.PipeReader";
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
