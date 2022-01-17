package it.unive.golisa.cfg.expression;

import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.caches.Caches;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.UnaryExpression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.operator.binary.TypeCast;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeTokenType;
import it.unive.lisa.util.collections.externalSet.ExternalSet;

public class GoTypeConversion extends UnaryExpression {

	private Type type;

	public GoTypeConversion(CFG cfg, SourceCodeLocation location, Type type, Expression exp) {
		super(cfg, location, "(" + type + ")", exp);
		this.type = type;
	}

	public Type getType() {
		return type;
	}

	@Override
	protected <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> unarySemantics(
			InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> state, SymbolicExpression expr)
			throws SemanticException {
		ExternalSet<Type> castType = Caches.types().mkSingletonSet(type);
		Constant typeCast = new Constant(new TypeTokenType(castType), type, getLocation());
		return state.smallStepSemantics(
				new BinaryExpression(castType, expr, typeCast, TypeCast.INSTANCE, getLocation()), this);
	
	}
}
