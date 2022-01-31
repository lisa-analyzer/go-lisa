package it.unive.golisa.cfg.type.composite;

import it.unive.golisa.cfg.expression.literal.GoNil;
import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.PointerType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class GoMapType implements GoType, PointerType {

	private Type keyType;
	private Type elementType;

	private static final Set<GoMapType> mapTypes = new HashSet<>();

	public static GoMapType lookup(GoMapType type) {
		if (!mapTypes.contains(type))
			mapTypes.add(type);
		return mapTypes.stream().filter(x -> x.equals(type)).findFirst().get();
	}

	public GoMapType(Type keyType, Type elementType) {
		this.keyType = keyType;
		this.elementType = elementType;
	}

	public Type getKeyType() {
		return keyType;
	}

	public Type getElementType() {
		return elementType;
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		if (other instanceof GoMapType)
			return keyType.canBeAssignedTo(((GoMapType) other).keyType)
					&& elementType.canBeAssignedTo(((GoMapType) other).elementType);
		return other.isUntyped();
	}

	@Override
	public Type commonSupertype(Type other) {
		if (other instanceof GoMapType)
			if (keyType.canBeAssignedTo(((GoMapType) other).keyType)
					&& elementType.canBeAssignedTo(((GoMapType) other).elementType))
				return other;
		return Untyped.INSTANCE;
	}

	@Override
	public String toString() {
		return "map[" + keyType + "]" + elementType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((elementType == null) ? 0 : elementType.hashCode());
		result = prime * result + ((keyType == null) ? 0 : keyType.hashCode());
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
		GoMapType other = (GoMapType) obj;
		if (elementType == null) {
			if (other.elementType != null)
				return false;
		} else if (!elementType.equals(other.elementType))
			return false;
		if (keyType == null) {
			if (other.keyType != null)
				return false;
		} else if (!keyType.equals(other.keyType))
			return false;
		return true;
	}

	@Override
	public boolean isPointerType() {
		return true;
	}

	@Override
	public Expression defaultValue(CFG cfg, SourceCodeLocation location) {
		return new GoNil(cfg, location);
	}

	public static Collection<Type> all() {
		Collection<Type> instances = new HashSet<>();
		for (GoMapType in : mapTypes)
			instances.add(in);
		return instances;
	}

	@Override
	public Collection<Type> allInstances() {
		Collection<Type> instances = new HashSet<>();
		for (GoMapType in : mapTypes)
			instances.add(in);
		return instances;
	}
}