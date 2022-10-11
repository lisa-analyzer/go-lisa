package it.unive.golisa.cfg.runtime.shim.method;

import it.unive.golisa.cfg.runtime.peer.type.Response;
import it.unive.golisa.cfg.runtime.shim.type.ChaincodeStub;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.lattices.ExpressionSet;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CFGDescriptor;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.NativeCFG;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.NaryExpression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;

/**
 * func (s *ChaincodeStub) InvokeChaincode(chaincodeName string, args [][]byte, channel string) pb.Response
 * https://pkg.go.dev/github.com/hyperledger/fabric-chaincode-go/shim#ChaincodeStub.InvokeChaincode
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class InvokeChaincode extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param shimUnit the unit to which this native cfg belongs to
	 */
	public InvokeChaincode(CodeLocation location, CompilationUnit shimUnit) {
		super(new CFGDescriptor(location, shimUnit, false, "InvokeChaincode", Response.INSTANCE,
				new Parameter(location, "this", ChaincodeStub.INSTANCE),
				new Parameter(location, "chaincodeName", GoStringType.INSTANCE),
				new Parameter(location, "args", GoSliceType.lookup(GoSliceType.getSliceOfSliceOfBytes())),
				new Parameter(location, "channel", GoStringType.INSTANCE)),
				InvokeChaincodeImpl.class);
	}

	/**
	 * The {@link InvokeChaincode} implementation.
	 * 
	 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
	 */
	public static class InvokeChaincodeImpl extends NaryExpression
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
		public static InvokeChaincodeImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new InvokeChaincodeImpl(cfg, location, params);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param params   the parameters
		 */
		public InvokeChaincodeImpl(CFG cfg, CodeLocation location, Expression... params) {
			super(cfg, location, "InvokeChaincodeImpl", Response.INSTANCE, params);
		}

		@Override
		public <A extends AbstractState<A, H, V, T>,
				H extends HeapDomain<H>,
				V extends ValueDomain<V>,
				T extends TypeDomain<T>> AnalysisState<A, H, V, T> expressionSemantics(
						InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
						ExpressionSet<SymbolicExpression>[] params, StatementStore<A, H, V, T> expressions)
						throws SemanticException {
			AnalysisState<A, H, V, T> result = state.bottom();
			for (ExpressionSet<SymbolicExpression> exprs : params)
				for (SymbolicExpression expr : exprs)
					result = result.lub(state.smallStepSemantics(expr, original));
			return result;
		}
	}
}
