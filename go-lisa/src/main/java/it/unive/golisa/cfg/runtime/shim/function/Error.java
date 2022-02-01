package it.unive.golisa.cfg.runtime.shim.function;

import it.unive.golisa.cfg.runtime.peer.type.Response;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.caches.Caches;
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
 * Error response chaincodes.
 * 
 * func Error(msg string) pb.Response
 * 
 * @link https://pkg.go.dev/github.com/hyperledger/fabric-chaincode-go/shim#Error
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class Error extends NativeCFG {

	public Error(CodeLocation location, CompilationUnit shimUnit) {
		super(new CFGDescriptor(location, shimUnit, false, "Error", Response.INSTANCE,
				new Parameter(location, "msg", GoStringType.INSTANCE)),
				ErrorImpl.class);
	}

	public static class ErrorImpl extends UnaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static ErrorImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new ErrorImpl(cfg, location, params[0]);
		}

		public ErrorImpl(CFG cfg, CodeLocation location, Expression e) {
			super(cfg, location, "ErrorImpl", Response.INSTANCE, e);
		}

		@Override
		protected <A extends AbstractState<A, H, V>,
				H extends HeapDomain<H>,
				V extends ValueDomain<V>> AnalysisState<A, H, V> unarySemantics(
						InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> state,
						SymbolicExpression expr, StatementStore<A, H, V> expressions)
						throws SemanticException {
			return state.smallStepSemantics(new PushAny(Caches.types().mkUniversalSet(), getLocation()), original);
		}
	}
}