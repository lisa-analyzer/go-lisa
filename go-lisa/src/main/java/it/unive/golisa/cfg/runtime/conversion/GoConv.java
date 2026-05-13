package it.unive.golisa.cfg.runtime.conversion;

import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt8Type;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Go conversion symbolic expression.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoConv implements BinaryOperator {

	/**
	 * The singleton instance of this class.
	 */
	public static final GoConv INSTANCE = new GoConv();

	private GoConv() {
	}

	@Override
	public Set<Type> typeInference(TypeSystem types, Set<Type> left, Set<Type> right) {
		if (right.stream().noneMatch(Type::isTypeTokenType))
			return new HashSet<Type>();
		Set<Type> set = convert(left, right);
		if (set.isEmpty())
			return new HashSet<Type>();
		return set;
	}

	private Set<Type> convert(Set<Type> left, Set<Type> right) {

		Set<Type> result = new HashSet<>();
		for (Type token : right.stream().filter(Type::isTypeTokenType)
				.flatMap(t -> t.asTypeTokenType().getTypes().stream()).collect(Collectors.toSet()))
			for (Type t : left)
				if ((t.isNumericType() && token.isStringType()) || (token.isNumericType() && t.isStringType())
						|| (t.isStringType() && isSliceOfBytes(token)))
					result.add(token);
				else if (t.canBeAssignedTo(token))
					result.add(token);

		return result;

	}

	private boolean isSliceOfBytes(Type token) {
		if (token instanceof GoSliceType) {
			GoSliceType s = (GoSliceType) token;
			return s.getContentType().equals(GoUInt8Type.INSTANCE);
		}
		return false;
	}

}