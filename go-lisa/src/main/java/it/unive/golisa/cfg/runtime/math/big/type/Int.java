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
//	public static final Int INSTANCE = new Int();

	private Int(CompilationUnit unit) {
		super("big.Int", unit);
	}

	public static Int getIntType(Program program) {
		ClassUnit intUnit = new ClassUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, program, "Int", false);
		return new Int(intUnit);
	}

	/**
	 * Registers the methods of a {@link Int} type.
	 */
//	public static void registerMethods() {
//		SourceCodeLocation runtimeLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
//
//		// TODO: add methods
//
//	}

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
