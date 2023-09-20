package it.unive.golisa.cfg.runtime.pkg.statebased.type;

import it.unive.golisa.cfg.runtime.pkg.statebased.method.Policy;
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

	/**
	 * Yields the {@link KeyEndorsementPolicy} type.
	 * 
	 * @param program the program to which this type belongs
	 * 
	 * @return the {@link KeyEndorsementPolicy} type
	 */
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

	public static void registerMethods() {
		CompilationUnit unit = INSTANCE.getUnit();
		unit.addInstanceCodeMember(new Policy(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, unit));
	}
}
