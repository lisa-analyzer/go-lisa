package it.unive.golisa.cfg.runtime.shim.type;

import it.unive.golisa.cfg.runtime.shim.method.Start;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.SourceCodeLocation;

public class ChaincodeServer extends GoStructType {

	/**
	 * Unique instance of the {@link ChaincodeServer} type.
	 */
	public static final ChaincodeServer INSTANCE = new ChaincodeServer();

	private ChaincodeServer() {
		super("ChaincodeServer", buildChaincodeServer());
	}

	private static CompilationUnit buildChaincodeServer() {
		SourceCodeLocation unknownLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
		CompilationUnit chaincodeStubUnit = new CompilationUnit(unknownLocation, "ChaincodeServer", false);

		// Add globals
		chaincodeStubUnit.addGlobal(new Global(unknownLocation, "CCID", GoStringType.INSTANCE));
		chaincodeStubUnit.addGlobal(new Global(unknownLocation, "Address", GoStringType.INSTANCE));
		chaincodeStubUnit.addGlobal(new Global(unknownLocation, "CC", Chaincode.INSTANCE));
		chaincodeStubUnit.addGlobal(new Global(unknownLocation, "TLSProps", TLSProperties.INSTANCE));

		// missing KaOpts *keepalive.ServerParameters
		return chaincodeStubUnit;
	}

	/**
	 * Registers the methods of the {@link ChaincodeStub} type.
	 */
	public static void registerMethods() {
		SourceCodeLocation runtimeLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);

		ChaincodeServer.INSTANCE.getUnit()
				.addInstanceConstruct(new Start(runtimeLocation, ChaincodeServer.INSTANCE.getUnit()));

	}

	@Override
	public String toString() {
		return "shim.ChaincodeServer";
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
