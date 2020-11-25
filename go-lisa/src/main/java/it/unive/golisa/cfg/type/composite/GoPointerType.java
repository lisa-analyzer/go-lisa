package it.unive.golisa.cfg.type.composite;

import it.unive.lisa.cfg.type.PointerType;
import it.unive.lisa.cfg.type.Type;

public class GoPointerType implements PointerType {

	private Type baseType;
	
	public GoPointerType(Type baseType) {
		this.baseType = baseType;
	}
	
	public Type getBaseType() {
		return baseType;
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
	public String toString() {
		return "*" + baseType.toString();
	}
	
	@Override
	public int hashCode() {
		return baseType.hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof GoPointerType && baseType.equals(((GoPointerType) other).getBaseType());
	}
}