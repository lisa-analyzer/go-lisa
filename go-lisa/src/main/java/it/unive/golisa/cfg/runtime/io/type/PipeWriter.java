package it.unive.golisa.cfg.runtime.io.type;

import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.ClassUnit;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Program;

/**
 * A PipeWriter type.
 * 
 * @link https://pkg.go.dev/io#PipeWriter
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class PipeWriter extends GoStructType {

	/**
	 * Unique instance of {@link PipeWriter} type.
	 */
	private static PipeWriter INSTANCE;

	private PipeWriter(String name, CompilationUnit unit) {
		super(name, unit);
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

	public static PipeWriter getPiperWriter(Program program) {
		if (INSTANCE == null) { 
			ClassUnit pipeWriterUnit = new ClassUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, program, "PipeWriter",
					false);
			INSTANCE = new PipeWriter("PipeWriter", pipeWriterUnit);
		}
		
		return INSTANCE;
	}
}
