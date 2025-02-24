package it.unive.golisa.checker.hf;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import it.unive.golisa.analysis.utils.FileInfo;
import it.unive.golisa.checker.hf.cci.CrossContractInvocationInformation;
import it.unive.lisa.analysis.string.tarsis.RegexAutomaton;
import it.unive.lisa.analysis.string.tarsis.Tarsis;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.util.datastructures.automaton.State;
import it.unive.lisa.util.datastructures.automaton.Transition;
import it.unive.lisa.util.datastructures.regex.RegularExpression;
import it.unive.lisa.util.datastructures.regex.TopAtom;

public class CchiUtils {

	public static Set<Statement> computeCchisToCheck(FileInfo fi, Map<Statement, CrossContractInvocationInformation> cchis) {
		
		Set<Statement> result = null;
		if(cchis != null) {
			
			result = new HashSet<>();
			
			if(fi.getContractName() == null && fi.getChannel() == null) {
				// no info, then we need to try all possible contracts
				return  cchis.keySet();							
			} else if(fi.getContractName() != null && fi.getChannel() == null) {
				for(Statement cchi : cchis.keySet()) {
					CrossContractInvocationInformation info = cchis.get(cchi);
					for (Tarsis t : info.getContractNameApproximations()) {			
							if(mayContractNameTarget(fi.getContractName(), t)) {
								result.add(cchi);
							}
					}
				}
				
			} if(fi.getContractName() == null && fi.getChannel() != null) {
				
				for(Statement cchi : cchis.keySet()) {
					CrossContractInvocationInformation info = cchis.get(cchi);
					for (Tarsis t : info.getChannelApproximations()) {			
							if(mayCrossChannel(fi.getChannel(), t)) {
								result.add(cchi);
							}
					}
				}
				
			} else {
				
				for(Statement cchi : cchis.keySet()) {
					boolean foundChannel = false;
					boolean foundContractName = false;
					CrossContractInvocationInformation info = cchis.get(cchi);
					for (Tarsis t : info.getChannelApproximations()) {			
							if(mayCrossChannelTarget(fi.getChannel(), t)) {
								foundChannel = true;
								break;
							}
					}
					
					if(foundChannel) {
						for (Tarsis t : info.getContractNameApproximations()) {			
							if(mayContractNameTarget(fi.getContractName(), t)) {
								foundContractName = true;
								break;
							}
						}
					
					 	if(foundContractName)
					 		result.add(cchi);
					}
				}
			}
		}
		
		return result;
	}

	public static boolean isNameChannel(String channelName, Tarsis t) {
		if(channelName != null) {
			// https://github.com/hyperledger/fabric-chaincode-go/blob/main/shim/interfaces.go#L73C2-L74C17
			// if `channel` is empty string, the caller's channel is assumed.
			return  t.getAutomaton().isEqualTo(RegexAutomaton.emptyStr())
					|| t.getAutomaton().isEqualTo(RegexAutomaton.string(channelName));
		}
		return false;
	}
	
	public static boolean isContractName(String contractName, Tarsis t) {
		if(contractName != null) {
			return  t.getAutomaton().isEqualTo(RegexAutomaton.string(contractName));
		}
		return false;
	}


	public static boolean mayCrossChannel(String channelName, Tarsis t) {
		return  containsApproximations(t)
				|| !isNameChannel(channelName,t);
	}
	
	private static boolean containsApproximations(Tarsis t) {
		return t.isTop() || t.getAutomaton().getFinalStates().size() > 1
		|| hasFinalStateMultipleIngoingEdges(t.getAutomaton())
		|| t.getAutomaton().acceptsTopEventually()
		|| hasCycle(t.getAutomaton());
	}

	private static boolean mayContractNameTarget(String contractName, Tarsis t) {
		return t.isTop() || isContractName(contractName, t) 
				|| (contractName != null  && containsApproximations(t)
						&& RegexAutomaton.string(contractName).isContained(t.getAutomaton()));
	}

	public static boolean mayCrossChannelTarget(String channelName, Tarsis t) {
		return t.isTop() || isNameChannel(channelName,t) 
				||  (channelName != null  && containsApproximations(t) 
						&& RegexAutomaton.string(channelName).isContained(t.getAutomaton()));
	}

	private static boolean hasFinalStateMultipleIngoingEdges(RegexAutomaton automaton) {
		for(State f : automaton.getFinalStates())
			if(automaton.getIngoingTransitionsFrom(f).size() > 1)
				return true;
		return false;
	}
	
	
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
				else {
					if (recursiveDFS(t.getDestination(), automaton, new HashSet<State>(seen)))
						return true;
				}
			}
			return false;
			
		} else
			return true;
	}
	

}
