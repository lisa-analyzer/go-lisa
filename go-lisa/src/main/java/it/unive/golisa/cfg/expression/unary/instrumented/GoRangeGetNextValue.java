package it.unive.golisa.cfg.expression.unary.instrumented;

import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoArrayType;
import it.unive.golisa.cfg.type.composite.GoMapType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt8Type;
import it.unive.lisa.analysis.AbstractDomain;
import it.unive.lisa.analysis.AbstractLattice;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.lattices.ExpressionSet;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.NaryExpression;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.type.ReferenceType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;

/**
 * Instrumented class.
 * 
 * @author OlivieriL
 */
public class GoRangeGetNextValue extends NaryExpression {

	/**
	 * Builds the next value expression.
	 * 
	 * @param cfg       the {@link CFG} where this expression lies
	 * @param location  the location where this expression is defined
	 * @param rangeItem the subexpression
	 */
	public GoRangeGetNextValue(CFG cfg, CodeLocation location, Expression rangeItem) {
		super(cfg, location, "GoRangeGetNextValue", computeType(rangeItem.getStaticType()));
	}

	@Override
	protected int compareSameClassAndParams(Statement o) {
		return 0; // nothing else to compare
	}

	private static Type computeType(Type type) {
		if (!type.equals(Untyped.INSTANCE)) {
			if (type instanceof GoStringType)
				return GoUInt8Type.INSTANCE;
			else if (type instanceof GoArrayType)
				return ((GoArrayType) type).getContenType();
			else if (type instanceof GoSliceType)
				return ((GoSliceType) type).getContentType();
			else if (type instanceof GoMapType)
				return ((GoMapType) type).getElementType();
			else if (type instanceof ReferenceType)
				return computeType(((ReferenceType) type).getInnerType());
		}
		return Untyped.INSTANCE;
	}

	@Override
	public <A extends AbstractLattice<A>, D extends AbstractDomain<A>> AnalysisState<A> forwardSemanticsAux(
			InterproceduralAnalysis<A, D> interprocedural, AnalysisState<A> state, ExpressionSet[] params,
			StatementStore<A> expressions) throws SemanticException {
		if (state.getExecutionExpressions().size() == 1) {
			for (SymbolicExpression e : state.getExecutionExpressions()) {
				Type etype = interprocedural.getAnalysis().getDynamicTypeOf(state, e, this);
				Type computed = computeType(etype);
				if (!computed.equals(Untyped.INSTANCE))
					return interprocedural.getAnalysis().smallStepSemantics(state,
							new PushAny(computed, this.getLocation()),
							this);
			}
		}

		return state;
	}

}