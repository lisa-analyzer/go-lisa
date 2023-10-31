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
 * The StateQueryIteratorInterface type.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class StateQueryIteratorInterface extends GoInterfaceType {

	/**
	 * Unique instance of {@link StateQueryIteratorInterface} type.
	 */
	private static StateQueryIteratorInterface INSTANCE;

	private StateQueryIteratorInterface(CompilationUnit unit) {
		super("StateQueryIteratorInterface", unit);
	}

	/**
	 * Yields the {@link StateQueryIteratorInterface} type.
	 * 
	 * @param program the program to which this type belongs
	 * 
	 * @return the {@link StateQueryIteratorInterface} type
	 */
	public static StateQueryIteratorInterface getStateQueryIteratorInterfaceType(Program program) {
		if (INSTANCE == null) {
			InterfaceUnit stateQueryIteratorInterfaceUnit = new InterfaceUnit(
					GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION,
					program, "StateQueryIteratorInterface",
					false);
			// add superclasses and implemented interfaces
			stateQueryIteratorInterfaceUnit
					.addAncestor(CommonIteratorInterface.getCommonIteratorInterfaceType(program).getUnit());
			StateQueryIteratorInterface stateQueryIteratorInterfaceeType = new StateQueryIteratorInterface(
					stateQueryIteratorInterfaceUnit);

			CodeMemberDescriptor desc = new CodeMemberDescriptor(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION,
					stateQueryIteratorInterfaceUnit, true, "Next",
					Untyped.INSTANCE,
					new Parameter(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, "this",
							stateQueryIteratorInterfaceeType));

			stateQueryIteratorInterfaceUnit.addInstanceCodeMember(new AbstractCodeMember(desc));

			INSTANCE = stateQueryIteratorInterfaceeType;
		}

		return INSTANCE;
	}

	@Override
	public String toString() {
		return "stub.StateQueryIteratorInterface";
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