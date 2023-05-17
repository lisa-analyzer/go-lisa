package it.unive.golisa.cfg.runtime.strconv;

import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoPointerType;
import it.unive.golisa.cfg.type.composite.GoTupleType;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.numeric.Interval;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.CodeUnit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.NativeCFG;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.UnaryExpression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.AccessChild;
import it.unive.lisa.symbolic.heap.HeapDereference;
import it.unive.lisa.symbolic.heap.HeapReference;
import it.unive.lisa.symbolic.heap.MemoryAllocation;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.PushAny;

/**
 * func Atoi(s string) int.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class Atoi extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location    the location where this native cfg is defined
	 * @param strconvUnit the unit to which this native cfg belongs to
	 */
	public Atoi(CodeLocation location, CodeUnit strconvUnit) {
		super(new CodeMemberDescriptor(location, strconvUnit, false, "Atoi", GoIntType.INSTANCE,
				new Parameter(location, "this", GoTupleType.getTupleTypeOf(location, GoIntType.INSTANCE, GoErrorType.INSTANCE))),
				AtoiImpl.class);
		GoPointerType.lookup(GoTupleType.getTupleTypeOf(location, GoIntType.INSTANCE, GoErrorType.INSTANCE)); //mandatory
	}

	/**
	 * The Atoi implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class AtoiImpl extends UnaryExpression implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param params   the parameters
		 * 
		 * @return the pluggable statement
		 */
		public static AtoiImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new AtoiImpl(cfg, location, params[0]);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param expr     the expression
		 */
		public AtoiImpl(CFG cfg, CodeLocation location, Expression expr) {
			super(cfg, location, "Atoi", GoTupleType.getTupleTypeOf(location, GoIntType.INSTANCE, GoErrorType.INSTANCE), expr);
		}

		@Override
		public <A extends AbstractState<A, H, V, T>,
				H extends HeapDomain<H>,
				V extends ValueDomain<V>,
				T extends TypeDomain<T>> AnalysisState<A, H, V, T> unarySemantics(
						InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
						SymbolicExpression expr, StatementStore<A, H, V, T> expressions) throws SemanticException {
		
			GoTupleType returnType = GoTupleType.getTupleTypeOf(getLocation(), GoIntType.INSTANCE, GoErrorType.INSTANCE);
			MemoryAllocation memalloc = new MemoryAllocation(returnType, getLocation());

			state = state.smallStepSemantics(memalloc, original);
			AnalysisState<A, H, V, T> result = state.bottom();
			for(SymbolicExpression e : state.getComputedExpressions()) {
				AnalysisState<A, H, V, T> res = state.bottom();
				HeapReference heapRef = new HeapReference(GoPointerType.lookup(returnType), e, getLocation());
								
				AnalysisState<A, H, V, T> tmp = state.smallStepSemantics(heapRef, original);
				for(SymbolicExpression e2 : tmp.getComputedExpressions()) {
				
					AccessChild acRight = new AccessChild(GoIntType.INSTANCE, e2, new Constant(GoIntType.INSTANCE, 0, getLocation()), getLocation());
					AccessChild acLeft = new AccessChild(GoErrorType.INSTANCE, e2, new Constant(GoIntType.INSTANCE, 1, getLocation()), getLocation());
					
					AnalysisState<A, H, V, T> tmp2 = tmp.smallStepSemantics(acRight, original);
					for(SymbolicExpression e3 : tmp2.getComputedExpressions()) {
						res = res.lub(tmp2.assign(e3, new PushAny(GoIntType.INSTANCE, getLocation()),original));
					}
					
					tmp2 = tmp2.smallStepSemantics(acLeft, original);
					for(SymbolicExpression e3 : tmp2.getComputedExpressions()) {
						res = res.lub(tmp2.assign(e3, new PushAny(GoErrorType.INSTANCE, getLocation()),original));
					}
				}
				
				result = result.lub(res.smallStepSemantics(heapRef, original));
				
			}
			
			return result;
			
			/*
			ValueEnvironment<?> env = state.getDomainInstance(ValueEnvironment.class);
			if (env != null) {
				ValueEnvironment<?> ve = state.smallStepSemantics(expr, original).getDomainInstance(ValueEnvironment.class);
				if (ve.lattice instanceof Interval) {
					return state.smallStepSemantics(new PushAny(GoIntType.INSTANCE, getLocation()), original);
				}
			}
			
			if (!expr.getDynamicType().isStringType() && !expr.getDynamicType().isUntyped())
				return state.bottom();
		
			return state.smallStepSemantics(expr, original);
			*/
		}
	}
}
