package it.unive.golisa.analysis.ni;

import java.util.IdentityHashMap;
import java.util.Map;

import it.unive.golisa.analysis.taint.Tainted;
import it.unive.golisa.cfg.expression.unary.GoRange;
import it.unive.golisa.cfg.expression.unary.GoRangeGetNextIndex;
import it.unive.golisa.cfg.expression.unary.GoRangeGetNextValue;
import it.unive.golisa.cfg.type.composite.GoMapType;
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
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.VariableRef;
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
import it.unive.lisa.type.Untyped;

/**
 * The type-system based implementation of the non interference analysis.
 * 
 * @author <a href="mailto:luca.negrini@unive.it">Luca Negrini</a>
 * 
 * @see <a href=
 *          "https://en.wikipedia.org/wiki/Non-interference_(security)">Non-interference</a>
 */
public class IntegrityNIDomain implements BaseInferredValue<IntegrityNIDomain> {

	/**
	 * The annotation Low.
	 */
	public static final Annotation LOW_ANNOTATION = new Annotation("lisa.intni.Low");

	/**
	 * The matcher of the Low annotation.
	 */
	private static final AnnotationMatcher LOW_MATCHER = new BasicAnnotationMatcher(LOW_ANNOTATION);

	/**
	 * The annotation High.
	 */
	public static final Annotation HIGH_ANNOTATION = new Annotation("lisa.intni.High");

	/**
	 * The matcher of the High annotation.
	 */
	private static final AnnotationMatcher HIGH_MATCHER = new BasicAnnotationMatcher(HIGH_ANNOTATION);

	/**
	 * The top state.
	 */
	private static final IntegrityNIDomain TOP = new IntegrityNIDomain((byte) 3);

	/**
	 * The low state.
	 */
	private static final IntegrityNIDomain LOW = new IntegrityNIDomain((byte) 2);

	/**
	 * The high state.
	 */
	private static final IntegrityNIDomain HIGH = new IntegrityNIDomain((byte) 1);

	/**
	 * The bottom state.
	 */
	private static final IntegrityNIDomain BOTTOM = new IntegrityNIDomain((byte) 0);

	private final byte v;

	/**
	 * The guards of statements.
	 */
	private final Map<ProgramPoint, IntegrityNIDomain> guards;

	/**
	 * Builds a new instance of non interference, referring to the top element
	 * of the lattice.
	 */
	public IntegrityNIDomain() {
		this((byte) 3);
	}

	/**
	 * Builds a new instance of non interference, referring to a specific
	 * element of the lattice.
	 * 
	 * @param v the {@code byte} representing the element
	 */
	private IntegrityNIDomain(byte v) {
		this.v = v;
		this.guards = new IdentityHashMap<>();
	}

	@Override
	public IntegrityNIDomain variable(Identifier id, ProgramPoint pp) throws SemanticException {

		boolean isAssignedFromMapIteration = pp.getCFG().getControlFlowStructures().stream().anyMatch(g -> {

			Statement condition = g.getCondition();
			if (condition instanceof GoRange && isMapRange((GoRange) condition)
					&& matchMapRangeIds((GoRange) condition, id))
				return true;
			return false;
		});

		if (isAssignedFromMapIteration)
			return LOW;

		Annotations annots = id.getAnnotations();
		if (annots.isEmpty())
			return BaseInferredValue.super.variable(id, pp);

		if (annots.contains(LOW_MATCHER))
			return LOW;

		if (annots.contains(HIGH_MATCHER))
			return HIGH;

		return BaseInferredValue.super.variable(id, pp);
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
					if (st.getEvaluationPredecessor() instanceof GoRangeGetNextIndex
							|| st.getEvaluationPredecessor() instanceof GoRangeGetNextValue) {
						for (Type t : id.getRuntimeTypes(null))
							if (t instanceof GoMapType)
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
	public DomainRepresentation representation() {
		return this == BOTTOM ? Lattice.bottomRepresentation()
				: this == HIGH ? new StringRepresentation("H")
						: this == LOW ? new StringRepresentation("L") : Lattice.topRepresentation();
	}

	@Override
	public IntegrityNIDomain top() {
		return LOW;
	}

	@Override
	public IntegrityNIDomain bottom() {
		return BOTTOM;
	}

	/**
	 * Yields true if the state is low.
	 * 
	 * @return {@code true} if the the state is low, otherwise {@code false}
	 */
	public boolean isLowIntegrity() {
		return this == LOW;
	}

	/**
	 * Yields true if the state is high.
	 * 
	 * @return {@code true} if the the state is high, otherwise {@code false}
	 */
	public boolean isHighIntegrity() {
		return this == HIGH;
	}

	@Override
	public InferredPair<IntegrityNIDomain> evalNullConstant(IntegrityNIDomain state, ProgramPoint pp)
			throws SemanticException {
		return new InferredPair<>(this, HIGH, state(state, pp));
	}

	@Override
	public InferredPair<IntegrityNIDomain> evalNonNullConstant(Constant constant, IntegrityNIDomain state,
			ProgramPoint pp) throws SemanticException {
		if (constant instanceof Tainted)
			return new InferredPair<>(this, LOW, state(state, pp));
		return new InferredPair<>(this, HIGH, state(state, pp));
	}

	@Override
	public InferredPair<IntegrityNIDomain> evalUnaryExpression(UnaryOperator operator, IntegrityNIDomain arg,
			IntegrityNIDomain state, ProgramPoint pp) throws SemanticException {
		return new InferredPair<>(this, arg, state(state, pp));
	}

	@Override
	public InferredPair<IntegrityNIDomain> evalBinaryExpression(BinaryOperator operator,
			IntegrityNIDomain left,
			IntegrityNIDomain right, IntegrityNIDomain state, ProgramPoint pp) throws SemanticException {
		if (left == LOW || right == LOW)
			return new InferredPair<>(this, LOW, state(state, pp));

		if (left == TOP || right == TOP)
			return new InferredPair<>(this, TOP, state(state, pp));

		return new InferredPair<>(this, HIGH, state(state, pp));
	}

	@Override
	public InferredPair<IntegrityNIDomain> evalTernaryExpression(TernaryOperator operator,
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
	public InferredPair<IntegrityNIDomain> evalIdentifier(Identifier id,
			InferenceSystem<IntegrityNIDomain> environment, ProgramPoint pp) throws SemanticException {
		IntegrityNIDomain variable = variable(id, pp);
		if (!variable.isBottom())
			return new InferredPair<>(this, variable, state(environment.getExecutionState(), pp));
		else
			return new InferredPair<>(this, environment.getState(id), state(environment.getExecutionState(), pp));
	}

	@Override
	public InferredPair<IntegrityNIDomain> evalPushAny(PushAny pushAny, IntegrityNIDomain state, ProgramPoint pp)
			throws SemanticException {
		return new InferredPair<>(this, LOW, state(state, pp));
	}

	@Override
	public InferredPair<IntegrityNIDomain> evalTypeConv(BinaryExpression conv, IntegrityNIDomain left,
			IntegrityNIDomain right, IntegrityNIDomain state, ProgramPoint pp) throws SemanticException {
		if (left == LOW || right == LOW)
			return new InferredPair<>(this, LOW, state(state, pp));

		if (left == TOP || right == TOP)
			return new InferredPair<>(this, TOP, state(state, pp));

		return new InferredPair<>(this, HIGH, state(state, pp));
	}

	@Override
	public InferredPair<IntegrityNIDomain> evalTypeCast(BinaryExpression cast, IntegrityNIDomain left,
			IntegrityNIDomain right, IntegrityNIDomain state, ProgramPoint pp) throws SemanticException {
		if (left == LOW || right == LOW)
			return new InferredPair<>(this, LOW, state(state, pp));

		if (left == TOP || right == TOP)
			return new InferredPair<>(this, TOP, state(state, pp));

		return new InferredPair<>(this, HIGH, state(state, pp));
	}

	@Override
	public boolean tracksIdentifiers(Identifier id) {
		for (Type t : id.getRuntimeTypes(null))
			if (!(t.isInMemoryType()))
				return true;
		return false;
	}

	@Override
	public boolean canProcess(SymbolicExpression expression) {
		return true;
	}

	@Override
	public IntegrityNIDomain lubAux(IntegrityNIDomain other) throws SemanticException {
		return TOP; // should never happen
	}

	@Override
	public IntegrityNIDomain wideningAux(IntegrityNIDomain other) throws SemanticException {
		return TOP; // should never happen
	}

	@Override
	public boolean lessOrEqualAux(IntegrityNIDomain other) throws SemanticException {
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

	/**
	 * Yields the computed state.
	 * 
	 * @param state the state used to compute the returned state
	 * @param pp    the program point
	 * 
	 * @return the returned state after the computation
	 * 
	 * @throws SemanticException the exception triggered by lub operator
	 */
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
			ValueExpression expression, ProgramPoint src, ProgramPoint dest) throws SemanticException {
		InferredPair<IntegrityNIDomain> eval = eval(expression, environment, src);
		IntegrityNIDomain inf = eval.getInferred();
		eval.getState().guards.forEach(inf.guards::put);
		inf.guards.put(src, inf);
		return new InferenceSystem<>(environment, inf);
	}
}