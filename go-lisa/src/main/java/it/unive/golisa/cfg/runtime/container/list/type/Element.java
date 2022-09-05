package it.unive.golisa.cfg.runtime.container.list.type;

import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.type.Untyped;

/**
 * A Element type of List .
 * 
 * @link https://pkg.go.dev/container/list#Element
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class Element extends GoStructType {

	/**
	 * Unique instance of Element type.
	 */
	public static final Element INSTANCE = new Element();

	private Element() {
		this("Element", buildElementUnit());
	}

	private Element(String name, CompilationUnit unit) {
		super(name, unit);
	}

	private static CompilationUnit buildElementUnit() {
		SourceCodeLocation unknownLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
		CompilationUnit unit = new CompilationUnit(unknownLocation, "Element", false);
		unit.addGlobal(new Global(unknownLocation, "Value", Untyped.INSTANCE));
		return unit;
	}

	/**
	 * Registers methods of List.
	 */
	public static void registerMethods() {
		// TODO: add methods
	}
	
	

	@Override
	public String toString() {
		return "container.list.Element";
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
