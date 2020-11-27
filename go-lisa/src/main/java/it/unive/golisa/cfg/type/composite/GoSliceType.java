package it.unive.golisa.cfg.type.composite;

import java.util.HashSet;
import java.util.Set;

import it.unive.golisa.cfg.expression.literal.GoNil;
import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.type.Type;

public class GoSliceType implements GoType {
	
	private Type contentType;

	private static final Set<GoSliceType> sliceTypes = new HashSet<>();

	public static GoSliceType lookup(GoSliceType type)  {
		if (!sliceTypes.contains(type))
			sliceTypes.add(type);
		return sliceTypes.stream().filter(x -> x.equals(type)).findFirst().get();
	}
	
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

	@Override
	public Expression defaultValue(CFG cfg) {
		return new GoNil(cfg);
	}
	
}
