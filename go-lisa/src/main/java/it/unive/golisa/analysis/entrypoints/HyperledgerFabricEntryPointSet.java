package it.unive.golisa.analysis.entrypoints;

import java.util.Set;

public class HyperledgerFabricEntryPointSet extends EntryPointSet {

	@Override
	protected void build(Set<String> entryPoints) {
		entryPoints.add("Init");
		entryPoints.add("Invoke");
	}

}
