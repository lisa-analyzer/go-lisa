package it.unive.golisa.checker.readwrite;

import it.unive.lisa.program.cfg.statement.Statement;

public class WriteBlockchainHF {
	private final Statement st;
	private final Object key;
	
	
	public Statement getSt() {
		return st;
	}

	public Object getKey() {
		return key;
	}


	public WriteBlockchainHF(Statement st, Object key) {
		this.st = st;
		this.key = key;
	}


	@Override
	public String toString() {
		return "Write(" + key + ")";
	}

}
