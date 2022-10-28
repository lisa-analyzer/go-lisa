package it.unive.golisa.cfg.runtime.pkg.statebased.type;

import it.unive.golisa.cfg.type.composite.GoInterfaceType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.InterfaceUnit;
import it.unive.lisa.program.Program;

/**
 * A KeyEndorsementPolicy type.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class KeyEndorsementPolicy extends GoInterfaceType {

	/**
	 * Unique instance of KeyEndorsementPolicy type.
	 */
	private static KeyEndorsementPolicy INSTANCE;

	private KeyEndorsementPolicy(CompilationUnit unit) {
		super("KeyEndorsementPolicy", unit);
	}

	public static KeyEndorsementPolicy getKeyEndorsementPolicyType(Program program) {
		if (INSTANCE == null) {
			InterfaceUnit keyEndorsementUnit = new InterfaceUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, program,
					"KeyEndorsementPolicy", false);

			INSTANCE = new KeyEndorsementPolicy(keyEndorsementUnit);
		}

		return INSTANCE;
	}

	@Override
	public String toString() {
		return "statebased.KeyEndorsementPolicy";
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
