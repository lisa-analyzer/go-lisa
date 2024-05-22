package it.unive.golisa.loader.annotation.sets;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import it.unive.golisa.golang.api.signature.FuncGoLangApiSignature;
import it.unive.golisa.golang.api.signature.MethodGoLangApiSignature;
import it.unive.golisa.golang.util.GoLangAPISignatureLoader;
import it.unive.lisa.program.cfg.statement.call.Call.CallType;

/**
 * The class represents the set of annotations for the non-determinism analysis
 * related to Go APIs.
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class GoNonDeterminismAnnotationSet extends NonDeterminismAnnotationSet {

	/**
	 * Builds an instance of an annotation set for non-determinism related to
	 * Go.
	 */
	public GoNonDeterminismAnnotationSet() {
		super("go-runtimes");
	}

	static {

		try {
			InputStream input = GoNonDeterminismAnnotationSet.class
					.getResourceAsStream("/for-analysis/nondeterm_sources.txt");

			Map<Pair<String, CallType>, Set<String>> map = new HashMap<>();

			GoLangAPISignatureLoader loader = new GoLangAPISignatureLoader(input);

			for (Entry<String, ? extends Set<FuncGoLangApiSignature>> e : loader.getFunctionAPIs().entrySet())
				for (FuncGoLangApiSignature sig : e.getValue()) {
					map.putIfAbsent(Pair.of(e.getKey(), CallType.STATIC), new HashSet<>());
					map.get(Pair.of(e.getKey(), CallType.STATIC)).add(sig.getName());
				}

			SOURCE_CODE_MEMBER_ANNOTATIONS.put(Kind.METHOD, map);

			map = new HashMap<>();

			for (Entry<String, ? extends Set<MethodGoLangApiSignature>> e : loader.getMethodAPIs().entrySet())
				for (MethodGoLangApiSignature sig : e.getValue()) {
					map.putIfAbsent(Pair.of(sig.getReceiver().replace("*", ""), CallType.INSTANCE), new HashSet<>());
					map.get(Pair.of(sig.getReceiver().replace("*", ""), CallType.INSTANCE)).add(sig.getName());
				}

			SOURCE_CONSTRUCTORS_ANNOTATIONS.put(Kind.METHOD, map);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}