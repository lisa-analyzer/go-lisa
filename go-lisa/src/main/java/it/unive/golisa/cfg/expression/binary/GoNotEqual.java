package it.unive.golisa.cfg.expression.binary;

import it.unive.golisa.cfg.type.GoBoolType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.operator.binary.ComparisonNe;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;

/**
 * A Go equal expression (e.g., x == y).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoNotEqual extends it.unive.lisa.program.cfg.statement.BinaryExpression {

	/**
	 * Builds the not equal expression.
	 *
	 * @param cfg      the {@link CFG} where this expression lies
	 * @param location the location where this expression is defined
	 * @param left     the left-hand side of this expression
	 * @param right    the right-hand side of this expression
	 */
	public GoNotEqual(CFG cfg, SourceCodeLocation location, Expression left, Expression right) {
		super(cfg, location, "!=", GoBoolType.INSTANCE, left, right);
	}

	@Override
	public <A extends AbstractState<A, H, V, T>,
	H extends HeapDomain<H>,
	V extends ValueDomain<V>,
	T extends TypeDomain<T>> AnalysisState<A, H, V, T> binarySemantics(
			InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
			SymbolicExpression left, SymbolicExpression right, StatementStore<A, H, V, T> expressions)
					throws SemanticException {
		TypeSystem types = getProgram().getTypes();

		if (right.getStaticType().canBeAssignedTo(left.getStaticType()) 
				|| left.getStaticType().canBeAssignedTo(right.getStaticType())) 
			return state
					.smallStepSemantics(new BinaryExpression(GoBoolType.INSTANCE,
							left, right,
							ComparisonNe.INSTANCE, getLocation()), this);
		// TODO: composite types are not covered yey
		AnalysisState<A, H, V, T> result = state.bottom();
		for (Type lType : left.getRuntimeTypes(types))
			for (Type rType : right.getRuntimeTypes(types)) 
				if (rType.canBeAssignedTo(lType) || lType.canBeAssignedTo(rType)) 
					result = result.lub(state
							.smallStepSemantics(new BinaryExpression(GoBoolType.INSTANCE,
									left, right,
									ComparisonNe.INSTANCE, getLocation()), this));
		return result;
	}
}
