package it.unive.golisa.cfg.type.composite;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;

public class GoVariadicType implements GoType {

	public static final Set<GoVariadicType> variadicTypes = new HashSet<>();

	private final GoType contentType;


	public static GoVariadicType lookup(GoVariadicType type)  {
		if (!variadicTypes.contains(type))
			variadicTypes.add(type);
		return variadicTypes.stream().filter(x -> x.equals(type)).findFirst().get();
	}

	public GoVariadicType(GoType contentType) {
		this.contentType = contentType;
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		if (other instanceof GoVariadicType) {
			GoVariadicType that = (GoVariadicType) other;
			return this.contentType.canBeAssignedTo(that.contentType);
		}

		//TODO: what about slices?
		return other.isUntyped();
	}

	@Override
	public Type commonSupertype(Type other) {
		if (other instanceof GoVariadicType) {
			Type contentCommonType = contentType.commonSupertype(((GoVariadicType) other).contentType);
			if (!contentCommonType.isUntyped())
				return new GoVariadicType((GoType) contentCommonType);
		}
		
		//TODO: what about slices?
		return Untyped.INSTANCE;
	}

	@Override
	public int hashCode() {
		return Objects.hash(contentType);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GoVariadicType other = (GoVariadicType) obj;
		return Objects.equals(contentType, other.contentType);
	}

	@Override
	public String toString() {
		return "..." + contentType.toString();
	}
	
	@Override
	public Expression defaultValue(CFG cfg, SourceCodeLocation location) {
		// TODO: default value of a variadic type?
		return null;
	}

	public static Collection<Type> all() {
		Collection<Type> instances = new HashSet<>();
		for (GoVariadicType in : variadicTypes)
			instances.add(in);
		return instances;	
	}

	@Override
	public Collection<Type> allInstances() {
		Collection<Type> instances = new HashSet<>();
		for (GoVariadicType in : variadicTypes)
			instances.add(in);
		return instances;
	}

}
