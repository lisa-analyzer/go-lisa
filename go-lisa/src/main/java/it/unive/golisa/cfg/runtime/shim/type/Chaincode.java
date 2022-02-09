package it.unive.golisa.cfg.runtime.shim.type;

import it.unive.golisa.cfg.type.composite.GoInterfaceType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.SourceCodeLocation;

public class Chaincode extends GoInterfaceType {

	public static final Chaincode INSTANCE = new Chaincode();

	private Chaincode() {
		this("Chaincode", buildChaincodeUnit());
	}

	private Chaincode(String name, CompilationUnit unit) {
		super(name, unit);
	}

	private static CompilationUnit buildChaincodeUnit() {
		SourceCodeLocation unknownLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
		CompilationUnit chaincodeType = new CompilationUnit(unknownLocation, "Chaincode", false);
//
//		CFGDescriptor desc = new CFGDescriptor(unknownLocation, chaincodeType, true, "Init", Untyped.INSTANCE,
//				new Parameter(unknownLocation, "stub", ChaincodeStubInterface.INSTANCE));
//		chaincodeType.addInstanceCFG(new CFG(desc));
//
//		desc = new CFGDescriptor(unknownLocation, chaincodeType, true, "Invoke", Untyped.INSTANCE,
//				new Parameter(unknownLocation, "stub", ChaincodeStubInterface.INSTANCE));
//		chaincodeType.addInstanceCFG(new CFG(desc));

		return chaincodeType;
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
