package it.unive.golisa.loader.annotation.sets;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;

/**
 * The class represents the set of annotations for the untrusted cross contract invoking analysis
 * related to Hyperledger Fabric.
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class HyperledgerFabricUntrustedCrossContractInvokingAnnotationSet extends UntrustedCrossContractInvokingAnnotationSet {

	/**
	 * Builds an instance of an annotation set for non-determinism related to
	 * Hyperledger Fabric.
	 */
	public HyperledgerFabricUntrustedCrossContractInvokingAnnotationSet() {
		super("hyperledger-fabric");
	}

	static {
		Map<String, Set<Pair<String, Integer>>> map2 = new HashMap<>();

		// Hyperledger Fabric API
		map2.put("ChaincodeStub", Set.of(Pair.of("InvokeChaincode", 1)));
	
		map2.put("ChaincodeStubInterface",
				Set.of(Pair.of("InvokeChaincode", 1)));

		SINK_CONSTRUCTOR_PARAMETER_ANNOTATIONS.put(Kind.PARAM, map2);
	}
}
