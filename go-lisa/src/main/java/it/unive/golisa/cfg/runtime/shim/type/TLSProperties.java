package it.unive.golisa.cfg.runtime.shim.type;

import it.unive.golisa.cfg.type.GoBoolType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt8Type;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.ClassUnit;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.Program;

/**
 * A TLSProperties type.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class TLSProperties extends GoStructType {

	/**
	 * Unique instance of the {@link TLSProperties} type.
	 */
	private static TLSProperties INSTANCE;

	private TLSProperties(CompilationUnit unit) {
		super("TLSProperties", unit);
	}

	public static TLSProperties getTLSPropertiesType(Program program) {
		if (INSTANCE == null) {
			ClassUnit TLSPropertiesType = new ClassUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, program,
					"TLSProperties", false);

			// add globals
			TLSPropertiesType.addGlobal(new Global(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, TLSPropertiesType,
					"Disabled", true, GoBoolType.INSTANCE));
			GoSliceType byteSliceType = GoSliceType.lookup(GoSliceType.lookup(GoUInt8Type.INSTANCE));
			TLSPropertiesType.addGlobal(
					new Global(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, TLSPropertiesType, "Key", true,
							byteSliceType));
			TLSPropertiesType.addGlobal(
					new Global(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, TLSPropertiesType, "Cert", true,
							byteSliceType));
			TLSPropertiesType.addGlobal(new Global(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, TLSPropertiesType,
					"ClientCACerts", true, byteSliceType));

			INSTANCE = new TLSProperties(TLSPropertiesType);
		}

		return INSTANCE;
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
