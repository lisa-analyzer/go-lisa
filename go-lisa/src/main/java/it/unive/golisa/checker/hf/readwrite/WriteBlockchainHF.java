package it.unive.golisa.checker.hf.readwrite;

import it.unive.lisa.program.cfg.statement.Statement;

/**
 * Write blockchain operation in Hyperledger Fabric
 */
public class WriteBlockchainHF {
	private final Statement st;
	private final Object key;

	/**
	 * Yields the statement.
	 * @return the statement
	 */
	public Statement getSt() {
		return st;
	}

	/**
	 * Yields the key.
	 * @return the key
	 */
	public Object getKey() {
		return key;
	}

	/**
	 * Builds the instance.
	 * @param st the statement
	 * @param key the key
	 */
	public WriteBlockchainHF(Statement st, Object key) {
		this.st = st;
		this.key = key;
	}

	@Override
	public String toString() {
		return "Write(" + key + ")";
	}

}
