package it.unive.golisa.cfg.runtime.io.type;

import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.SourceCodeLocation;

/**
 * A PipeWriter
 * 
 * @link https://pkg.go.dev/io#PipeWriter
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class PipeWriter extends GoStructType {

	public static final PipeWriter INSTANCE = new PipeWriter();

	private PipeWriter() {
		this("PipeWriter", buildPipeWriterUnit());
	}

	private PipeWriter(String name, CompilationUnit unit) {
		super(name, unit);
	}

	private static CompilationUnit buildPipeWriterUnit() {
		SourceCodeLocation unknownLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
		CompilationUnit randUnit = new CompilationUnit(unknownLocation, "PipeWriter", false);
		return randUnit;
	}

	public static void registerMethods() {
		SourceCodeLocation runtimeLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);

		// TODO: add methods

	}

	@Override
	public String toString() {
		return "io.PipeWriter";
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
