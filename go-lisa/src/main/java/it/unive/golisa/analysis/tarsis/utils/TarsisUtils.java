package it.unive.golisa.analysis.tarsis.utils;

import it.unive.lisa.analysis.string.tarsis.Tarsis;

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
	public static boolean possibleEqualsMatch(Tarsis state1, Tarsis state2) {
		return state1.getAutomaton().isEqualTo(state2.getAutomaton())
				|| state1.getAutomaton().isContained(state2.getAutomaton())
				|| state2.getAutomaton().isContained(state1.getAutomaton());
	}

	/**
	 * Compute if possible the exact string value from Tarsis state.
	 * 
	 * @param state the Tarsis state
	 * 
	 * @return the string value, if it is possible exactly compute the value.
	 *             Otherwise, {@code null}.
	 */
	public static String extractValueStringFromTarsisStates(Tarsis state) {
		if (state.getAutomaton().emptyString().equals(state.getAutomaton()))
			return "";
		else if (!state.getAutomaton().acceptsTopEventually())
			return state.getAutomaton().toString();

		return null;
	}
}
