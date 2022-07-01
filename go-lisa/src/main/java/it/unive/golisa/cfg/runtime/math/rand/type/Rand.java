package it.unive.golisa.cfg.runtime.math.rand.type;

import it.unive.golisa.cfg.runtime.math.rand.function.Read;
import it.unive.golisa.cfg.runtime.math.rand.method.ExpFloat64;
import it.unive.golisa.cfg.runtime.math.rand.method.Float32;
import it.unive.golisa.cfg.runtime.math.rand.method.Float64;
import it.unive.golisa.cfg.runtime.math.rand.method.Int;
import it.unive.golisa.cfg.runtime.math.rand.method.Int31;
import it.unive.golisa.cfg.runtime.math.rand.method.Int31n;
import it.unive.golisa.cfg.runtime.math.rand.method.Int63;
import it.unive.golisa.cfg.runtime.math.rand.method.Int63n;
import it.unive.golisa.cfg.runtime.math.rand.method.Intn;
import it.unive.golisa.cfg.runtime.math.rand.method.NormFloat64;
import it.unive.golisa.cfg.runtime.math.rand.method.Perm;
import it.unive.golisa.cfg.runtime.math.rand.method.Seed;
import it.unive.golisa.cfg.runtime.math.rand.method.Shuffle;
import it.unive.golisa.cfg.runtime.math.rand.method.UInt32;
import it.unive.golisa.cfg.runtime.math.rand.method.UInt64;
import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.SourceCodeLocation;

/**
 * A Random generator.
 * 
 * @link https://pkg.go.dev/math/rand#Rand
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class Rand extends GoStructType {

	/**
	 * Unique instance of Rand.
	 */
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

	public static void registerMethods() {
		SourceCodeLocation runtimeLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);

		Rand.INSTANCE.getUnit().addInstanceConstruct(new ExpFloat64(runtimeLocation, Rand.INSTANCE.getUnit()));
		Rand.INSTANCE.getUnit().addInstanceConstruct(new Float32(runtimeLocation, Rand.INSTANCE.getUnit()));
		Rand.INSTANCE.getUnit().addInstanceConstruct(new Float64(runtimeLocation, Rand.INSTANCE.getUnit()));
		Rand.INSTANCE.getUnit().addInstanceConstruct(new Int(runtimeLocation, Rand.INSTANCE.getUnit()));
		Rand.INSTANCE.getUnit().addInstanceConstruct(new Int31(runtimeLocation, Rand.INSTANCE.getUnit()));
		Rand.INSTANCE.getUnit().addInstanceConstruct(new Int31n(runtimeLocation, Rand.INSTANCE.getUnit()));
		Rand.INSTANCE.getUnit().addInstanceConstruct(new Int63(runtimeLocation, Rand.INSTANCE.getUnit()));
		Rand.INSTANCE.getUnit().addInstanceConstruct(new Int63n(runtimeLocation, Rand.INSTANCE.getUnit()));
		Rand.INSTANCE.getUnit().addInstanceConstruct(new Intn(runtimeLocation, Rand.INSTANCE.getUnit()));
		Rand.INSTANCE.getUnit().addInstanceConstruct(new NormFloat64(runtimeLocation, Rand.INSTANCE.getUnit()));
		Rand.INSTANCE.getUnit().addInstanceConstruct(new Perm(runtimeLocation, Rand.INSTANCE.getUnit()));
		Rand.INSTANCE.getUnit().addInstanceConstruct(new UInt32(runtimeLocation, Rand.INSTANCE.getUnit()));
		Rand.INSTANCE.getUnit().addInstanceConstruct(new UInt64(runtimeLocation, Rand.INSTANCE.getUnit()));
		Rand.INSTANCE.getUnit().addInstanceConstruct(new Read(runtimeLocation, Rand.INSTANCE.getUnit()));
		Rand.INSTANCE.getUnit().addInstanceConstruct(new Seed(runtimeLocation, Rand.INSTANCE.getUnit()));
		Rand.INSTANCE.getUnit().addInstanceConstruct(new Shuffle(runtimeLocation, Rand.INSTANCE.getUnit()));

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
