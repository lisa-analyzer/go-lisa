package it.unive.golisa.cfg.type.composite;

import java.util.HashSet;
import java.util.Set;

import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.type.PointerType;
import it.unive.lisa.cfg.type.Type;

public class GoPointerType implements PointerType, GoType {

	private GoType baseType;
	
	private static final Set<GoPointerType> pointerTypes = new HashSet<>();

	public static GoPointerType lookup(GoPointerType type)  {
		if (!pointerTypes.contains(type))
			pointerTypes.add(type);
		return pointerTypes.stream().filter(x -> x.equals(type)).findFirst().get();
	}
	
	public GoPointerType(GoType baseType) {
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

	@Override
	public Expression defaultValue(CFG cfg) {
		// TODO Auto-generated method stub
		return null;
	}
}