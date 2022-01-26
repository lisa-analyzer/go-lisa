package it.unive.golisa.cfg.runtime.shim.type;

import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.SourceCodeLocation;

public class ChaincodeStub extends GoStructType {
	public static final ChaincodeStub INSTANCE = new ChaincodeStub();
	
	private ChaincodeStub() {
		this("ChaincodeStub", buildChaincodeStubUnit());
	}

	private ChaincodeStub(String name, CompilationUnit unit) {
		super(name, unit);
	}

	private static CompilationUnit buildChaincodeStubUnit() {
		SourceCodeLocation unknownLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
		CompilationUnit chaincodeStubUnit = new CompilationUnit(unknownLocation, "ChaincodeStub", false);
		
		// Add globals
		chaincodeStubUnit.addGlobal(new Global(unknownLocation, "TxID", GoStringType.INSTANCE));
		chaincodeStubUnit.addGlobal(new Global(unknownLocation, "ChannelID", GoStringType.INSTANCE));
		return chaincodeStubUnit;
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
