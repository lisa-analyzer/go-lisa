package it.unive.golisa.cfg.runtime.fabriccontractapigo.type;


import it.unive.golisa.cfg.runtime.fabriccontractapigo.method.GetStub;
import it.unive.golisa.cfg.runtime.shim.type.ChaincodeStub;
import it.unive.golisa.cfg.type.composite.GoInterfaceType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.InterfaceUnit;
import it.unive.lisa.program.Program;

/**
 * A TransactionContextInterface type.
 * 
 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
 */
public class TransactionContextInterface extends GoInterfaceType {

	/**
	 * Unique instance of {@link TransactionContextInterface} type.
	 */
	private static TransactionContextInterface INSTANCE;

	private TransactionContextInterface(CompilationUnit unit) {
		super("TransactionContextInterface", unit);
	}

	/**
	 * Yields the {@link TransactionContextInterface} type.
	 * 
	 * @param program the program to which this type belongs
	 * 
	 * @return the {@link TransactionContextInterface} type
	 */
	public static TransactionContextInterface getTransactionContextInterfaceType(Program program) {
		
		if (INSTANCE == null) {
			InterfaceUnit transactionContextInterfaceUnit = new InterfaceUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION,
					program, "TransactionContextInterface",
					false);
			TransactionContextInterface transactionContextInterfaceType = new TransactionContextInterface(transactionContextInterfaceUnit);

			INSTANCE = transactionContextInterfaceType;
		}
		return INSTANCE;
	}

	
	
	@Override
	public String toString() {
		return "contractapi.TransactionContextInterface";
	}

	@Override
	public boolean equals(Object other) {
		return this == other;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}

	
	/**
	 * Registers the methods of the {@link TransactionContextInterface} type.
	 */
	public static void registerMethods() {
		
		CompilationUnit unit = INSTANCE.getUnit();
		unit.addInstanceCodeMember(new GetStub(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, unit));

		
	}

}
