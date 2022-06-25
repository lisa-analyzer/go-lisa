package it.unive.golisa.cfg.type.composite;

import it.unive.golisa.cfg.expression.literal.GoNil;
import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.caches.Caches;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.InMemoryType;
import it.unive.lisa.type.PointerType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.UnitType;
import it.unive.lisa.type.Untyped;
import it.unive.lisa.util.collections.externalSet.ExternalSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GoInterfaceType implements GoType, UnitType, InMemoryType {

	private static final Map<String, GoInterfaceType> interfaces = new HashMap<>();

	public static GoInterfaceType lookup(String name, CompilationUnit unit) {
		if (name.equals(EmptyInterface.EMPTY_INTERFACE_NAME))
			return interfaces.computeIfAbsent(name, x -> new EmptyInterface());
		return interfaces.computeIfAbsent(name, x -> new GoInterfaceType(name, unit));
	}

	public static GoInterfaceType getEmptyInterface() {
		return GoInterfaceType.get(EmptyInterface.EMPTY_INTERFACE_NAME);
	}

	private final String name;
	private final CompilationUnit unit;

	public GoInterfaceType(String name, CompilationUnit unit) {
		this.unit = unit;
		this.name = name;
	}

	public static boolean hasStructType(String structType) {
		return interfaces.containsKey(structType);
	}

	public static GoInterfaceType get(String interfaceName) {
		return interfaces.get(interfaceName);
	}

	public boolean isEmptyInterface() {
		return name.equals(EmptyInterface.EMPTY_INTERFACE_NAME);
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		return other instanceof GoInterfaceType ? ((GoInterfaceType) other).name.equals(name) : other.isUntyped();
	}

	@Override
	public Type commonSupertype(Type other) {
		return other instanceof GoInterfaceType ? other : Untyped.INSTANCE;
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
		GoInterfaceType other = (GoInterfaceType) obj;
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
	public String toString() {
		return name;
	}

	@Override
	public Expression defaultValue(CFG cfg, SourceCodeLocation location) {
		return new GoNil(cfg, location);
	}

	@Override
	public Collection<Type> allInstances() {
		return Collections.singleton(this);
	}

	@Override
	public CompilationUnit getUnit() {
		return unit;
	}

	private static class EmptyInterface extends GoInterfaceType implements PointerType {

		private static final String EMPTY_INTERFACE_NAME = "EMPTY_INTERFACE";

		public EmptyInterface() {
			super(EMPTY_INTERFACE_NAME, it.unive.golisa.golang.runtime.EmptyInterface.INSTANCE);
		}

		@Override
		public ExternalSet<Type> getInnerTypes() {
			return Caches.types().mkSingletonSet(this);
		}

		@Override
		public String toString() {
			return "interface{}";
		}

		@Override
		public boolean equals(Object obj) {
			return this instanceof EmptyInterface;
		}

		@Override
		public int hashCode() {
			return 1;
		}
	}
}
