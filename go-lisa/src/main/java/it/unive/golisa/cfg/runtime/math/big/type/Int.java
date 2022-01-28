package it.unive.golisa.cfg.runtime.math.big.type;

import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.SourceCodeLocation;

/**
 * A Big integer
 * 
 * @link https://pkg.go.dev/math/big#Int
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class Int extends GoStructType {

	public static final Int INSTANCE = new Int();

	private Int() {
		this("Int", buildIntUnit());
	}

	private Int(String name, CompilationUnit unit) {
		super(name, unit);
	}

	private static CompilationUnit buildIntUnit() {
		SourceCodeLocation unknownLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
		CompilationUnit randUnit = new CompilationUnit(unknownLocation, "Int", false);
		return randUnit;
	}

	public static void registerMethods() {
		SourceCodeLocation runtimeLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);

		//TODO: add methods
		
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
