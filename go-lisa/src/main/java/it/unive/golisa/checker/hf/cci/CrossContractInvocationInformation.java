package it.unive.golisa.checker.hf.cci;

import java.util.HashSet;
import java.util.Set;


import it.unive.lisa.analysis.string.tarsis.Tarsis;

public class CrossContractInvocationInformation {
	
	Set<Tarsis> contractNameApproximations;
	
	Set<Tarsis> channelApproximations;
	
	
	
	public CrossContractInvocationInformation() {
		
		contractNameApproximations = new HashSet<Tarsis>();
		channelApproximations = new HashSet<Tarsis>();
	}

	public CrossContractInvocationInformation(Set<Tarsis> contractNameApproximations,
			Set<Tarsis> channelApproximations) {
		this.contractNameApproximations = contractNameApproximations;
		this.channelApproximations = channelApproximations;
	}

	public Set<Tarsis> getContractNameApproximations() {
		return contractNameApproximations;
	}

	public Set<Tarsis> getChannelApproximations() {
		return channelApproximations;
	}

	public void addAllContractNameApproximations(Set<Tarsis> contractNameApproximations) {
		this.contractNameApproximations.addAll(contractNameApproximations);
	}

	public void addAllChannelApproximations(Set<Tarsis> channelApproximations) {
		this.channelApproximations.addAll(channelApproximations);
	}
	
	
	

}
