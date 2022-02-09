package it.unive.golisa.analysis.entrypoints;

import java.util.HashSet;
import java.util.Set;

public abstract class EntryPointSet {
	private final Set<String> entryPoints;

	protected EntryPointSet() {
		entryPoints = new HashSet<String>();
		build(entryPoints);
	}

	protected abstract void build(Set<String> entryPoints);

	public Set<String> getEntryPoints() {
		return entryPoints;
	}

}
