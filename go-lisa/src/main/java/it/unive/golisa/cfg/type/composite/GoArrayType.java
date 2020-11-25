package it.unive.golisa.cfg.type.composite;

import it.unive.golisa.cfg.literal.GoInteger;
import it.unive.lisa.cfg.type.Type;

public class GoArrayType implements Type {

	private Type contentType;
	private GoInteger length;
	
	public GoArrayType(Type contentType, GoInteger length) {
		this.contentType = contentType;
		this.length = length;
	}

	public Type getContentType() {
		return contentType;
	}
	
	public GoInteger getLength() {
		return length;
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
		return "[" + length + "]" + contentType.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((contentType == null) ? 0 : contentType.hashCode());
		result = prime * result + ((length == null) ? 0 : length.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GoArrayType other = (GoArrayType) obj;
		if (contentType == null) {
			if (other.contentType != null)
				return false;
		} else if (!contentType.equals(other.contentType))
			return false;
		if (length == null) {
			if (other.length != null)
				return false;
		} else if (!length.equals(other.length))
			return false;
		return true;
	}
}
