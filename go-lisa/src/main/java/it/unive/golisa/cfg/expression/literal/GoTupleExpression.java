package it.unive.golisa.cfg.expression.literal;

import java.util.Collections;

import it.unive.golisa.cfg.statement.assignment.GoShortVariableDeclaration.NumericalTyper;
import it.unive.golisa.cfg.type.composite.GoTupleType;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.lattices.ExpressionSet;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.annotations.Annotations;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.NaryExpression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.AccessChild;
import it.unive.lisa.symbolic.heap.HeapDereference;
import it.unive.lisa.symbolic.heap.HeapReference;
import it.unive.lisa.symbolic.heap.MemoryAllocation;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.type.ReferenceType;

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
	 * @param location    the location where this expression is defined
	 * @param expressions the expressions composing the tuple
	 */
	public GoTupleExpression(CFG cfg, CodeLocation location, Expression... expressions) {
		super(cfg, location, "(tuple)", expressions);

		Parameter[] types = new Parameter[expressions.length];
		for (int i = 0; i < types.length; i++) 
			types[i] = new Parameter(expressions[i].getLocation(), "_", expressions[i].getStaticType());

		this.tupleType = GoTupleType.lookup(types);
	}

	@Override
	public <A extends AbstractState<A, H, V, T>,
	H extends HeapDomain<H>,
	V extends ValueDomain<V>,
	T extends TypeDomain<T>> AnalysisState<A, H, V, T> expressionSemantics(
			InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
			ExpressionSet<SymbolicExpression>[] params, StatementStore<A, H, V, T> expressions)
					throws SemanticException {

		// Allocates the new heap allocation
		MemoryAllocation created = new MemoryAllocation(tupleType, getLocation(), new Annotations());
		AnalysisState<A, H, V, T> allocState = state.smallStepSemantics(created, this);
		HeapReference ref = new HeapReference(new ReferenceType(getStaticType()), created,
				getLocation());
		HeapDereference deref = new HeapDereference(getStaticType(), ref,
				getLocation());

		AnalysisState<A, H, V, T> tmp = allocState;
		for (int i = 0; i < getSubExpressions().length; i++) {
			AccessChild access = new AccessChild(tupleType.getTypeAt(i), deref,
					new Constant(GoIntType.INSTANCE, i, getLocation()), getLocation());
			for (SymbolicExpression v : params[i])
				tmp = tmp.assign(access, NumericalTyper.type(v), this);
		}

		return tmp.smallStepSemantics(ref, this);
	}


	@SafeVarargs
	public static <A extends AbstractState<A, H, V, T>,
	H extends HeapDomain<H>,
	V extends ValueDomain<V>,
	T extends TypeDomain<T>> AnalysisState<A, H, V, T> allocateTupleExpression(AnalysisState<A, H, V, T> entryState, Annotations anns, ProgramPoint pp, CodeLocation location, GoTupleType tupleType, SymbolicExpression... exps) throws SemanticException {
		// Allocates the new heap allocation
		MemoryAllocation created = new MemoryAllocation(tupleType, location, anns, true);
		created.setRuntimeTypes(Collections.singleton(tupleType));
		entryState = entryState.smallStepSemantics(created, pp);
		HeapReference ref = new HeapReference(new ReferenceType(tupleType), created,
				location);
		HeapDereference deref = new HeapDereference(tupleType, ref,
				location);

		AnalysisState<A, H, V, T> tmp = entryState;
		for (int i = 0; i < exps.length; i++) {
			AccessChild access = new AccessChild(tupleType.getTypeAt(i), deref,
					new Constant(GoIntType.INSTANCE, i, location), location);
			tmp = tmp.assign(access, NumericalTyper.type(exps[i]), pp);
		}

		return tmp.smallStepSemantics(ref, pp);
	}
}
