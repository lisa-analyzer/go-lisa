package it.unive.golisa.analysis.taint;

import it.unive.golisa.cfg.expression.unary.GoRange;
import it.unive.golisa.cfg.expression.unary.GoRangeGetNextIndex;
import it.unive.golisa.cfg.expression.unary.GoRangeGetNextValue;
import it.unive.golisa.cfg.runtime.conversion.GoConv;
import it.unive.golisa.cfg.type.composite.GoMapType;
import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.nonrelational.value.BaseNonRelationalValueDomain;
import it.unive.lisa.program.annotations.Annotation;
import it.unive.lisa.program.annotations.Annotations;
import it.unive.lisa.program.annotations.matcher.AnnotationMatcher;
import it.unive.lisa.program.annotations.matcher.BasicAnnotationMatcher;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.symbolic.value.operator.ternary.TernaryOperator;
import it.unive.lisa.symbolic.value.operator.unary.UnaryOperator;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;
import java.util.Set;

/**
 * The taint domain, used for the taint analysis.
 */
public class TaintDomain implements BaseNonRelationalValueDomain<TaintDomain> {

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
	private static final TaintDomain TOP = new TaintDomain((byte) 3);

	/**
	 * The top state.
	 */
	private static final TaintDomain TAINTED = new TaintDomain((byte) 2);

	/**
	 * The clean state.
	 */
	private static final TaintDomain CLEAN = new TaintDomain((byte) 1);

	/**
	 * The bottom state.
	 */
	private static final TaintDomain BOTTOM = new TaintDomain((byte) 0);

	private final byte v;

	/**
	 * Builds a new instance of taint, referring to the top element of the
	 * lattice.
	 */
	public TaintDomain() {
		this((byte) 3);
	}

	private TaintDomain(byte v) {
		this.v = v;
	}

	@Override
	public TaintDomain fixedVariable(Identifier id, ProgramPoint pp, SemanticOracle oracle) throws SemanticException {

		boolean isAssignedFromMapIteration = pp.getCFG().getControlFlowStructures().stream().anyMatch(g -> {

			Statement condition = g.getCondition();
			if (condition instanceof GoRange && isMapRange((GoRange) condition)
					&& matchMapRangeIds((GoRange) condition, id))
				return true;
			return false;
		});

		if (isAssignedFromMapIteration)
			return TAINTED;

		Annotations annots = id.getAnnotations();
		if (annots.isEmpty())
			return BaseNonRelationalValueDomain.super.fixedVariable(id, pp, oracle);

		if (annots.contains(TAINTED_MATCHER))
			return TAINTED;

		if (annots.contains(CLEAN_MATCHER))
			return CLEAN;

		return BaseNonRelationalValueDomain.super.fixedVariable(id, pp, oracle);
	}

	private boolean matchMapRangeIds(GoRange range, Identifier id) {
		return matchMapRangeId(range.getIdxRange(), id) || matchMapRangeId(range.getValRange(), id);
	}

	private boolean matchMapRangeId(Statement st, Identifier id) {

		if (st instanceof VariableRef) {
			VariableRef vRef = (VariableRef) st;
			if (vRef.getVariable().equals(id)) {
				Statement pred = st.getEvaluationPredecessor();
				if (pred != null) {
					if (pred instanceof GoRangeGetNextIndex
							|| pred instanceof GoRangeGetNextValue) {
						return true;
					}
				}
			}
		}

		return false;
	}

	private boolean isMapRange(GoRange range) {

		if (range.getCollectionTypes() == null) {
			// range not evaluated yet
			return false;
		}

		return range.getCollectionTypes().stream()
				.anyMatch(type -> type instanceof GoMapType || type == Untyped.INSTANCE);
	}

	@Override
	public StructuredRepresentation representation() {
		return this == BOTTOM ? Lattice.bottomRepresentation()
				: this == CLEAN ? new StringRepresentation("_")
						: this == TAINTED ? new StringRepresentation("#") : Lattice.topRepresentation();
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
	public TaintDomain evalNullConstant(ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
		return CLEAN;
	}

	@Override
	public TaintDomain evalNonNullConstant(Constant constant, ProgramPoint pp, SemanticOracle oracle)
			throws SemanticException {
		return CLEAN;
	}

	@Override
	public TaintDomain evalUnaryExpression(UnaryOperator operator, TaintDomain arg, ProgramPoint pp,
			SemanticOracle oracle)
			throws SemanticException {
		return arg;
	}

	@Override
	public TaintDomain evalBinaryExpression(BinaryOperator operator, TaintDomain left, TaintDomain right,
			ProgramPoint pp, SemanticOracle oracle) throws SemanticException {

		if (operator == GoConv.INSTANCE)
			return left;

		if (left == TAINTED || right == TAINTED)
			return TAINTED;

		if (left == TOP || right == TOP)
			return TOP;

		return CLEAN;
	}

	@Override
	public TaintDomain evalTernaryExpression(TernaryOperator operator, TaintDomain left, TaintDomain middle,
			TaintDomain right, ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
		if (left == TAINTED || right == TAINTED || middle == TAINTED)
			return TAINTED;

		if (left == TOP || right == TOP || middle == TOP)
			return TOP;

		return CLEAN;
	}

	@Override
	public TaintDomain evalPushAny(PushAny pushAny, ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
		return TAINTED;
	}

	@Override
	public boolean canProcess(SymbolicExpression expression, ProgramPoint pp, SemanticOracle oracle) {
		Set<Type> types;
		try {
			types = oracle.getRuntimeTypesOf(expression, pp, oracle);
		} catch (SemanticException e) {
			return false;
		}

		if (!types.isEmpty())
			return types.stream().anyMatch(t -> !t.isPointerType() && !t.isInMemoryType());
		return !expression.getStaticType().isPointerType() && !expression.getStaticType().isInMemoryType();
	}

	@Override
	public TaintDomain lubAux(TaintDomain other) throws SemanticException {
		return TOP; // should never happen
	}

	@Override
	public TaintDomain wideningAux(TaintDomain other) throws SemanticException {
		return TOP; // should never happen
	}

	@Override
	public boolean lessOrEqualAux(TaintDomain other) throws SemanticException {
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