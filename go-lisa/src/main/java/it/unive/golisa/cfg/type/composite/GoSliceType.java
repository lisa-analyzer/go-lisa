package it.unive.golisa.cfg.type.composite;

import it.unive.golisa.cfg.expression.literal.GoNil;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.GoType;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt8Type;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.InMemoryType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A Go slice type.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoSliceType implements GoType, InMemoryType {

	private Type contentType;

	private static final Set<GoSliceType> sliceTypes = new HashSet<>();

	/**
	 * Yields a unique instance (either an existing one or a fresh one) of
	 * {@link GoSliceType}.
	 * 
	 * @param type the slice type to look up
	 * 
	 * @return the unique instance of {@link GoSliceType} representing the slice
	 *             type with the given name
	 */
	public static GoSliceType lookup(GoSliceType type) {
		if (!sliceTypes.contains(type))
			sliceTypes.add(type);
		return sliceTypes.stream().filter(x -> x.equals(type)).findFirst().get();
	}

	/**
	 * Builds the slice type.
	 * 
	 * @param contentType the content type
	 */
	public GoSliceType(Type contentType) {
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
	public Expression defaultValue(CFG cfg, SourceCodeLocation location) {
		return new GoNil(cfg, location);
	}

	@Override
	public Collection<Type> allInstances() {
		Collection<Type> instances = new HashSet<>();
		for (GoSliceType in : sliceTypes)
			instances.add(in);
		return instances;
	}

	/**
	 * Yields all the slice types.
	 * 
	 * @return all the slice types
	 */
	public static Collection<Type> all() {
		Collection<Type> instances = new HashSet<>();
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
		return GoSliceType.lookup(new GoSliceType(GoUInt8Type.INSTANCE));
	}

	/**
	 * Yields the slice type [][]byte.
	 * 
	 * @return the slice type [][]byte
	 */
	public static GoSliceType getSliceOfSliceOfBytes() {
		return GoSliceType.lookup(new GoSliceType(new GoSliceType(GoUInt8Type.INSTANCE)));
	}

	/**
	 * Yields the slice type []string.
	 * 
	 * @return the slice type []string
	 */
	public static GoSliceType getSliceOfStrings() {
		return GoSliceType.lookup(new GoSliceType(GoStringType.INSTANCE));
	}
}
