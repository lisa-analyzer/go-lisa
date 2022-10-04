package it.unive.golisa.cfg.runtime.shim.type;

import it.unive.golisa.cfg.type.GoBoolType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoInterfaceType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CFGDescriptor;

/**
 * A CommonIteratorInterface type.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class CommonIteratorInterface extends GoInterfaceType {

	/**
	 * Unique instance of {@link CommonIteratorInterface} type.
	 */
	public static final CommonIteratorInterface INSTANCE = new CommonIteratorInterface();

	private CommonIteratorInterface() {
		this("CommonIteratorInterface", buildCommonIteratorInterfaceUnit());
	}

	private CommonIteratorInterface(String name, CompilationUnit unit) {
		super(name, unit);
	}

	private static CompilationUnit buildCommonIteratorInterfaceUnit() {
		SourceCodeLocation unknownLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
		CompilationUnit commonIteratorInterfeceUnit = new CompilationUnit(unknownLocation, "CommonIteratorInterface",
				false);

		CFGDescriptor desc = new CFGDescriptor(unknownLocation, commonIteratorInterfeceUnit, true, "HasNext",
				GoBoolType.INSTANCE);
		commonIteratorInterfeceUnit.addInstanceCFG(new CFG(desc));

		desc = new CFGDescriptor(unknownLocation, commonIteratorInterfeceUnit, true, "Close", GoErrorType.INSTANCE);
		commonIteratorInterfeceUnit.addInstanceCFG(new CFG(desc));

		return commonIteratorInterfeceUnit;
	}

	@Override
	public String toString() {
		return "stub.CommonIteratorInterface";
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
