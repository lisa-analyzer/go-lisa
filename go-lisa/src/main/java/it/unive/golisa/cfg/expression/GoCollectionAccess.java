package it.unive.golisa.cfg.expression;

import java.util.Set;

import it.unive.golisa.analysis.taint.TaintDomainForPrivacyHF;
import it.unive.golisa.analysis.taint.Tainted;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.nonrelational.value.NonRelationalValueDomain;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.BinaryExpression;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.AccessChild;
import it.unive.lisa.symbolic.heap.HeapDereference;
import it.unive.lisa.symbolic.heap.HeapExpression;
import it.unive.lisa.symbolic.heap.HeapReference;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.symbolic.value.ValueExpression;
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

	@Override
	protected int compareSameClassAndParams(Statement o) {
		return 0; // nothing else to compare
	}

	/**
	 * Yields the receiver of this access expression.
	 * 
	 * @return the receiver of this access expression.
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
		
		if (right instanceof Tainted)
			return state.smallStepSemantics(right, this);
		
		
		SymbolicExpression inner;
		if (left instanceof HeapReference)
			inner = ((HeapReference) left).getExpression();
		else if (left instanceof HeapExpression)
			inner = left;
		else
			inner = new HeapDereference(getStaticType(), left, getLocation());
		
		AnalysisState<A> result = state
				.smallStepSemantics(new AccessChild(Untyped.INSTANCE, inner, right, getLocation()), this);
		
		Tainted tainted = new Tainted(getLocation());
		
		// Workaround for cases such as arr[t], where t is tainted

		if (state.getState() instanceof SimpleAbstractState<?, ?, ?>) {
			SimpleAbstractState<?, ?, ?> simpleState = (SimpleAbstractState<?, ?, ?>) state.getState();
			if (simpleState.getValueState() instanceof ValueEnvironment<?>) {
				ValueEnvironment<?> valueEnv = (ValueEnvironment<?>) simpleState.getValueState();
				
				if(left instanceof Identifier) {
					
					NonRelationalValueDomain<?> leftState = valueEnv.getMap().get(left);
					if (leftState instanceof TaintDomainForPrivacyHF) {
						if(((TaintDomainForPrivacyHF) leftState).isTainted()) {
							AnalysisState<A> tmp = state.bottom();
							for (SymbolicExpression id : result.getComputedExpressions())
								tmp = tmp.lub(result.assign(id, tainted, this));
							result = result.lub(new AnalysisState<>(tmp.getState(), result.getComputedExpressions()));
							return result;
						}
					}
				}
				if(right instanceof Identifier) {
					
					NonRelationalValueDomain<?> rightState = valueEnv.getMap().get(right);
					if (rightState instanceof TaintDomainForPrivacyHF) {
						if(((TaintDomainForPrivacyHF) rightState).isTainted()) {
							AnalysisState<A> tmp = state.bottom();
							for (SymbolicExpression id : result.getComputedExpressions())
								tmp = tmp.lub(result.assign(id, tainted, this));
							result = result.lub(new AnalysisState<>(tmp.getState(), result.getComputedExpressions()));
							return result;
						}
					}
				}
				
				for (SymbolicExpression stack : simpleState.rewrite(state.getComputedExpressions(), getReceiver(), simpleState)) {
					NonRelationalValueDomain<?> stackValue = valueEnv.eval((ValueExpression)stack, getReceiver(), simpleState);
					if (stackValue instanceof TaintDomainForPrivacyHF) {
						if(((TaintDomainForPrivacyHF) stackValue).isTainted()) {
							AnalysisState<A> tmp = state.bottom();
							for (SymbolicExpression id : result.getComputedExpressions())
								tmp = tmp.lub(result.assign(id, tainted, this));
							result = result.lub(new AnalysisState<>(tmp.getState(), result.getComputedExpressions()));
						} else {
							AnalysisState<A> tmp = state.bottom();
							for (SymbolicExpression id : result.getComputedExpressions())
								tmp = tmp.lub(result.assign(id, new PushAny(getStaticType(), getLocation()), this));
							result = result.lub(new AnalysisState<>(tmp.getState(), result.getComputedExpressions()));
						}
						
					}
				}				
			}
		}

		Set<Type> ltypes = state.getState().getRuntimeTypesOf(left, this, state.getState());
		for (Type type : ltypes) {
			if (type.isPointerType()) {
				result = result.lub(state.smallStepSemantics(
						new AccessChild(Untyped.INSTANCE,
								new HeapDereference(getStaticType(), left, getLocation()), right, getLocation()),
						this));
			}
		}
		return result;
	}
}
