package it.unive.golisa.cfg.type.composite;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import it.unive.golisa.cfg.statement.method.GoMethodSpecification;
import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.Type;

public class GoInterfaceType implements GoType {

	private final Set<GoMethodSpecification> methods;

	private static final Map<String, GoInterfaceType> interfaces = new HashMap<>();

	public static final GoInterfaceType EMPTY_INTERFACE = new GoInterfaceType(new HashSet<>());
	
	public static GoInterfaceType lookup(String name, GoInterfaceType type)  {
		if (!interfaces.containsKey(name))
			interfaces.put(name, type);
		return interfaces.get(name);
	}

	public GoInterfaceType(Set<GoMethodSpecification> methods) {
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
	
	@Override
	public boolean isGoInteger() {
		return false;
	}
	
	@Override
	public Collection<Type> allInstances() {
		return Collections.singleton(this);
	}
}
