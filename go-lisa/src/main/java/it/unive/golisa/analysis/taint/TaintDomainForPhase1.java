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
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.program.cfg.statement.call.NativeCall;
import it.unive.lisa.program.cfg.statement.call.UnresolvedCall;
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
 * The taint domain, used for the taint analysis.
 */
public class TaintDomainForPhase1 extends BaseNonRelationalValueDomain<TaintDomainForPhase1> {

	/**
	 * The annotation Tainted.
	 */
	public static final Annotation TAINTED_ANNOTATION_PHASE1 = new Annotation("lisa.taint.TaintedPhase1");

	/**
	 * The matcher for the Tainted annotation.
	 */
	private static final AnnotationMatcher TAINTED_MATCHER_PHASE1 = new BasicAnnotationMatcher(TAINTED_ANNOTATION_PHASE1);

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
	private static final TaintDomainForPhase1 TOP = new TaintDomainForPhase1((byte) 3);

	/**
	 * The top state.
	 */
	private static final TaintDomainForPhase1 TAINTED = new TaintDomainForPhase1((byte) 2);

	/**
	 * The clean state.
	 */
	private static final TaintDomainForPhase1 CLEAN = new TaintDomainForPhase1((byte) 1);

	/**
	 * The bottom state.
	 */
	private static final TaintDomainForPhase1 BOTTOM = new TaintDomainForPhase1((byte) 0);

	private final byte v;

	/**
	 * Builds a new instance of taint, referring to the top element of the
	 * lattice.
	 */
	public TaintDomainForPhase1() {
		this((byte) 3);
	}

	private TaintDomainForPhase1(byte v) {
		this.v = v;
	}

	@Override
	public TaintDomainForPhase1 variable(Identifier id, ProgramPoint pp) throws SemanticException {
		TaintDomainForPhase1 def = defaultApprox(id, pp);
		if (def != BOTTOM)
			return def;
		return super.variable(id, pp);
	}

	private TaintDomainForPhase1 defaultApprox(Identifier id, ProgramPoint pp) throws SemanticException {
		
		Annotations annots = id.getAnnotations();

		if (annots.isEmpty())
			return super.variable(id, pp);

		if (annots.contains(TAINTED_MATCHER_PHASE1))
			return TAINTED;

		if (annots.contains(CLEAN_MATCHER))
			return CLEAN;

		return BOTTOM;
	}

	@Override
	public TaintDomainForPhase1 evalIdentifier(Identifier id, ValueEnvironment<TaintDomainForPhase1> environment, ProgramPoint pp)
			throws SemanticException {
		TaintDomainForPhase1 def = defaultApprox(id, pp);
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
	public TaintDomainForPhase1 top() {
		return TOP;
	}

	@Override
	public TaintDomainForPhase1 bottom() {
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
	public TaintDomainForPhase1 evalNullConstant(ProgramPoint pp) throws SemanticException {
		return CLEAN;
	}

	@Override
	public TaintDomainForPhase1 evalNonNullConstant(Constant constant, ProgramPoint pp) throws SemanticException {
		if (constant instanceof TaintedP1)
			return TAINTED;
		return CLEAN;
	}

	@Override
	public TaintDomainForPhase1 evalUnaryExpression(UnaryOperator operator, TaintDomainForPhase1 arg, ProgramPoint pp)
			throws SemanticException {
		return arg;
	}

	@Override
	public TaintDomainForPhase1 evalBinaryExpression(BinaryOperator operator, TaintDomainForPhase1 left, TaintDomainForPhase1 right,
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
	public TaintDomainForPhase1 evalTernaryExpression(TernaryOperator operator, TaintDomainForPhase1 left, TaintDomainForPhase1 middle,
			TaintDomainForPhase1 right, ProgramPoint pp) throws SemanticException {
		if (left == TAINTED || right == TAINTED || middle == TAINTED)
			return TAINTED;

		if (left == TOP || right == TOP || middle == TOP)
			return TOP;

		return CLEAN;
	}

	@Override
	public TaintDomainForPhase1 evalPushAny(PushAny pushAny, ProgramPoint pp) throws SemanticException {
		return TAINTED;
	}

	@Override
	public Satisfiability satisfies(ValueExpression expression, ValueEnvironment<TaintDomainForPhase1> environment,
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
	public TaintDomainForPhase1 evalTypeCast(BinaryExpression cast, TaintDomainForPhase1 left, TaintDomainForPhase1 right, ProgramPoint pp)
			throws SemanticException {
		return left;
	}

	@Override
	public TaintDomainForPhase1 evalTypeConv(BinaryExpression conv, TaintDomainForPhase1 left, TaintDomainForPhase1 right, ProgramPoint pp)
			throws SemanticException {
		return left;
	}

	@Override
	public boolean canProcess(SymbolicExpression expression) {
		return true;
	}

	@Override
	public TaintDomainForPhase1 lubAux(TaintDomainForPhase1 other) throws SemanticException {
		return TOP; // should never happen
	}

	@Override
	public TaintDomainForPhase1 wideningAux(TaintDomainForPhase1 other) throws SemanticException {
		return TOP; // should never happen
	}

	@Override
	public boolean lessOrEqualAux(TaintDomainForPhase1 other) throws SemanticException {
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
		TaintDomainForPhase1 other = (TaintDomainForPhase1) obj;
		if (v != other.v)
			return false;
		return true;
	}
}