package it.unive.golisa.cfg.runtime.math.big.type;

import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.ClassUnit;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Program;

/**
 * A big integer type.
 * 
 * @link https://pkg.go.dev/math/big#Int
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class Int extends GoStructType {

	/**
	 * Unique instance of a big integer type.
	 */
	private static Int INSTANCE;

	private Int(CompilationUnit unit) {
		super("big.Int", unit);
	}

	/**
	 * Yields the {@link Int} type.
	 * 
	 * @param program the program to which this type belongs
	 * 
	 * @return the {@link Int} type
	 */
	public static Int getIntType(Program program) {
		if (INSTANCE == null) {
			ClassUnit intUnit = new ClassUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, program, "Int", false);
			INSTANCE = new Int(intUnit);
		}

		return INSTANCE;
	}

	@Override
	public String toString() {
		return "big.Int";
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
