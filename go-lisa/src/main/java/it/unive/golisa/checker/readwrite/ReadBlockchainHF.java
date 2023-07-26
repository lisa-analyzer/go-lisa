package it.unive.golisa.checker.readwrite;

import it.unive.lisa.program.cfg.statement.Statement;

public abstract class ReadBlockchainHF {
	
	private final Statement st;
	
	protected ReadBlockchainHF(Statement st) {
		this.st = st;
	}

	public Statement getSt() {
		return st;
	}

	public class ReadBlockchainHFSingleKey extends ReadBlockchainHF {

		private final Object key;
		
		protected ReadBlockchainHFSingleKey(Statement st, Object key) {
			super(st);
			this.key = key;
		}

		public Object getKey() {
			return key;
		}
		
	}
	
	public class ReadBlockchainHFRangKey extends ReadBlockchainHF {

		private final Object keyStart;
		private final Object keyEnd;
		
		protected ReadBlockchainHFRangKey(Statement st, Object keyStart, Object keyEnd) {
			super(st);
			this.keyStart = keyStart;
			this.keyEnd = keyEnd;
		}

		public Object getKeyStart() {
			return keyStart;
		}

		public Object getKeyEnd() {
			return keyEnd;
		}

	}
	
	public class ReadBlockchainHFPrefixKey extends ReadBlockchainHF {
		
		private final Object keyPrefixs;
		
		protected ReadBlockchainHFPrefixKey(Statement st, Object ...keyPrefixs  ) {
			super(st);
			this.keyPrefixs = keyPrefixs;
		}

		public Object getKeyPrefixs() {
			return keyPrefixs;
		}
		
	}
}
