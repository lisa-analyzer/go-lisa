package it.unive.golisa.analysis;

import it.unive.lisa.analysis.BaseLattice;
import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

/**
 * The lattice of string constant propagation domain.
 */
public class StringConstantPropagationLattice
		implements BaseLattice<StringConstantPropagationLattice> {

	/**
	 * The top element.
	 */
	public static final StringConstantPropagationLattice TOP = new StringConstantPropagationLattice(true, false);

	/**
	 * The bottom element.
	 */
	public static final StringConstantPropagationLattice BOTTOM = new StringConstantPropagationLattice(false, true);

	private final boolean isTop, isBottom;

	/**
	 * The string constant representing a value of this domain element.
	 */
	private final String value;

	/**
	 * Builds the string constant propagation.
	 */
	public StringConstantPropagationLattice() {
		this(null, true, false);
	}

	/**
	 * Builds the string constant propagation.
	 * 
	 * @param value    the string value of this domain
	 * @param isTop    if it is top
	 * @param isBottom if it is bottom
	 */
	private StringConstantPropagationLattice(String value, boolean isTop, boolean isBottom) {
		this.value = value;
		this.isTop = isTop;
		this.isBottom = isBottom;
	}

	/**
	 * Builds the string constant propagation.
	 * 
	 * @param value the string value of this domain
	 */
	public StringConstantPropagationLattice(String value) {
		this(value, false, false);
	}

	private StringConstantPropagationLattice(boolean isTop, boolean isBottom) {
		this(null, isTop, isBottom);
	}

	@Override
	public StringConstantPropagationLattice top() {
		return TOP;
	}

	@Override
	public StringConstantPropagationLattice bottom() {
		return BOTTOM;
	}

	@Override
	public StructuredRepresentation representation() {
		if (isBottom())
			return Lattice.bottomRepresentation();
		if (isTop())
			return Lattice.topRepresentation();

		return new StringRepresentation(value.toString());
	}

	@Override
	public StringConstantPropagationLattice lubAux(StringConstantPropagationLattice other) throws SemanticException {
		return TOP;
	}

	@Override
	public boolean lessOrEqualAux(StringConstantPropagationLattice other) throws SemanticException {
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isBottom ? 1231 : 1237);
		result = prime * result + (isTop ? 1231 : 1237);
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		StringConstantPropagationLattice other = (StringConstantPropagationLattice) obj;
		if (isBottom != other.isBottom)
			return false;
		if (isTop != other.isTop)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	/**
	 * Yields the string value.
	 * 
	 * @return the string value
	 */
	public String getValue() {
		return value;
	}

}
