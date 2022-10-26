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

//	/**
//	 * Unique instance of Writer type.
//	 */
//	public static final Writer INSTANCE = new Writer();

	
	private Writer(CompilationUnit unit) {
		super("Writer", unit);
	}

	public static Writer getWriterType(Program program) {
		ClassUnit randUnit = new ClassUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, program, "Writer", false);
		return new Writer(randUnit);
	}

	/**
//	 * Registers methods of Writer.
//	 */
//	public static void registerMethods() {
//		SourceCodeLocation runtimeLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
//
//		// TODO: add methods
//
//	}

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
