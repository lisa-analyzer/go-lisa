package it.unive.golisa.cfg.runtime.fabriccontractapigo.type;

import it.unive.golisa.cfg.runtime.fabriccontractapigo.method.GetStub;
import it.unive.golisa.cfg.runtime.shim.type.ChaincodeStub;
import it.unive.golisa.cfg.runtime.shim.type.ChaincodeStubInterface;
import it.unive.golisa.cfg.type.composite.GoInterfaceType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.ClassUnit;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Program;

/**
 * A TransactionContext type.
 * 
 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
 */
public class TransactionContext extends GoInterfaceType {
	
	/**
	 * Unique instance of {@link TransactionContext} type.
	 */
	private static TransactionContext INSTANCE;

	private TransactionContext(CompilationUnit unit) {
		super("TransactionContext", unit);
	}

	/**
	 * Yields the {@link TransactionContext} type.
	 * 
	 * @param program the program to which this type belongs
	 * 
	 * @return the {@link ChaincodeStubInterface} type
	 */
	public static TransactionContext getTransactionContextType(Program program) {
		if (INSTANCE == null) {
			ClassUnit randUnit = new ClassUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, program, "TransactionContext", false);
			INSTANCE = new TransactionContext(randUnit);
		}

		return INSTANCE;
	}
	

	/**
	 * Registers the methods of the {@link ChaincodeStub} type.
	 */
	public static void registerMethods() {
		CompilationUnit unit = INSTANCE.getUnit();
		unit.addInstanceCodeMember(
						new GetStub(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, unit));
		
	}

	@Override
	public String toString() {
		return "contractapi.TransactionContext";
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
