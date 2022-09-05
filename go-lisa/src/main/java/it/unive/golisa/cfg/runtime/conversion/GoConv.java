package it.unive.golisa.cfg.runtime.conversion;

import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt8Type;
import it.unive.lisa.caches.Caches;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.symbolic.value.operator.binary.TypeConv;
import it.unive.lisa.type.Type;
import it.unive.lisa.util.collections.externalSet.ExternalSet;

public class GoConv implements BinaryOperator{


	/**
	 * The singleton instance of this class.
	 */
	public static final GoConv INSTANCE = new GoConv();

	private GoConv() {
	}

	@Override
	public ExternalSet<Type> typeInference(ExternalSet<Type> left, ExternalSet<Type> right) {

			if (right.noneMatch(Type::isTypeTokenType))
				return Caches.types().mkEmptySet();
			ExternalSet<Type> set = convert(left, right);
			if (set.isEmpty())
				return Caches.types().mkEmptySet();
			return set;
	}

	private ExternalSet<Type> convert(ExternalSet<Type> left, ExternalSet<Type> right){

		ExternalSet<Type> result = Caches.types().mkEmptySet();
		for (Type token : right.filter(Type::isTypeTokenType).multiTransform(t -> t.asTypeTokenType().getTypes()))
			for (Type t : left)
				if((t.isNumericType() && token.isStringType()) || (token.isNumericType() && t.isStringType())
						|| (t.isStringType() && isSliceOfBytes(token)))
					result.add(token);
				else if (t.canBeAssignedTo(token))
					result.add(token);

		return result;


	}

	private boolean isSliceOfBytes(Type token) {
		if(token instanceof GoSliceType) {
			GoSliceType s = (GoSliceType) token;
			return s.getContentType().equals(GoUInt8Type.INSTANCE);
		}
		return false;
	}

}