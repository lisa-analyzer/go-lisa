package it.unive.golisa.cfg.expression.binary;

import it.unive.lisa.caches.Caches;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import it.unive.lisa.util.collections.externalSet.ExternalSet;

public interface GoBinaryNumericalOperation {

	public default ExternalSet<Type> resultType(SymbolicExpression left, SymbolicExpression right) {
		if (left.getRuntimeTypes().noneMatch(Type::isNumericType)
				&& right.getRuntimeTypes().noneMatch(Type::isNumericType))
			// if none have numeric types in them, we cannot really compute the
			// result
			return Caches.types().mkSingletonSet(Untyped.INSTANCE);

		ExternalSet<Type> result = Caches.types().mkEmptySet();
		for (Type t1 : left.getRuntimeTypes().filter(type -> type.isNumericType() || type.isUntyped()))
			for (Type t2 : right.getRuntimeTypes().filter(type -> type.isNumericType() || type.isUntyped()))
				if (t1.isUntyped() && t2.isUntyped())
					// we do not really consider this case,
					// it will fall back into the last corner case before return
					continue;
				else if (t1.isUntyped())
					result.add(t2);
				else if (t2.isUntyped())
					result.add(t1);
				else if (t1.canBeAssignedTo(t2))
					result.add(t2);
				else if (t2.canBeAssignedTo(t1))
					result.add(t1);
				else
					result.add(Untyped.INSTANCE);
		if (result.isEmpty())
			result.add(Untyped.INSTANCE);
		return result;
	}

	public default Type resultType(Type left, Type right) {
		if (!left.isNumericType() && !right.isNumericType())
			// if none have numeric types in them, we cannot really compute the
			// result
			return Untyped.INSTANCE;

		if (left.isUntyped() && right.isUntyped())
			return left;
		else if (left.isUntyped())
			return right;
		else if (right.isUntyped())
			return left;
		else if (left.canBeAssignedTo(right))
			return right;
		else if (right.canBeAssignedTo(left))
			return left;
		else
			return Untyped.INSTANCE;
	}
}
