package it.unive.golisa.cfg.runtime.shim.type;

import it.unive.golisa.cfg.runtime.shim.method.Close;
import it.unive.golisa.cfg.runtime.shim.method.HasNext;
import it.unive.golisa.cfg.runtime.shim.method.Next;
import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.ClassUnit;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Program;

/**
 * The HistoryQueryIterator type.
 * 
 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
 */
public class HistoryQueryIterator extends GoStructType {

	/**
	 * Unique instance of the {@link Handler} type.
	 */
	private static HistoryQueryIterator INSTANCE;

	private HistoryQueryIterator(CompilationUnit unit) {
		super("HistoryQueryIterator", unit);
	}

	/**
	 * Yields the {@link Handler} type.
	 * 
	 * @param program the program to which this type belongs
	 * 
	 * @return the {@link Handler} type
	 */
	public static HistoryQueryIterator getStateQueryIterator(Program program) {
		if (INSTANCE == null) {
			ClassUnit historyQueryIteratorUnit = new ClassUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, program,
					"HistoryQueryIterator",
					false);

			// add superclasses and implemented interfaces
			historyQueryIteratorUnit
					.addAncestor(HistoryQueryIteratorInterface.getHistoryQueryIteratorInterfaceType(program).getUnit());

			INSTANCE = new HistoryQueryIterator(historyQueryIteratorUnit);
		}

		return INSTANCE;
	}

	/**
	 * Registers the methods of the {@link HistoryQueryIterator} type.
	 */
	public static void registerMethods() {
		CompilationUnit historyQueryIteratorUnit = INSTANCE.getUnit();
		historyQueryIteratorUnit
				.addInstanceCodeMember(
						new Close(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, historyQueryIteratorUnit));
		historyQueryIteratorUnit
				.addInstanceCodeMember(
						new HasNext(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, historyQueryIteratorUnit));

		historyQueryIteratorUnit
				.addInstanceCodeMember(
						new Next(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, historyQueryIteratorUnit));
	}

	@Override
	public String toString() {
		return "shim.HistoryQueryIterator";
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
