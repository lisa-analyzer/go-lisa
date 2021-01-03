package it.unive.golisa.cfg.type.composite;

import java.util.HashSet;
import java.util.Set;

import it.unive.golisa.cfg.expression.literal.GoNil;
import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.Parameter;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.type.Type;

public class GoFunctionType implements GoType {

	private Parameter[] params;
	private Type returnType;
			
	private static final Set<GoFunctionType> functionTypes = new HashSet<>();

	public static GoFunctionType lookup(GoFunctionType type)  {
		if (!functionTypes.contains(type))
			functionTypes.add(type);
		return functionTypes.stream().filter(x -> x.equals(type)).findFirst().get();
	}
	
	public GoFunctionType(Parameter[] params, Type returnType) {
		this.params = params;
		this.returnType = returnType;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((params == null) ? 0 : params.hashCode());
		result = prime * result + ((returnType == null) ? 0 : returnType.hashCode());
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
		GoFunctionType other = (GoFunctionType) obj;
		if (params == null) {
			if (other.params != null)
				return false;
		} else if (!params.equals(other.params))
			return false;
		if (returnType == null) {
			if (other.returnType != null)
				return false;
		} else if (!returnType.equals(other.returnType))
			return false;
		return true;
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Type commonSupertype(Type other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression defaultValue(CFG cfg) {
		return new GoNil(cfg);
	}
	
	@Override
	public boolean isGoInteger() {
		return false;
	}
}
