package benchmarks.tarsis;

import static benchmarks.tarsis.TarsisJournalEvaluation.GEN;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import it.unive.lisa.analysis.string.tarsis.RegexAutomaton;
import it.unive.lisa.analysis.string.tarsis.Tarsis;
import it.unive.lisa.util.datastructures.automaton.Automaton;
import it.unive.lisa.util.datastructures.automaton.State;
import it.unive.lisa.util.datastructures.automaton.Transition;
import it.unive.lisa.util.datastructures.regex.Atom;
import it.unive.lisa.util.datastructures.regex.RegularExpression;
import it.unive.lisa.util.datastructures.regex.TopAtom;

public class AutomataGenerator {
	private static final State qinit = new State(0, true, false);
	private static final State qinitfinal = new State(0, true, true);
	private static final State qfinal = new State(1, false, true);

	/**
	 * Random atomic regular expression of maximum length {@code maxLength}. If
	 * {@code canBeTop} is {@code true}, there is a {@code chanceOfTop}
	 * probability of the returned value being {@link TopAtom#INSTANCE}.
	 */
	private static RegularExpression randomString(int maxLength, boolean canBeTop, double chanceOfTop) {
		if (canBeTop && GEN.nextDouble() <= chanceOfTop)
			return TopAtom.INSTANCE;

		String ALPHA_NUMERIC_STRING = "0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
		int len = GEN.nextInt(maxLength);
		int init = GEN.nextInt(ALPHA_NUMERIC_STRING.length() - len);
		int end = init + len;

		return new Atom(ALPHA_NUMERIC_STRING.substring(init, end));
	}

	/**
	 * Automaton with two states joined by one transition having a non-top atom
	 * of maximum length 10.
	 */
	static Tarsis atom() {
		SortedSet<State> states = new TreeSet<>();
		states.add(qinit);
		states.add(qfinal);
		SortedSet<Transition<RegularExpression>> delta = new TreeSet<>();
		delta.add(new Transition<>(qinit, qfinal, randomString(10, false, 0.0)));
		return new Tarsis(new RegexAutomaton(states, delta));
	}

	/**
	 * Automaton with two states joined by one transition having a top atom.
	 */
	static Tarsis top() {
		SortedSet<State> states = new TreeSet<>();
		states.add(qinit);
		states.add(qfinal);
		SortedSet<Transition<RegularExpression>> delta = new TreeSet<>();
		delta.add(new Transition<>(qinit, qfinal, randomString(0, true, 2.0)));
		return new Tarsis(new RegexAutomaton(states, delta));
	}

	/**
	 * Automaton with 2 to 5 states, modeling a single string obtained by
	 * concatenating constants. Only the last state is final, and there is a
	 * single path in the automaton. Each transition has a non-top atom of
	 * maximum length 10.
	 */
	static Tarsis concatConstants() {
		int n = 2 + GEN.nextInt(4);
		List<State> states = new ArrayList<>(n + 1);
		states.add(qinit);
		for (int i = 2; i < n + 1; i++)
			states.add(new State(i, false, false));
		states.add(qfinal);
		SortedSet<Transition<RegularExpression>> delta = new TreeSet<>();
		for (int i = 0; i < n; i++)
			delta.add(new Transition<>(states.get(i), states.get(i + 1), randomString(10, false, 0.0)));
		return new Tarsis(new RegexAutomaton(new TreeSet<>(states), delta));
	}

	/**
	 * Automaton with 2 to 5 states, modeling a single string obtained by
	 * concatenating constants or top. Only the last state is final, and there
	 * is a single path in the automaton. Each transition has an atom of maximum
	 * length 10, with a 10% chance of it being top.
	 */
	static Tarsis concatConstantsAndTop() {
		int n = 2 + GEN.nextInt(4);
		List<State> states = new ArrayList<>(n + 1);
		states.add(qinit);
		for (int i = 2; i < n + 1; i++)
			states.add(new State(i, false, false));
		states.add(qfinal);
		SortedSet<Transition<RegularExpression>> delta = new TreeSet<>();
		for (int i = 0; i < n; i++)
			delta.add(new Transition<>(states.get(i), states.get(i + 1), randomString(10, true, 0.1)));
		return new Tarsis(new RegexAutomaton(new TreeSet<>(states), delta));
	}

	/**
	 * Automaton built by joining 2 to 5 instances returned by
	 * {@link #concatConstants()} using {@link Automaton#union(Automaton)}.
	 */
	static Tarsis joinConstants() {
		int n = 2 + GEN.nextInt(4);
		Tarsis branches = null;
		for (int i = 0; i < n; i++)
			if (i == 0)
				branches = concatConstants();
			else
				branches = new Tarsis(branches.getAutomaton().union(concatConstants().getAutomaton()));
		return branches;
	}

	/**
	 * Automaton built by joining 2 to 5 instances returned by
	 * {@link #concatConstantsAndTop()} using
	 * {@link Automaton#union(Automaton)}.
	 */
	static Tarsis joinConstantsAndTop() {
		int n = 2 + GEN.nextInt(4);
		Tarsis branches = null;
		for (int i = 0; i < n; i++)
			if (i == 0)
				branches = concatConstantsAndTop();
			else
				branches = new Tarsis(branches.getAutomaton().union(concatConstantsAndTop().getAutomaton()));
		return branches;
	}

	/**
	 * Automaton built starting from one built {@link #concatConstants()} and
	 * adding 1 to 2 loops between random states. Each loop has non-top atom of
	 * maximum length 10.
	 */
	static Tarsis loopingConstants() {
		Tarsis starting = concatConstants();
		SortedSet<State> states = starting.getAutomaton().getStates();
		SortedSet<Transition<RegularExpression>> transitions = new TreeSet<>(starting.getAutomaton().getTransitions());

		int loops = 1 + GEN.nextInt(1);

		for (int k = 0; k < loops; k++) {
			int n = GEN.nextInt(states.size());
			int m = GEN.nextInt(states.size());
			State nState = null, mState = null;
			int i = 0;
			for (State st : states) {
				if (i == n)
					nState = st;
				if (i == m)
					mState = st;
				i++;
			}
			transitions.add(new Transition<>(nState, mState, randomString(10, false, 0.0)));
		}

		return new Tarsis(new RegexAutomaton(states, transitions));
	}

	/**
	 * Automaton built starting from one built {@link #concatConstantsAndTop()}
	 * and adding 1 to 2 loops between random states. Each loop has an atom of
	 * maximum length 10, with a 10% chance of it being top.
	 */
	static Tarsis loopingConstantsAndTop() {
		Tarsis starting = concatConstantsAndTop();
		SortedSet<State> states = starting.getAutomaton().getStates();
		SortedSet<Transition<RegularExpression>> transitions = new TreeSet<>(starting.getAutomaton().getTransitions());

		int loops = 1 + GEN.nextInt(1);

		for (int k = 0; k < loops; k++) {
			int n = GEN.nextInt(states.size());
			int m = GEN.nextInt(states.size());
			State nState = null, mState = null;
			int i = 0;
			for (State st : states) {
				if (i == n)
					nState = st;
				if (i == m)
					mState = st;
				i++;
			}
			transitions.add(new Transition<>(nState, mState, randomString(10, true, 0.1)));
		}

		return new Tarsis(new RegexAutomaton(states, transitions));
	}

	/**
	 * Automaton with 2 to 5 states, modeling a single path automaton. The last
	 * state is always final, while intermediate ones have a 50% chance of being
	 * final. Each transition has a non-top atom of maximum length 10.
	 */
	static Tarsis singlePath() {
		int n = 2 + GEN.nextInt(4);
		List<State> states = new ArrayList<>(n + 1);
		if (GEN.nextBoolean())
			states.add(qinit);
		else
			states.add(qinitfinal);
		for (int i = 2; i < n + 1; i++)
			states.add(new State(i, false, GEN.nextBoolean()));
		states.add(qfinal);
		SortedSet<Transition<RegularExpression>> delta = new TreeSet<>();
		for (int i = 0; i < n; i++)
			delta.add(new Transition<>(states.get(i), states.get(i + 1), randomString(10, false, 0.0)));
		return new Tarsis(new RegexAutomaton(new TreeSet<>(states), delta));
	}

	/**
	 * Automaton with 2 to 5 states, modeling a single path automaton. The last
	 * state is always final, while intermediate ones have a 50% chance of being
	 * final. Each transition has an atom of maximum length 10, with a 10%
	 * chance of it being top.
	 */
	static Tarsis singlePathWithTop() {
		int n = 2 + GEN.nextInt(4);
		List<State> states = new ArrayList<>(n + 1);
		states.add(GEN.nextBoolean() ? qinit : qinitfinal);
		for (int i = 2; i < n + 1; i++)
			states.add(new State(i, false, GEN.nextBoolean()));
		states.add(qfinal);
		SortedSet<Transition<RegularExpression>> delta = new TreeSet<>();
		for (int i = 0; i < n; i++)
			delta.add(new Transition<>(states.get(i), states.get(i + 1), randomString(10, true, 0.1)));
		return new Tarsis(new RegexAutomaton(new TreeSet<>(states), delta));
	}

	/**
	 * Automaton with 2 to 5 states, and with 1 to 3 transitions per state. Each
	 * state has a 25% chance of being final (at least one is guaranteed to be).
	 * Each transition has an atom of maximum length 10, with a 10% chance of it
	 * being top.
	 */
	static Tarsis random() {
		int n = 2 + GEN.nextInt(4);
		List<State> states = new ArrayList<>(n);
		states.add(GEN.nextDouble() <= 0.25 ? qinitfinal : qinit);
		for (int i = 2; i < n + 1; i++)
			states.add(new State(i, false, GEN.nextDouble() <= 0.25));
		states.add(qfinal);
		SortedSet<State> sortedStates = new TreeSet<>(states);

		RegexAutomaton a = null;
		int tries = 0;
		do {
			SortedSet<Transition<RegularExpression>> delta = new TreeSet<>();
			if (tries > 2)
				// failsafe: after 3 failures, we force termination by
				// adding a direct edge from qinit/qinitfinal to qfinal
				delta.add(new Transition<>(states.get(0), qfinal, randomString(10, true, 0.1)));

			for (State s : states) {
				int m = GEN.nextInt(3);
				if (s.isInitial() && tries > 2)
					// we already added a transition to enforce termination
					m--;
				for (int i = 0; i < m; i++)
					delta.add(new Transition<>(s, states.get(GEN.nextInt(n)), randomString(10, true, 0.1)));
			}

			a = new RegexAutomaton(sortedStates, delta).minimize();
			tries++;
		} while (a.acceptsEmptyLanguage());

		return new Tarsis(a);
	}

}
