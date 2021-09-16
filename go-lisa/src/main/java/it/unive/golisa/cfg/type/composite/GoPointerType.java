package it.unive.golisa.cfg.type.composite;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import it.unive.golisa.cfg.expression.literal.GoNil;
import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.PointerType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;

public class GoPointerType implements PointerType, GoType {

	private Type baseType;

	private static final Set<GoPointerType> pointerTypes = new HashSet<>();

	public static GoPointerType lookup(GoPointerType type)  {
		if (!pointerTypes.contains(type))
			pointerTypes.add(type);
		return pointerTypes.stream().filter(x -> x.equals(type)).findFirst().get();
	}

	public GoPointerType(Type baseType) {
		this.baseType = baseType;
	}

	public Type getBaseType() {
		return baseType;
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		if (other instanceof GoPointerType)
			return baseType.canBeAssignedTo(((GoPointerType) other).baseType);
		if (other instanceof GoInterfaceType)
			return ((GoInterfaceType) other).isEmptyInterface();

		return other.isUntyped();
	}

	@Override
	public Type commonSupertype(Type other) {
		if (other instanceof GoPointerType)
			return baseType.canBeAssignedTo(((GoPointerType) other).baseType) ? other : Untyped.INSTANCE;
		if (other instanceof GoInterfaceType)
			return ((GoInterfaceType) other).isEmptyInterface() ?  other : Untyped.INSTANCE;

		return Untyped.INSTANCE;
	}

	@Override
	public String toString() {
		return "*" + baseType.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((baseType == null) ? 0 : baseType.hashCode());
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
		GoPointerType other = (GoPointerType) obj;
		if (baseType == null) {
			if (other.baseType != null)
				return false;
		} else if (!baseType.equals(other.baseType))
			return false;
		return true;
	}

	@Override
	public Expression defaultValue(CFG cfg, SourceCodeLocation location) {
		return new GoNil(cfg, location);
	}

	public static Collection<Type> all() {
		Collection<Type> instances = new HashSet<>();
		for (GoPointerType in : pointerTypes)
			instances.add(in);
		return instances;	
	}

	@Override
	public Collection<Type> allInstances() {
		Collection<Type> instances = new HashSet<>();
		for (GoPointerType in : pointerTypes)
			instances.add(in);
		return instances;
	}
}