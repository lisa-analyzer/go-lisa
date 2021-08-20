package it.unive.golisa.util;

import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.symbolic.value.Variable;

/**
 * @GoLangUtils contains useful utility methods and constants for handle GoLang statements
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class GoLangUtils {

	public static final String BLANK_IDENTIFIER = "_";
	
	public static boolean isBlankIdentifier(Variable v) {
		return v.getName().equals(BLANK_IDENTIFIER);
	}
	
	public static boolean refersToBlankIdentifier(Expression vref) {
		return vref instanceof VariableRef && ((VariableRef) vref).getName().equals(BLANK_IDENTIFIER);
	}
}
