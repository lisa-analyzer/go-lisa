package it.unive.golisa.cfg.type.composite;

import it.unive.golisa.cfg.expression.literal.GoNonKeyedLiteral;
import it.unive.golisa.cfg.expression.unknown.GoUnknown;
import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.InMemoryType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;
import it.unive.lisa.type.Untyped;
import java.util.HashSet;
import java.util.Set;

/**
 * A Go array type.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoArrayType implements GoType, InMemoryType {

	private Type contentType;
	private Integer length;

	private static final Set<GoArrayType> arrayTypes = new HashSet<>();

	/**
	 * Yields a unique instance (either an existing one or a fresh one) of
	 * {@link GoArrayType} representing an array type.
	 * 
	 * @param type   the content type of the array type to lookup
	 * @param length the length of the the array type to lookup
	 * 
	 * @return the unique instance of {@link GoArrayType} representing the array
	 *             type given as argument
	 */
	public static GoArrayType lookup(Type type, Integer length) {
		GoArrayType arrayType = new GoArrayType(type, length);
		if (!arrayTypes.contains(arrayType))
			arrayTypes.add(arrayType);

		return arrayTypes.stream().filter(x -> x.equals(arrayType)).findFirst().get();
	}

	/**
	 * Builds an array type.
	 * 
	 * @param contentType the content type
	 * @param length      the length
	 */
	private GoArrayType(Type contentType, Integer length) {
		this.contentType = contentType;
		this.length = length;
	}

	/**
	 * Yields the content type.
	 * 
	 * @return the content type
	 */
	public Type getContenType() {
		return contentType;
	}

	/**
	 * Yields the length.
	 * 
	 * @return the length
	 */
	public Integer getLength() {
		return length;
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		if (other instanceof GoArrayType)
			return contentType.canBeAssignedTo(((GoArrayType) other).contentType)
					&& length.equals(((GoArrayType) other).length);
		return false;
	}

	@Override
	public Type commonSupertype(Type other) {
		if (other instanceof GoArrayType)
			if (contentType.canBeAssignedTo(((GoArrayType) other).contentType)
					&& length.equals(((GoArrayType) other).length))
				return other;
		return Untyped.INSTANCE;
	}

	@Override
	public String toString() {
		return "[" + length + "]" + contentType.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((contentType == null) ? 0 : contentType.hashCode());
		result = prime * result + ((length == null) ? 0 : length.hashCode());
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
		GoArrayType other = (GoArrayType) obj;
		if (contentType == null) {
			if (other.contentType != null)
				return false;
		} else if (!contentType.equals(other.contentType))
			return false;
		if (length == null) {
			if (other.length != null)
				return false;
		} else if (!length.equals(other.length))
			return false;
		return true;
	}

	@Override
	public Expression defaultValue(CFG cfg, SourceCodeLocation location) {
		Expression[] result = new Expression[length];
		for (int i = 0; i < length; i++)
			if (contentType instanceof GoType)
				result[i] = ((GoType) contentType).defaultValue(cfg, location);
			else
				result[i] = new GoUnknown(cfg, location);

		return new GoNonKeyedLiteral(cfg, location, result, this);
	}

	/**
	 * Yields all the array types.
	 * 
	 * @return all the array types
	 */
	public static Set<Type> all() {
		Set<Type> instances = new HashSet<>();
		for (GoArrayType in : arrayTypes)
			instances.add(in);
		return instances;
	}

	@Override
	public Set<Type> allInstances(TypeSystem type) {
		return all();
	}

	/**
	 * Clears all the array types.
	 */
	public static void clearAll() {
		arrayTypes.clear();
	}
}
