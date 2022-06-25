package it.unive.golisa.golang.runtime;

import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.SyntheticLocation;

public class EmptyInterface extends CompilationUnit {

	public static final EmptyInterface INSTANCE = new EmptyInterface();

	private EmptyInterface() {
		super(SyntheticLocation.INSTANCE, "EmptyInterface", false);
	}
}
