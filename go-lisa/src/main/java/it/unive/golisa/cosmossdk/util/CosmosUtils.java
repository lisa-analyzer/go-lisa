package it.unive.golisa.cosmossdk.util;

import java.util.Map;
import java.util.Set;

import it.unive.golisa.golang.api.signature.FuncGoLangApiSignature;

public class CosmosUtils {
	
	public static Map<String, Set<FuncGoLangApiSignature>>  getCosmosApiFunctionSignatures() {
		return CosmosSDKAPISignatureMapper.getGoApiSignatures().getMapFunc();
	}

}
