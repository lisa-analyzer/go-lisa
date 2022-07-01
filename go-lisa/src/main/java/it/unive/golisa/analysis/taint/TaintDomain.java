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
import it.unive.lisa.type.Type;

/**
 * The taint domain, used for the taint analysis
 */
public class TaintDomain extends BaseInferredValue<TaintDomain> {

	public static final Annotation TAINTED_ANNOTATION = new Annotation("lisa.taint.Tainted");

	private static final AnnotationMatcher TAINTED_MATCHER = new BasicAnnotationMatcher(TAINTED_ANNOTATION);

	public static final Annotation CLEAN_ANNOTATION = new Annotation("lisa.taint.Clean");

	private static final AnnotationMatcher CLEAN_MATCHER = new BasicAnnotationMatcher(CLEAN_ANNOTATION);

	private static final TaintDomain TOP = new TaintDomain((byte) 3);

	private static final TaintDomain TAINTED = new TaintDomain((byte) 2);

	private static final TaintDomain CLEAN = new TaintDomain((byte) 1);

	private static final TaintDomain BOTTOM = new TaintDomain((byte) 0);

	private final byte v;

	public TaintDomain() {
		this((byte) 3);
	}

	private TaintDomain(byte v) {
		this.v = v;
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
				: this == CLEAN ? new StringRepresentation("_")
						: this == TAINTED ? new StringRepresentation("#") : Lattice.TOP_REPR;
	}

	@Override
	public TaintDomain top() {
		return TAINTED;
	}

	@Override
	public TaintDomain bottom() {
		return BOTTOM;
	}

	/**
	 * Yields if the state is tatinted
	 * 
	 * @return {@code true} if is tainted, otherwise {@code false}
	 */
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
		if (constant instanceof Tainted)
			return new InferredPair<>(this, TAINTED, bottom());
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
		if (left == TAINTED || right == TAINTED)
			return new InferredPair<>(this, TAINTED, bottom());

		if (left == TOP || right == TOP)
			return new InferredPair<>(this, TOP, bottom());

		return new InferredPair<>(this, CLEAN, bottom());
	}

	@Override
	protected InferredPair<TaintDomain> evalTernaryExpression(TernaryOperator operator, TaintDomain left,
			TaintDomain middle, TaintDomain right, TaintDomain state, ProgramPoint pp)
			throws SemanticException {
		if (left == TAINTED || right == TAINTED || middle == TAINTED)
			return new InferredPair<>(this, TAINTED, bottom());

		if (left == TOP || right == TOP || middle == TOP)
			return new InferredPair<>(this, TOP, bottom());

		return new InferredPair<>(this, CLEAN, bottom());
	}

	@Override
	public boolean tracksIdentifiers(Identifier id) {
		for (Type t : id.getRuntimeTypes())
			if (!(t.isInMemoryType()))
				return true;
		return false;
	}

	@Override
	public boolean canProcess(SymbolicExpression expression) {
		return true;
	}

	@Override
	protected TaintDomain lubAux(TaintDomain other) throws SemanticException {
		return TOP; // should never happen
	}

	@Override
	protected TaintDomain wideningAux(TaintDomain other) throws SemanticException {
		return TOP; // should never happen
	}

	@Override
	protected boolean lessOrEqualAux(TaintDomain other) throws SemanticException {
		return false; // should never happen
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + v;
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
		if (v != other.v)
			return false;
		return true;
	}
}
