package it.unive.golisa.cfg.runtime.shim.type;

import it.unive.golisa.cfg.runtime.shim.method.Close;
import it.unive.golisa.cfg.runtime.shim.method.HasNext;
import it.unive.golisa.cfg.runtime.shim.method.Next;
import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.ClassUnit;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Program;

public class StateQueryIterator extends GoStructType {
	/**
	 * Unique instance of the {@link Handler} type.
	 */
	private static StateQueryIterator INSTANCE;

	private StateQueryIterator(CompilationUnit unit) {
		super("StateQueryIterator", unit);
	}

	/**
	 * Yields the {@link Handler} type.
	 * 
	 * @param program the program to which this type belongs
	 * 
	 * @return the {@link Handler} type
	 */
	public static StateQueryIterator getStateQueryIterator(Program program) {
		if (INSTANCE == null) {
			ClassUnit stateQueryIteratorUnit = new ClassUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, program,
					"StateQueryIterator",
					false);

			// add superclasses and implemented interfaces
			stateQueryIteratorUnit
					.addAncestor(StateQueryIteratorInterface.getStateQueryIteratorInterfaceType(program).getUnit());

			INSTANCE = new StateQueryIterator(stateQueryIteratorUnit);
		}

		return INSTANCE;
	}

	/**
	 * Registers the methods of the {@link StateQueryIterator} type.
	 */
	public static void registerMethods() {
		CompilationUnit stateQueryIteratorUnit = INSTANCE.getUnit();
		stateQueryIteratorUnit
				.addInstanceCodeMember(
						new Close(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, stateQueryIteratorUnit));
		stateQueryIteratorUnit
				.addInstanceCodeMember(
						new HasNext(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, stateQueryIteratorUnit));

		stateQueryIteratorUnit
				.addInstanceCodeMember(
						new Next(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, stateQueryIteratorUnit));
	}

	@Override
	public String toString() {
		return "shim.StateQueryIterator";
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
