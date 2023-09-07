package it.unive.golisa.cfg.runtime.encoding.pem.function;

import it.unive.golisa.analysis.ni.IntegrityNIDomain;
import it.unive.golisa.analysis.taint.Clean;
import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.analysis.taint.Tainted;
import it.unive.golisa.cfg.runtime.encoding.pem.type.Block;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoPointerType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.composite.GoTupleType;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt8Type;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.nonrelational.inference.InferenceSystem;
import it.unive.lisa.analysis.nonrelational.inference.InferredValue;
import it.unive.lisa.analysis.nonrelational.value.NonRelationalValueDomain;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.ProgramUnit;
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
import it.unive.lisa.symbolic.value.ValueExpression;

/**
 * func Decode(data []byte) (p *Block, rest []byte)
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class Decode extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param unit the unit to which this native cfg belongs to
	 */
	public Decode(CodeLocation location, ProgramUnit unit) {
		super(new CodeMemberDescriptor(location, unit, false, "Decode", 
				GoTupleType.getTupleTypeOf(location, GoPointerType.lookup(Block.getBlockType(unit.getProgram())),
						GoSliceType.lookup(GoUInt8Type.INSTANCE)),
				new Parameter(location, "data", GoSliceType.lookup(GoUInt8Type.INSTANCE))),
				DecodeImpl.class); 
	}

	/**
	 * The {@link Decode} implementation.
	 * 
	 */
	public static class DecodeImpl extends UnaryExpression
			implements PluggableStatement {

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
		public static DecodeImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new DecodeImpl(cfg, location, params[0]);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param expr     the expression
		 */
		public DecodeImpl(CFG cfg, CodeLocation location, Expression expr) {
			super(cfg, location, "DecodeImpl", GoTupleType.getTupleTypeOf(location, GoPointerType.lookup(Block.INSTANCE),
					GoSliceType.lookup(GoUInt8Type.INSTANCE)), expr);
		}

		@Override
		public <A extends AbstractState<A, H, V, T>, H extends HeapDomain<H>, V extends ValueDomain<V>, T extends TypeDomain<T>> AnalysisState<A, H, V, T> unarySemantics(
				InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
				SymbolicExpression expr, StatementStore<A, H, V, T> expressions) throws SemanticException {
			
	
			AnalysisState<A, H, V, T> exprState = state.smallStepSemantics(expr, this);
			
			AnalysisState<A, H, V, T> taintSemantics = semanticsForTaintDomain(state, exprState);
			if(taintSemantics != null)
				return taintSemantics;
			
			AnalysisState<A, H, V, T> integrityNISemantics = semanticsForIntegrityNIDomain(state, exprState);
			if(integrityNISemantics != null)
				return integrityNISemantics;
			
			
			return state.smallStepSemantics(expr, original);
		}
		
		
		private <A extends AbstractState<A, H, V, T>,
		H extends HeapDomain<H>,
		V extends ValueDomain<V>,
		T extends TypeDomain<T>> AnalysisState<A, H, V, T> semanticsForTaintDomain(AnalysisState<A, H, V, T>  state, AnalysisState<A, H, V, T>  exprState) throws SemanticException {
				
			ValueEnvironment<?> exprEnv = exprState.getDomainInstance(ValueEnvironment.class);

			
			if (exprEnv != null) {
					boolean isTaintDomain = false;
						for( SymbolicExpression reWriteExpr : exprState.rewrite(exprState.getComputedExpressions(), this)) {
							NonRelationalValueDomain<?> stack = exprEnv.eval((ValueExpression) reWriteExpr, this);
							isTaintDomain = isTaintDomain || stack instanceof TaintDomain;
							if (stack != null && stack instanceof TaintDomain && ((TaintDomain) stack).isTainted()) {
								return state.smallStepSemantics(new Tainted(getLocation()), original);
							}
						}
					if(isTaintDomain)
						return state.smallStepSemantics(new Clean(getStaticType(), getLocation()), original);
			}
			
			return null;
			
		}
		
		private <A extends AbstractState<A, H, V, T>,
		H extends HeapDomain<H>,
		V extends ValueDomain<V>,
		T extends TypeDomain<T>> AnalysisState<A, H, V, T> semanticsForIntegrityNIDomain(AnalysisState<A, H, V, T>  state, AnalysisState<A, H, V, T>  exprState) throws SemanticException {
			
				InferenceSystem<?> exprSys = exprState.getDomainInstance(InferenceSystem.class);
				
				if (exprSys != null) {
					boolean isIntegrityNIDomain = false;

					for( SymbolicExpression reWriteExpr : exprState.rewrite(exprState.getComputedExpressions(), this)) {
						InferredValue<?> stack = exprSys.eval((ValueExpression) reWriteExpr, this);
						isIntegrityNIDomain = isIntegrityNIDomain || stack instanceof IntegrityNIDomain;
						if (stack != null && stack instanceof IntegrityNIDomain) {
							if(((IntegrityNIDomain) stack).isLowIntegrity())
								return state.smallStepSemantics(new Tainted(getLocation()), original);
						}
					}

					if(isIntegrityNIDomain)
						return state.smallStepSemantics(new Clean(getStaticType(), getLocation()), original);
				}
				
				return null;
			
		}
	}
}
