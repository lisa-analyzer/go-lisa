package it.unive.golisa.cfg.type.composite;

import java.util.HashMap;
import java.util.Map;

import it.unive.lisa.cfg.type.Type;

public class GoStructType implements Type {

	public static final Map<String, GoStructType> structTypes = new HashMap<>();

	private final Map<String, Type> fields;

	public GoStructType(Map<String, Type> fields) {
		this.fields = fields;
	}

	public static void addtStructType(String name, GoStructType structType) {
		structTypes.put(name, structType);
	}

	public GoStructType getStructType(String structType) {
		return structTypes.get(structType);
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
		return "struct {" + fields.toString() + "}";
	}
}
