package it.unive.golisa.analysis.entrypoints;

import java.util.Set;

public class TendermintCoreEntryPointSet extends EntryPointSet{

	@Override
	protected void build(Set<String> entryPoints) {
		entryPoints.add("BeginBlock");
		entryPoints.add("DeliverTx");
		entryPoints.add("EndBlock");
		entryPoints.add("Commit");
	}

}
