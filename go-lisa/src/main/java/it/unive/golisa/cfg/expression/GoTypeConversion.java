package it.unive.golisa.cfg.expression;

import java.util.Collections;
import java.util.Set;

import it.unive.golisa.cfg.runtime.conversion.GoConv;
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
import it.unive.lisa.program.cfg.statement.UnaryExpression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeTokenType;

/**
 * A Go type casting (e.g., (string) x).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoTypeConversion extends UnaryExpression {

	private Type type;

	/**
	 * Builds the type casting expression.
	 *
	 * @param cfg      the {@link CFG} where this expression lies
	 * @param location the location where this expression is defined
	 * @param type     the type
	 * @param exp      the expression to cast to {@code type}
	 */
	public GoTypeConversion(CFG cfg, SourceCodeLocation location, Type type, Expression exp) {
		super(cfg, location, "(" + type + ")", exp);
		this.type = type;
	}

	@Override
	public <A extends AbstractState<A, H, V, T>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>,
			T extends TypeDomain<T>> AnalysisState<A, H, V, T> unarySemantics(
					InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
					SymbolicExpression expr, StatementStore<A, H, V, T> expressions) throws SemanticException {
		Set<Type> castType = Collections.singleton(type);
		Constant typeCast = new Constant(new TypeTokenType(castType), type, getLocation());
		return state.smallStepSemantics(
				new BinaryExpression(type, expr, typeCast, GoConv.INSTANCE, getLocation()), this);
	}
}
