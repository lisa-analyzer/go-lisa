package it.unive.golisa.cfg.type.composite;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import it.unive.golisa.cfg.expression.literal.GoNil;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.InterfaceUnit;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.InMemoryType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;
import it.unive.lisa.type.UnitType;
import it.unive.lisa.type.Untyped;

/**
 * A Go interface type.
 *
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoInterfaceType implements Type, UnitType, InMemoryType {

	private static final Map<String, GoInterfaceType> interfaces = new HashMap<>();

	/**
	 * Yields a unique instance (either an existing one or a fresh one) of
	 * {@link GoInterfaceType} representing an interface type with the given
	 * {@code name}, representing the given {@code unit}.
	 * 
	 * @param name    the name of the interface type
	 * @param unit    the unit underlying this type
	 * @param program the program to which this type belongs to
	 * 
	 * @return the unique instance of {@link GoInterfaceType} representing the
	 *             interface type with the given name
	 */
	public static GoInterfaceType lookup(String name, CompilationUnit unit, Program program) {
		if (name.equals(EmptyInterface.EMPTY_INTERFACE_NAME))
			return interfaces.computeIfAbsent(name, x -> new EmptyInterface(program));
		return interfaces.computeIfAbsent(name, x -> new GoInterfaceType(name, unit));
	}

	/**
	 * Registers an interface type.
	 * 
	 * @param type the interface type to be registered
	 */
	public static void registerType(GoInterfaceType type) {
		interfaces.put(type.name, type);
	}

	/**
	 * Yields the empty interface.
	 * 
	 * @return the empty interface
	 */
	public static GoInterfaceType getEmptyInterface() {
		return GoInterfaceType.lookup(EmptyInterface.EMPTY_INTERFACE_NAME, null, null);
	}

	private final String name;
	private final CompilationUnit unit;

	/**
	 * Builds an interface type.
	 * 
	 * @param name the name of the interface type
	 * @param unit the corresponding unit
	 */
	protected GoInterfaceType(String name, CompilationUnit unit) {
		this.unit = unit;
		this.name = name;
	}

	/**
	 * Checks whether an interface type named {@code name} has been already
	 * built.
	 * 
	 * @param intfType the name of the interface type
	 * 
	 * @return whether an interface type named {@code name} has been already
	 *             built.
	 */
	public static boolean hasInterfaceType(String intfType) {
		return interfaces.containsKey(intfType);
	}

	/**
	 * Checks whether this interface is empty.
	 * 
	 * @return whether this interface is emtpy
	 */
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
	public Expression defaultValue(CFG cfg, CodeLocation location) {
		return new GoNil(cfg, location);
	}

	/**
	 * Yields all the interface types.
	 * 
	 * @return all the interface types
	 */
	public static Collection<Type> all() {
		Collection<Type> instances = new HashSet<>();
		for (GoInterfaceType in : interfaces.values())
			instances.add(in);
		return instances;
	}

	/**
	 * Clears all the interface types.
	 */
	public static void clearAll() {
		interfaces.clear();
	}

	@Override
	public Set<Type> allInstances(TypeSystem type) {
		Set<Type> instances = new HashSet<>();
		for (Unit un : unit.getInstances())
			if (un instanceof InterfaceUnit)
				instances.add(interfaces.get(un.getName()));
			else
				instances.add(GoStructType.get(un.getName()));
		return instances;
	}

	@Override
	public CompilationUnit getUnit() {
		return unit;
	}

	private static class EmptyInterface extends GoInterfaceType {

		private static final String EMPTY_INTERFACE_NAME = "EMPTY_INTERFACE";

		protected EmptyInterface(Program program) {
			super(EMPTY_INTERFACE_NAME, new EmptyInterfaceUnit(program));
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
