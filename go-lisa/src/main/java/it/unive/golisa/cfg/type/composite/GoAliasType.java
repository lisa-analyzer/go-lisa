package it.unive.golisa.cfg.type.composite;

import java.util.HashMap;
import java.util.Map;

import it.unive.lisa.cfg.type.Type;

public class GoAliasType implements Type {

	public static final Map<String, GoAliasType> aliases = new HashMap<>();

	public static void addAlias(String alias, GoAliasType type) {
		aliases.put(alias, type);
	} 
	
	private final String alias;
	private final Type baseType;
	
	public GoAliasType(String alias, Type baseType) {
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

}
