package it.unive.golisa.cfg.type.composite;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import it.unive.golisa.cfg.statement.method.GoMethod;
import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.type.Type;

public class GoInterfaceType implements GoType {

	private final Set<GoMethod> methods;

	private static final Map<String, GoInterfaceType> interfaces = new HashMap<>();

	public static final GoInterfaceType EMPTY_INTERFACE = new GoInterfaceType(new HashSet<>());
	
	public static GoInterfaceType lookup(String name, GoInterfaceType type)  {
		if (!interfaces.containsKey(name))
			interfaces.put(name, type);
		return interfaces.get(name);
	}

	public GoInterfaceType(Set<GoMethod> methods) {
		this.methods = methods;
	}

	public static boolean hasStructType(String structType) {
		return interfaces.containsKey(structType);
	}

	public static GoInterfaceType get(String interfaceName) {
		return interfaces.get(interfaceName);
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
	public Expression defaultValue(CFG cfg) {
		// TODO Auto-generated method stub
		return null;
	}
}
