package it.unive.golisa.loader.annotation.sets;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;

/**
 * The class represents the set of annotations for the phantom reads analysis
 * related to Hyperledger Fabric.
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class UCCIPhase1AnnotationSet extends TaintAnnotationSet {

	public UCCIPhase1AnnotationSet() {
		super("hyperledger-fabric");
	}

	static {

		Map<String, Set<String>> map1 = new HashMap<>();

		map1.put("ChaincodeStub", Set.of("GetArgs", "GetStringArgs", "GetFunctionAndParameters", "GetArgsSlice"));

		map1.put("ChaincodeStubInterface", Set.of("GetArgs", "GetStringArgs", "GetFunctionAndParameters", "GetArgsSlice"));
		
		SOURCE_CODE_MEMBER_ANNOTATIONS.put(Kind.METHOD, map1);

		Map<String, Set<Pair<String, Integer>>> map2 = new HashMap<>();

		map2.put("ChaincodeStub", Set.of(Pair.of("InvokeChaincode", 1), Pair.of("InvokeChaincode", 2)));

		map2.put("ChaincodeStubInterface", Set.of(Pair.of("InvokeChaincode", 1), Pair.of("InvokeChaincode", 2)));

		SINK_CONSTRUCTOR_PARAMETER_ANNOTATIONS.put(Kind.PARAM, map2);
	}

}
