package it.unive.golisa.loader.annotation.sets;

import it.unive.golisa.golang.api.signature.FuncGoLangApiSignature;
import it.unive.golisa.golang.api.signature.MethodGoLangApiSignature;
import it.unive.golisa.golang.util.GoLangAPISignatureLoader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * The class represents the set of annotations for the non-determinism analysis
 * related to Go APIs.
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class GoNonDeterminismAnnotationSet extends TaintAnnotationSet {

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

			Map<String, Set<String>> map = new HashMap<>();

			GoLangAPISignatureLoader loader = new GoLangAPISignatureLoader(input);

			for (Entry<String, ? extends Set<FuncGoLangApiSignature>> e : loader.getFunctionAPIs().entrySet())
				for (FuncGoLangApiSignature sig : e.getValue()) {
					map.putIfAbsent(e.getKey(), new HashSet<>());
					map.get(e.getKey()).add(sig.getName());
				}

			SOURCE_CODE_MEMBER_ANNOTATIONS.put(Kind.METHOD, map);

			map = new HashMap<>();

			for (Entry<String, ? extends Set<MethodGoLangApiSignature>> e : loader.getMethodAPIs().entrySet())
				for (MethodGoLangApiSignature sig : e.getValue()) {
					map.putIfAbsent(sig.getReceiver().replace("*", ""), new HashSet<>());
					map.get(sig.getReceiver().replace("*", "")).add(sig.getName());
				}

			SOURCE_CONSTRUCTORS_ANNOTATIONS.put(Kind.METHOD, map);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
