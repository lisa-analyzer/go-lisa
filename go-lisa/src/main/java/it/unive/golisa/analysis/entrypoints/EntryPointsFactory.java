package it.unive.golisa.analysis.entrypoints;

import java.util.Set;

public class EntryPointsFactory {

	public static EntryPointSet getEntryPoints(String framework) {

		if (framework != null)
			if (framework.equalsIgnoreCase("HYPERLEDGER-FABRIC")) {
				return new HyperledgerFabricEntryPointSet();

			} else if (framework.equalsIgnoreCase("TENDERMINT-CORE")) {
				return new TendermintCoreEntryPointSet();

			} else if (framework.equalsIgnoreCase("COSMOS-SDK")) {
				return new CosmosSDKEntryPointSet();
			}

		return new EntryPointSet() {

			@Override
			protected void build(Set<String> entryPoints) {
				entryPoints.add("Main");
			}
		};
	}
}
