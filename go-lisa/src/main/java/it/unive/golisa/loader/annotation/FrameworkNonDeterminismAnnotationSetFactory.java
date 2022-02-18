package it.unive.golisa.loader.annotation;

import it.unive.golisa.loader.annotation.sets.CosmosSDKNonDeterminismAnnotationSet;
import it.unive.golisa.loader.annotation.sets.GoNonDeterminismAnnotationSet;
import it.unive.golisa.loader.annotation.sets.HyperledgerFabricNonDeterminismAnnotationSet;
import it.unive.golisa.loader.annotation.sets.NonDeterminismAnnotationSet;
import it.unive.golisa.loader.annotation.sets.TendermintCoreNonDeterminismAnnotationSet;

public class FrameworkNonDeterminismAnnotationSetFactory {

	public static NonDeterminismAnnotationSet[] getAnnotationSets(String framework) {

		if (framework != null) {
			NonDeterminismAnnotationSet specificFrameworkAnnotationSet = null;
			
			if (framework.equalsIgnoreCase("HYPERLEDGER-FABRIC")) {
				specificFrameworkAnnotationSet = new HyperledgerFabricNonDeterminismAnnotationSet();

			} else if (framework.equalsIgnoreCase("TENDERMINT-CORE")) {
				specificFrameworkAnnotationSet = new TendermintCoreNonDeterminismAnnotationSet();

			} else if (framework.equalsIgnoreCase("COSMOS-SDK")) {
				specificFrameworkAnnotationSet = new CosmosSDKNonDeterminismAnnotationSet();
			}
			
			if(specificFrameworkAnnotationSet != null)
				return new NonDeterminismAnnotationSet[] {new GoNonDeterminismAnnotationSet(), specificFrameworkAnnotationSet};
		}
		
		return new NonDeterminismAnnotationSet[] {new GoNonDeterminismAnnotationSet()};
	}
}
