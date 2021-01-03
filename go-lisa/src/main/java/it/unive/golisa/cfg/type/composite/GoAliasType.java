package it.unive.golisa.cfg.type.composite;

import java.util.HashMap;
import java.util.Map;

import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.type.Type;

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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Type commonSupertype(Type other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression defaultValue(CFG cfg) {
		return baseType.defaultValue(cfg);
	}
	
	@Override
	public boolean isGoInteger() {
		return baseType.isGoInteger();
	}
}
