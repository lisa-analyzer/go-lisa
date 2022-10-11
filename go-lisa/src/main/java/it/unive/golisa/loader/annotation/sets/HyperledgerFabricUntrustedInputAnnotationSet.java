package it.unive.golisa.loader.annotation.sets;


import java.util.HashMap;
import java.util.Map;

import java.util.Set;

/**
 * The class represents the set of annotations for the untrusted inputs
 * related to Go APIs.
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class HyperledgerFabricUntrustedInputAnnotationSet extends UntrustedCrossContractInvokingAnnotationSet {

	/**
	 * Builds an instance of an annotation set for non-determinism related to
	 * Go.
	 */
	public HyperledgerFabricUntrustedInputAnnotationSet() {
		super("hyperledger-fabric");
	}

	static {
			Map<String, Set<String>> map = new HashMap<>();
			
			map.put("ChaincodeStub", Set.of("GetArgs", "GetStringArgs", "GetFunctionAndParameters", "GetArgsSlice"));
			map.put("ChaincodeStubInterface", Set.of("GetArgs", "GetStringArgs", "GetFunctionAndParameters", "GetArgsSlice"));
			
			SOURCE_CODE_MEMBER_ANNOTATIONS.put(Kind.METHOD, map);
			SOURCE_CONSTRUCTORS_ANNOTATIONS.put(Kind.METHOD, map);

	}
}
