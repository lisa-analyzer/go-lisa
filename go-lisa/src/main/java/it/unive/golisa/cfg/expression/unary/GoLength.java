package it.unive.golisa.cfg.expression.unary;

import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.AccessChild;
import it.unive.lisa.symbolic.heap.HeapDereference;
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.symbolic.value.UnaryExpression;
import it.unive.lisa.symbolic.value.Variable;
import it.unive.lisa.symbolic.value.operator.unary.StringLength;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import java.util.Set;

/**
 * A Go len expression (e.g., len(x)).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoLength extends it.unive.lisa.program.cfg.statement.UnaryExpression {

	/**
	 * Builds the len expression.
	 * 
	 * @param cfg      the {@link CFG} where this expression lies
	 * @param location the location where this expression is defined
	 * @param exp      the expression
	 */
	public GoLength(CFG cfg, SourceCodeLocation location, Expression exp) {
		super(cfg, location, "len", exp);
	}

	@Override
	protected int compareSameClassAndParams(Statement o) {
		return 0; // nothing else to compare
	}

	@Override
	public <A extends AbstractState<A>> AnalysisState<A> fwdUnarySemantics(InterproceduralAnalysis<A> interprocedural,
			AnalysisState<A> state, SymbolicExpression expr, StatementStore<A> expressions) throws SemanticException {
		AnalysisState<A> result = state.bottom();
		Set<Type> etypes = state.getState().getRuntimeTypesOf(expr, this, state.getState());
		for (Type type : etypes) {
			if (type.isPointerType()) {
				HeapDereference deref = new HeapDereference(type.asPointerType().getInnerType(), expr, getLocation());
				AccessChild lenAccess = new AccessChild(GoIntType.INSTANCE, deref,
						new Variable(Untyped.INSTANCE, "len", getLocation()), getLocation());
				result = result.lub(state.smallStepSemantics(lenAccess, this));
			} else if (type.isArrayType() || type instanceof GoSliceType) {
				// FIXME we get here when rec is a parameter of an entrypoint,
				// and len is not defined yet..
				result = result.lub(state.smallStepSemantics(new PushAny(GoIntType.INSTANCE, getLocation()), this));
			} else if (type.isStringType())
				result = result.lub(state.smallStepSemantics(
						new UnaryExpression(GoIntType.INSTANCE, expr, StringLength.INSTANCE, getLocation()), this));
		}

		return result;
	}
}
