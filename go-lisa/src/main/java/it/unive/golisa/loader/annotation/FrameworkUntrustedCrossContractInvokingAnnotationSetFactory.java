package it.unive.golisa.loader.annotation;

import it.unive.golisa.loader.annotation.sets.HyperledgerFabricUntrustedCrossContractInvokingAnnotationSet;
import it.unive.golisa.loader.annotation.sets.HyperledgerFabricUntrustedInputAnnotationSet;
import it.unive.golisa.loader.annotation.sets.UntrustedCrossContractInvokingAnnotationSet;

/**
 * The class represents the factory of annotation set of a target frameworks.
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class FrameworkUntrustedCrossContractInvokingAnnotationSetFactory {

	/**
	 * Yields the annotation set for a specific framework.
	 * 
	 * @param framework the target framework
	 * 
	 * @return the set of annotationq
	 */
	public static UntrustedCrossContractInvokingAnnotationSet[] getAnnotationSets(String framework) {

		if (framework != null) {
			UntrustedCrossContractInvokingAnnotationSet specificFrameworkAnnotationSet = null;

			if (framework.equalsIgnoreCase("HYPERLEDGER-FABRIC")) {
				specificFrameworkAnnotationSet = new HyperledgerFabricUntrustedCrossContractInvokingAnnotationSet();

			} 

			if (specificFrameworkAnnotationSet != null)
				return new UntrustedCrossContractInvokingAnnotationSet[] { new HyperledgerFabricUntrustedInputAnnotationSet(),
						specificFrameworkAnnotationSet };
		}

		return new UntrustedCrossContractInvokingAnnotationSet[] { new HyperledgerFabricUntrustedInputAnnotationSet() };
	}
}
