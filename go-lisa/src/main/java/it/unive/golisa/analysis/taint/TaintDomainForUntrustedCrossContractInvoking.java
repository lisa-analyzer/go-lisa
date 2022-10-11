package it.unive.golisa.analysis.taint;

import it.unive.golisa.cfg.runtime.conversion.GoConv;
import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.nonrelational.value.BaseNonRelationalValueDomain;
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
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.symbolic.value.operator.ternary.TernaryOperator;
import it.unive.lisa.symbolic.value.operator.unary.UnaryOperator;
import it.unive.lisa.type.Type;

/**
 * The taint domain, used for the taint analysis.
 */
public class TaintDomainForUntrustedCrossContractInvoking extends BaseNonRelationalValueDomain<TaintDomainForUntrustedCrossContractInvoking> {

	/**
	 * The annotation Tainted.
	 */
	public static final Annotation TAINTED_ANNOTATION = new Annotation("lisa.taint.Tainted");

	/**
	 * The matcher for the Tainted annotation.
	 */
	private static final AnnotationMatcher TAINTED_MATCHER = new BasicAnnotationMatcher(TAINTED_ANNOTATION);

	/**
	 * The annotation Clean.
	 */
	public static final Annotation CLEAN_ANNOTATION = new Annotation("lisa.taint.Clean");

	/**
	 * The matcher for the Clean annotation.
	 */
	private static final AnnotationMatcher CLEAN_MATCHER = new BasicAnnotationMatcher(CLEAN_ANNOTATION);

	/**
	 * The top state.
	 */
	private static final TaintDomainForUntrustedCrossContractInvoking TOP = new TaintDomainForUntrustedCrossContractInvoking((byte) 3);

	/**
	 * The top state.
	 */
	private static final TaintDomainForUntrustedCrossContractInvoking TAINTED = new TaintDomainForUntrustedCrossContractInvoking((byte) 2);

	/**
	 * The clean state.
	 */
	private static final TaintDomainForUntrustedCrossContractInvoking CLEAN = new TaintDomainForUntrustedCrossContractInvoking((byte) 1);

	/**
	 * The bottom state.
	 */
	private static final TaintDomainForUntrustedCrossContractInvoking BOTTOM = new TaintDomainForUntrustedCrossContractInvoking((byte) 0);

	private final byte v;

	/**
	 * Builds a new instance of taint, referring to the top element of the lattice.
	 */
	public TaintDomainForUntrustedCrossContractInvoking() {
		this((byte) 3);
	}

	private TaintDomainForUntrustedCrossContractInvoking(byte v) {
		this.v = v;
	}
		
	@Override
	public TaintDomainForUntrustedCrossContractInvoking variable(Identifier id, ProgramPoint pp) throws SemanticException {

		
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
		return this == BOTTOM ? Lattice.bottomRepresentation()
				: this == CLEAN ? new StringRepresentation("_")
						: this == TAINTED ? new StringRepresentation("#") : Lattice.topRepresentation();
	}

	@Override
	public TaintDomainForUntrustedCrossContractInvoking top() {
		return TOP;
	}

	@Override
	public TaintDomainForUntrustedCrossContractInvoking bottom() {
		return BOTTOM;
	}

	/**
	 * Yields if the state is tainted.
	 * 
	 * @return {@code true} if is tainted, otherwise {@code false}
	 */
	public boolean isTainted() {
		return this == TAINTED;
	}
	
	public boolean isClean() {
		return this == CLEAN;
	}

	@Override
	protected TaintDomainForUntrustedCrossContractInvoking evalNullConstant(ProgramPoint pp) throws SemanticException {
		return CLEAN;
	}

	@Override
	protected TaintDomainForUntrustedCrossContractInvoking evalNonNullConstant(Constant constant, ProgramPoint pp) throws SemanticException {
		if (constant instanceof Tainted)
			return TAINTED;
		return CLEAN;
	}

	@Override
	protected TaintDomainForUntrustedCrossContractInvoking evalUnaryExpression(UnaryOperator operator, TaintDomainForUntrustedCrossContractInvoking arg, ProgramPoint pp)
			throws SemanticException {
		return arg;
	}

	@Override
	protected TaintDomainForUntrustedCrossContractInvoking evalBinaryExpression(BinaryOperator operator, TaintDomainForUntrustedCrossContractInvoking left, TaintDomainForUntrustedCrossContractInvoking right,
			ProgramPoint pp) throws SemanticException {
		
		if (operator == GoConv.INSTANCE)
			return left;
		
		if (left == TAINTED || right == TAINTED)
			return TAINTED;

		if (left == TOP || right == TOP)
			return TOP;

		return CLEAN;
	}

	@Override
	protected TaintDomainForUntrustedCrossContractInvoking evalTernaryExpression(TernaryOperator operator, TaintDomainForUntrustedCrossContractInvoking left, TaintDomainForUntrustedCrossContractInvoking middle,
			TaintDomainForUntrustedCrossContractInvoking right, ProgramPoint pp) throws SemanticException {
		if (left == TAINTED || right == TAINTED || middle == TAINTED)
			return TAINTED;

		if (left == TOP || right == TOP || middle == TOP)
			return TOP;

		return CLEAN;
	}

	@Override
	protected TaintDomainForUntrustedCrossContractInvoking evalPushAny(PushAny pushAny, ProgramPoint pp) throws SemanticException {
 		return TAINTED;
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
	protected TaintDomainForUntrustedCrossContractInvoking lubAux(TaintDomainForUntrustedCrossContractInvoking other) throws SemanticException {
		return TOP; // should never happen
	}

	@Override
	protected TaintDomainForUntrustedCrossContractInvoking wideningAux(TaintDomainForUntrustedCrossContractInvoking other) throws SemanticException {
		return TOP; // should never happen
	}

	@Override
	protected boolean lessOrEqualAux(TaintDomainForUntrustedCrossContractInvoking other) throws SemanticException {
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
		TaintDomainForUntrustedCrossContractInvoking other = (TaintDomainForUntrustedCrossContractInvoking) obj;
		if (v != other.v)
			return false;
		return true;
	}
}