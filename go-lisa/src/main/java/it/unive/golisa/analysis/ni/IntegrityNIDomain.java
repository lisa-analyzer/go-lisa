package it.unive.golisa.analysis.ni;

import java.util.IdentityHashMap;
import java.util.Map;

import it.unive.golisa.analysis.taint.Tainted;
import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.nonrelational.inference.BaseInferredValue;
import it.unive.lisa.analysis.nonrelational.inference.InferenceSystem;
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
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.symbolic.value.operator.ternary.TernaryOperator;
import it.unive.lisa.symbolic.value.operator.unary.UnaryOperator;
import it.unive.lisa.type.Type;

public class IntegrityNIDomain extends BaseInferredValue<IntegrityNIDomain> {

	public static final Annotation LOW_ANNOTATION = new Annotation("lisa.intni.Low");

	private static final AnnotationMatcher LOW_MATCHER = new BasicAnnotationMatcher(LOW_ANNOTATION);

	public static final Annotation HIGH_ANNOTATION = new Annotation("lisa.intni.High");

	private static final AnnotationMatcher HIGH_MATCHER = new BasicAnnotationMatcher(HIGH_ANNOTATION);

	private static final IntegrityNIDomain TOP = new IntegrityNIDomain((byte) 3);

	private static final IntegrityNIDomain LOW = new IntegrityNIDomain((byte) 2);

	private static final IntegrityNIDomain HIGH = new IntegrityNIDomain((byte) 1);

	private static final IntegrityNIDomain BOTTOM = new IntegrityNIDomain((byte) 0);

	private final byte v;

	private final Map<ProgramPoint, IntegrityNIDomain> guards;

	public IntegrityNIDomain() {
		this((byte) 3);
	}

	private IntegrityNIDomain(byte v) {
		this.v = v;
		this.guards = new IdentityHashMap<>();
	}

	@Override
	public IntegrityNIDomain variable(Identifier id, ProgramPoint pp) throws SemanticException {
		Annotations annots = id.getAnnotations();
		if (annots.isEmpty())
			return super.variable(id, pp);

		if (annots.contains(LOW_MATCHER))
			return LOW;

		if (annots.contains(HIGH_MATCHER))
			return HIGH;

		return super.variable(id, pp);
	}

	@Override
	public DomainRepresentation representation() {
		return this == BOTTOM ? Lattice.BOTTOM_REPR
				: this == HIGH ? new StringRepresentation("H")
						: this == LOW ? new StringRepresentation("L") : Lattice.TOP_REPR;
	}

	@Override
	public IntegrityNIDomain top() {
		return LOW;
	}

	@Override
	public IntegrityNIDomain bottom() {
		return BOTTOM;
	}

	public boolean isLowIntegrity() {
		return this == LOW;
	}

	@Override
	protected InferredPair<IntegrityNIDomain> evalNullConstant(IntegrityNIDomain state, ProgramPoint pp)
			throws SemanticException {
		return new InferredPair<>(this, HIGH, state(state, pp));
	}

	@Override
	protected InferredPair<IntegrityNIDomain> evalNonNullConstant(Constant constant, IntegrityNIDomain state,
			ProgramPoint pp) throws SemanticException {
		if (constant instanceof Tainted)
			return new InferredPair<>(this, LOW, state(state, pp));
		return new InferredPair<>(this, HIGH, state(state, pp));
	}

	@Override
	protected InferredPair<IntegrityNIDomain> evalUnaryExpression(UnaryOperator operator, IntegrityNIDomain arg,
			IntegrityNIDomain state, ProgramPoint pp) throws SemanticException {
		return new InferredPair<>(this, arg, state(state, pp));
	}

	@Override
	protected InferredPair<IntegrityNIDomain> evalBinaryExpression(BinaryOperator operator,
			IntegrityNIDomain left,
			IntegrityNIDomain right, IntegrityNIDomain state, ProgramPoint pp) throws SemanticException {
		if (left == LOW || right == LOW)
			return new InferredPair<>(this, LOW, state(state, pp));

		if (left == TOP || right == TOP)
			return new InferredPair<>(this, TOP, state(state, pp));

		return new InferredPair<>(this, HIGH, state(state, pp));
	}

	@Override
	protected InferredPair<IntegrityNIDomain> evalTernaryExpression(TernaryOperator operator,
			IntegrityNIDomain left,
			IntegrityNIDomain middle, IntegrityNIDomain right, IntegrityNIDomain state, ProgramPoint pp)
			throws SemanticException {
		if (left == LOW || right == LOW || middle == LOW)
			return new InferredPair<>(this, LOW, state(state, pp));

		if (left == TOP || right == TOP || middle == TOP)
			return new InferredPair<>(this, TOP, state(state, pp));

		return new InferredPair<>(this, HIGH, state(state, pp));
	}

	@Override
	protected InferredPair<IntegrityNIDomain> evalIdentifier(Identifier id,
			InferenceSystem<IntegrityNIDomain> environment, ProgramPoint pp) throws SemanticException {
		IntegrityNIDomain variable = variable(id, null);
		if (!variable.isBottom())
			return new InferredPair<>(this, variable, state(environment.getExecutionState(), pp));
		else
			return new InferredPair<>(this, environment.getState(id), state(environment.getExecutionState(), pp));
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
	protected IntegrityNIDomain lubAux(IntegrityNIDomain other) throws SemanticException {
		return TOP; // should never happen
	}

	@Override
	protected IntegrityNIDomain wideningAux(IntegrityNIDomain other) throws SemanticException {
		return TOP; // should never happen
	}

	@Override
	protected boolean lessOrEqualAux(IntegrityNIDomain other) throws SemanticException {
		return false; // should never happen
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((guards == null) ? 0 : guards.hashCode());
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
		IntegrityNIDomain other = (IntegrityNIDomain) obj;
		if (guards == null) {
			if (other.guards != null)
				return false;
		} else if (!guards.equals(other.guards))
			return false;
		if (v != other.v)
			return false;
		return true;
	}

	private IntegrityNIDomain state(IntegrityNIDomain state, ProgramPoint pp) throws SemanticException {
		Map<ProgramPoint, IntegrityNIDomain> guards = new IdentityHashMap<>();
		for (ProgramPoint guard : pp.getCFG().getGuards(pp))
			guards.put(guard, state.guards.getOrDefault(guard, bottom()));
		IntegrityNIDomain res = bottom();
		for (IntegrityNIDomain guard : guards.values())
			res = res.lub(guard);

		// we have to create a new one here, otherwise we would end up
		// adding those entries to one of the
		guards.forEach(res.guards::put);
		return res;
	}

	@Override
	public InferenceSystem<IntegrityNIDomain> assume(InferenceSystem<IntegrityNIDomain> environment,
			ValueExpression expression, ProgramPoint pp) throws SemanticException {
		InferredPair<IntegrityNIDomain> eval = eval(expression, environment, pp);
		IntegrityNIDomain inf = eval.getInferred();
		eval.getState().guards.forEach(inf.guards::put);
		inf.guards.put(pp, inf);
		return new InferenceSystem<>(environment, inf);
	}
}
