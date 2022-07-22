package it.unive.golisa.cfg.runtime.shim.type;

import it.unive.golisa.cfg.runtime.shim.function.CreateCompositeKey.CreateCompositeKeyImpl;
import it.unive.golisa.cfg.runtime.shim.method.GetArgs.GetArgsImpl;
import it.unive.golisa.cfg.runtime.shim.method.GetFunctionAndParameters.GetFunctionAndParametersImpl;
import it.unive.golisa.cfg.runtime.shim.method.GetState.GetStateImpl;
import it.unive.golisa.cfg.runtime.shim.method.GetStringArgs.GetStringArgsImpl;
import it.unive.golisa.cfg.runtime.shim.method.PutState.PutStateImpl;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoInterfaceType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.composite.GoTupleType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CFGDescriptor;
import it.unive.lisa.program.cfg.NativeCFG;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.type.Untyped;

/**
 * A ChaincodeStubInterface type.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class ChaincodeStubInterface extends GoInterfaceType {

	/**
	 * Unique instance of {@link ChaincodeStubInterface} type.
	 */
	public static final ChaincodeStubInterface INSTANCE = new ChaincodeStubInterface();

	private ChaincodeStubInterface() {
		super("ChaincodeStubInterface", buildChainCodeStubInterfaceUnit());
	}

	/**
	 * Registers methods of the {@link ChaincodeStubInterface} type.
	 */
	public static void registerMethods() {
		SourceCodeLocation unknownLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);

		// []string
		GoSliceType stringSliceType = GoSliceType.getSliceOfStrings();

		// [][] byte
		GoSliceType byteSliceSliceType = GoSliceType.getSliceOfSliceOfBytes();

		// (string, []string)
		GoTupleType tuple1 = GoTupleType.getTupleTypeOf(unknownLocation, GoStringType.INSTANCE, stringSliceType);

		// ([]byte, error)
		GoTupleType tuple2 = GoTupleType.getTupleTypeOf(unknownLocation, GoSliceType.getSliceOfBytes(),
				GoErrorType.INSTANCE);

		// GetArgs
		CompilationUnit chainCodeStubInterfaceUnit = INSTANCE.getUnit();
		CFGDescriptor desc = new CFGDescriptor(unknownLocation, chainCodeStubInterfaceUnit, true, "GetArgs",
				byteSliceSliceType,
				new Parameter(unknownLocation, "this", ChaincodeStubInterface.INSTANCE));
		chainCodeStubInterfaceUnit.addInstanceConstruct(new NativeCFG(desc, GetArgsImpl.class));

		// GetStringArgs
		desc = new CFGDescriptor(unknownLocation, chainCodeStubInterfaceUnit, true, "GetStringArgs",
				stringSliceType, new Parameter(unknownLocation, "this", ChaincodeStubInterface.INSTANCE));
		chainCodeStubInterfaceUnit.addInstanceConstruct(new NativeCFG(desc, GetStringArgsImpl.class));

		// GetFunctionAndParameters
		desc = new CFGDescriptor(unknownLocation, chainCodeStubInterfaceUnit, true, "GetFunctionAndParameters",
				tuple1, new Parameter(unknownLocation, "this", ChaincodeStubInterface.INSTANCE));
		chainCodeStubInterfaceUnit.addInstanceConstruct(new NativeCFG(desc, GetFunctionAndParametersImpl.class));

		desc = new CFGDescriptor(unknownLocation, chainCodeStubInterfaceUnit, true, "GetArgsSlice",
				tuple2);
		chainCodeStubInterfaceUnit.addInstanceCFG(new CFG(desc));

		desc = new CFGDescriptor(unknownLocation, chainCodeStubInterfaceUnit, true, "GetTxID",
				GoStringType.INSTANCE);
		chainCodeStubInterfaceUnit.addInstanceCFG(new CFG(desc));

		desc = new CFGDescriptor(unknownLocation, chainCodeStubInterfaceUnit, true, "GetChannelID",
				GoStringType.INSTANCE);
		chainCodeStubInterfaceUnit.addInstanceCFG(new CFG(desc));

		// GetState
		desc = new CFGDescriptor(unknownLocation, chainCodeStubInterfaceUnit, true, "GetState",
				tuple2,
				new Parameter(unknownLocation, "this", ChaincodeStubInterface.INSTANCE),
				new Parameter(unknownLocation, "key", GoStringType.INSTANCE));
		chainCodeStubInterfaceUnit.addInstanceConstruct(new NativeCFG(desc, GetStateImpl.class));

		// PutState
		desc = new CFGDescriptor(unknownLocation, chainCodeStubInterfaceUnit, true, "PutState",
				GoErrorType.INSTANCE,
				new Parameter(unknownLocation, "this", ChaincodeStubInterface.INSTANCE),
				new Parameter(unknownLocation, "key", GoStringType.INSTANCE),
				new Parameter(unknownLocation, "value", Untyped.INSTANCE));
		chainCodeStubInterfaceUnit.addInstanceConstruct(new NativeCFG(desc, PutStateImpl.class));

		// CreateCompositeKey
		desc = new CFGDescriptor(unknownLocation, chainCodeStubInterfaceUnit, true, "CreateCompositeKey",
				GoTupleType.getTupleTypeOf(unknownLocation, GoStringType.INSTANCE,
						GoErrorType.INSTANCE),
				new Parameter(unknownLocation, "this", ChaincodeStubInterface.INSTANCE),
				new Parameter(unknownLocation, "objectType", GoStringType.INSTANCE),
				new Parameter(unknownLocation, "attributes",
						GoSliceType.lookup(new GoSliceType(GoStringType.INSTANCE))));
		chainCodeStubInterfaceUnit.addInstanceConstruct(new NativeCFG(desc, CreateCompositeKeyImpl.class));

		desc = new CFGDescriptor(unknownLocation, chainCodeStubInterfaceUnit, true, "DelState",
				GoErrorType.INSTANCE,
				new Parameter(unknownLocation, "key", GoStringType.INSTANCE));
		chainCodeStubInterfaceUnit.addInstanceCFG(new CFG(desc));

		desc = new CFGDescriptor(unknownLocation, chainCodeStubInterfaceUnit, true, "SetStateValidationParameter",
				GoErrorType.INSTANCE,
				new Parameter(unknownLocation, "key", GoStringType.INSTANCE),
				new Parameter(unknownLocation, "ep", GoSliceType.getSliceOfBytes()));
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
	}



	private static CompilationUnit buildChainCodeStubInterfaceUnit() {
		SourceCodeLocation unknownLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
		CompilationUnit chainCodeStubInterfaceUnit = new CompilationUnit(unknownLocation, "ChaincodeStubInterface",
				false);

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
