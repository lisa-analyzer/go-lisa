package it.unive.golisa.cfg.type.composite;

import it.unive.lisa.cfg.type.Type;

public class GoSliceType implements Type {
	
	private Type contentType;

	public GoSliceType(Type contentType) {
		this.contentType = contentType;
	}

	public Type getContentType() {
		return contentType;
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
		return "[]" + contentType.toString();
	}

	@Override
	public int hashCode() {
		return contentType.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof GoSliceType && contentType.equals(((GoSliceType) other).getContentType());
	}
}
