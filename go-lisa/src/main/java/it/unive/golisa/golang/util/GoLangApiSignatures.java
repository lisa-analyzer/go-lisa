package it.unive.golisa.golang.util;

import java.util.Map;
import java.util.Set;

import it.unive.golisa.golang.api.signature.GoLangApiSignature;

/**
 * Go Lang Api Signatures
 * 
 * Handle a map that contains the Go Lang API information
 * It is handled using a singleton design patter in order to avoid multiple parsing
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
class GoLangApiSignatures {
	
	private static GoLangApiSignatures instance = null;
	
    // singleton pattern
    public static synchronized GoLangApiSignatures getGoApiSignatures(){
        if (instance == null) {
        	instance = new GoLangApiSignatures();
        }
        return instance;
    }
	
	private final Map<String, Set<GoLangApiSignature>> mapPackages;
	
	private GoLangApiSignatures() {
		mapPackages = GoLangApiParser.parseFiles("/go-api");
	}

	public Map<String, Set<GoLangApiSignature>> getMapPackages() {
		return mapPackages;
	}
	

}
