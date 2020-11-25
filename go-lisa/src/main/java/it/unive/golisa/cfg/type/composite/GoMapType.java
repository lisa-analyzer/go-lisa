package it.unive.golisa.cfg.type.composite;

import it.unive.lisa.cfg.type.Type;

public class GoMapType implements Type {

	private Type keyType;
	private Type elementType;

	public GoMapType(Type keyType, Type elementType) {
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
}