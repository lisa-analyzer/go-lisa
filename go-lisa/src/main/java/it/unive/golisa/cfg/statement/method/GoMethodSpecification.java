package it.unive.golisa.cfg.statement.method;

import java.util.Arrays;

import it.unive.lisa.cfg.Parameter;
import it.unive.lisa.cfg.type.Type;

public class GoMethodSpecification {
	private String name;
	private Type type;
	private Parameter[] params;
	
	public GoMethodSpecification(String name, Type type, Parameter[] params) {
		super();
		this.name = name;
		this.type = type;
		this.params = params;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + Arrays.hashCode(params);
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		GoMethodSpecification other = (GoMethodSpecification) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (!Arrays.equals(params, other.params))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
}
