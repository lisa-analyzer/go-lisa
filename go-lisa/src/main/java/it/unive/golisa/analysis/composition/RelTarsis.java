package it.unive.golisa.analysis.composition;

import java.util.function.Predicate;

import it.unive.golisa.analysis.StringConstantPropagation;
import it.unive.golisa.analysis.rsubs.RelationalSubstringDomain;
import it.unive.lisa.analysis.BaseLattice;
import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.lattices.Satisfiability;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.string.tarsis.Tarsis;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.util.representation.ListRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

/**
 * The reduced product between Tarsis, string constant propagation and RSub.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class RelTarsis implements BaseLattice<RelTarsis>, ValueDomain<RelTarsis> {

	private final ValueEnvironment<Tarsis> tarsis;
	private final ValueEnvironment<StringConstantPropagation> constant;
	private final RelationalSubstringDomain rsubs;

	/**
	 * Builds the top abstract value.
	 */
	public RelTarsis() {
		this(new ValueEnvironment<Tarsis>(new Tarsis()), new RelationalSubstringDomain(),
				new ValueEnvironment<StringConstantPropagation>(new StringConstantPropagation()));
	}

	private RelTarsis(ValueEnvironment<Tarsis> tarsis, RelationalSubstringDomain rsubs,
			ValueEnvironment<StringConstantPropagation> constant) {
		this.tarsis = tarsis;
		this.rsubs = rsubs;
		this.constant = constant;
	}

	@Override
	public RelTarsis assign(Identifier id, ValueExpression expression, ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
		RelationalSubstringDomain rsubsAssign = rsubs.assign(id, expression, pp, oracle);
		ValueEnvironment<StringConstantPropagation> csAssign = constant.assign(id, expression, pp, oracle);
		return new RelTarsis(tarsis.assign(id, expression, pp, oracle), rsubsAssign.propagateConstants(csAssign), csAssign);
	}

	@Override
	public RelTarsis smallStepSemantics(ValueExpression expression, ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
		return new RelTarsis(tarsis.smallStepSemantics(expression, pp, oracle), rsubs.smallStepSemantics(expression, pp, oracle),
				constant.smallStepSemantics(expression, pp, oracle));
	}

	@Override
	public RelTarsis assume(ValueExpression expression, ProgramPoint src, ProgramPoint dest, SemanticOracle oracle) throws SemanticException {
		return new RelTarsis(tarsis.assume(expression, src, dest, oracle), rsubs.assume(expression, src, dest, oracle),
				constant.assume(expression, src, dest, oracle));
	}

	@Override
	public RelTarsis forgetIdentifier(Identifier id) throws SemanticException {
		return new RelTarsis(
				tarsis.forgetIdentifier(id),
				rsubs.forgetIdentifier(id),
				constant.forgetIdentifier(id));
	}

	@Override
	public RelTarsis forgetIdentifiersIf(Predicate<Identifier> test) throws SemanticException {
		return new RelTarsis(
				tarsis.forgetIdentifiersIf(test),
				rsubs.forgetIdentifiersIf(test),
				constant.forgetIdentifiersIf(test));
	}

	@Override
	public Satisfiability satisfies(ValueExpression expression, ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
		if (tarsis.satisfies(expression, pp, oracle) == Satisfiability.SATISFIED
				|| rsubs.satisfies(expression, pp, oracle) == Satisfiability.SATISFIED
				|| constant.satisfies(expression, pp, oracle) == Satisfiability.SATISFIED)
			return Satisfiability.SATISFIED;

		if (tarsis.satisfies(expression, pp, oracle) == Satisfiability.NOT_SATISFIED
				|| rsubs.satisfies(expression, pp, oracle) == Satisfiability.NOT_SATISFIED
				|| constant.satisfies(expression, pp, oracle) == Satisfiability.NOT_SATISFIED)
			return Satisfiability.NOT_SATISFIED;

		return Satisfiability.UNKNOWN;
	}

	@Override
	public StructuredRepresentation representation() {
		if (isTop())
			return Lattice.topRepresentation();
		if (isBottom())
			return Lattice.bottomRepresentation();

		return new ListRepresentation(tarsis.representation(), rsubs.representation(),
				constant.representation());
	}

	@Override
	public boolean isTop() {
		return tarsis.isTop() && rsubs.isTop() && constant.isTop();
	}

	@Override
	public boolean isBottom() {
		return tarsis.isBottom() && rsubs.isBottom() && constant.isTop();
	}

	@Override
	public RelTarsis top() {
		return new RelTarsis(new ValueEnvironment<Tarsis>(new Tarsis()), new RelationalSubstringDomain(),
				new ValueEnvironment<StringConstantPropagation>(new StringConstantPropagation()));
	}

	@Override
	public RelTarsis bottom() {
		return new RelTarsis(tarsis.bottom(), rsubs.bottom(), constant.bottom());
	}

	@Override
	public RelTarsis lubAux(RelTarsis other) throws SemanticException {
		return new RelTarsis(tarsis.lub(other.tarsis), rsubs.lub(other.rsubs), constant.lub(other.constant));
	}

	@Override
	public RelTarsis wideningAux(RelTarsis other) throws SemanticException {
		return new RelTarsis(tarsis.widening(other.tarsis), rsubs.widening(other.rsubs),
				constant.widening(other.constant));
	}

	@Override
	public boolean lessOrEqualAux(RelTarsis other) throws SemanticException {
		return tarsis.lessOrEqual(other.tarsis) && rsubs.lessOrEqual(other.rsubs)
				&& constant.lessOrEqual(other.constant);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((constant == null) ? 0 : constant.hashCode());
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
		if (constant == null) {
			if (other.constant != null)
				return false;
		} else if (!constant.equals(other.constant))
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
		return true;
	}

	@Override
	public String toString() {
		return representation().toString();
	}

	@Override
	public RelTarsis pushScope(ScopeToken token) throws SemanticException {
		return new RelTarsis(tarsis.pushScope(token), rsubs.pushScope(token), constant.pushScope(token));
	}

	@Override
	public RelTarsis popScope(ScopeToken token) throws SemanticException {
		return new RelTarsis(tarsis.popScope(token), rsubs.popScope(token), constant.popScope(token));
	}

	@Override
	public boolean knowsIdentifier(Identifier id) {
		return tarsis.knowsIdentifier(id) || constant.knowsIdentifier(id);
	}
}