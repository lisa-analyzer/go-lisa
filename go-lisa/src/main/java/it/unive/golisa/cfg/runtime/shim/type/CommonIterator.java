package it.unive.golisa.cfg.runtime.shim.type;

import it.unive.golisa.cfg.runtime.shim.method.Close;
import it.unive.golisa.cfg.runtime.shim.method.HasNext;
import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.ClassUnit;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Program;

public class CommonIterator extends GoStructType {
	
	/**
	 * Unique instance of the {@link CommonIterator} type.
	 */
	private static CommonIterator INSTANCE;

	private CommonIterator(CompilationUnit unit) {
		super("CommonIterator", unit);
	}

	/**
	 * Yields the {@link CommonIterator} type.
	 * 
	 * @param program the program to which this type belongs
	 * 
	 * @return the {@link CommonIterator} type
	 */
	public static CommonIterator getCommonIteratorType(Program program) {
		if (INSTANCE == null) {
			ClassUnit commonIteratorUnit = new ClassUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, program,
					"CommonIterator", false);
			// add superclasses and implemented interfaces
			commonIteratorUnit.addAncestor(CommonIteratorInterface.getCommonIteratorInterfaceType(program).getUnit());

			INSTANCE = new CommonIterator(commonIteratorUnit);
		}

		return INSTANCE;
	}

	@Override
	public String toString() {
		return "shim.CommonIterator";
	}

	@Override
	public boolean equals(Object other) {
		return this == other;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}
	
	/**
	 * Registers the instance methods of this type.
	 */
	public static void registerMethods() {
		CompilationUnit commonIteratorUnit = INSTANCE.getUnit();
		commonIteratorUnit.addInstanceCodeMember(new Close(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, commonIteratorUnit));
		commonIteratorUnit.addInstanceCodeMember(new HasNext(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, commonIteratorUnit));
	}
}
