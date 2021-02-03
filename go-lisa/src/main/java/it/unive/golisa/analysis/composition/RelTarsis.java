package it.unive.golisa.analysis.composition;

import it.unive.golisa.analysis.rsubs.RelationalSubstringDomain;
import it.unive.golisa.analysis.tarsis.Tarsis;
import it.unive.lisa.analysis.BaseLattice;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.ValueDomain;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.ValueExpression;

public class RelTarsis extends BaseLattice<RelTarsis> implements ValueDomain<RelTarsis> {

	private final static RelTarsis TOP = new RelTarsis();
	private final static RelTarsis BOTTOM = new RelTarsis();

	private final ValueEnvironment<Tarsis> tarsis;
	private final RelationalSubstringDomain rsubs;

	private final boolean isTop;
	private final boolean isBottom;

	public RelTarsis() {
		this(new ValueEnvironment<Tarsis>(new Tarsis()), new RelationalSubstringDomain());
	}

	private RelTarsis(ValueEnvironment<Tarsis> tarsis, RelationalSubstringDomain rsubs) {
		this.tarsis = tarsis;
		this.rsubs = rsubs;
		this.isTop = tarsis.isTop() && rsubs.isTop();
		this.isBottom = tarsis.isBottom() && rsubs.isBottom();
	}

	@Override
	public RelTarsis assign(Identifier id, ValueExpression expression, ProgramPoint pp) throws SemanticException {
		return new RelTarsis(tarsis.assign(id, expression, pp), rsubs.assign(id, expression, pp));
	}

	@Override
	public RelTarsis smallStepSemantics(ValueExpression expression, ProgramPoint pp) throws SemanticException {
		return new RelTarsis(tarsis.smallStepSemantics(expression, pp), rsubs.smallStepSemantics(expression, pp));
	}

	@Override
	public RelTarsis assume(ValueExpression expression, ProgramPoint pp) throws SemanticException {
		// TODO Auto-generated method stub
		return new RelTarsis(tarsis.assume(expression, pp), rsubs.assume(expression, pp));
	}

	@Override
	public RelTarsis forgetIdentifier(Identifier id) throws SemanticException {
		return new RelTarsis(tarsis.forgetIdentifier(id), rsubs.forgetIdentifier(id));
	}

	@Override
	public Satisfiability satisfies(ValueExpression expression, ProgramPoint pp) throws SemanticException {
		if (tarsis.satisfies(expression, pp) == Satisfiability.SATISFIED || rsubs.satisfies(expression, pp) == Satisfiability.SATISFIED)
			return Satisfiability.SATISFIED;

		if (tarsis.satisfies(expression, pp) == Satisfiability.NOT_SATISFIED && rsubs.satisfies(expression, pp) == Satisfiability.NOT_SATISFIED)
			return Satisfiability.NOT_SATISFIED;

		return Satisfiability.UNKNOWN;
	}

	@Override
	public String representation() {
		if (isTop())
			return "TOP";
		if (isBottom())
			return "BOTTOM";

		return tarsis.representation() + " " + rsubs.representation();
	}

	@Override
	public boolean isTop() {
		return isTop;
	}

	@Override
	public boolean isBottom() {
		return isBottom;
	}

	@Override
	public RelTarsis top() {
		return TOP;
	}

	@Override
	public RelTarsis bottom() {
		return BOTTOM;
	}

	@Override
	protected RelTarsis lubAux(RelTarsis other) throws SemanticException {
		return new RelTarsis(tarsis.lub(other.tarsis), rsubs.lub(other.rsubs));
	}

	@Override
	protected RelTarsis wideningAux(RelTarsis other) throws SemanticException {
		return new RelTarsis(tarsis.widening(other.tarsis), rsubs.widening(other.rsubs));
	}

	@Override
	protected boolean lessOrEqualAux(RelTarsis other) throws SemanticException {
		return tarsis.lessOrEqual(other.tarsis) && rsubs.lessOrEqual(other.rsubs);
	}

	@Override
	public int hashCode() {
		if (isTop())
			return 1;
		if (isBottom())
			return 2;

		final int prime = 31;
		int result = 1;
		result = prime * result + (isBottom ? 1231 : 1237);
		result = prime * result + (isTop ? 1231 : 1237);
		result = prime * result + ((rsubs == null) ? 0 : rsubs.hashCode());
		result = prime * result + ((tarsis == null) ? 0 : tarsis.hashCode());
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
		RelTarsis other = (RelTarsis) obj;
		if (isBottom != other.isBottom)
			return false;
		if (isTop != other.isTop)
			return false;
		if (rsubs == null) {
			if (other.rsubs != null)
				return false;
		} else if (!rsubs.equals(other.rsubs))
			return false;
		if (tarsis == null) {
			if (other.tarsis != null)
				return false;
		} else if (!tarsis.equals(other.tarsis))
			return false;
		return isTop && other.isTop;
	}

	@Override
	public String toString() {
		return representation();
	}
}