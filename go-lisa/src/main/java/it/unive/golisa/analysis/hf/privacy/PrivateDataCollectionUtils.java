 package it.unive.golisa.analysis.hf.privacy;

public class PrivateDataCollectionUtils {
	
	public static final String IMPLICIT_COLLECTION_PREFIX = "implicit_org_";
	
	public static boolean isImplicitCollection(String collectionName) {
		
		return collectionName.startsWith(collectionName);
	}
	
	

}
