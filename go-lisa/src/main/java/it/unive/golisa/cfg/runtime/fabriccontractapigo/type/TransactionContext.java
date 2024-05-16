package it.unive.golisa.cfg.runtime.fabriccontractapigo.type;

import it.unive.golisa.cfg.runtime.fabriccontractapigo.method.GetStub;
import it.unive.golisa.cfg.runtime.shim.method.CreateCompositeKey;
import it.unive.golisa.cfg.runtime.shim.method.DelPrivateData;
import it.unive.golisa.cfg.runtime.shim.method.DelState;
import it.unive.golisa.cfg.runtime.shim.method.GetArgs;
import it.unive.golisa.cfg.runtime.shim.method.GetFunctionAndParameters;
import it.unive.golisa.cfg.runtime.shim.method.GetHistoryForKey;
import it.unive.golisa.cfg.runtime.shim.method.GetPrivateData;
import it.unive.golisa.cfg.runtime.shim.method.GetPrivateDataHash;
import it.unive.golisa.cfg.runtime.shim.method.GetPrivateDataValidationParameter;
import it.unive.golisa.cfg.runtime.shim.method.GetQueryResult;
import it.unive.golisa.cfg.runtime.shim.method.GetState;
import it.unive.golisa.cfg.runtime.shim.method.GetStateByPartialCompositeKey;
import it.unive.golisa.cfg.runtime.shim.method.GetStateByRange;
import it.unive.golisa.cfg.runtime.shim.method.GetStateValidationParameter;
import it.unive.golisa.cfg.runtime.shim.method.GetStringArgs;
import it.unive.golisa.cfg.runtime.shim.method.GetTransient;
import it.unive.golisa.cfg.runtime.shim.method.InvokeChaincode;
import it.unive.golisa.cfg.runtime.shim.method.PurgePrivateData;
import it.unive.golisa.cfg.runtime.shim.method.PutPrivateData;
import it.unive.golisa.cfg.runtime.shim.method.PutState;
import it.unive.golisa.cfg.runtime.shim.method.SplitCompositeKey;
import it.unive.golisa.cfg.runtime.shim.type.ChaincodeStub;
import it.unive.golisa.cfg.runtime.shim.type.ChaincodeStubInterface;
import it.unive.golisa.cfg.type.composite.GoInterfaceType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.InterfaceUnit;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.cfg.AbstractCodeMember;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.Parameter;

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
			InterfaceUnit transactionContextUnit = new InterfaceUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION,
					program, "TransactionContext",
					false);
			
			TransactionContext transactionContextType = new TransactionContext(transactionContextUnit);

			// GetStub
			CodeMemberDescriptor desc = new CodeMemberDescriptor(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION,
					transactionContextUnit, true, "GetStub",
					ChaincodeStubInterface.getChainCodeStubInterfaceType(transactionContextUnit.getProgram()),
					new Parameter(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, "this", transactionContextType));
			transactionContextUnit.addInstanceCodeMember(new AbstractCodeMember(desc));

			
			INSTANCE = transactionContextType;
			return INSTANCE;
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
