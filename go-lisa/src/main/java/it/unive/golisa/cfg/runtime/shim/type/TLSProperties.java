package it.unive.golisa.cfg.runtime.shim.type;

import it.unive.golisa.cfg.type.GoBoolType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt8Type;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.SourceCodeLocation;

/**
 * A TLSProperties type.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class TLSProperties extends GoStructType {

	/**
	 * Unique instance of the {@link TLSProperties} type.
	 */
	public static final TLSProperties INSTANCE = new TLSProperties();

	private TLSProperties() {
		this("TLSProperties", buildTLSPropertiesUnit());
	}

	private TLSProperties(String name, CompilationUnit unit) {
		super(name, unit);
	}

	private static CompilationUnit buildTLSPropertiesUnit() {
		SourceCodeLocation unknownLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
		CompilationUnit TLSPropertiesType = new CompilationUnit(unknownLocation, "TLSProperties", false);
		TLSPropertiesType.addGlobal(new Global(unknownLocation, "Disabled", GoBoolType.INSTANCE));
		GoSliceType byteSliceType = GoSliceType.lookup(GoSliceType.lookup(GoUInt8Type.INSTANCE));
		TLSPropertiesType.addGlobal(new Global(unknownLocation, "Key", byteSliceType));
		TLSPropertiesType.addGlobal(new Global(unknownLocation, "Cert", byteSliceType));
		TLSPropertiesType.addGlobal(new Global(unknownLocation, "ClientCACerts", byteSliceType));

		return TLSPropertiesType;
	}

	@Override
	public String toString() {
		return "shim.TLSProperties";
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
