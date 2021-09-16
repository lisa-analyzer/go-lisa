package it.unive.golisa.golang.util;

import java.util.Map;
import java.util.Set;
import it.unive.golisa.golang.api.signature.GoLangApiSignature;
import it.unive.lisa.program.cfg.statement.Expression;import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.symbolic.value.Variable;

/**
 * @GoLangUtils contains useful utility methods and constants for handle GoLang statements
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class GoLangUtils {

	public static final String BLANK_IDENTIFIER = "_";
	
	public static final String GO_RUNTIME_SOURCE = "go-runtime";
	public static final String GO_UNKNOWN_SOURCE = "unknown";
	
	public static boolean isBlankIdentifier(Variable v) {
		return v.getName().equals(BLANK_IDENTIFIER);
	}
	
	public static boolean refersToBlankIdentifier(Expression vref) {
		return vref instanceof VariableRef && ((VariableRef) vref).getName().equals(BLANK_IDENTIFIER);
	}
	
	/**
	 * Yield the mapping by package of Go Lang API
	 * @return the mapping by package of Go Lang API
	 */
	public static Map<String, Set<GoLangApiSignature>> getGoLangApiSignatures() {
		return GoLangApiSignatures.getGoApiSignatures().getMapPackages();
	}
}
