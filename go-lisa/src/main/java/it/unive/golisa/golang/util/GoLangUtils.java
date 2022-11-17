package it.unive.golisa.golang.util;

import it.unive.golisa.golang.api.signature.FuncGoLangApiSignature;
import it.unive.golisa.golang.api.signature.MethodGoLangApiSignature;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.symbolic.value.Variable;
import java.util.Map;
import java.util.Set;

/**
 * This class contains useful utility methods and constants for handle GoLang
 * statements.
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class GoLangUtils {

	/**
	 * The name of the black identifier.
	 */
	public static final String BLANK_IDENTIFIER = "_";

	/**
	 * The file name of the source code location of code members belonging to
	 * run-times.
	 */
	public static final String GO_RUNTIME_SOURCE = "go-runtime";

	/**
	 * The file name of the source code location of unknown code members.
	 */
	public static final String GO_UNKNOWN_SOURCE = "unknown";

	public static final SourceCodeLocation GO_RUNTIME_SOURCECODE_LOCATION = new SourceCodeLocation(GO_RUNTIME_SOURCE, 0,
			0);

	/**
	 * Checks whether the variable {@code v} is blank.
	 * 
	 * @param v the variable
	 * 
	 * @return whether the variable {@code v} is blank
	 */
	public static boolean isBlankIdentifier(Variable v) {
		return v.getName().equals(BLANK_IDENTIFIER);
	}

	/**
	 * Checks whether the expression {@code exp} is an instanceof
	 * {@link VariableRef} and it is blank.
	 * 
	 * @param e the expression
	 * 
	 * @return whether the expression {@code exp} is an instanceof
	 *             {@link VariableRef} and it
	 */
	public static boolean refersToBlankIdentifier(Expression exp) {
		return exp instanceof VariableRef && ((VariableRef) exp).getName().equals(BLANK_IDENTIFIER);
	}

	public static Map<String, Set<MethodGoLangApiSignature>> getGoLangApiMethodSignatures() {
		return GoLangAPISignatureMapper.getGoApiSignatures().getMapMethod();
	}

	public static Map<String, Set<FuncGoLangApiSignature>> getGoLangApiFunctionSignatures() {
		return GoLangAPISignatureMapper.getGoApiSignatures().getMapFunc();
	}

	public static Set<String> getGoLangApiPackageSignatures() {
		return GoLangAPISignatureMapper.getGoApiSignatures().getPackages();
	}
}
