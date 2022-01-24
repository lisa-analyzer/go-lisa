package it.unive.golisa.cfg.runtime.shim.type;

import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoInterfaceType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.composite.GoTypesTuple;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt8Type;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CFGDescriptor;
import it.unive.lisa.program.cfg.Parameter;

public class ChaincodeStubInterface extends GoInterfaceType {
	public static final ChaincodeStubInterface INSTANCE = new ChaincodeStubInterface();

	private ChaincodeStubInterface() {
		this("ChaincodeStubInterface", buildChainCodeStubInterfaceUnit());
	}

	private ChaincodeStubInterface(String name, CompilationUnit unit) {
		super(name, unit);
	}

	private static CompilationUnit buildChainCodeStubInterfaceUnit() {
		SourceCodeLocation unknownLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
		CompilationUnit chainCodeStubInterfaceUnit = new CompilationUnit(unknownLocation, "ChaincodeStubInterface",
				false);

		// []byte
		GoSliceType byteSliceType = GoSliceType.lookup(new GoSliceType(GoUInt8Type.INSTANCE));

		// []string
		GoSliceType stringSliceType = GoSliceType.lookup(new GoSliceType(GoStringType.INSTANCE));

		// [][] byte
		GoSliceType byteSliceSliceType = GoSliceType.lookup(new GoSliceType(byteSliceType));

		// (string, []string)
		GoTypesTuple tuple1 = new GoTypesTuple(new Parameter(unknownLocation, "_", GoStringType.INSTANCE),
				new Parameter(unknownLocation, "_", stringSliceType));

		// ([]byte, error)
		GoTypesTuple tuple2 = new GoTypesTuple(new Parameter(unknownLocation, "_", byteSliceType),
				new Parameter(unknownLocation, "_", GoErrorType.INSTANCE));

		CFGDescriptor desc = new CFGDescriptor(unknownLocation, chainCodeStubInterfaceUnit, true, "GetArgs",
				byteSliceSliceType);
		chainCodeStubInterfaceUnit.addInstanceCFG(new CFG(desc));

		desc = new CFGDescriptor(unknownLocation, chainCodeStubInterfaceUnit, true, "getStringArgs",
				stringSliceType);
		chainCodeStubInterfaceUnit.addInstanceCFG(new CFG(desc));

		desc = new CFGDescriptor(unknownLocation, chainCodeStubInterfaceUnit, true, "GetFunctionAndParameters",
				tuple1);
		chainCodeStubInterfaceUnit.addInstanceCFG(new CFG(desc));

		desc = new CFGDescriptor(unknownLocation, chainCodeStubInterfaceUnit, true, "GetArgsSlice",
				tuple2);
		chainCodeStubInterfaceUnit.addInstanceCFG(new CFG(desc));

		desc = new CFGDescriptor(unknownLocation, chainCodeStubInterfaceUnit, true, "GetTxID",
				GoStringType.INSTANCE);
		chainCodeStubInterfaceUnit.addInstanceCFG(new CFG(desc));

		desc = new CFGDescriptor(unknownLocation, chainCodeStubInterfaceUnit, true, "GetChannelID",
				GoStringType.INSTANCE);
		chainCodeStubInterfaceUnit.addInstanceCFG(new CFG(desc));

		desc = new CFGDescriptor(unknownLocation, chainCodeStubInterfaceUnit, true, "GetState",
				tuple2,
				new Parameter(unknownLocation, "key", GoStringType.INSTANCE));
		chainCodeStubInterfaceUnit.addInstanceCFG(new CFG(desc));

		desc = new CFGDescriptor(unknownLocation, chainCodeStubInterfaceUnit, true, "PutState",
				GoErrorType.INSTANCE,
				new Parameter(unknownLocation, "key", GoStringType.INSTANCE),
				new Parameter(unknownLocation, "value", byteSliceType));
		chainCodeStubInterfaceUnit.addInstanceCFG(new CFG(desc));

		desc = new CFGDescriptor(unknownLocation, chainCodeStubInterfaceUnit, true, "DelState",
				GoErrorType.INSTANCE,
				new Parameter(unknownLocation, "key", GoStringType.INSTANCE));
		chainCodeStubInterfaceUnit.addInstanceCFG(new CFG(desc));

		desc = new CFGDescriptor(unknownLocation, chainCodeStubInterfaceUnit, true, "SetStateValidationParameter",
				GoErrorType.INSTANCE,
				new Parameter(unknownLocation, "key", GoStringType.INSTANCE),
				new Parameter(unknownLocation, "ep", byteSliceType));
		chainCodeStubInterfaceUnit.addInstanceCFG(new CFG(desc));

		desc = new CFGDescriptor(unknownLocation, chainCodeStubInterfaceUnit, true, "GetStateValidationParameter",
				tuple2,
				new Parameter(unknownLocation, "key", GoStringType.INSTANCE));
		chainCodeStubInterfaceUnit.addInstanceCFG(new CFG(desc));

		// missing
		// - InvokeChaincode
		// - GetStateByRange
		// - GetStateByRangeWithPagination
		// - GetStateByPartialCompositeKey
		// - GetStateByPartialCompositeKeyWithPagination
		// - ...
		return chainCodeStubInterfaceUnit;
	}

	@Override
	public String toString() {
		return "shim.ChaincodeStubInterface";
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
