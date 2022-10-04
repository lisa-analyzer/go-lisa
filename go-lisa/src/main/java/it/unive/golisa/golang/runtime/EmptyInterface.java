package it.unive.golisa.golang.runtime;

import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.SyntheticLocation;

/**
 * The empty interface.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class EmptyInterface extends CompilationUnit {

	/**
	 * Unique instance of the empty interface.
	 */
	public static final EmptyInterface INSTANCE = new EmptyInterface();

	private EmptyInterface() {
		super(SyntheticLocation.INSTANCE, "EmptyInterface", false);
	}
}
