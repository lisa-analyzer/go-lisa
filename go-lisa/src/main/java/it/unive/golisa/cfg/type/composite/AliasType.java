package it.unive.golisa.cfg.type.composite;

import it.unive.lisa.cfg.type.Type;

public class AliasType implements Type {

	private final String alias;
	private final Type baseType;
	
	public AliasType(String alias, Type baseType) {
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
