package it.unive.golisa.cfg.type.composite;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;

public class GoAliasType implements GoType {

	public static final Map<String, GoAliasType> aliases = new HashMap<>();

	public static GoAliasType lookup(String name, GoAliasType type)  {
		if (!aliases.containsKey(name))
			aliases.put(name, type);
		return aliases.get(name);
	}

	public static boolean hasAliasType(String alias) {
		return aliases.containsKey(alias);
	}

	public static GoAliasType get(String alias) {
		return aliases.get(alias);
	}

	private final String alias;
	private final GoType baseType;

	public GoAliasType(String alias, GoType baseType) {
		this.alias = alias;
		this.baseType = baseType;
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		return (other instanceof GoAliasType && ((GoAliasType) other).alias.equals(alias) && baseType.canBeAssignedTo(((GoAliasType) other).baseType)) || other.isUntyped();
	}

	@Override
	public Type commonSupertype(Type other) {
		if (other instanceof GoAliasType && ((GoAliasType) other).alias.equals(alias) && baseType.canBeAssignedTo(((GoAliasType) other).baseType))
			return other;
		return Untyped.INSTANCE;
	}

	@Override
	public Expression defaultValue(CFG cfg) {
		return baseType.defaultValue(cfg);
	}

	@Override
	public boolean isGoInteger() {
		return baseType.isGoInteger();
	}

	@Override
	public Collection<Type> allInstances() {
		return Collections.singleton(this);
	}
}
