package it.unive.golisa.loader;

import it.unive.lisa.program.Program;

/**
 * The interface represents a component ables to load information into a
 * program.
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public interface Loader {

	/**
	 * The method loads the information into a program.
	 * 
	 * @param program to load the information
	 */
	void load(Program program);
}
