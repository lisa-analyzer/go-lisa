package it.unive.golisa.cfg.runtime.io.type;

import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.ClassUnit;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Program;

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
	private static PipeReader INSTANCE;

	private PipeReader(CompilationUnit unit) {
		super("PipeReader", unit);
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

	/**
	 * Yields the {@link PipeReader} type.
	 * 
	 * @param program the program to which this type belongs
	 * 
	 * @return the {@link PipeReader} type
	 */
	public static PipeReader getPipeReaderType(Program program) {
		if (INSTANCE == null) {
			ClassUnit pipeReaderUnit = new ClassUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, program, "PipeReader",
					false);
			INSTANCE = new PipeReader(pipeReaderUnit);
		}

		return INSTANCE;
	}
}
