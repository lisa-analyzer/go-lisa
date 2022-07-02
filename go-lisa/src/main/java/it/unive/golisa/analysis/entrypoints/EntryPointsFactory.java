package it.unive.golisa.analysis.entrypoints;

import java.util.Set;

/**
 * The class is a factory for the creation of entrypoint sets.
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class EntryPointsFactory {

	/**
	 * Yields the set of entrypoints for the target framework.
	 * 
	 * @param framework the target framework
	 * 
	 * @return the set of entrypoints for the target framework, otherwise, a set
	 *             contained a main method signature.
	 */
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
				entryPoints.add("main");
			}
		};
	}
}
