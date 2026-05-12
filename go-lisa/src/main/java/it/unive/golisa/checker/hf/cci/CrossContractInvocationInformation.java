package it.unive.golisa.checker.hf.cci;

import java.util.HashSet;
import java.util.Set;

import it.unive.lisa.lattices.string.tarsis.RegexAutomaton;

public class CrossContractInvocationInformation {
	
	Set<RegexAutomaton> contractNameApproximations;
	
	Set<RegexAutomaton> channelApproximations;
	
	
	
	public CrossContractInvocationInformation() {
		
		contractNameApproximations = new HashSet<RegexAutomaton>();
		channelApproximations = new HashSet<RegexAutomaton>();
	}

	public CrossContractInvocationInformation(Set<RegexAutomaton> contractNameApproximations,
			Set<RegexAutomaton> channelApproximations) {
		this.contractNameApproximations = contractNameApproximations;
		this.channelApproximations = channelApproximations;
	}

	public Set<RegexAutomaton> getContractNameApproximations() {
		return contractNameApproximations;
	}

	public Set<RegexAutomaton> getChannelApproximations() {
		return channelApproximations;
	}

	public void addAllContractNameApproximations(Set<RegexAutomaton> contractNameApproximations) {
		this.contractNameApproximations.addAll(contractNameApproximations);
	}

	public void addAllChannelApproximations(Set<RegexAutomaton> channelApproximations) {
		this.channelApproximations.addAll(channelApproximations);
	}
	
	
	

}
