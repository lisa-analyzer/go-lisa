package it.unive.golisa.cfg.runtime.encoding.json.function;

import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoInterfaceType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.composite.GoTypesTuple;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt8Type;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
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

/**
 * func Marshal(v interface{}) ([]byte, error)
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class Marshal extends NativeCFG {

	public Marshal(CodeLocation location, CompilationUnit jsonUnit) {
		super(new CFGDescriptor(location, jsonUnit, false, "Marshal",
				GoTypesTuple.getTupleTypeOf(location, GoSliceType.lookup(new GoSliceType(GoUInt8Type.INSTANCE)),
						GoErrorType.INSTANCE),
				new Parameter(location, "v", GoInterfaceType.getEmptyInterface())),
				MarshalImpl.class);
	}

	public static class MarshalImpl extends UnaryExpression
	implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static MarshalImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new MarshalImpl(cfg, location, params[0]);
		}

		public MarshalImpl(CFG cfg, CodeLocation location, Expression expr) {
			super(cfg, location, "MarshalImpl",
					GoTypesTuple.getTupleTypeOf(location, GoSliceType.lookup(new GoSliceType(GoUInt8Type.INSTANCE)),
							GoErrorType.INSTANCE),
					expr);
		}

		@Override
		protected <A extends AbstractState<A, H, V>,
		H extends HeapDomain<H>,
		V extends ValueDomain<V>> AnalysisState<A, H, V> unarySemantics(
				InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> state,
				SymbolicExpression expr,
				StatementStore<A, H, V> expressions) throws SemanticException {
			return state.smallStepSemantics(expr, original);
		}
	}
}
