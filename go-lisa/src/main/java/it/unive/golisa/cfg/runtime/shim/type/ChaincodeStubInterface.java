package it.unive.golisa.cfg.runtime.shim.type;

import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoInterfaceType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.composite.GoTupleType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.InterfaceUnit;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.cfg.AbstractCodeMember;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
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
	private static ChaincodeStubInterface INSTANCE;

	private ChaincodeStubInterface(CompilationUnit unit) {
		super("ChaincodeStubInterface", unit);
	}

	/**
	 * Yields the {@link ChaincodeStubInterface} type.
	 * 
	 * @param program the program to which this type belongs
	 * 
	 * @return the {@link ChaincodeStubInterface} type
	 */
	public static ChaincodeStubInterface getChainCodeStubInterfaceType(Program program) {
		if (INSTANCE == null) {
			InterfaceUnit chainCodeStubInterfaceUnit = new InterfaceUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION,
					program, "ChaincodeStubInterface",
					false);

			// []string
			GoSliceType stringSliceType = GoSliceType.getSliceOfStrings();

			// [][] byte
			GoSliceType byteSliceSliceType = GoSliceType.getSliceOfSliceOfBytes();

			// (string, []string)
			GoTupleType tuple1 = GoTupleType.getTupleTypeOf(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION,
					GoStringType.INSTANCE, stringSliceType);

			// ([]byte, error)
			GoTupleType tuple2 = GoTupleType.getTupleTypeOf(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION,
					GoSliceType.getSliceOfBytes(),
					GoErrorType.INSTANCE);

			ChaincodeStubInterface chaincodeStubInterfaceType = new ChaincodeStubInterface(chainCodeStubInterfaceUnit);

			// GetArgs
			CodeMemberDescriptor desc = new CodeMemberDescriptor(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION,
					chainCodeStubInterfaceUnit, true, "GetArgs",
					byteSliceSliceType,
					new Parameter(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, "this", chaincodeStubInterfaceType));
			chainCodeStubInterfaceUnit.addInstanceCodeMember(new AbstractCodeMember(desc));

			// GetStringArgs
			desc = new CodeMemberDescriptor(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chainCodeStubInterfaceUnit,
					true,
					"GetStringArgs",
					stringSliceType,
					new Parameter(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, "this", chaincodeStubInterfaceType));
			chainCodeStubInterfaceUnit.addInstanceCodeMember(new AbstractCodeMember(desc));

			// GetFunctionAndParameters
			desc = new CodeMemberDescriptor(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chainCodeStubInterfaceUnit,
					true,
					"GetFunctionAndParameters",
					tuple1,
					new Parameter(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, "this", chaincodeStubInterfaceType));
			chainCodeStubInterfaceUnit.addInstanceCodeMember(new AbstractCodeMember(desc));

//			desc = new CodeMemberDescriptor(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chainCodeStubInterfaceUnit, true,
//					"GetArgsSlice",
//					tuple2);
//			chainCodeStubInterfaceUnit.addInstanceCodeMember(new AbstractCodeMember(desc));

//			desc = new CodeMemberDescriptor(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chainCodeStubInterfaceUnit, true,
//					"GetTxID",
//					GoStringType.INSTANCE);
//			chainCodeStubInterfaceUnit.addInstanceCodeMember(new AbstractCodeMember(desc));

//			desc = new CodeMemberDescriptor(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chainCodeStubInterfaceUnit, true,
//					"GetChannelID",
//					GoStringType.INSTANCE);
//			chainCodeStubInterfaceUnit.addInstanceCodeMember(new AbstractCodeMember(desc));

			// GetState
			desc = new CodeMemberDescriptor(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chainCodeStubInterfaceUnit,
					true,
					"GetState",
					tuple2,
					new Parameter(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, "this", chaincodeStubInterfaceType),
					new Parameter(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, "key", GoStringType.INSTANCE));
			chainCodeStubInterfaceUnit.addInstanceCodeMember(new AbstractCodeMember(desc));

			// PutState
			desc = new CodeMemberDescriptor(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chainCodeStubInterfaceUnit,
					true,
					"PutState",
					GoErrorType.INSTANCE,
					new Parameter(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, "this", chaincodeStubInterfaceType),
					new Parameter(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, "key", GoStringType.INSTANCE),
					new Parameter(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, "value", Untyped.INSTANCE));
			chainCodeStubInterfaceUnit.addInstanceCodeMember(new AbstractCodeMember(desc));

			// CreateCompositeKey
			desc = new CodeMemberDescriptor(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chainCodeStubInterfaceUnit,
					true,
					"CreateCompositeKey",
					GoTupleType.getTupleTypeOf(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, GoStringType.INSTANCE,
							GoErrorType.INSTANCE),
					new Parameter(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, "this", chaincodeStubInterfaceType),
					new Parameter(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, "objectType", GoStringType.INSTANCE),
					new Parameter(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, "attributes",
							GoSliceType.lookup(GoStringType.INSTANCE)));
			chainCodeStubInterfaceUnit.addInstanceCodeMember(new AbstractCodeMember(desc));

			desc = new CodeMemberDescriptor(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chainCodeStubInterfaceUnit,
					true,
					"DelState",
					GoErrorType.INSTANCE,
					new Parameter(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, "this", chaincodeStubInterfaceType),
					new Parameter(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, "key", GoStringType.INSTANCE));
			chainCodeStubInterfaceUnit.addInstanceCodeMember(new AbstractCodeMember(desc));
			
			desc = new CodeMemberDescriptor(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chainCodeStubInterfaceUnit,
					true,
					"SplitCompositeKey",
					GoTupleType.getTupleTypeOf(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, GoStringType.INSTANCE, GoSliceType.getSliceOfStrings() ,GoErrorType.INSTANCE),
					new Parameter(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, "this", chaincodeStubInterfaceType),
					new Parameter(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, "compositeKey", GoStringType.INSTANCE));
			chainCodeStubInterfaceUnit.addInstanceCodeMember(new AbstractCodeMember(desc));
			
			desc = new CodeMemberDescriptor(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chainCodeStubInterfaceUnit,
					true,
					"GetStateByRange",
					GoTupleType.getTupleTypeOf(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, Untyped.INSTANCE ,GoErrorType.INSTANCE),
					new Parameter(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, "this", chaincodeStubInterfaceType),
					new Parameter(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, "startKey", GoStringType.INSTANCE),
					new Parameter(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, "endKey", GoStringType.INSTANCE));
			chainCodeStubInterfaceUnit.addInstanceCodeMember(new AbstractCodeMember(desc));
						
//			desc = new CodeMemberDescriptor(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chainCodeStubInterfaceUnit, true,
//					"SetStateValidationParameter",
//					GoErrorType.INSTANCE,
//					new Parameter(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, "key", GoStringType.INSTANCE),
//					new Parameter(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, "ep", GoSliceType.getSliceOfBytes()));
//			chainCodeStubInterfaceUnit.addInstanceCodeMember(new AbstractCodeMember(desc));

//			desc = new CodeMemberDescriptor(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chainCodeStubInterfaceUnit, true,
//					"GetStateValidationParameter",
//					tuple2,
//					new Parameter(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, "key", GoStringType.INSTANCE));
//			chainCodeStubInterfaceUnit.addInstanceCodeMember(new AbstractCodeMember(desc));

			// missing
			// - InvokeChaincode
			// - GetStateByRange
			// - GetStateByRangeWithPagination
			// - GetStateByPartialCompositeKey
			// - GetStateByPartialCompositeKeyWithPagination
			// - ...
			INSTANCE = chaincodeStubInterfaceType;
			return INSTANCE;
		}

		return INSTANCE;
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

//	@Override
//	public Collection<Type> allInstances() {
//		return Collections.singleton(this);
//	}
}
