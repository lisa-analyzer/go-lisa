package it.unive.golisa.cfg.type.composite;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.unive.golisa.cfg.expression.literal.GoNonKeyedLiteral;
import it.unive.golisa.cfg.expression.literal.GoInteger;
import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.type.Type;

public class GoArrayType implements GoType {

	private GoType contentType;
	private GoInteger length;

	private static final Set<GoArrayType> arrayTypes = new HashSet<>();

	public static GoArrayType lookup(GoArrayType type)  {
		if (!arrayTypes.contains(type))
			arrayTypes.add(type);

		return arrayTypes.stream().filter(x -> x.equals(type)).findFirst().get();
	}

	public GoArrayType(GoType contentType, GoInteger length) {
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

	@Override
	public Expression defaultValue(CFG cfg) {
		List<Expression> result = new ArrayList<>();
		for (int i = 0; i < (Integer) length.getValue(); i++)
			result.add(contentType.defaultValue(cfg));
		
		return new GoNonKeyedLiteral(cfg, result, this);
	}
	
	@Override
	public boolean isGoInteger() {
		return false;
	}
}
