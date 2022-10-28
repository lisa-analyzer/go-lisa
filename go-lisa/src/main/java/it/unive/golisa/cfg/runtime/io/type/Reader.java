package it.unive.golisa.cfg.runtime.io.type;

import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.ClassUnit;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Program;

/**
 * A I/O Reader type.
 * 
 * @link https://pkg.go.dev/io#Reader
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class Reader extends GoStructType {

	/**
	 * // * Unique instance of Reader type. //
	 */
	private static Reader INSTANCE;


	private Reader(CompilationUnit unit) {
		super("Reader", unit);
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

	public static Reader getReaderType(Program program) {
		if (INSTANCE == null) {
			ClassUnit readerUnit = new ClassUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, program, "Reader", false);
			INSTANCE = new Reader(readerUnit);
		}
		
		return INSTANCE;
	}
}
