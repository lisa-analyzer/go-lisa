package it.unive.golisa.cfg.type.composite;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;
import it.unive.lisa.type.Untyped;

/**
 * A Go aliased type.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoAliasType implements Type {

	/**
	 * Aliases map.
	 */
	public static final Map<String, GoAliasType> aliases = new HashMap<>();

	/**
	 * Yields a unique instance (either an existing one or a fresh one) of
	 * {@link GoAliasType} representing an alias type with the given
	 * {@code name}.
	 * 
	 * @param name the name of the alias type
	 * @param type the alias type
	 * 
	 * @return the unique instance of {@link GoAliasType} representing the alias
	 *             type with the given name
	 */
	public static GoAliasType lookup(String name, GoAliasType type) {
		if (!aliases.containsKey(name))
			aliases.put(name, type);
		return aliases.get(name);
	}

	/**
	 * Checks whether the type named {@code alias} is aliased.
	 * 
	 * @param alias the type name
	 * 
	 * @return whether the type named {@code alias} is aliased
	 */
	public static boolean hasAliasType(String alias) {
		return aliases.containsKey(alias);
	}

	/**
	 * Yields the type corresponding to {@code alias}.
	 * 
	 * @param alias the type name
	 * 
	 * @return the type corresponding to {@code alias}
	 */
	public static GoAliasType get(String alias) {
		return aliases.get(alias);
	}

	private final String alias;
	private final Type baseType;

	/**
	 * Builds an alias type.
	 * 
	 * @param alias    the name of the alias
	 * @param baseType the type
	 */
	public GoAliasType(String alias, Type baseType) {
		this.alias = alias;
		this.baseType = baseType;
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		return (other instanceof GoAliasType && ((GoAliasType) other).alias.equals(alias)
				&& baseType.canBeAssignedTo(((GoAliasType) other).baseType)) || other.isUntyped();
	}

	@Override
	public Type commonSupertype(Type other) {
		if (other instanceof GoAliasType && ((GoAliasType) other).alias.equals(alias)
				&& baseType.canBeAssignedTo(((GoAliasType) other).baseType))
			return other;
		return Untyped.INSTANCE;
	}

	@Override
	public Expression defaultValue(CFG cfg, CodeLocation location) {
		return baseType.defaultValue(cfg, location);
	}

	/**
	 * Yields all the alias types.
	 * 
	 * @return all the interface types
	 */
	public static Set<Type> all() {
		Set<Type> instances = new HashSet<>();
		for (GoAliasType in : aliases.values())
			instances.add(in);
		return instances;
	}

	/**
	 * Clears all the alias types.
	 */
	public static void clearAll() {
		aliases.clear();
	}

	@Override
	public Set<Type> allInstances(TypeSystem type) {
		return all();
	}
}
