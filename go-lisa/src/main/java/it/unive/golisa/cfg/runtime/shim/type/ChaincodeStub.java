package it.unive.golisa.cfg.runtime.shim.type;

import it.unive.golisa.cfg.runtime.shim.method.CreateCompositeKey;
import it.unive.golisa.cfg.runtime.shim.method.DelPrivateData;
import it.unive.golisa.cfg.runtime.shim.method.DelState;
import it.unive.golisa.cfg.runtime.shim.method.GetArgs;
import it.unive.golisa.cfg.runtime.shim.method.GetFunctionAndParameters;
import it.unive.golisa.cfg.runtime.shim.method.GetState;
import it.unive.golisa.cfg.runtime.shim.method.GetStringArgs;
import it.unive.golisa.cfg.runtime.shim.method.PutPrivateData;
import it.unive.golisa.cfg.runtime.shim.method.PutState;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.ClassUnit;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.Program;

/**
 * A ChaincodeStub type.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class ChaincodeStub extends GoStructType {

	/**
	 * Unique instance of the {@link ChaincodeStub} type.
	 */
//	public static final ChaincodeStub INSTANCE = new ChaincodeStub();

	private ChaincodeStub(CompilationUnit unit) {
		super("ChaincodeStub", unit);
	}

	public static ChaincodeStub getChaincodeStubType(Program program) {
		ClassUnit chaincodeStubUnit = new ClassUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, program, "ChaincodeStub", false);

		// add globals
		chaincodeStubUnit.addGlobal(new Global(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeStubUnit, "TxID", true, GoStringType.INSTANCE));
		chaincodeStubUnit.addGlobal(new Global(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeStubUnit, "ChannelID", true, GoStringType.INSTANCE));
		
		return new ChaincodeStub(chaincodeStubUnit);
	}

	/**
	 * Registers the methods of the {@link ChaincodeStub} type.
	 */
	public static void registerMethods() {
		CompilationUnit chaincodeStubUnit = GoStructType.get("ChaincodeStub").getUnit();
		chaincodeStubUnit
		.addInstanceCodeMember(new DelPrivateData(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeStubUnit));
		chaincodeStubUnit.addInstanceCodeMember(new DelState(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeStubUnit));
		chaincodeStubUnit
		.addInstanceCodeMember(new GetFunctionAndParameters(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeStubUnit));
		chaincodeStubUnit
		.addInstanceCodeMember(new GetArgs(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeStubUnit));
		chaincodeStubUnit
		.addInstanceCodeMember(new GetStringArgs(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeStubUnit));
		chaincodeStubUnit
		.addInstanceCodeMember(new PutPrivateData(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeStubUnit));
		chaincodeStubUnit
		.addInstanceCodeMember(new PutState(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeStubUnit));
		chaincodeStubUnit
		.addInstanceCodeMember(new GetState(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeStubUnit));
		chaincodeStubUnit
		.addInstanceCodeMember(new CreateCompositeKey(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeStubUnit));

	}

	@Override
	public String toString() {
		return "shim.ChaincodeStub";
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
