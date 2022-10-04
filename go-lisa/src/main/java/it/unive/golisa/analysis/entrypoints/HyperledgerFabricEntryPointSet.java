package it.unive.golisa.analysis.entrypoints;

import java.util.Set;

/**
 * This class represents the set of entry point signatures for the Go API
 * related to the Hyperledger Fabric framework.
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class HyperledgerFabricEntryPointSet extends EntryPointSet {

	@Override
	protected void build(Set<String> entryPoints) {
		entryPoints.add("Init");
		entryPoints.add("Invoke");
		entryPoints.add("reflectMetadata");
	}

}
