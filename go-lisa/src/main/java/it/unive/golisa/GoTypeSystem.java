package it.unive.golisa;

import it.unive.golisa.cfg.type.GoBoolType;
import it.unive.golisa.cfg.type.GoNilType;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.lisa.type.BooleanType;
import it.unive.lisa.type.CharacterType;
import it.unive.lisa.type.NumericType;
import it.unive.lisa.type.StringType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;

/**
 * The {@link TypeSystem} for the Go language.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoTypeSystem extends TypeSystem {

	@Override
	public BooleanType getBooleanType() {
		return GoBoolType.INSTANCE;
	}

	@Override
	public StringType getStringType() {
		return GoStringType.INSTANCE;
	}

	@Override
	public NumericType getIntegerType() {
		return GoIntType.INSTANCE;
	}

	@Override
	public boolean canBeReferenced(Type type) {
		return true;
	}

	@Override
	public CharacterType getCharacterType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int distanceBetweenTypes(Type first, Type second) {

		return 0;
		/*
		 * if (first instanceof Untyped || second instanceof Untyped) return 0;
		 * if (second instanceof GoNumericType numericParam) if (first
		 * instanceof GoNumericType numericFormal) { int paramDist =
		 * numericParam.distance(numericFormal); if (paramDist < 0) return -1;
		 * // incomparable return paramDist; } else return -1; else if
		 * (second.isBooleanType() && first.isBooleanType()) return 0; else if
		 * (second instanceof ReferenceType refTypeParam && first instanceof
		 * ReferenceType refTypeFormal) { if
		 * (refTypeParam.getInnerType().isNullType() ||
		 * refTypeParam.getInnerType() instanceof GoNilType) return 0; else if
		 * ((refTypeParam.getInnerType() instanceof GoArrayType actualInner &&
		 * refTypeFormal.getInnerType() instanceof GoArrayType formalInner))
		 * return actualInner.equals(formalInner) ? 0 : -1; else if
		 * (refTypeParam.getInnerType() instanceof GoSliceType actualInner &&
		 * refTypeFormal.getInnerType() instanceof GoSliceType formalInner)
		 * return actualInner.equals(formalInner) ? 0 : -1; } else
		 * if(areSameClass(second,first)){ return 0; } else
		 * if(second.canBeAssignedTo(first) ) return 0; if (isNullType(first) ||
		 * isNullType(second)) return 0; return -1;
		 */
	}

	/**
	 * Yields {@code true} if the type is null
	 * 
	 * @param t the type to check
	 * 
	 * @return {@code true} if the type is null
	 */
	private boolean isNullType(Type t) {
		return t.isNullType() || t instanceof GoNilType;
	}

	/**
	 * Yields {@code true} if they have the same class.
	 * 
	 * @param a the class to check
	 * @param b the other class to check
	 * 
	 * @return {@code true} if they have the same class
	 */
	public static boolean areSameClass(Object a, Object b) {
		return a != null &&
				b != null &&
				a.getClass() == b.getClass();
	}

}
