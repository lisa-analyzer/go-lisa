package it.unive.golisa.loader.annotation.sets;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;

import it.unive.golisa.analysis.taint.TaintDomainForPhase2;
import it.unive.golisa.checker.UCCICheckerPhase2;

/**
 * The class represents the set of annotations for the phantom reads analysis
 * related to Hyperledger Fabric.
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class UCCIPhase2AnnotationSet extends TaintAnnotationSet {

	public UCCIPhase2AnnotationSet() {
		super("hyperledger-fabric", Set.of(TaintDomainForPhase2.TAINTED_ANNOTATION_PHASE2), Set.of(UCCICheckerPhase2.SINK_ANNOTATION_PHASE2),Set.of(TaintDomainForPhase2.CLEAN_ANNOTATION));
	}

	static {

		// Sources are annotated by the checker in phase 1
		
		SOURCE_CODE_MEMBER_ANNOTATIONS.put(Kind.METHOD,  new HashMap<>());
		
		Map<String, Set<Pair<String, Integer>>> map2 = new HashMap<>();

		map2.put("ChaincodeStub", Set.of(Pair.of("PutState", 1), Pair.of("PutState", 2), Pair.of("DelState", 1),
				Pair.of("SetStateValidationParameter", 1), Pair.of("SetStateValidationParameter", 2), Pair.of("SetStateValidationParameter", 3),
				Pair.of("PutPrivateData", 1), Pair.of("PutPrivateData", 2), Pair.of("PutPrivateData", 3),
				Pair.of("DelPrivateData", 1), Pair.of("DelPrivateData", 2),
				Pair.of("PurgePrivateData", 1), Pair.of("PurgePrivateData", 2),
				Pair.of("SetPrivateDataValidationParameter", 1), Pair.of("SetPrivateDataValidationParameter", 2), Pair.of("SetPrivateDataValidationParameter", 3)));

		map2.put("ChaincodeStubInterface", Set.of(Pair.of("PutState", 1), Pair.of("PutState", 2), Pair.of("DelState", 1),
				Pair.of("SetStateValidationParameter", 1), Pair.of("SetStateValidationParameter", 2), Pair.of("SetStateValidationParameter", 3),
				Pair.of("PutPrivateData", 1), Pair.of("PutPrivateData", 2), Pair.of("PutPrivateData", 3),
				Pair.of("DelPrivateData", 1), Pair.of("DelPrivateData", 2),
				Pair.of("PurgePrivateData", 1), Pair.of("PurgePrivateData", 2),
				Pair.of("SetPrivateDataValidationParameter", 1), Pair.of("SetPrivateDataValidationParameter", 2), Pair.of("SetPrivateDataValidationParameter", 3)));

		map2.put("shim", Set.of(Pair.of("Success", 0), Pair.of("Error", 0)));
		
		SINK_CONSTRUCTOR_PARAMETER_ANNOTATIONS.put(Kind.PARAM, map2);
	}

}
