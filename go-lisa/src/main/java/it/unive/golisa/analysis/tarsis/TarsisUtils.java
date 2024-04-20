package it.unive.golisa.analysis.tarsis;

import it.unive.tarsis.AutomatonString;

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
		return state1.getAutomatonString().mayBeEqualTo(state2.getAutomatonString())
				|| state1.getAutomatonString().mayContain(state2.getAutomatonString())
				|| state2.getAutomatonString().mayContain(state1.getAutomatonString());
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
		AutomatonString emptyString = new AutomatonString("");
		if (emptyString.mayBeEqualTo(state.getAutomatonString()))
			return "";
		else if (!state.getAutomatonString().getAutomaton().acceptsTopEventually())
			return state.getAutomatonString().toString();

		return null;
	}
}