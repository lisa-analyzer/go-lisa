package it.unive.golisa.cfg.runtime.container.list.type;

import it.unive.golisa.cfg.expression.literal.GoNonKeyedLiteral;
import it.unive.golisa.cfg.type.GoType;
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
 * A List type.
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class List implements GoType, InMemoryType {

	private Type contentType;

	private static final Set<List> listTypes = new HashSet<>();

	/**
	 * Yields a unique instance (either an existing one or a fresh one) of
	 * {@link List} representing an array type.
	 * 
	 * @param type   the content type of the array type to lookup
	 * @param length the length of the the array type to lookup
	 * 
	 * @return the unique instance of {@link List} representing the array
	 *         type given as argument
	 */
	public static List lookup(Type type) {
		List arrayType = new List(type);
		if (!listTypes.contains(arrayType))
			listTypes.add(arrayType);

		return listTypes.stream().filter(x -> x.equals(arrayType)).findFirst().get();
	}

	/**
	 * Builds an array type.
	 * 
	 * @param contentType the content type
	 * @param length      the length
	 */
	private List(Type contentType) {
		this.contentType = contentType;
	}

	/**
	 * Yields the content type.
	 * 
	 * @return the content type
	 */
	public Type getContentType() {
		return Element.INSTANCE;
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		if (other instanceof List)
			return contentType.canBeAssignedTo(((List) other).contentType);
		return false;
	}

	@Override
	public Type commonSupertype(Type other) {
		if (other instanceof List)
			if (contentType.canBeAssignedTo(((List) other).contentType))
				return other;
		return Untyped.INSTANCE;
	}

	@Override
	public String toString() {
		return contentType.toString();
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
		List other = (List) obj;
		if (contentType == null) {
			if (other.contentType != null)
				return false;
		} else if (!contentType.equals(other.contentType))
			return false;
		return true;
	}

	@Override
	public Expression defaultValue(CFG cfg, SourceCodeLocation location) {
		return new GoNonKeyedLiteral(cfg, location, new Expression[] {}, this);
	}

	/**
	 * Yields all the array types.
	 * 
	 * @return all the array types
	 */
	public static Collection<Type> all() {
		Collection<Type> instances = new HashSet<>();
		return instances;
	}

	@Override
	public Collection<Type> allInstances() {
		return all();
	}
}
