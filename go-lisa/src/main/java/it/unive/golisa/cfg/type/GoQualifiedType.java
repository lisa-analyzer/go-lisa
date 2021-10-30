package it.unive.golisa.cfg.type;

import it.unive.golisa.cfg.expression.literal.GoNil;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class GoQualifiedType implements GoType {

	public static final Set<GoQualifiedType> qualTypes = new HashSet<>();

	public static GoQualifiedType lookup(GoQualifiedType type) {
		if (!qualTypes.contains(type))
			qualTypes.add(type);
		return qualTypes.stream().filter(x -> x.equals(type)).findFirst().get();
	}

	private String left;
	private String right;

	public GoQualifiedType(String left, String right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		return equals(other) || other.isUntyped();
	}

	@Override
	public Type commonSupertype(Type other) {
		return equals(other) ? other : Untyped.INSTANCE;
	}

	@Override
	public String toString() {
		return left.toString() + "." + right.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
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
		GoQualifiedType other = (GoQualifiedType) obj;
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		return true;
	}

	@Override
	public Expression defaultValue(CFG cfg, SourceCodeLocation location) {
		return new GoNil(cfg, location);
	}

	public static Collection<Type> all() {
		Collection<Type> instances = new HashSet<>();
		for (GoQualifiedType in : qualTypes)
			instances.add(in);
		return instances;
	}

	@Override
	public Collection<Type> allInstances() {
		Collection<Type> instances = new HashSet<>();
		for (GoQualifiedType in : qualTypes)
			instances.add(in);
		return instances;
	}
}
