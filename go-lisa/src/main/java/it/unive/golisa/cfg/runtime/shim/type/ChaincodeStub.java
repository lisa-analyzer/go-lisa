package it.unive.golisa.cfg.runtime.shim.type;

import it.unive.golisa.cfg.runtime.shim.method.CreateCompositeKey;
import it.unive.golisa.cfg.runtime.shim.method.DelPrivateData;
import it.unive.golisa.cfg.runtime.shim.method.DelState;
import it.unive.golisa.cfg.runtime.shim.method.GetArgs;
import it.unive.golisa.cfg.runtime.shim.method.GetArgsSlice;
import it.unive.golisa.cfg.runtime.shim.method.GetFunctionAndParameters;
import it.unive.golisa.cfg.runtime.shim.method.GetHistoryForKey;
import it.unive.golisa.cfg.runtime.shim.method.GetPrivateData;
import it.unive.golisa.cfg.runtime.shim.method.GetPrivateDataByPartialCompositeKey;
import it.unive.golisa.cfg.runtime.shim.method.GetPrivateDataByRange;
import it.unive.golisa.cfg.runtime.shim.method.GetPrivateDataHash;
import it.unive.golisa.cfg.runtime.shim.method.GetPrivateDataQueryResult;
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
import it.unive.golisa.cfg.runtime.shim.method.SetPrivateDataValidationParameter;
import it.unive.golisa.cfg.runtime.shim.method.SetStateValidationParameter;
import it.unive.golisa.cfg.runtime.shim.method.SplitCompositeKey;
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
	public static ChaincodeStub INSTANCE;

	private ChaincodeStub(CompilationUnit unit) {
		super("ChaincodeStub", unit);
	}

	/**
	 * Yields the {@link ChaincodeStub} type.
	 * 
	 * @param program the program to which this type belongs
	 * 
	 * @return the {@link ChaincodeStub} type
	 */
	public static ChaincodeStub getChaincodeStubType(Program program) {
		if (INSTANCE == null) {
			ClassUnit chaincodeStubUnit = new ClassUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, program,
					"ChaincodeStub", false);
			// add superclasses and implemented interfaces
			chaincodeStubUnit.addAncestor(ChaincodeStubInterface.getChainCodeStubInterfaceType(program).getUnit());

			// add globals
			chaincodeStubUnit
					.addInstanceGlobal(new Global(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeStubUnit, "TxID",
							true, GoStringType.INSTANCE));
			chaincodeStubUnit
					.addInstanceGlobal(new Global(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeStubUnit,
							"ChannelID", true, GoStringType.INSTANCE));

			INSTANCE = new ChaincodeStub(chaincodeStubUnit);
		}

		return INSTANCE;
	}

	/**
	 * Registers the methods of the {@link ChaincodeStub} type.
	 */
	public static void registerMethods() {
		CompilationUnit chaincodeStubUnit = INSTANCE.getUnit();
		chaincodeStubUnit
				.addInstanceCodeMember(
						new DelPrivateData(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeStubUnit));
		chaincodeStubUnit
				.addInstanceCodeMember(new DelState(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeStubUnit));
		chaincodeStubUnit
				.addInstanceCodeMember(
						new GetFunctionAndParameters(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeStubUnit));
		chaincodeStubUnit
				.addInstanceCodeMember(new GetArgs(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeStubUnit));

		chaincodeStubUnit
				.addInstanceCodeMember(
						new GetStringArgs(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeStubUnit));
		chaincodeStubUnit
				.addInstanceCodeMember(
						new PutPrivateData(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeStubUnit));
		chaincodeStubUnit
				.addInstanceCodeMember(new PutState(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeStubUnit));
		chaincodeStubUnit
				.addInstanceCodeMember(new GetState(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeStubUnit));
		chaincodeStubUnit
				.addInstanceCodeMember(
						new CreateCompositeKey(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeStubUnit));
		chaincodeStubUnit
				.addInstanceCodeMember(
						new SplitCompositeKey(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeStubUnit));
		chaincodeStubUnit.addInstanceCodeMember(
				new GetStateByRange(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeStubUnit));
		chaincodeStubUnit.addInstanceCodeMember(
				new GetStateByPartialCompositeKey(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeStubUnit));
		chaincodeStubUnit.addInstanceCodeMember(
				new GetHistoryForKey(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeStubUnit));
		chaincodeStubUnit.addInstanceCodeMember(
				new GetQueryResult(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeStubUnit));
		chaincodeStubUnit.addInstanceCodeMember(
				new InvokeChaincode(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeStubUnit));
		chaincodeStubUnit.addInstanceCodeMember(
				new GetPrivateData(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeStubUnit));
		chaincodeStubUnit.addInstanceCodeMember(
				new GetPrivateDataHash(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeStubUnit));
		chaincodeStubUnit.addInstanceCodeMember(
				new GetPrivateDataValidationParameter(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeStubUnit));
		chaincodeStubUnit.addInstanceCodeMember(
				new GetStateValidationParameter(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeStubUnit));
		chaincodeStubUnit.addInstanceCodeMember(
				new GetTransient(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeStubUnit));
		chaincodeStubUnit.addInstanceCodeMember(
				new PurgePrivateData(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeStubUnit));
	
		chaincodeStubUnit
		.addInstanceCodeMember(new GetArgsSlice(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeStubUnit));
		chaincodeStubUnit
		.addInstanceCodeMember(new SetStateValidationParameter(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeStubUnit));
		chaincodeStubUnit
		.addInstanceCodeMember(new SetPrivateDataValidationParameter(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeStubUnit));
		chaincodeStubUnit
		.addInstanceCodeMember(new GetPrivateDataByRange(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeStubUnit));
		chaincodeStubUnit
		.addInstanceCodeMember(new GetPrivateDataQueryResult(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeStubUnit));
		chaincodeStubUnit
		.addInstanceCodeMember(new GetPrivateDataByPartialCompositeKey(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeStubUnit));

	
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
