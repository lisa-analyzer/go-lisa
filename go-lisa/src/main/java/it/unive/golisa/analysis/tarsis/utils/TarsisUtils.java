package it.unive.golisa.analysis.tarsis.utils;

import it.unive.lisa.analysis.string.tarsis.Tarsis;
import it.unive.lisa.lattices.string.tarsis.RegexAutomaton;

/**
 * Utility class for {@link Tarsis}.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class TarsisUtils {

	/**
	 * Compute if there is a possible equal match between two key values.
	 * 
	 * @param a1 the automaton of the first key parameter to check
	 * @param a2 the automaton of the second key parameter to check
	 * 
	 * @return {@code true}, if there is a possible match. Otherwise,
	 *             {@code false}.
	 */
	public static boolean possibleEqualsMatch(RegexAutomaton a1, RegexAutomaton a2) {
		return a1.isEqualTo(a2) || a1.isContained(a2) || a2.isContained(a1);
	}

	/**
	 * Compute if possible the exact string value from a string automaton.
	 * 
	 * @param a the automaton
	 * 
	 * @return the string value, if it is possible exactly compute the value.
	 *             Otherwise, {@code null}.
	 */
	public static String extractValueStringFromTarsisStates(RegexAutomaton a) {
		if (a.emptyString().equals(a))
			return "";
		else if (!a.acceptsTopEventually())
			return a.toString();

		return null;
	}
}
