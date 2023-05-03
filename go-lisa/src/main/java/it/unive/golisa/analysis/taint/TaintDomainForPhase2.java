package it.unive.golisa.analysis.taint;

import it.unive.golisa.cfg.runtime.conversion.GoConv;
import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.SemanticDomain.Satisfiability;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.heap.pointbased.AllocationSite;
import it.unive.lisa.analysis.nonrelational.value.BaseNonRelationalValueDomain;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.representation.DomainRepresentation;
import it.unive.lisa.analysis.representation.StringRepresentation;
import it.unive.lisa.program.annotations.Annotation;
import it.unive.lisa.program.annotations.Annotations;
import it.unive.lisa.program.annotations.matcher.AnnotationMatcher;
import it.unive.lisa.program.annotations.matcher.BasicAnnotationMatcher;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.symbolic.value.operator.ternary.TernaryOperator;
import it.unive.lisa.symbolic.value.operator.unary.UnaryOperator;
import it.unive.lisa.type.Type;

/**
 * The taint domain, used for phase 2 of the UCCI analysis.
 */
public class TaintDomainForPhase2 extends BaseNonRelationalValueDomain<TaintDomainForPhase2> {

	/**
	 * The annotation Tainted.
	 */
	public static final Annotation TAINTED_ANNOTATION_PHASE2 = new Annotation("lisa.taint.TaintedPhase2");

	/**
	 * The matcher for the Tainted annotation.
	 */
	private static final AnnotationMatcher TAINTED_MATCHER_PHASE2 = new BasicAnnotationMatcher(TAINTED_ANNOTATION_PHASE2);

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
	private static final TaintDomainForPhase2 TOP = new TaintDomainForPhase2((byte) 3);

	/**
	 * The top state.
	 */
	private static final TaintDomainForPhase2 TAINTED = new TaintDomainForPhase2((byte) 2);

	/**
	 * The clean state.
	 */
	private static final TaintDomainForPhase2 CLEAN = new TaintDomainForPhase2((byte) 1);

	/**
	 * The bottom state.
	 */
	private static final TaintDomainForPhase2 BOTTOM = new TaintDomainForPhase2((byte) 0);

	private final byte v;

	/**
	 * Builds a new instance of taint, referring to the top element of the
	 * lattice.
	 */
	public TaintDomainForPhase2() {
		this((byte) 3);
	}

	private TaintDomainForPhase2(byte v) {
		this.v = v;
	}

	@Override
	public TaintDomainForPhase2 variable(Identifier id, ProgramPoint pp) throws SemanticException {
		TaintDomainForPhase2 def = defaultApprox(id, pp);
		if (def != BOTTOM)
			return def;
		return super.variable(id, pp);
	}

	private TaintDomainForPhase2 defaultApprox(Identifier id, ProgramPoint pp) throws SemanticException {
		
		Annotations annots = id.getAnnotations();
		if (annots.isEmpty())
			return super.variable(id, pp);

		if (annots.contains(TAINTED_MATCHER_PHASE2))
			return TAINTED;

		if (annots.contains(CLEAN_MATCHER))
			return CLEAN;

		return BOTTOM;
	}

	@Override
	public TaintDomainForPhase2 evalIdentifier(Identifier id, ValueEnvironment<TaintDomainForPhase2> environment, ProgramPoint pp)
			throws SemanticException {
		TaintDomainForPhase2 def = defaultApprox(id, pp);
		if (def != BOTTOM)
			return def;
		return super.evalIdentifier(id, environment, pp);
	}


	@Override
	public DomainRepresentation representation() {
		return this == BOTTOM ? Lattice.bottomRepresentation()
				: this == CLEAN ? new StringRepresentation("_")
						: this == TAINTED ? new StringRepresentation("#") : Lattice.topRepresentation();
	}

	@Override
	public TaintDomainForPhase2 top() {
		return TAINTED;
	}

	@Override
	public TaintDomainForPhase2 bottom() {
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

	/**
	 * Yields if the state is cleaned.
	 * 
	 * @return {@code true} if is clean, otherwise {@code false}
	 */
	public boolean isClean() {
		return this == CLEAN;
	}

	@Override
	public TaintDomainForPhase2 evalNullConstant(ProgramPoint pp) throws SemanticException {
		return CLEAN;
	}

	@Override
	public TaintDomainForPhase2 evalNonNullConstant(Constant constant, ProgramPoint pp) throws SemanticException {
		if (constant instanceof TaintedP2)
			return TAINTED;
		return CLEAN;
	}

	@Override
	public TaintDomainForPhase2 evalUnaryExpression(UnaryOperator operator, TaintDomainForPhase2 arg, ProgramPoint pp)
			throws SemanticException {
		return arg;
	}

	@Override
	public TaintDomainForPhase2 evalBinaryExpression(BinaryOperator operator, TaintDomainForPhase2 left, TaintDomainForPhase2 right,
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
	public TaintDomainForPhase2 evalTernaryExpression(TernaryOperator operator, TaintDomainForPhase2 left, TaintDomainForPhase2 middle,
			TaintDomainForPhase2 right, ProgramPoint pp) throws SemanticException {
		if (left == TAINTED || right == TAINTED || middle == TAINTED)
			return TAINTED;

		if (left == TOP || right == TOP || middle == TOP)
			return TOP;

		return CLEAN;
	}

	@Override
	public TaintDomainForPhase2 evalPushAny(PushAny pushAny, ProgramPoint pp) throws SemanticException {
		return TAINTED;
	}

	@Override
	public Satisfiability satisfies(ValueExpression expression, ValueEnvironment<TaintDomainForPhase2> environment,
			ProgramPoint pp) throws SemanticException {
		return Satisfiability.UNKNOWN;
	}

	@Override
	public boolean tracksIdentifiers(Identifier id) {
		for (Type t : id.getRuntimeTypes(null))
			if (!(t.isInMemoryType()))
				return true;
		if (id instanceof AllocationSite)
			return true;
		return false;
	}

	@Override
	public TaintDomainForPhase2 evalTypeCast(BinaryExpression cast, TaintDomainForPhase2 left, TaintDomainForPhase2 right, ProgramPoint pp)
			throws SemanticException {
		return left;
	}

	@Override
	public TaintDomainForPhase2 evalTypeConv(BinaryExpression conv, TaintDomainForPhase2 left, TaintDomainForPhase2 right, ProgramPoint pp)
			throws SemanticException {
		return left;
	}

	@Override
	public boolean canProcess(SymbolicExpression expression) {
		return true;
	}

	@Override
	public TaintDomainForPhase2 lubAux(TaintDomainForPhase2 other) throws SemanticException {
		return TOP; // should never happen
	}

	@Override
	public TaintDomainForPhase2 wideningAux(TaintDomainForPhase2 other) throws SemanticException {
		return TOP; // should never happen
	}

	@Override
	public boolean lessOrEqualAux(TaintDomainForPhase2 other) throws SemanticException {
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
		TaintDomainForPhase2 other = (TaintDomainForPhase2) obj;
		if (v != other.v)
			return false;
		return true;
	}
}