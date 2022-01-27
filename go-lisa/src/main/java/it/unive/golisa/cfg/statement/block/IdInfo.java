package it.unive.golisa.cfg.statement.block;

import it.unive.lisa.program.cfg.statement.VariableRef;

public class IdInfo {

	private final VariableRef ref;
	private final int blockDeep;

	public IdInfo(VariableRef ref, int blockDeep) {
		this.ref = ref;
		this.blockDeep = blockDeep;
	}

	public VariableRef getRef() {
		return ref;
	}

	public int getBlockDeep() {
		return blockDeep;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + blockDeep;
		result = prime * result + ((ref == null) ? 0 : ref.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IdInfo other = (IdInfo) obj;
		if (blockDeep != other.blockDeep)
			return false;
		if (ref == null) {
			if (other.ref != null)
				return false;
		} else {
			if (ref.getName().equals(other.ref.getName()))
				return true;
		}

		return false;
	}

}
