package it.unive.golisa.cfg.runtime.shim.function;

import it.unive.golisa.cfg.runtime.peer.type.Response;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt8Type;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
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
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.UnaryExpression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.PushAny;

/**
 * Success response chaincodes.
 * 
 * func Success(payload []byte) pb.Response
 *
 * @link https://pkg.go.dev/github.com/hyperledger/fabric-chaincode-go/shim#Success
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class Success extends NativeCFG {

	public Success(CodeLocation location, CompilationUnit shimUnit) {
		super(new CFGDescriptor(location, shimUnit, false, "Success", Response.INSTANCE,
				new Parameter(location, "payload", GoSliceType.lookup(new GoSliceType(GoUInt8Type.INSTANCE)))),
				SuccessImpl.class);
	}

	public static class SuccessImpl extends UnaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static SuccessImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new SuccessImpl(cfg, location, params[0]);
		}

		public SuccessImpl(CFG cfg, CodeLocation location, Expression e) {
			super(cfg, location, "SuccessImpl", Response.INSTANCE, e);
		}

		@Override
		protected <A extends AbstractState<A, H, V, T>, H extends HeapDomain<H>, V extends ValueDomain<V>, T extends TypeDomain<T>> AnalysisState<A, H, V, T> unarySemantics(
				InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
				SymbolicExpression expr, StatementStore<A, H, V, T> expressions) throws SemanticException {
			return state.smallStepSemantics(new PushAny(Response.INSTANCE, getLocation()), original);
		}
	}
}