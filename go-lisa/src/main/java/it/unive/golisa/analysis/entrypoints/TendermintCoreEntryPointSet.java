package it.unive.golisa.analysis.entrypoints;

import java.util.Set;

/**
 * This class represents the set of entry point signatures for the Go API
 * related to the Tendermint Core framework.
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class TendermintCoreEntryPointSet extends EntryPointSet {

	@Override
	protected void build(Set<String> entryPoints) {
		entryPoints.add("BeginBlock");
		entryPoints.add("DeliverTx");
		entryPoints.add("EndBlock");
		entryPoints.add("Commit");
	}

}
