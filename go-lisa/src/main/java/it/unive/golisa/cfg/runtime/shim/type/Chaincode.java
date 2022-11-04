package it.unive.golisa.cfg.runtime.shim.type;

import it.unive.golisa.cfg.type.composite.GoInterfaceType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.InterfaceUnit;
import it.unive.lisa.program.Program;

/**
 * A Chaincode type.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class Chaincode extends GoInterfaceType {

	/**
	 * Unique instance of the {@link Chaincode} type.
	 */
	private static Chaincode INSTANCE;

	private Chaincode(CompilationUnit unit) {
		super("Chaincode", unit);
	}

	/**
	 * Yields the {@link Chaincode} type.
	 * 
	 * @param program the program to which this type belongs
	 * 
	 * @return the {@link Chaincode} type
	 */
	public static Chaincode getChaincodeType(Program program) {
		if (INSTANCE == null) {
			InterfaceUnit chaincodeType = new InterfaceUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, program,
					"Chaincode", false);
			INSTANCE = new Chaincode(chaincodeType);
		}

		return INSTANCE;
	}

	@Override
	public String toString() {
		return "shim.Chaincode";
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
