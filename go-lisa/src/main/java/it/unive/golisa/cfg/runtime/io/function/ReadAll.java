package it.unive.golisa.cfg.runtime.io.function;

import it.unive.golisa.cfg.runtime.io.type.Reader;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.composite.GoTypesTuple;
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
import it.unive.lisa.type.common.UInt8;

/**
 * func ReadAll(r Reader) ([]byte, error)
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class ReadAll extends NativeCFG {

	public ReadAll(CodeLocation location, CompilationUnit ioUnit) {
		super(new CFGDescriptor(location, ioUnit, false, "ReadAll",
				GoTypesTuple.getTupleTypeOf(location, GoSliceType.lookup(new GoSliceType(UInt8.INSTANCE)),
						GoErrorType.INSTANCE),
				new Parameter(location, "r", Reader.INSTANCE)),
				ReadAllImpl.class);
	}

	public static class ReadAllImpl extends UnaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static ReadAllImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new ReadAllImpl(cfg, location, params[0]);
		}

		public ReadAllImpl(CFG cfg, CodeLocation location, Expression expr) {
			super(cfg, location, "ReadAllImpl",
					GoTypesTuple.getTupleTypeOf(location, GoSliceType.lookup(new GoSliceType(UInt8.INSTANCE)),
							GoErrorType.INSTANCE),	expr);
		}

		@Override
		protected <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> unarySemantics(
				InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> state, SymbolicExpression expr,
				StatementStore<A, H, V> expressions) throws SemanticException {
			return state.top();
		}

	}
}