package it.unive.golisa.cfg.statement.block;

import it.unive.lisa.program.cfg.statement.VariableRef;

/**
 * Information about a variable, i.e., at which block deep the variable is
 * declared.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class IdInfo {

	private final VariableRef var;
	private final int blockDeep;
	private final boolean multiDeclar;

	/**
	 * Builds a identifier information.
	 * 
	 * @param var       the variable
	 * @param blockDeep the block deep at which the variable is declared
	 */
	public IdInfo(VariableRef var, int blockDeep) {
		this.var = var;
		this.blockDeep = blockDeep;
		this.multiDeclar = false;
	}
	
	/**
	 * Builds a identifier information.
	 * 
	 * @param var       the variable
	 * @param blockDeep the block deep at which the variable is declared
	 */
	public IdInfo(VariableRef var, int blockDeep, boolean multiDeclar) {
		this.var = var;
		this.blockDeep = blockDeep;
		this.multiDeclar = multiDeclar;
	}

	public boolean isMultiDeclar() {
		return multiDeclar;
	}

	/**
	 * Yields the variable.
	 * 
	 * @return the variable
	 */
	public VariableRef getRef() {
		return var;
	}

	/**
	 * Yields the block deep.
	 * 
	 * @return the block deep
	 */
	public int getBlockDeep() {
		return blockDeep;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + blockDeep;
		result = prime * result + ((var == null) ? 0 : var.hashCode());
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
		if (var == null) {
			if (other.var != null)
				return false;
		} else {
			if (var.getName().equals(other.var.getName()))
				return true;
		}

		return false;
	}
}
