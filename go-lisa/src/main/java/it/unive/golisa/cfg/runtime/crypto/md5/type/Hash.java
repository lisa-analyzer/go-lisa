package it.unive.golisa.cfg.runtime.crypto.md5.type;

import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.ClassUnit;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Program;

/**
 * A Hash type.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class Hash extends GoStructType {

	/**
	 * Unique instance of the {@link Hash} type.
	 */
	public static Hash INSTANCE;

	private Hash(CompilationUnit unit) {
		super("Hash", unit);
	}

	/**
	 * Yields the {@link Hash} type.
	 * 
	 * @param program the program to which this type belongs
	 * 
	 * @return the {@link Hash} type
	 */
	public static Hash getHashType(Program program) {
		if (INSTANCE == null) {
			ClassUnit chaincodeStubUnit = new ClassUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, program,
					"ChaincodeStub", false);
			INSTANCE = new Hash(chaincodeStubUnit);
		}

		return INSTANCE;
	}

	/**
	 * Registers the methods of the {@link Hash} type.
	 */
	public static void registerMethods() {
		CompilationUnit chaincodeStubUnit = INSTANCE.getUnit();
	}

	@Override
	public String toString() {
		return "hash.Hash";
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
