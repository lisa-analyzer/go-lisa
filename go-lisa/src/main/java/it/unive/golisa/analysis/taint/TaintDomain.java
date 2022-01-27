package it.unive.golisa.analysis.taint;

import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.nonrelational.inference.BaseInferredValue;
import it.unive.lisa.analysis.representation.DomainRepresentation;
import it.unive.lisa.analysis.representation.StringRepresentation;
import it.unive.lisa.program.annotations.Annotation;
import it.unive.lisa.program.annotations.Annotations;
import it.unive.lisa.program.annotations.matcher.AnnotationMatcher;
import it.unive.lisa.program.annotations.matcher.BasicAnnotationMatcher;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.symbolic.value.operator.ternary.TernaryOperator;
import it.unive.lisa.symbolic.value.operator.unary.UnaryOperator;

public class TaintDomain extends BaseInferredValue<TaintDomain> {

	public static final Annotation TAINTED_ANNOTATION = new Annotation("lisa.taint.Tainted");

	private static final AnnotationMatcher TAINTED_MATCHER = new BasicAnnotationMatcher(TAINTED_ANNOTATION);

	public static final Annotation CLEAN_ANNOTATION = new Annotation("lisa.taint.Clean");

	private static final AnnotationMatcher CLEAN_MATCHER = new BasicAnnotationMatcher(CLEAN_ANNOTATION);

	private static final TaintDomain TAINTED = new TaintDomain(true);

	private static final TaintDomain CLEAN = new TaintDomain(false);

	private static final TaintDomain BOTTOM = new TaintDomain(null);

	private final Boolean taint;

	public TaintDomain() {
		this(true);
	}

	private TaintDomain(Boolean taint) {
		this.taint = taint;
	}

	@Override
	public TaintDomain variable(Identifier id, ProgramPoint pp) throws SemanticException {
		Annotations annots = id.getAnnotations();
		if (annots.isEmpty())
			return super.variable(id, pp);

		if (annots.contains(TAINTED_MATCHER))
			return TAINTED;

		if (annots.contains(CLEAN_MATCHER))
			return CLEAN;

		return super.variable(id, pp);
	}

	@Override
	public DomainRepresentation representation() {
		return this == BOTTOM ? Lattice.BOTTOM_REPR
				: this == CLEAN ? new StringRepresentation("_") : new StringRepresentation("#");
	}

	@Override
	public TaintDomain top() {
		return TAINTED;
	}

	@Override
	public TaintDomain bottom() {
		return BOTTOM;
	}

	public boolean isTainted() {
		return this == TAINTED;
	}

	@Override
	protected InferredPair<TaintDomain> evalNullConstant(TaintDomain state, ProgramPoint pp)
			throws SemanticException {
		return new InferredPair<>(this, CLEAN, bottom());
	}

	@Override
	protected InferredPair<TaintDomain> evalNonNullConstant(Constant constant, TaintDomain state,
			ProgramPoint pp) throws SemanticException {
		return new InferredPair<>(this, CLEAN, bottom());
	}

	@Override
	protected InferredPair<TaintDomain> evalUnaryExpression(UnaryOperator operator, TaintDomain arg,
			TaintDomain state, ProgramPoint pp) throws SemanticException {
		return new InferredPair<>(this, arg, bottom());
	}

	@Override
	protected InferredPair<TaintDomain> evalBinaryExpression(BinaryOperator operator, TaintDomain left,
			TaintDomain right, TaintDomain state, ProgramPoint pp) throws SemanticException {
		return new InferredPair<>(this, left.lub(right), bottom());
	}

	@Override
	protected InferredPair<TaintDomain> evalTernaryExpression(TernaryOperator operator, TaintDomain left,
			TaintDomain middle, TaintDomain right, TaintDomain state, ProgramPoint pp)
			throws SemanticException {
		return new InferredPair<>(this, left.lub(middle).lub(right), bottom());
	}

	@Override
	public boolean tracksIdentifiers(Identifier id) {
		return true;
	}

	@Override
	public boolean canProcess(SymbolicExpression expression) {
		return !expression.getDynamicType().isPointerType();
	}

	@Override
	protected TaintDomain lubAux(TaintDomain other) throws SemanticException {
		return TAINTED; // should never happen
	}

	@Override
	protected TaintDomain wideningAux(TaintDomain other) throws SemanticException {
		return TAINTED; // should never happen
	}

	@Override
	protected boolean lessOrEqualAux(TaintDomain other) throws SemanticException {
		return false; // should never happen
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((taint == null) ? 0 : taint.hashCode());
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
		TaintDomain other = (TaintDomain) obj;
		if (taint == null) {
			if (other.taint != null)
				return false;
		} else if (!taint.equals(other.taint))
			return false;
		return true;
	}
}
