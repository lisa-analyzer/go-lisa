package it.unive.golisa.cfg.expression;

import java.util.HashSet;
import java.util.Set;

import it.unive.golisa.analysis.ni.IntegrityNIDomain;
import it.unive.golisa.analysis.taint.Clean;
import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.analysis.taint.TaintDomainForPhase1;
import it.unive.golisa.analysis.taint.TaintDomainForPhase2;
import it.unive.golisa.analysis.taint.Tainted;
import it.unive.golisa.analysis.taint.TaintedP1;
import it.unive.golisa.analysis.taint.TaintedP2;
import it.unive.golisa.cfg.expression.unary.GoDeref;
import it.unive.golisa.cfg.runtime.time.type.Duration;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.nonrelational.inference.InferenceSystem;
import it.unive.lisa.analysis.nonrelational.value.NonRelationalValueDomain;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.BinaryExpression;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.AccessChild;
import it.unive.lisa.symbolic.heap.HeapDereference;
import it.unive.lisa.symbolic.heap.HeapExpression;
import it.unive.lisa.symbolic.heap.HeapReference;
import it.unive.lisa.symbolic.value.Constant;
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
	public String toString() {
		return getReceiver() + "::" + getTarget();
	}
	
	@Override
	public <A extends AbstractState<A, H, V, T>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>,
			T extends TypeDomain<T>> AnalysisState<A, H, V, T> binarySemantics(
					InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
					SymbolicExpression left, SymbolicExpression right, StatementStore<A, H, V, T> expressions)
					throws SemanticException {

		if (getLeft().toString().startsWith("args") || getLeft().toString().startsWith("para"))
			return state.smallStepSemantics(left, this);
		if (getLeft().toString().startsWith("blocks"))
			return state;
		if (getLeft().toString().equals("time") && getRight().toString().equals("Second"))
			return state.smallStepSemantics(new Constant(Duration.INSTANCE, "SECOND_VALUE", getLocation()), this);
		if (right instanceof Tainted || right instanceof TaintedP1 || right instanceof TaintedP2)
			return state.smallStepSemantics(right, this);
		if (getLeft().toString().startsWith("response") || getRight().toString().startsWith("Payload"))
			return state.smallStepSemantics(left, this);
		if (getLeft().toString().startsWith("resp") || getRight().toString().startsWith("Payload"))
			return state.smallStepSemantics(left, this);
		if (getRight().toString().startsWith("Payload"))
			return state.smallStepSemantics(left, this);
		

		Expression rec = getReceiver();
		if (state.getDomainInstance(ValueEnvironment.class) != null) {
			ValueEnvironment<?> linst = state.smallStepSemantics(left, getReceiver()).getDomainInstance(ValueEnvironment.class);
			ValueEnvironment<?> rinst = state.smallStepSemantics(right, getReceiver()).getDomainInstance(ValueEnvironment.class);
			
			if (linst.getValueOnStack() instanceof TaintDomain) {
				if(rec instanceof GoCollectionAccess) {
					rec = getGoCollectionReceiver((GoCollectionAccess) rec);

				}	
				if (((TaintDomain)linst.getValueOnStack()).isTainted()
						|| ((TaintDomain)rinst.getValueOnStack()).isTainted()) {
					if(rec instanceof VariableRef) {
						VariableRef var = (VariableRef) rec;
						return state.assign(var.getVariable(),new Tainted(getLocation()), getReceiver());
					}else
						return state.smallStepSemantics(new Tainted(getLocation()), getReceiver());
				}
				if( rec instanceof VariableRef) {
					VariableRef var = (VariableRef) rec;
					return state.assign(var.getVariable(),new Clean(Untyped.INSTANCE, getLocation()),  getReceiver());
				} else
					return state.smallStepSemantics(new Clean(Untyped.INSTANCE, getLocation()), getReceiver());
			}
		
			
			if (linst.getValueOnStack() instanceof TaintDomainForPhase1) {
				if(rec instanceof GoCollectionAccess) {
					rec = getGoCollectionReceiver((GoCollectionAccess) rec);

				}	
				if (((TaintDomainForPhase1)linst.getValueOnStack()).isTainted()
						|| ((TaintDomainForPhase1)rinst.getValueOnStack()).isTainted()) {
					if(rec instanceof VariableRef) {
						VariableRef var = (VariableRef) rec;
						return state.assign(var.getVariable(),new TaintedP1(getLocation()), getReceiver());
					}else
						return state.smallStepSemantics(new TaintedP1(getLocation()), getReceiver());
				}
				if( rec instanceof VariableRef) {
					VariableRef var = (VariableRef) rec;
					return state.assign(var.getVariable(),new Clean(Untyped.INSTANCE, getLocation()),  getReceiver());
				} else
					return state.smallStepSemantics(new Clean(Untyped.INSTANCE, getLocation()), getReceiver());
			}
		
			if (linst.getValueOnStack() instanceof TaintDomainForPhase2) {
				if(rec instanceof GoCollectionAccess) {
					rec = getGoCollectionReceiver((GoCollectionAccess) rec);

				}	
				if (((TaintDomainForPhase2)linst.getValueOnStack()).isTainted()
						|| ((TaintDomainForPhase2)rinst.getValueOnStack()).isTainted()) {
					if(rec instanceof VariableRef) {
						VariableRef var = (VariableRef) rec;
						return state.assign(var.getVariable(),new TaintedP2(getLocation()), getReceiver());
					}else
						return state.smallStepSemantics(new TaintedP2(getLocation()), getReceiver());
				}
				if( rec instanceof VariableRef) {
					VariableRef var = (VariableRef) rec;
					return state.assign(var.getVariable(),new Clean(Untyped.INSTANCE, getLocation()),  getReceiver());
				} else
					return state.smallStepSemantics(new Clean(Untyped.INSTANCE, getLocation()), getReceiver());
			}
		}
		// Access global
		for (Unit unit : getProgram().getUnits())
			if (unit.toString().equals(getReceiver().toString()))
				for (Global g : unit.getGlobals())
					if (g.toString().endsWith(getTarget().toString()))
						return state.smallStepSemantics(new Clean(g.getStaticType(), getLocation()), getReceiver());

		SymbolicExpression inner;
		if (left instanceof HeapReference)
			inner = ((HeapReference) left).getExpression();
		else if (left instanceof HeapExpression)
			inner = left;
		else
			inner = new HeapDereference(getStaticType(), left, getLocation());
		AnalysisState<A, H, V, T> result = state
				.smallStepSemantics(new AccessChild(Untyped.INSTANCE, inner, right, getLocation()), this);

		// Workaround for cases such as arr[t], where t is tainted

		AnalysisState<A, H, V, T> rightState = state.smallStepSemantics(right, this);

		ValueEnvironment<?> env = rightState.getDomainInstance(ValueEnvironment.class);
		if (env != null) {
			NonRelationalValueDomain<?> stack = env.getValueOnStack();
			if (stack instanceof TaintDomain && ((TaintDomain) stack).isTainted()
					|| stack instanceof TaintDomainForPhase1 && ((TaintDomainForPhase1) stack).isTainted()
					|| stack instanceof TaintDomainForPhase2 && ((TaintDomainForPhase2) stack).isTainted()) {
				AnalysisState<A, H, V, T> tmp = state.bottom();
				for (SymbolicExpression id : result.getComputedExpressions())
					tmp = tmp.lub(result.assign(id, new Tainted(getLocation()), this));
				return new AnalysisState<>(tmp.getState(), result.getComputedExpressions(),
						tmp.getAliasing());
			}
		}
		
		InferenceSystem<?> sys = rightState.getDomainInstance(InferenceSystem.class);
		if (sys != null) {
			Object value = sys.getInferredValue();
			if (value instanceof IntegrityNIDomain 
					&& ((IntegrityNIDomain) value).isLowIntegrity()) {
				AnalysisState<A, H, V, T> tmp = state.bottom();
				for (SymbolicExpression id : result.getComputedExpressions())
					tmp = tmp.lub(result.assign(id, new Tainted(getLocation()), this));
				return new AnalysisState<>(tmp.getState(), result.getComputedExpressions(),
						tmp.getAliasing());
			}
		}

		return result;

	}

	private Expression getGoCollectionReceiver(GoCollectionAccess ca) {
		Set<Expression> seen = new HashSet<>();
		return getGoCollectionReceiverRecursive(ca, seen);
	}
	

	private Expression getGoCollectionReceiverRecursive(GoCollectionAccess ca, Set<Expression> seen) {
		
		if(!seen.contains(ca)) {
			seen.add(ca);
			Expression res = ca.getLeft();
			if(res instanceof GoCollectionAccess) 
				return getGoCollectionReceiverRecursive((GoCollectionAccess) res, seen);
			return res;
		}
		return ca;
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
}
