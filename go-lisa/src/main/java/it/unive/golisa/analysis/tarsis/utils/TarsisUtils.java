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
	 * @param state1 the state of the first key parameter to check
	 * @param state2 the state of the second key parameter to check
	 * 
	 * @return {@code true}, if there is a possible match. Otherwise,
	 *             {@code false}.
	 */
	public static boolean possibleEqualsMatch(RegexAutomaton a1, RegexAutomaton a2) {
		return a1.isEqualTo(a2)	|| a1.isContained(a2) || a2.isContained(a1);
	}

	/**
	 * Compute if possible the exact string value from Tarsis state.
	 * 
	 * @param state the Tarsis state
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
