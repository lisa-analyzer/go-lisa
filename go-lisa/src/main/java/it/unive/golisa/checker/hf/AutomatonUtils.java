package it.unive.golisa.checker.hf;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;

import it.unive.lisa.analysis.string.tarsis.RegexAutomaton;
import it.unive.lisa.util.datastructures.automaton.State;
import it.unive.lisa.util.datastructures.automaton.Transition;
import it.unive.lisa.util.datastructures.regex.RegularExpression;
import it.unive.lisa.util.datastructures.regex.TopAtom;

public class AutomatonUtils {

	public static boolean containsTopTransaction(RegexAutomaton automaton) {
		for(Transition<RegularExpression> t : automaton.getTransitions())
			if(t.getSymbol().equals(TopAtom.INSTANCE))
				return true;
		return false;
	}

	public static boolean hasCycle(RegexAutomaton automaton) {

       Set<State> initStates = automaton.getInitialStates();
       
       for(State i : initStates) {
    	   Set<State> seen = new HashSet<State>();
    	   return recursiveDFS(i,automaton,seen);
       }
       return false;
       
	}
	
    private static boolean recursiveDFS(State s, RegexAutomaton automaton, Set<State> seen) {
		if(seen.add(s)) {
			for(Transition<RegularExpression> t : automaton.getOutgoingTransitionsFrom(s)) {
				if(seen.contains(t.getDestination()))
					return true;
				else 
					recursiveDFS(t.getDestination(), automaton, Set.copyOf(seen));
			}
			return false;
			
		} else
			return true;
	}



}
