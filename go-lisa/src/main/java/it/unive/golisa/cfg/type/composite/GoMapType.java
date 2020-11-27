package it.unive.golisa.cfg.type.composite;

import java.util.HashSet;
import java.util.Set;

import it.unive.golisa.cfg.expression.literal.GoNil;
import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.type.Type;

public class GoMapType implements GoType {

	private GoType keyType;
	private GoType elementType;

	private static final Set<GoMapType> mapTypes = new HashSet<>();

	public static GoMapType lookup(GoMapType type)  {
		if (!mapTypes.contains(type))
			mapTypes.add(type);
		return mapTypes.stream().filter(x -> x.equals(type)).findFirst().get();
	}

	public GoMapType(GoType keyType, GoType elementType) {
		this.keyType = keyType;
		this.elementType = elementType;
	}

	public Type getKeyType() {
		return keyType;
	}

	public Type getElementType() {
		return elementType;
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
		return "map[" + keyType + "]"+ elementType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((keyType == null) ? 0 : keyType.hashCode());
		result = prime * result + ((elementType == null) ? 0 : elementType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof GoMapType 
				&& keyType.equals(((GoMapType) other).getKeyType())
				&& elementType.equals(((GoMapType) other).getElementType());
	}

	@Override
	public Expression defaultValue(CFG cfg) {
		return new GoNil(cfg);
	}
}