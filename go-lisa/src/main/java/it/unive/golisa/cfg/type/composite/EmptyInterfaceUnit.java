package it.unive.golisa.cfg.type.composite;

import it.unive.lisa.program.InterfaceUnit;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.SyntheticLocation;

/**
 * The empty interface.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class EmptyInterfaceUnit extends InterfaceUnit {

	/**
	 * Builds the empty interface unit.
	 * 
	 * @param program the program to which this unit belongs to
	 */
	public EmptyInterfaceUnit(Program program) {
		super(SyntheticLocation.INSTANCE, program, "EmptyInterface", false);
	}
}
