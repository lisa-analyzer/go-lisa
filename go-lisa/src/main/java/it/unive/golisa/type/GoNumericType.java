package it.unive.golisa.type;

import it.unive.lisa.type.NumericType;

/**
 * The numerical type of Go.
 */
public interface GoNumericType extends NumericType {

	private static int distanceFromSmallest(
			GoNumericType type) {
		if (type.is8Bits()) { // byte
			return 0;
		}
		if (type.is16Bits()) { // short, char
			return 1;
		}
		if (type.is32Bits() && type.isIntegral()) { // int
			return 2;
		}
		if (type.is64Bits() && type.isIntegral()) { // long
			return 3;
		}
		if (type.is32Bits() && !type.isIntegral()) { // float
			return 4;
		}
		if (type.is64Bits() && !type.isIntegral()) { // double
			return 5;
		}
		return -1; // incomparable (should never happen)
	}

	/**
	 * Compute the distance between the numerical types.
	 * 
	 * @param other the other type
	 * 
	 * @return the distance between numerical types
	 */
	default int distance(
			GoNumericType other) {
		return distanceFromSmallest(other) - distanceFromSmallest(this);
	}
}
