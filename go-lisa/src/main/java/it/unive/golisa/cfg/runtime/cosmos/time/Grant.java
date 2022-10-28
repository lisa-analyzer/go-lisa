package it.unive.golisa.cfg.runtime.cosmos.time;

import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.ClassUnit;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.Program;

/**
 * A Grant type.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class Grant extends GoStructType {

	/**
	 * Unique instance of the {@link Grant} type.
	 */
	private static Grant INSTANCE;

	private Grant(String name, CompilationUnit unit) {
		super(name, unit);
	}

	@Override
	public String toString() {
		return "Grant";
	}

	@Override
	public boolean equals(Object other) {
		return this == other;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}

	public static Grant getGrantType(Program program) {
		if (INSTANCE == null) {
			ClassUnit grantUnit = new ClassUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, program, "Grant", false);
			grantUnit.addGlobal(new Global(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, grantUnit, "Expiration", true,
					GoStructType.get("Time")));
			INSTANCE = new Grant("Grant", grantUnit);
		}
		
		return INSTANCE;
	}
}
