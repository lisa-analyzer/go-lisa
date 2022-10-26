package it.unive.golisa.cfg.runtime.math.rand.type;

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
import it.unive.golisa.cfg.runtime.math.rand.method.Read;
import it.unive.golisa.cfg.runtime.math.rand.method.Seed;
import it.unive.golisa.cfg.runtime.math.rand.method.Shuffle;
import it.unive.golisa.cfg.runtime.math.rand.method.UInt32;
import it.unive.golisa.cfg.runtime.math.rand.method.UInt64;
import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.ClassUnit;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Program;

/**
 * A Random generator.
 * 
 * @link https://pkg.go.dev/math/rand#Rand
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class Rand extends GoStructType {

	// /**
	// * Unique instance of Rand type.
	// */
	// public static final Rand INSTANCE = new Rand();
	//
	// private Rand() {
	// this("Rand", buildRandUnit());
	// }

	private Rand(String name, CompilationUnit unit) {
		super(name, unit);
	}

	// private static CompilationUnit buildRandUnit() {
	// SourceCodeLocation unknownLocation = new
	// SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
	// return randUnit;
	// }

	// /**
	// * Registers the methods of Rand type.
	// */
	// public static void registerMethods() {
	// SourceCodeLocation GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION = new
	// SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
	//
	//
	//
	// }

	public static Rand getRandType(Program program) {
		// builds the unit
		ClassUnit randUnit = new ClassUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, program, "Rand", false);
		return new Rand("Rand", randUnit);
	}

	public static void registerMethods() {
		CompilationUnit randUnit = GoStructType.get("Rand").getUnit();
		randUnit.addInstanceCodeMember(new ExpFloat64(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, randUnit));
		randUnit.addInstanceCodeMember(new Float32(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, randUnit));
		randUnit.addInstanceCodeMember(new Float64(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, randUnit));
		randUnit.addInstanceCodeMember(new Int(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, randUnit));
		randUnit.addInstanceCodeMember(new Int31(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, randUnit));
		randUnit.addInstanceCodeMember(new Int31n(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, randUnit));
		randUnit.addInstanceCodeMember(new Int63(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, randUnit));
		randUnit.addInstanceCodeMember(new Int63n(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, randUnit));
		randUnit.addInstanceCodeMember(new Intn(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, randUnit));
		randUnit.addInstanceCodeMember(new NormFloat64(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, randUnit));
		randUnit.addInstanceCodeMember(new Perm(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, randUnit));
		randUnit.addInstanceCodeMember(new UInt32(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, randUnit));
		randUnit.addInstanceCodeMember(new UInt64(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, randUnit));
		randUnit.addInstanceCodeMember(new Read(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, randUnit));
		randUnit.addInstanceCodeMember(new Seed(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, randUnit));
		randUnit.addInstanceCodeMember(new Shuffle(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, randUnit));
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
