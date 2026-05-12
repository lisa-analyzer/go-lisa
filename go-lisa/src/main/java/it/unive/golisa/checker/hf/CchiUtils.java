package it.unive.golisa.checker.hf;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import it.unive.golisa.analysis.utils.FileInfo;
import it.unive.golisa.checker.hf.cci.CrossContractInvocationInformation;
import it.unive.lisa.lattices.string.tarsis.RegexAutomaton;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.util.datastructures.automaton.State;
import it.unive.lisa.util.datastructures.automaton.Transition;
import it.unive.lisa.util.datastructures.regex.RegularExpression;
import it.unive.lisa.util.datastructures.regex.TopAtom;

/**
 * Utility class for cross channel invocation analysis.
 */
public class CchiUtils {

	/**
	 * Computes the cross-channel invocations to check.
	 * 
	 * @param fi the file information of the contracts
	 * @param cchis the set of cross-contract invocations to check
	 * @return the set of cross-channel invocations
	 */
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
					for (RegexAutomaton t : info.getContractNameApproximations()) {			
							if(mayContractNameTarget(fi.getContractName(), t)) {
								result.add(cchi);
							}
					}
				}
				
			} if(fi.getContractName() == null && fi.getChannel() != null) {
				
				for(Statement cchi : cchis.keySet()) {
					CrossContractInvocationInformation info = cchis.get(cchi);
					for (RegexAutomaton t : info.getChannelApproximations()) {			
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
					for (RegexAutomaton t : info.getChannelApproximations()) {			
							if(mayCrossChannelTarget(fi.getChannel(), t)) {
								foundChannel = true;
								break;
							}
					}
					
					if(foundChannel) {
						for (RegexAutomaton t : info.getContractNameApproximations()) {			
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

	/**
	 * Yields {@code true} if the channel name matches the automaton.
	 * 
	 * @param channelName the channel name
	 * @param a the automaton
	 * @return {@code true} if the channel name matches the automaton
	 */
	public static boolean isNameChannel(String channelName, RegexAutomaton a) {
		if(channelName != null) {
			// https://github.com/hyperledger/fabric-chaincode-go/blob/main/shim/interfaces.go#L73C2-L74C17
			// if `channel` is empty string, the caller's channel is assumed.
			return  a.isEqualTo(RegexAutomaton.emptyStr())
					|| a.isEqualTo(RegexAutomaton.string(channelName));
		}
		return false;
	}
	
	/**
	 * Yields {@code true} if the contract name matches the automaton.
	 * 
	 * @param contractName the contract name
	 * @param a the automaton
	 * @return {@code true} if the channel name matches the automaton
	 */
	public static boolean isContractName(String contractName,  RegexAutomaton a) {
		if(contractName != null) {
			return  a.isEqualTo(RegexAutomaton.string(contractName));
		}
		return false;
	}

	/**
	 * Yields {@code true} if the channel name may different from the automaton.
	 * @param channelName the channel name
	 * @param a the automaton
	 * @return {@code true} if the channel name may different from the automaton
	 */
	public static boolean mayCrossChannel(String channelName, RegexAutomaton a) {
		return  containsApproximations(a)
				|| (channelName != null && !isNameChannel(channelName,a));
	}
	
	private static boolean containsApproximations(RegexAutomaton a) {
		return a.isTop() || a.getFinalStates().size() > 1
		|| hasFinalStateMultipleIngoingEdges(a)
		|| a.acceptsTopEventually()
		|| hasCycle(a);
	}

	/**
	 * Yields {@code true} if the contract name may match the automaton.
	 * @param contractName the contract name
	 * @param a the automaton
	 * @return {@code true} if the contract name may match the automaton
	 */
	private static boolean mayContractNameTarget(String contractName, RegexAutomaton a) {
		return a.isTop() || isContractName(contractName, a) 
				|| (contractName != null  && containsApproximations(a)
						&& RegexAutomaton.string(contractName).isContained(a));
	}

	/**
	 * Yields {@code true} if may be a cross channel target.
	 * @param channelName the channel name
	 * @param a the automaton
	 * @return {@code true} if may be a cross channel target
	 */
	public static boolean mayCrossChannelTarget(String channelName, RegexAutomaton a) {
		return a.isTop() || isNameChannel(channelName,a) 
				||  (channelName != null  && containsApproximations(a) 
						&& RegexAutomaton.string(channelName).isContained(a));
	}

	private static boolean hasFinalStateMultipleIngoingEdges(RegexAutomaton automaton) {
		for(State f : automaton.getFinalStates())
			if(automaton.getIngoingTransitionsFrom(f).size() > 1)
				return true;
		return false;
	}
	
	/**
	 * Yields {@code true} if the automaton contains top transaction.
	 * @param automaton the automaton
	 * @return {@code true} if the automaton contains top transaction
	 */
	public static boolean containsTopTransaction(RegexAutomaton automaton) {
		for(Transition<RegularExpression> t : automaton.getTransitions())
			if(t.getSymbol().equals(TopAtom.INSTANCE))
				return true;
		return false;
	}

	/**
	 * Yields {@code true} if the automaton contains a cycle.
	 * @param automaton the automaton
	 * @return {@code true} if the automaton contains a cycle
	 */
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
