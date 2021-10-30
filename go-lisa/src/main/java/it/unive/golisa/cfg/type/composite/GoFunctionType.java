package it.unive.golisa.cfg.type.composite;

import it.unive.golisa.cfg.expression.literal.GoNil;
import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class GoFunctionType implements GoType {

	private Parameter[] params;
	private Type returnType;

	private static final Set<GoFunctionType> functionTypes = new HashSet<>();

	public static GoFunctionType lookup(GoFunctionType type) {
		if (!functionTypes.contains(type))
			functionTypes.add(type);
		return functionTypes.stream().filter(x -> x.equals(type)).findFirst().get();
	}

	public GoFunctionType(Parameter[] params, Type returnType) {
		this.params = params;
		this.returnType = returnType;
	}

	@Override
	public String toString() {
		return "func (" + Arrays.toString(params) + ")" + returnType;
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
		return equals(other) || other.isUntyped();
	}

	@Override
	public Type commonSupertype(Type other) {
		return equals(other) ? other : Untyped.INSTANCE;
	}

	@Override
	public Expression defaultValue(CFG cfg, SourceCodeLocation location) {
		return new GoNil(cfg, location);
	}

	public static Collection<Type> all() {
		Collection<Type> instances = new HashSet<>();
		for (GoFunctionType in : functionTypes)
			instances.add(in);
		return instances;
	}

	@Override
	public Collection<Type> allInstances() {
		Collection<Type> instances = new HashSet<>();
		for (GoFunctionType in : functionTypes)
			instances.add(in);
		return instances;
	}
}
