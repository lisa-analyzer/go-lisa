package it.unive.golisa.loader.annotation.sets;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;

import it.unive.golisa.loader.annotation.CodeAnnotation;
import it.unive.golisa.loader.annotation.FrameworkAnnotationSet;
import it.unive.golisa.loader.annotation.AnnotationSet.Kind;

/**
 * The class represents the set of annotations for the phantom reads analysis
 * related to Hyperledger Fabric.
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class PhantomReadAnnotationSet extends TaintAnnotationSet {

	public PhantomReadAnnotationSet() {
		super("hyperledger-fabric");
	}

	static {
		
		Map<String, Set<String>> map1 = new HashMap<>();

		map1.put("ChaincodeStub", Set.of("GetQueryResult", "GetHistoryForKey", "GetPrivateDataQueryResult"));

		map1.put("ChaincodeStubInterface",
				Set.of("GetQueryResult", "GetHistoryForKey", "GetPrivateDataQueryResult"));

		SOURCE_CODE_MEMBER_ANNOTATIONS.put(Kind.METHOD, map1);
		
		Map<String, Set<Pair<String, Integer>>> map2 = new HashMap<>();

		map2.put("ChaincodeStub", Set.of(Pair.of("PutState", 1), Pair.of("PutState", 2), Pair.of("DelState", 1),
				Pair.of("PutPrivateData", 1), Pair.of("PutPrivateData", 2), Pair.of("DelPrivateData", 1)));

		map2.put("ChaincodeStubInterface",
				Set.of(Pair.of("PutState", 1), Pair.of("PutState", 2), Pair.of("DelState", 1),
						Pair.of("PutPrivateData", 1), Pair.of("PutPrivateData", 2), Pair.of("DelPrivateData", 1)));

		SINK_CONSTRUCTOR_PARAMETER_ANNOTATIONS.put(Kind.PARAM, map2);
	}
	
}
