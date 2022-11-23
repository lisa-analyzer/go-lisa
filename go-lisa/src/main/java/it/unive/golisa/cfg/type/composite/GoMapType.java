package it.unive.golisa.cfg.type.composite;

import java.util.HashSet;
import java.util.Set;

import it.unive.golisa.cfg.expression.literal.GoNil;
import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.InMemoryType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;
import it.unive.lisa.type.Untyped;

/**
 * A Go map type.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoMapType implements GoType, InMemoryType {

	private Type keyType;
	private Type elementType;

	private static final Set<GoMapType> mapTypes = new HashSet<>();

	/**
	 * Yields a unique instance (either an existing one or a fresh one) of
	 * {@link GoMapType} representing a map type.
	 * 
	 * @param keyType     the key type of the map type to lookup
	 * @param elementType the element type of the map type to lookup
	 * 
	 * @return the unique instance of {@link GoMapType} representing the map
	 *             type given as argument
	 */
	public static GoMapType lookup(Type keyType, Type elementType) {
		GoMapType type = new GoMapType(keyType, elementType);
		if (!mapTypes.contains(type))
			mapTypes.add(type);
		return mapTypes.stream().filter(x -> x.equals(type)).findFirst().get();
	}

	/**
	 * Builds a map type.
	 * 
	 * @param keyType     key type
	 * @param elementType element type
	 */
	private GoMapType(Type keyType, Type elementType) {
		this.keyType = keyType;
		this.elementType = elementType;
	}

	/**
	 * Yields the key type of this map type.
	 * 
	 * @return the key type of this map type
	 */
	public Type getKeyType() {
		return keyType;
	}

	/**
	 * Yields the element type of this map type.
	 * 
	 * @return the element type of this map type
	 */
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
		return false;
	}

	@Override
	public Expression defaultValue(CFG cfg, SourceCodeLocation location) {
		return new GoNil(cfg, location);
	}

	/**
	 * Yields all the map types.
	 * 
	 * @return all the map types
	 */
	public static Set<Type> all() {
		Set<Type> instances = new HashSet<>();
		for (GoMapType in : mapTypes)
			instances.add(in);
		return instances;
	}

	@Override
	public Set<Type> allInstances(TypeSystem type) {
		return all();
	}

	/**
	 * Clears all the map types.
	 */
	public static void clearAll() {
		mapTypes.clear();
	}
}