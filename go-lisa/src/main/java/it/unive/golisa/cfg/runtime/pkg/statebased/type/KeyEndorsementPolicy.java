package it.unive.golisa.cfg.runtime.pkg.statebased.type;

import it.unive.golisa.cfg.type.composite.GoInterfaceType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.SourceCodeLocation;

public class KeyEndorsementPolicy extends GoInterfaceType {

	public static final KeyEndorsementPolicy INSTANCE = new KeyEndorsementPolicy();

	private KeyEndorsementPolicy() {
		this("KeyEndorsementPolicy", buildKeyEndorsementPolicyUnit());
	}

	private KeyEndorsementPolicy(String name, CompilationUnit unit) {
		super(name, unit);
	}

	private static CompilationUnit buildKeyEndorsementPolicyUnit() {
		SourceCodeLocation unknownLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
		CompilationUnit chaincodeType = new CompilationUnit(unknownLocation, "KeyEndorsementPolicy", false);

		return chaincodeType;
	}

	@Override
	public String toString() {
		return "statebased.KeyEndorsementPolicy";
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
