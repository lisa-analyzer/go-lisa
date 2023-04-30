package it.unive.golisa.cfg.runtime.encoding.pem.function;

import it.unive.golisa.analysis.ni.IntegrityNIDomain;
import it.unive.golisa.analysis.taint.Clean;
import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.analysis.taint.TaintDomainForPhase1;
import it.unive.golisa.analysis.taint.TaintDomainForPhase2;
import it.unive.golisa.analysis.taint.Tainted;
import it.unive.golisa.analysis.taint.TaintedP1;
import it.unive.golisa.analysis.taint.TaintedP2;
import it.unive.golisa.cfg.runtime.encoding.pem.type.Block;
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
			ValueEnvironment<?> env = state.getDomainInstance(ValueEnvironment.class);
			if (env != null) {
				ValueEnvironment<?> linst = state.smallStepSemantics(expr, original).getDomainInstance(ValueEnvironment.class);
				if (linst.getValueOnStack() instanceof TaintDomainForPhase1) {
					if (((TaintDomainForPhase1)linst.getValueOnStack()).isTainted())
						return state.smallStepSemantics(new TaintedP1(getStaticType(),getLocation()), original);
					return state.smallStepSemantics(new Clean(getStaticType(), getLocation()), original);
				}
				if (linst.getValueOnStack() instanceof TaintDomainForPhase2) {
					if (((TaintDomainForPhase2)linst.getValueOnStack()).isTainted())
						return state.smallStepSemantics(new TaintedP2(getStaticType(),getLocation()), original);
					return state.smallStepSemantics(new Clean(getStaticType(), getLocation()), original);
				}
				
				if (linst.getValueOnStack() instanceof TaintDomain) {
					if (((TaintDomain)linst.getValueOnStack()).isTainted())
						return state.smallStepSemantics(new Tainted(getStaticType(),getLocation()), original);
					return state.smallStepSemantics(new Clean(getStaticType(), getLocation()), original);
				}
			}
			
			InferenceSystem<?> sys = state.getDomainInstance(InferenceSystem.class);
			if (sys != null) {
				InferenceSystem<?> linst = state.smallStepSemantics(expr, original).getDomainInstance(InferenceSystem.class);
				if (linst.getInferredValue() instanceof IntegrityNIDomain) {
					if (((IntegrityNIDomain)linst.getInferredValue()).isLowIntegrity())
						return state.smallStepSemantics(new Tainted(getStaticType(), getLocation()), original);
					return state.smallStepSemantics(new Clean(getStaticType(), getLocation()), original);
				}
			}
			return state.smallStepSemantics(expr, original);
		}
	}
}
