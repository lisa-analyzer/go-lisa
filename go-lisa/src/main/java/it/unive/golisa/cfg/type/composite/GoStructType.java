package it.unive.golisa.cfg.type.composite;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import it.unive.golisa.cfg.expression.literal.GoNil;
import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.PointerType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.UnitType;
import it.unive.lisa.type.Untyped;

public class GoStructType implements GoType, UnitType, PointerType {

	private static final Map<String, GoStructType> structTypes = new HashMap<>();

	public static GoStructType lookup(String name, CompilationUnit unit)  {
		return structTypes.computeIfAbsent(name, x -> new GoStructType(name, unit));
	}

	public static void updateReference(String name, CompilationUnit unit)  {
		if (structTypes.containsKey(name))
			structTypes.put(name, new GoStructType(name, unit));
	}

	private final String name;
	private final CompilationUnit unit;

	public GoStructType(String name, CompilationUnit unit) {
		this.name = name;
		this.unit = unit;
	}

	public static boolean hasStructType(String structType) {
		return structTypes.containsKey(structType);
	}

	public static GoStructType get(String structType) {
		return structTypes.get(structType);
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		if (other instanceof GoStructType)
			return ((GoStructType) other).name.equals(name);
		if (other instanceof GoInterfaceType) {
			GoInterfaceType intf = (GoInterfaceType) other;

			if (intf.isEmptyInterface())
				return true;
			
			for (CFG methodSpec : intf.getUnit().getAllCFGs()) {
				String methodName = methodSpec.getDescriptor().getName();
				Type methodReturnType = methodSpec.getDescriptor().getReturnType();
				Parameter[] methodPars = methodSpec.getDescriptor().getArgs();
				boolean match = false;
				for (CFG structMethod : getUnit().getAllCFGs()) {
					String funcName = structMethod.getDescriptor().getName();
					Type funcReturnType = structMethod.getDescriptor().getReturnType();
					Parameter[] funcPars = structMethod.getDescriptor().getArgs();		

					if (funcName.equals(methodName) && funcReturnType.canBeAssignedTo(methodReturnType)) {
						if (methodPars.length == 0 && funcPars.length == 1) 
							match = true;
						else {
							for (int i = 0; i < methodPars.length; i++)
								if (methodPars[i].getName().equals(funcPars[i +1].getName())  && methodPars[i].getStaticType().canBeAssignedTo(funcPars[i+1].getStaticType()))
									match = true;
						}
					}
				}

				if (!match)
					return false;
			}

			return true;
		}

		return other.isUntyped();
	}

	@Override
	public Type commonSupertype(Type other) {
		if (other instanceof GoStructType)
			return ((GoStructType) other).name.equals(name) ? other : Untyped.INSTANCE;

		// TODO: how to treat interfaces?
		return Untyped.INSTANCE;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((unit == null) ? 0 : unit.hashCode());
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
		GoStructType other = (GoStructType) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (unit == null) {
			if (other.unit != null)
				return false;
		} else if (!unit.equals(other.unit))
			return false;
		return true;
	}

	@Override
	public Expression defaultValue(CFG cfg, SourceCodeLocation location) {
		return new GoNil(cfg, location);
	}

	@Override
	public boolean isPointerType() {
		return true;
	}

	@Override
	public CompilationUnit getUnit() {
		return unit;
	}
	
	public static Collection<Type> all() {
		Collection<Type> instances = new HashSet<>();
		for (GoStructType in : structTypes.values())
			instances.add(in);
		return instances;	
	}
	
	@Override
	public Collection<Type> allInstances() {
		Collection<Type> instances = new HashSet<>();
		for (GoStructType in : structTypes.values())
			instances.add(in);
		return instances;
	}
	
	public static void clearAll() {
		structTypes.clear();
	}
}
