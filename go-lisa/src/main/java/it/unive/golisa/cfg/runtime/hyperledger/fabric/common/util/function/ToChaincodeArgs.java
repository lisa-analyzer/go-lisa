package it.unive.golisa.cfg.runtime.hyperledger.fabric.common.util.function;

import it.unive.golisa.analysis.taint.Clean;
import it.unive.golisa.analysis.taint.TaintDomainForPhase1;
import it.unive.golisa.analysis.taint.TaintDomainForPhase2;
import it.unive.golisa.analysis.taint.TaintedP1;
import it.unive.golisa.analysis.taint.TaintedP2;
import it.unive.golisa.cfg.expression.unary.GoDeref;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.CodeUnit;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.NativeCFG;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.UnaryExpression;
import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.type.Untyped;

/**
 * func ToChaincodeArgs(args ...string) [][]byte
 *
 * @link https://github.com/hyperledger/fabric/blob/main/common/util/utils.go
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class ToChaincodeArgs extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param shimUnit the unit to which this native cfg belongs to
	 */
	public ToChaincodeArgs(CodeLocation location, CodeUnit utilUnit) {
		super(new CodeMemberDescriptor(location, utilUnit, false, "ToChaincodeArgs",
				GoSliceType.lookup(GoSliceType.getSliceOfSliceOfBytes()),
				new Parameter(location, "args", GoSliceType.lookup(GoStringType.INSTANCE))),
				ToChaincodeArgsImpl.class);
	}

	/**
	 * The ToChaincodeArgs implementation.
	 * 
	 */
	public static class ToChaincodeArgsImpl extends UnaryExpression
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
		public static ToChaincodeArgsImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new ToChaincodeArgsImpl(cfg, location, params[0]);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param expr     the expression
		 */
		public ToChaincodeArgsImpl(CFG cfg, CodeLocation location, Expression expr) {
			super(cfg, location, "ToChaincodeArgsImpl", GoSliceType.lookup(GoSliceType.getSliceOfSliceOfBytes()), expr);
		}

		@Override
		public <A extends AbstractState<A, H, V, T>,
				H extends HeapDomain<H>,
				V extends ValueDomain<V>,
				T extends TypeDomain<T>> AnalysisState<A, H, V, T> unarySemantics(
						InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
						SymbolicExpression expr, StatementStore<A, H, V, T> expressions) throws SemanticException {
			ValueEnvironment<?> env = state.getDomainInstance(ValueEnvironment.class);
			if (env != null) {
				ValueEnvironment<?> inst = state.smallStepSemantics(expr, original).getDomainInstance(ValueEnvironment.class);
				
				if (inst.getValueOnStack() instanceof TaintDomainForPhase1) {
					if (((TaintDomainForPhase1)inst.getValueOnStack()).isTainted()) {
						return state.smallStepSemantics(new TaintedP1(getLocation()), original);
					}
					return state.smallStepSemantics(new Clean(Untyped.INSTANCE, getLocation()), original);
				}
				if (inst.getValueOnStack() instanceof TaintDomainForPhase2) {
					if (((TaintDomainForPhase2)inst.getValueOnStack()).isTainted()) {
						return state.smallStepSemantics(new TaintedP2(getLocation()), original);
					}
					return state.smallStepSemantics(new Clean(Untyped.INSTANCE, getLocation()), original);
				}
			}
		return GoSliceType.lookup(GoSliceType.getSliceOfSliceOfBytes()).defaultValue(getCFG(), (SourceCodeLocation) getLocation())
			.semantics(state, interprocedural, expressions);
		}
	}
}