package it.unive.golisa.cfg.type.composite;

import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A Go variadic type.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoVariadicType implements GoType {

	private static final Set<GoVariadicType> variadicTypes = new HashSet<>();

	private final Type contentType;

	/**
	 * Yields a unique instance (either an existing one or a fresh one) of
	 * {@link GoVariadicType} representing a variadic type.
	 * 
	 * @param type the variadic type to lookup
	 * 
	 * @return the unique instance of {@link GoVariadicType} representing the
	 *             function type given as argument
	 */
	public static GoVariadicType lookup(Type conentType) {
		GoVariadicType type = new GoVariadicType(conentType);
		if (!variadicTypes.contains(type))
			variadicTypes.add(type);
		return variadicTypes.stream().filter(x -> x.equals(type)).findFirst().get();
	}

	/**
	 * Builds a variadic type.
	 * 
	 * @param contentType the content type
	 */
	private GoVariadicType(Type contentType) {
		this.contentType = contentType;
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		if (other instanceof GoVariadicType) {
			GoVariadicType that = (GoVariadicType) other;
			return this.contentType.canBeAssignedTo(that.contentType);
		}

		// TODO: what about slices?
		return other.isUntyped();
	}

	@Override
	public Type commonSupertype(Type other) {
		if (other instanceof GoVariadicType) {
			Type contentCommonType = contentType.commonSupertype(((GoVariadicType) other).contentType);
			if (!contentCommonType.isUntyped())
				return new GoVariadicType((GoType) contentCommonType);
		}

		// TODO: what about slices?
		return Untyped.INSTANCE;
	}

	@Override
	public int hashCode() {
		return Objects.hash(contentType);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GoVariadicType other = (GoVariadicType) obj;
		return Objects.equals(contentType, other.contentType);
	}

	@Override
	public String toString() {
		return "..." + contentType.toString();
	}

	@Override
	public Expression defaultValue(CFG cfg, SourceCodeLocation location) {
		// TODO: default value of a variadic type?
		return null;
	}

	/**
	 * Yields all the variadic types.
	 * 
	 * @return all the variadic types
	 */
	public static Collection<Type> all() {
		Collection<Type> instances = new HashSet<>();
		for (GoVariadicType in : variadicTypes)
			instances.add(in);
		return instances;
	}

	@Override
	public Collection<Type> allInstances() {
		return all();
	}

	/**
	 * Clears all the variadic types.
	 */
	public static void clearAll() {
		variadicTypes.clear();
	}
}
