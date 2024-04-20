package it.unive.golisa.loader.annotation.sets;


import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.checker.TaintChecker;
import it.unive.lisa.program.cfg.statement.call.Call.CallType;


/**
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class CustomTaintAnnotationSet extends TaintAnnotationSet {

	public CustomTaintAnnotationSet(String framework, Map<Pair<String, CallType>, Set<String>> sources, Map<Pair<String, CallType>, Set<Pair<String, Integer>>> sinks) {
		super(framework, Set.of(TaintDomain.TAINTED_ANNOTATION), Set.of(TaintChecker.SINK_ANNOTATION),Set.of(TaintDomain.CLEAN_ANNOTATION));
		
		SOURCE_CODE_MEMBER_ANNOTATIONS.put(Kind.METHOD,sources);
		SINK_CONSTRUCTOR_PARAMETER_ANNOTATIONS.put(Kind.PARAM, sinks);

	}

}
