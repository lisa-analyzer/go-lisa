package it.unive.golisa.cfg.runtime.shim.type;

import it.unive.golisa.cfg.type.composite.GoInterfaceType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.InterfaceUnit;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.cfg.AbstractCodeMember;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.type.Untyped;

/**
 * The HistoryQueryIteratorInterface type.
 * 
 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
 */
public class HistoryQueryIteratorInterface extends GoInterfaceType {

	/**
	 * Unique instance of {@link HistoryQueryIteratorInterface} type.
	 */
	private static HistoryQueryIteratorInterface INSTANCE;

	private HistoryQueryIteratorInterface(CompilationUnit unit) {
		super("HistoryQueryIteratorInterface", unit);
	}

	/**
	 * Yields the {@link HistoryQueryIteratorInterface} type.
	 * 
	 * @param program the program to which this type belongs
	 * 
	 * @return the {@link HistoryQueryIteratorInterface} type
	 */
	public static HistoryQueryIteratorInterface getHistoryQueryIteratorInterfaceType(Program program) {
		if (INSTANCE == null) {
			InterfaceUnit stateHistoryIteratorInterfaceUnit = new InterfaceUnit(
					GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION,
					program, "HistoryQueryIteratorInterface",
					false);
			// add superclasses and implemented interfaces
			stateHistoryIteratorInterfaceUnit
					.addAncestor(CommonIteratorInterface.getCommonIteratorInterfaceType(program).getUnit());
			HistoryQueryIteratorInterface stateHistoryIteratorInterfaceType = new HistoryQueryIteratorInterface(
					stateHistoryIteratorInterfaceUnit);

			CodeMemberDescriptor desc = new CodeMemberDescriptor(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION,
					stateHistoryIteratorInterfaceUnit, true, "Next",
					Untyped.INSTANCE,
					new Parameter(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, "this",
							stateHistoryIteratorInterfaceType));

			stateHistoryIteratorInterfaceUnit.addInstanceCodeMember(new AbstractCodeMember(desc));

			INSTANCE = stateHistoryIteratorInterfaceType;
		}

		return INSTANCE;
	}

	@Override
	public String toString() {
		return "stub.HistoryQueryIteratorInterface";
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