package it.unive.golisa.analysis.taint;

import java.util.Set;

import it.unive.golisa.cfg.runtime.conversion.GoConv;
import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.nonrelational.value.BaseNonRelationalValueDomain;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.program.annotations.Annotation;
import it.unive.lisa.program.annotations.Annotations;
import it.unive.lisa.program.annotations.matcher.AnnotationMatcher;
import it.unive.lisa.program.annotations.matcher.BasicAnnotationMatcher;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.symbolic.value.QuaternaryExpression;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.symbolic.value.operator.ternary.TernaryOperator;
import it.unive.lisa.symbolic.value.operator.unary.UnaryOperator;
import it.unive.lisa.type.Type;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

/**
 * The taint domain, used for the taint analysis.
 */
public class TaintDomainForPrivacyHF implements BaseNonRelationalValueDomain<TaintDomainForPrivacyHF> {

	@Override
	public TaintDomainForPrivacyHF evalPushAny(PushAny pushAny, ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
		return CLEAN;
	}

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
	protected static final TaintDomainForPrivacyHF TOP = new TaintDomainForPrivacyHF((byte) 3);

	/**
	 * The top state.
	 */
	protected static final TaintDomainForPrivacyHF TAINTED = new TaintDomainForPrivacyHF((byte) 2);

	/**
	 * The clean state.
	 */
	protected static final TaintDomainForPrivacyHF CLEAN = new TaintDomainForPrivacyHF((byte) 1);

	/**
	 * The bottom state.
	 */
	protected static final TaintDomainForPrivacyHF BOTTOM = new TaintDomainForPrivacyHF((byte) 0);

	private final byte v;

	/**
	 * Builds a new instance of taint, referring to the top element of the
	 * lattice.
	 */
	public TaintDomainForPrivacyHF() {
		this((byte) 3);
	}

	private TaintDomainForPrivacyHF(byte v) {
		this.v = v;
	}
	
	

	@Override
	public TaintDomainForPrivacyHF fixedVariable(Identifier id, ProgramPoint pp, SemanticOracle oracle) throws SemanticException {

	
		Annotations annots = id.getAnnotations();
		if (annots.isEmpty())
			return BaseNonRelationalValueDomain.super.fixedVariable(id, pp, oracle);

		if (annots.contains(TAINTED_MATCHER))
			return TAINTED;

		if (annots.contains(CLEAN_MATCHER))
			return CLEAN;

		return BaseNonRelationalValueDomain.super.fixedVariable(id, pp, oracle);
	}


	@Override
	public StructuredRepresentation representation() {
		return this == BOTTOM ? Lattice.bottomRepresentation()
				: this == CLEAN ? new StringRepresentation("_")
						: this == TAINTED ? new StringRepresentation("#") : Lattice.topRepresentation();
	}
	
	@Override
	public String toString() {
		return representation().toString();
	}

	@Override
	public TaintDomainForPrivacyHF top() {
		return TOP;
	}

	@Override
	public TaintDomainForPrivacyHF bottom() {
		return BOTTOM;
	}

	/**
	 * Yields if the state is tainted.
	 * 
	 * @return {@code true} if is tainted, otherwise {@code false}
	 */
	public boolean isTainted() {
		return this == TAINTED || this == TOP;
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
	public TaintDomainForPrivacyHF evalNullConstant(ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
		return CLEAN;
	}

	@Override
	public TaintDomainForPrivacyHF evalNonNullConstant(Constant constant, ProgramPoint pp, SemanticOracle oracle)
			throws SemanticException {
		return CLEAN;
	}

	@Override
	public TaintDomainForPrivacyHF evalUnaryExpression(UnaryOperator operator, TaintDomainForPrivacyHF arg, ProgramPoint pp,
			SemanticOracle oracle)
			throws SemanticException {
		return arg;
	}

	@Override
	public TaintDomainForPrivacyHF evalBinaryExpression(BinaryOperator operator, TaintDomainForPrivacyHF left, TaintDomainForPrivacyHF right,
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
	public TaintDomainForPrivacyHF evalTernaryExpression(TernaryOperator operator, TaintDomainForPrivacyHF left, TaintDomainForPrivacyHF middle,
			TaintDomainForPrivacyHF right, ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
		if (left == TAINTED || right == TAINTED || middle == TAINTED)
			return TAINTED;

		if (left == TOP || right == TOP || middle == TOP)
			return TOP;

		return CLEAN;
	}



	@Override
	public TaintDomainForPrivacyHF eval(ValueExpression expression,
			ValueEnvironment<TaintDomainForPrivacyHF> environment, ProgramPoint pp, SemanticOracle oracle)
			throws SemanticException {
		if (expression instanceof Identifier) {
			TaintDomainForPrivacyHF fixed = fixedVariable((Identifier) expression, pp, oracle);
			if (!fixed.isBottom())
				return fixed;
		}
		
		if(expression instanceof Tainted)
			return TAINTED;
		if(expression instanceof QuaternaryExpression) {
			QuaternaryExpression quat = (QuaternaryExpression) expression;
			SymbolicExpression e2 = quat.getSymbolicExpr2();
			SymbolicExpression e3 = quat.getSymbolicExpr3();
			SymbolicExpression e4 = quat.getSymbolicExpr4();
			TaintDomainForPrivacyHF ev2 = BaseNonRelationalValueDomain.super.eval((ValueExpression) e2,environment, pp, oracle);
			TaintDomainForPrivacyHF ev3 = BaseNonRelationalValueDomain.super.eval((ValueExpression) e3,environment, pp, oracle);
			TaintDomainForPrivacyHF ev4 = BaseNonRelationalValueDomain.super.eval((ValueExpression) e4,environment, pp, oracle);
			
			if(ev2.isTainted() || ev3.isTainted() || ev4.isTainted())
				return TAINTED;
			else
				return CLEAN;
		}
			

		return BaseNonRelationalValueDomain.super.eval(expression, environment, pp, oracle);

	}
	
	

	@Override
	public TaintDomainForPrivacyHF evalIdentifier(Identifier id, ValueEnvironment<TaintDomainForPrivacyHF> environment,
			ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
		return BaseNonRelationalValueDomain.super.evalIdentifier(id, environment, pp, oracle);
	}

	@Override
	public boolean canProcess(SymbolicExpression expression, ProgramPoint pp, SemanticOracle oracle) {
		Set<Type> types;
		try {
			types = oracle.getRuntimeTypesOf(expression, pp, oracle);
		} catch (SemanticException e) {
			return false;
		}

//		if (!types.isEmpty())
//			return types.stream().anyMatch(t -> !t.isPointerType() && !t.isInMemoryType());
//		return !expression.getStaticType().isPointerType() && !expression.getStaticType().isInMemoryType();
		
		return true;
	}

	@Override
	public TaintDomainForPrivacyHF lubAux(TaintDomainForPrivacyHF other) throws SemanticException {
		return TOP; // should never happen
	}

	@Override
	public TaintDomainForPrivacyHF wideningAux(TaintDomainForPrivacyHF other) throws SemanticException {
		return TOP; // should never happen
	}

	@Override
	public boolean lessOrEqualAux(TaintDomainForPrivacyHF other) throws SemanticException {
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
		TaintDomainForPrivacyHF other = (TaintDomainForPrivacyHF) obj;
		if (v != other.v)
			return false;
		return true;
	}
	
//	@Override
//	public TaintDomainForPrivacyHF unknownVariable(
//			Identifier id) {
//		return CLEAN;
//	}
}