package it.unive.golisa.cfg.expression.literal;

import it.unive.golisa.cfg.statement.assignment.GoShortVariableDeclaration.NumericalTyper;
import it.unive.golisa.cfg.type.composite.GoTupleType;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.lattices.ExpressionSet;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.annotations.Annotations;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.NaryExpression;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.AccessChild;
import it.unive.lisa.symbolic.heap.HeapDereference;
import it.unive.lisa.symbolic.heap.HeapReference;
import it.unive.lisa.symbolic.heap.MemoryAllocation;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.type.ReferenceType;
import it.unive.lisa.type.Type;

/**
 * A Go tuple expression (e.g., (5, 3, 7).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoTupleExpression extends NaryExpression {

	private final GoTupleType tupleType;

	/**
	 * Builds a Go tuple expression.
	 * 
	 * @param cfg         the {@link CFG} where this expression lies
	 * @param types       the parameters containing the types of this
	 *                        expressions
	 * @param location    the location where this expression is defined
	 * @param expressions the expressions composing the tuple
	 */
	public GoTupleExpression(CFG cfg, Parameter[] types, CodeLocation location, Expression... expressions) {
		this(cfg, GoTupleType.lookup(types), location, expressions);
	}
	
	@Override
	protected int compareSameClassAndParams(Statement o) {
		return 0; // nothing else to compare
	}

	/**
	 * Builds a Go tuple expression.
	 * 
	 * @param cfg         the {@link CFG} where this expression lies
	 * @param type        the type of this expressions
	 * @param location    the location where this expression is defined
	 * @param expressions the expressions composing the tuple
	 */
	public GoTupleExpression(CFG cfg, GoTupleType type, CodeLocation location, Expression... expressions) {
		super(cfg, location, "(tuple)", expressions);
		this.tupleType = type;
	}

	/**
	 * Yields an abstract state where the tuple expression passed as parameters
	 * has been allocated.
	 * 
	 * @param <A>        abstract state type parameter
	 * @param entryState the entry state
	 * @param anns       the annotations of the tuple expression
	 * @param pp         the program point where the allocation occurs
	 * @param location   the location where the allocation occurs
	 * @param tupleType  the type of the tuple expressions
	 * @param exps       the expressions initializing the tuple
	 * 
	 * @return an abstract state where the tuple expression passed as parameters
	 *             has been allocated
	 * 
	 * @throws SemanticException if an error occurs during the computation
	 */
	public static <A extends AbstractState<A>> AnalysisState<A> allocateTupleExpression(AnalysisState<A> entryState,
			Annotations anns, ProgramPoint pp, CodeLocation location, GoTupleType tupleType, SymbolicExpression... exps)
			throws SemanticException {
		// Allocates the new heap allocation
		MemoryAllocation created = new MemoryAllocation(tupleType, location, anns, true);
		entryState = entryState.smallStepSemantics(created, pp);
		HeapReference ref = new HeapReference(new ReferenceType(tupleType), created,
				location);
		HeapDereference deref = new HeapDereference(tupleType, ref,
				location);

		AnalysisState<A> tmp = entryState;
		for (int i = 0; i < exps.length; i++) {
			AccessChild access = new AccessChild(tupleType.getTypeAt(i), deref,
					new Constant(GoIntType.INSTANCE, i, location), location);
			Type t = tmp.getState().getDynamicTypeOf(exps[i], pp, tmp.getState());
			tmp = tmp.assign(access, NumericalTyper.type(exps[i], t), pp);
		}

		return tmp.smallStepSemantics(ref, pp);
	}

	@Override
	public <A extends AbstractState<A>> AnalysisState<A> forwardSemanticsAux(InterproceduralAnalysis<A> interprocedural,
			AnalysisState<A> state, ExpressionSet[] params, StatementStore<A> expressions) throws SemanticException {

		// Allocates the new heap allocation
		MemoryAllocation created = new MemoryAllocation(tupleType, getLocation(), new Annotations());
		AnalysisState<A> allocState = state.smallStepSemantics(created, this);
		HeapReference ref = new HeapReference(new ReferenceType(getStaticType()), created,
				getLocation());
		HeapDereference deref = new HeapDereference(getStaticType(), ref,
				getLocation());

		AnalysisState<A> tmp = allocState;
		for (int i = 0; i < getSubExpressions().length; i++) {
			AccessChild access = new AccessChild(tupleType.getTypeAt(i), deref,
					new Constant(GoIntType.INSTANCE, i, getLocation()), getLocation());
			for (SymbolicExpression v : params[i]) {
				Type vtype = tmp.getState().getDynamicTypeOf(v, this, tmp.getState());
				tmp = tmp.assign(access, NumericalTyper.type(v, vtype), this);
			}
		}

		return tmp.smallStepSemantics(ref, this);
	}
}
