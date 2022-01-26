package it.unive.golisa.cfg.runtime.math.rand.type;

import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.SourceCodeLocation;

/**
 * A Random generator
 * 
 * @link https://pkg.go.dev/math/rand#Rand Rand
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class Rand extends GoStructType {

	public static final Rand INSTANCE = new Rand();

	private Rand() {
		this("Rand", buildRandUnit());
	}

	private Rand(String name, CompilationUnit unit) {
		super(name, unit);
	}

	private static CompilationUnit buildRandUnit() {
		SourceCodeLocation unknownLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
		CompilationUnit randUnit = new CompilationUnit(unknownLocation, "Rand", false);
		return randUnit;
	}

	@Override
	public String toString() {
		return "rand.Rand";
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
