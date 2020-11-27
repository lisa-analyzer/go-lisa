package it.unive.golisa.cfg.type.composite;

import java.util.HashMap;
import java.util.Map;

import it.unive.golisa.cfg.expression.literal.GoStructLiteral;
import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.type.Type;

public class GoStructType implements GoType {

	private final Map<String, GoType> fields;
	
	private static final Map<String, GoStructType> structTypes = new HashMap<>();

	public static GoStructType lookup(String name, GoStructType type)  {
		if (!structTypes.containsKey(name))
			structTypes.put(name, type);
		return structTypes.get(name);
	}
	
	public GoStructType(Map<String, GoType> fields) {
		this.fields = fields;
	}

	public static boolean hasStructType(String structType) {
		return structTypes.containsKey(structType);
	}
	
	public static GoStructType get(String structType) {
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

	@Override
	public Expression defaultValue(CFG cfg) {
		Map<String, Expression> defaultFields = new HashMap<>();
		
		for (String key: fields.keySet())
			defaultFields.put(key, fields.get(key).defaultValue(cfg));
		
		return new GoStructLiteral(cfg, defaultFields, this);
	}
}
