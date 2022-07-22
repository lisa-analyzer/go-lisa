package it.unive.golisa.cfg.type.composite;

import it.unive.golisa.cfg.expression.literal.GoNonKeyedLiteral;
import it.unive.golisa.cfg.expression.unknown.GoUnknown;
import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.InMemoryType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.UnitType;
import it.unive.lisa.type.Untyped;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * A Go struct type.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoStructType implements GoType, UnitType, InMemoryType {

	private static final Map<String, GoStructType> structTypes = new HashMap<>();

	/**
	 * Yields a unique instance (either an existing one or a fresh one) of
	 * {@link GoStructType} representing a struct type with the given
	 * {@code name}, representing the given {@code unit}.
	 * 
	 * @param name the name of the struct type
	 * @param unit the unit underlying this type
	 * 
	 * @return the unique instance of {@link GoStructType} representing the
	 *             struct type with the given name
	 */
	public static GoStructType lookup(String name, CompilationUnit unit) {
		return structTypes.computeIfAbsent(name, x -> new GoStructType(name, unit));
	}

	/**
	 * Updates the reference to a struct type.
	 * 
	 * @param name name of the struct type to be updated
	 * @param unit the compilation unit to update
	 */
	public static void updateReference(String name, CompilationUnit unit) {
		if (structTypes.containsKey(name))
			structTypes.put(name, new GoStructType(name, unit));
	}

	private final String name;
	private final CompilationUnit unit;

	/**
	 * Builds the struct type.
	 * 
	 * @param name name of the struct type
	 * @param unit the compilation unit of the struct type.
	 */
	protected GoStructType(String name, CompilationUnit unit) {
		this.name = name;
		this.unit = unit;
	}

	/**
	 * Checks whether a struct type named {@code name} has been already built.
	 * 
	 * @param name the name of the struct type
	 * 
	 * @return whether a struct type named {@code name} has been already built.
	 */
	public static boolean hasStructType(String name) {
		return structTypes.containsKey(name);
	}

	/**
	 * Yields a Go struct type from given name.
	 * 
	 * @param structType the name
	 * 
	 * @return a Go struct type from given name
	 */
	public static GoStructType get(String structType) {
		return structTypes.get(structType);
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		if (other.isUntyped())
			return true;
		if (other instanceof GoStructType)
			return ((GoStructType) other).name.equals(name);
		if (other instanceof GoInterfaceType) {
			GoInterfaceType intf = (GoInterfaceType) other;

			if (intf.isEmptyInterface())
				return true;

			for (CFG methodSpec : intf.getUnit().getAllCFGs()) {
				String methodName = methodSpec.getDescriptor().getName();
				Type methodReturnType = methodSpec.getDescriptor().getReturnType();
				Parameter[] methodPars = methodSpec.getDescriptor().getFormals();
				boolean match = false;
				for (CFG structMethod : getUnit().getAllCFGs()) {
					String funcName = structMethod.getDescriptor().getName();
					Type funcReturnType = structMethod.getDescriptor().getReturnType();
					Parameter[] funcPars = structMethod.getDescriptor().getFormals();

					if (funcName.equals(methodName) && funcReturnType.canBeAssignedTo(methodReturnType)) {
						if (methodPars.length == 0 && funcPars.length == 1)
							match = true;
						else {
							if (methodPars.length + 1 == funcPars.length) {
								for (int i = 0; i < methodPars.length; i++)
									if (methodPars[i].getName().equals(funcPars[i + 1].getName()) && methodPars[i]
											.getStaticType().canBeAssignedTo(funcPars[i + 1].getStaticType()))
										match = true;
							}
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
		Collection<Global> fields = getUnit().getInstanceGlobals(true);
		Expression[] values = new Expression[fields.size()];

		int i = 0;
		for (Global key : fields)
			if (key.getStaticType() instanceof GoType)
				values[i++] = ((GoType) key.getStaticType()).defaultValue(cfg, location);
			else
				values[i++] = new GoUnknown(cfg, location);

		return new GoNonKeyedLiteral(cfg, location, values, this);
	}

	@Override
	public CompilationUnit getUnit() {
		return unit;
	}

	/**
	 * Yields all the struct types.
	 * 
	 * @return all the struct types
	 */
	public static Collection<Type> all() {
		Collection<Type> instances = new HashSet<>();
		for (GoStructType in : structTypes.values())
			instances.add(in);
		return instances;
	}

	@Override
	public Collection<Type> allInstances() {
		return all();
	}

	/**
	 * Clears all the struct types.
	 */
	public static void clearAll() {
		structTypes.clear();
	}
}
