package it.unive.golisa.analysis;

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
import it.unive.lisa.symbolic.value.BinaryOperator;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.TernaryOperator;
import it.unive.lisa.symbolic.value.UnaryOperator;

public class Taint extends BaseInferredValue<Taint> {

	public static final Annotation TAINTED_ANNOTATION = new Annotation("lisa.taint.Tainted");

	private static final AnnotationMatcher TAINTED_MATCHER = new BasicAnnotationMatcher(TAINTED_ANNOTATION);

	public static final Annotation CLEAN_ANNOTATION = new Annotation("lisa.taint.Clean");

	private static final AnnotationMatcher CLEAN_MATCHER = new BasicAnnotationMatcher(CLEAN_ANNOTATION);

	private static final Taint TAINTED = new Taint(true);

	private static final Taint CLEAN = new Taint(false);

	private static final Taint BOTTOM = new Taint(null);

	private final Boolean taint;

	public Taint() {
		this(true);
	}

	private Taint(Boolean taint) {
		this.taint = taint;
	}

	@Override
	public Taint variable(Identifier id, ProgramPoint pp) throws SemanticException {
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
	public Taint top() {
		return TAINTED;
	}

	@Override
	public Taint bottom() {
		return BOTTOM;
	}

	public boolean isTainted() {
		return this == TAINTED;
	}

	@Override
	protected InferredPair<Taint> evalNullConstant(Taint state, ProgramPoint pp)
			throws SemanticException {
		return new InferredPair<>(this, CLEAN, bottom());
	}

	@Override
	protected InferredPair<Taint> evalNonNullConstant(Constant constant, Taint state,
			ProgramPoint pp) throws SemanticException {
		return new InferredPair<>(this, CLEAN, bottom());
	}

	@Override
	protected InferredPair<Taint> evalUnaryExpression(UnaryOperator operator, Taint arg,
			Taint state, ProgramPoint pp) throws SemanticException {
		return new InferredPair<>(this, arg, bottom());
	}

	@Override
	protected InferredPair<Taint> evalBinaryExpression(BinaryOperator operator, Taint left,
			Taint right, Taint state, ProgramPoint pp) throws SemanticException {
		return new InferredPair<>(this, left.lub(right), bottom());
	}

	@Override
	protected InferredPair<Taint> evalTernaryExpression(TernaryOperator operator, Taint left,
			Taint middle, Taint right, Taint state, ProgramPoint pp)
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
	protected Taint lubAux(Taint other) throws SemanticException {
		return TAINTED; // should never happen
	}

	@Override
	protected Taint wideningAux(Taint other) throws SemanticException {
		return TAINTED; // should never happen
	}

	@Override
	protected boolean lessOrEqualAux(Taint other) throws SemanticException {
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
		Taint other = (Taint) obj;
		if (taint == null) {
			if (other.taint != null)
				return false;
		} else if (!taint.equals(other.taint))
			return false;
		return true;
	}
}
