package it.unive.golisa.analysis.entrypoints;

import java.util.HashSet;
import java.util.Set;

/**
 * This class represents the set of entry point signatures 
 * for a program/application/framework.
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 * 
 */
public abstract class EntryPointSet {
	private final Set<String> entryPoints;

	/**
	 * Builds a new instance of a set of entry points
	 */
	protected EntryPointSet() {
		entryPoints = new HashSet<String>();
		build(entryPoints);
	}

	/**
	 * Build the set of entry points, adding the signatures.
	 * 
	 * @param entryPoints, the set of entry points.
	 */
	protected abstract void build(Set<String> entryPoints);

	/**
	 * Yields the set of entry points.
	 * 
	 * @return the set of entry points.
	 */
	public Set<String> getEntryPoints() {
		return entryPoints;
	}

}
