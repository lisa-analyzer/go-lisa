package it.unive.golisa.cosmossdk.util;

import java.util.Map;
import java.util.Set;

import it.unive.golisa.golang.api.signature.FuncGoLangApiSignature;

/**
 * Utilities for Cosmos SDK framework.
 */
public class CosmosUtils {
	
	/**
	 * Yields the function signatures of Cosmos API.
	 * @return the function signatures
	 */
	public static Map<String, Set<FuncGoLangApiSignature>>  getCosmosApiFunctionSignatures() {
		return CosmosSDKAPISignatureMapper.getGoApiSignatures().getMapFunc();
	}

}
