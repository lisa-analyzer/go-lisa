package it.unive.golisa.checker.hf.cci;

import it.unive.lisa.lattices.string.tarsis.RegexAutomaton;
import java.util.HashSet;
import java.util.Set;

/**
 * Information for cross-contract invocations.
 */
public class CrossContractInvocationInformation {

	/**
	 * Approximations of contract name.
	 */
	Set<RegexAutomaton> contractNameApproximations;

	/**
	 * Approximations of channel name.
	 */
	Set<RegexAutomaton> channelApproximations;

	/**
	 * Builds the instance.
	 */
	public CrossContractInvocationInformation() {

		contractNameApproximations = new HashSet<RegexAutomaton>();
		channelApproximations = new HashSet<RegexAutomaton>();
	}

	/**
	 * Builds the instance.
	 * 
	 * @param contractNameApproximations the contract name approximations
	 * @param channelApproximations      the channel name approximations
	 */
	public CrossContractInvocationInformation(Set<RegexAutomaton> contractNameApproximations,
			Set<RegexAutomaton> channelApproximations) {
		this.contractNameApproximations = contractNameApproximations;
		this.channelApproximations = channelApproximations;
	}

	/**
	 * Yields the contract name approximations.
	 * 
	 * @return the approximations
	 */
	public Set<RegexAutomaton> getContractNameApproximations() {
		return contractNameApproximations;
	}

	/**
	 * Yields the channel name approximations.
	 * 
	 * @return the approximations
	 */
	public Set<RegexAutomaton> getChannelApproximations() {
		return channelApproximations;
	}

	/**
	 * Add contract name approximations.
	 * 
	 * @param contractNameApproximations the approximations
	 */
	public void addAllContractNameApproximations(Set<RegexAutomaton> contractNameApproximations) {
		this.contractNameApproximations.addAll(contractNameApproximations);
	}

	/**
	 * Add channel name approximations.
	 * 
	 * @param channelApproximations the approximations
	 */
	public void addAllChannelApproximations(Set<RegexAutomaton> channelApproximations) {
		this.channelApproximations.addAll(channelApproximations);
	}

}
