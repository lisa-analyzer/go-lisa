package it.unive.golisa.cfg.type.composite;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import it.unive.golisa.cfg.expression.literal.GoNil;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt8Type;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.InMemoryType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;
import it.unive.lisa.type.Untyped;

/**
 * A Go slice type.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoSliceType implements Type, InMemoryType {

	/**
	 * The content type of the slice type.
	 */
	private Type contentType;

	private static final Set<GoSliceType> sliceTypes = new HashSet<>();

	/**
	 * Yields a unique instance (either an existing one or a fresh one) of
	 * {@link GoSliceType} representing a slice type.
	 * 
	 * @param type the content type of the slice type to lookup
	 * 
	 * @return the unique instance of {@link GoSliceType} representing the slice
	 *             type given as argument
	 */
	public static GoSliceType lookup(Type type) {
		GoSliceType sliceType = new GoSliceType(type);
		if (!sliceTypes.contains(sliceType))
			sliceTypes.add(sliceType);
		return sliceTypes.stream().filter(x -> x.equals(sliceType)).findFirst().get();
	}

	/**
	 * Builds the slice type.
	 * 
	 * @param contentType the content type
	 */
	private GoSliceType(Type contentType) {
		this.contentType = contentType;
	}

	/**
	 * Yields the content type.
	 * 
	 * @return the content type
	 */
	public Type getContentType() {
		return contentType;
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		return (other instanceof GoSliceType && ((GoSliceType) other).contentType.canBeAssignedTo(contentType))
				|| other.isUntyped();
	}

	@Override
	public Type commonSupertype(Type other) {
		return (other instanceof GoSliceType && ((GoSliceType) other).contentType.equals(contentType)) ? this
				: Untyped.INSTANCE;
	}

	@Override
	public String toString() {
		return "[]" + contentType.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((contentType == null) ? 0 : contentType.hashCode());
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
		GoSliceType other = (GoSliceType) obj;
		if (contentType == null) {
			if (other.contentType != null)
				return false;
		} else if (!contentType.equals(other.contentType))
			return false;
		return true;
	}

	@Override
	public Expression defaultValue(CFG cfg, CodeLocation location) {
		return new GoNil(cfg, location);
	}

	@Override
	public Set<Type> allInstances(TypeSystem type) {
		return Collections.singleton(this);
	}

	/**
	 * Yields all the slice types.
	 * 
	 * @return all the slice types
	 */
	public static Set<Type> all() {
		Set<Type> instances = new HashSet<>();
		for (GoSliceType in : sliceTypes)
			instances.add(in);
		return instances;
	}

	/**
	 * Clears all the slice types.
	 */
	public static void clearAll() {
		sliceTypes.clear();
	}

	/**
	 * Yields the slice type []byte.
	 * 
	 * @return the slice type []byte
	 */
	public static GoSliceType getSliceOfBytes() {
		return GoSliceType.lookup(GoUInt8Type.INSTANCE);
	}

	/**
	 * Yields the slice type [][]byte.
	 * 
	 * @return the slice type [][]byte
	 */
	public static GoSliceType getSliceOfSliceOfBytes() {
		return GoSliceType.lookup(GoSliceType.getSliceOfBytes());
	}

	/**
	 * Yields the slice type []string.
	 * 
	 * @return the slice type []string
	 */
	public static GoSliceType getSliceOfStrings() {
		return GoSliceType.lookup(GoStringType.INSTANCE);
	}
}
