package it.unive.golisa.analysis.utils;

import java.util.Objects;

/**
 * Class containing file information of a contract.
 */
public class FileInfo {

	/**
	 * The file path.
	 */
	private final String input;

	/**
	 * The contract name at deployment time.
	 */
	private final String contractname;

	/**
	 * The channel name at deployment time.
	 */
	private final String channel;

	/**
	 * Builds the file information.
	 * 
	 * @param input        the file path
	 * @param contractname the contract name
	 * @param channel      the channel
	 */
	public FileInfo(String input, String contractname, String channel) {
		this.input = input;
		this.contractname = contractname;
		this.channel = channel;
	}

	@Override
	public String toString() {
		return "Input File: " + input + ", Contract Name: " + contractname + ", Channel: " + channel;
	}

	/**
	 * Yields the file path.
	 * 
	 * @return the file path
	 */
	public String getInput() {
		return input;
	}

	/**
	 * Yields the contract name.
	 * 
	 * @return the contract name
	 */
	public String getContractName() {
		return contractname;
	}

	/**
	 * Yields the channel name.
	 * 
	 * @return the channel name
	 */
	public String getChannel() {
		return channel;
	}

	@Override
	public int hashCode() {
		return Objects.hash(channel, input, contractname);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileInfo other = (FileInfo) obj;
		return Objects.equals(channel, other.channel) && Objects.equals(input, other.input)
				&& Objects.equals(contractname, other.contractname);
	}

}