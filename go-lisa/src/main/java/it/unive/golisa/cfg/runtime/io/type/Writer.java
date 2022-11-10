package it.unive.golisa.cfg.runtime.io.type;

import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.ClassUnit;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Program;

/**
 * A I/O Writer type.
 * 
 * @link https://pkg.go.dev/io#Writer
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class Writer extends GoStructType {

	/**
	 * Unique instance of Writer type.
	 */
	private static Writer INSTANCE;

	private Writer(CompilationUnit unit) {
		super("Writer", unit);
	}

	/**
	 * Yields the {@link Writer} type.
	 * 
	 * @param program the program to which this type belongs
	 * 
	 * @return the {@link Writer} type
	 */
	public static Writer getWriterType(Program program) {
		if (INSTANCE == null) {
			ClassUnit randUnit = new ClassUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, program, "Writer", false);
			INSTANCE = new Writer(randUnit);
		}

		return INSTANCE;
	}

	@Override
	public String toString() {
		return "io.Writer";
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
