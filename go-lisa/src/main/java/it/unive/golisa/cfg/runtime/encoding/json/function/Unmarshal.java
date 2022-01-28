package it.unive.golisa.cfg.runtime.encoding.json.function;

import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoInterfaceType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt8Type;
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
import it.unive.lisa.program.cfg.statement.BinaryExpression;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.PushAny;

/**
 * func Unmarshal(data []byte, v interface{}) error
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class Unmarshal extends NativeCFG {

	public Unmarshal(CodeLocation location, CompilationUnit jsonUnit) {
		super(new CFGDescriptor(location, jsonUnit, false, "Unmarshal", GoErrorType.INSTANCE,
				new Parameter(location, "data", GoSliceType.lookup(new GoSliceType(GoUInt8Type.INSTANCE))),
				new Parameter(location, "v", GoInterfaceType.getEmptyInterface())),
				UnmarshalImpl.class);
	}

	public static class UnmarshalImpl extends BinaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static UnmarshalImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new UnmarshalImpl(cfg, location, params[0], params[1]);
		}

		public UnmarshalImpl(CFG cfg, CodeLocation location, Expression expr, Expression expr2) {
			super(cfg, location, "UnmarshalImpl", GoErrorType.INSTANCE, expr, expr2);
		}

		@Override
		protected <A extends AbstractState<A, H, V>,
				H extends HeapDomain<H>,
				V extends ValueDomain<V>> AnalysisState<A, H, V> binarySemantics(
						InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> state,
						SymbolicExpression left,
						SymbolicExpression right, StatementStore<A, H, V> expressions) throws SemanticException {
			return state.smallStepSemantics(
					new PushAny(Caches.types().mkSingletonSet(GoErrorType.INSTANCE), getLocation()), original);
		}
	}
}
