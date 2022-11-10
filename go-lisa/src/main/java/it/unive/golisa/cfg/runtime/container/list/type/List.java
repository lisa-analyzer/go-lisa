package it.unive.golisa.cfg.runtime.container.list.type;

import it.unive.golisa.cfg.runtime.container.list.function.New;
import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.ClassUnit;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Program;

/**
 * A List type.
 * 
 * @link https://pkg.go.dev/container/list#List
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class List extends GoStructType {

	/**
	 * Unique instance of {@link List} type.
	 */
	public static List INSTANCE;

	private List(String name, CompilationUnit unit) {
		super(name, unit);
	}

	@Override
	public String toString() {
		return "container/list.List";
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
	 * Yields the {@link List} type.
	 * 
	 * @param program the program to which this type belongs
	 * 
	 * @return the {@link List} type
	 */
	public static List getListType(Program program) {
		if (INSTANCE == null) {
			ClassUnit listUnit = new ClassUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, program, "list",
					false);
			INSTANCE = new List("List", listUnit);
			return INSTANCE;
		}

		return INSTANCE;
	}

	/**
	 * Registers the methods of List type.
	 */
	public static void registerMethods() {
		CompilationUnit listUnit = INSTANCE.getUnit();
		listUnit.addInstanceCodeMember(new New(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, listUnit));
	}
}
