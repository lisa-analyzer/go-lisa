package it.unive.golisa.cfg.expression;

import java.util.Set;

import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.BinaryExpression;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.AccessChild;
import it.unive.lisa.symbolic.heap.HeapDereference;
import it.unive.lisa.type.PointerType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;

/**
 * A Go access expression (e.g., x.y).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoCollectionAccess extends BinaryExpression {

	/**
	 * Builds the access expression.
	 * 
	 * @param cfg       the {@link CFG} where this expression lies
	 * @param location  the location where this expression is defined
	 * @param container the left-hand side of this expression
	 * @param child     the right-hand side of this expression
	 */
	public GoCollectionAccess(CFG cfg, SourceCodeLocation location, Expression container, Expression child) {
		super(cfg, location, container + "::" + child, container, child);
	}

	/**
	 * Yields the recevier of this access expression.
	 * 
	 * @return the recevier of this access expression.
	 */
	public Expression getReceiver() {
		return getLeft();
	}

	/**
	 * Yields the target of this access expression.
	 * 
	 * @return the target of this access expression.
	 */
	public Expression getTarget() {
		return getRight();
	}

	@Override
	public <A extends AbstractState<A>> AnalysisState<A> fwdBinarySemantics(InterproceduralAnalysis<A> interprocedural,
			AnalysisState<A> state, SymbolicExpression left, SymbolicExpression right, StatementStore<A> expressions)
			throws SemanticException {
		AnalysisState<A> result = state.bottom();
		Set<Type> ltypes = state.getState().getRuntimeTypesOf(left, this, state.getState());
		for (Type type : ltypes) {
//			if (type.isArrayType() || type instanceof GoSliceType) {
//				// FIXME we get here when left is a parameter of an entrypoint,
//				// and nothing is defined in the heap for its elements..
//				Type inner;
//				if (type.isArrayType())
//					inner = type.asArrayType().getInnerType();
//				else
//					inner = ((GoSliceType) type).getContentType();
//				return state.smallStepSemantics(new PushAny(inner, getLocation()), this);
//			} else {

			if (type.isPointerType()) {
				PointerType pointer = type.asPointerType();
				Type inner = pointer.getInnerType();
				if (inner.isArrayType() || inner instanceof GoSliceType) {
					HeapDereference container = new HeapDereference(inner, left, getLocation());
					Type elemType;
					if (type.asPointerType().getInnerType().isArrayType())
						elemType = type.asPointerType().getInnerType().asArrayType().getInnerType();
					else
						elemType = ((GoSliceType) type.asPointerType().getInnerType()).getContentType();

					AccessChild access = new AccessChild(elemType, container, right, getLocation());
					result = result.lub(state.smallStepSemantics(access,
							this));
				} else
					result = result.lub(state.smallStepSemantics(
							new AccessChild(Untyped.INSTANCE,
									new HeapDereference(getStaticType(), left, getLocation()), right, getLocation()),
							this));

			}
		}

//		}

		return result;
	}
}
