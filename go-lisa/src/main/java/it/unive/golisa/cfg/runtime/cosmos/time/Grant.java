package it.unive.golisa.cfg.runtime.cosmos.time;

import it.unive.golisa.cfg.runtime.time.type.Time;
import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.SourceCodeLocation;

/**
 * A Grant type.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class Grant extends GoStructType {

	/**
	 * Unique instance of the {@link Grant} type.
	 */
	public static final Grant INSTANCE = new Grant();

	private Grant() {
		this("Grant", buildGrantUnit());
	}

	private Grant(String name, CompilationUnit unit) {
		super(name, unit);
	}

	private static CompilationUnit buildGrantUnit() {
		SourceCodeLocation unknownLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
		CompilationUnit grantUnit = new CompilationUnit(unknownLocation, "Grant", false);
		grantUnit.addGlobal(new Global(unknownLocation, "Expiration", Time.INSTANCE));
		return grantUnit;
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
}
