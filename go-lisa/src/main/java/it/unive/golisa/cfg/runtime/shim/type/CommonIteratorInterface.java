package it.unive.golisa.cfg.runtime.shim.type;

import it.unive.golisa.cfg.type.GoBoolType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoInterfaceType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.InterfaceUnit;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.cfg.AbstractCodeMember;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;

/**
 * A CommonIteratorInterface type.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class CommonIteratorInterface extends GoInterfaceType {

	/**
	 * Unique instance of {@link CommonIteratorInterface} type.
	 */
//	public static final CommonIteratorInterface INSTANCE = new CommonIteratorInterface();
//
//	private CommonIteratorInterface() {
//		this("", buildCommonIteratorInterfaceUnit());
//	}

	private CommonIteratorInterface(CompilationUnit unit) {
		super("CommonIteratorInterface", unit);
	}

	public static CommonIteratorInterface getCommonIteratorInterfaceType(Program program) {
		InterfaceUnit commonIteratorInterfeceUnit = new InterfaceUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION,
				program, "CommonIteratorInterface",
				false);

		CodeMemberDescriptor desc = new CodeMemberDescriptor(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION,
				commonIteratorInterfeceUnit, true, "HasNext",
				GoBoolType.INSTANCE);
		commonIteratorInterfeceUnit.addInstanceCodeMember(new AbstractCodeMember(desc));

		desc = new CodeMemberDescriptor(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, commonIteratorInterfeceUnit, true,
				"Close", GoErrorType.INSTANCE);
		commonIteratorInterfeceUnit.addInstanceCodeMember(new AbstractCodeMember(desc));

		return new CommonIteratorInterface(commonIteratorInterfeceUnit);
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
