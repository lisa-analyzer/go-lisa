package it.unive.golisa.cfg.runtime.io.function;

import it.unive.golisa.cfg.runtime.io.type.Reader;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.composite.GoTypesTuple;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.lattices.ExpressionSet;
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
import it.unive.lisa.type.common.UInt8;

/**
 * func ReadAtLeast(r Reader, buf []byte, min int) (n int, err error)
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class ReadAtLeast extends NativeCFG {

	public ReadAtLeast(CodeLocation location, CompilationUnit ioUnit) {
		super(new CFGDescriptor(location, ioUnit, false, "ReadAtLeast",
				GoTypesTuple.getTupleTypeOf(location, GoIntType.INSTANCE,
						GoErrorType.INSTANCE),
				new Parameter(location, "r", Reader.INSTANCE),
				new Parameter(location, "buf", GoSliceType.lookup(new GoSliceType(UInt8.INSTANCE))),
				new Parameter(location, "min", GoIntType.INSTANCE)),
				ReadAtLeastImpl.class);
	}

	public static class ReadAtLeastImpl extends NaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static ReadAtLeastImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new ReadAtLeastImpl(cfg, location, params);
		}

		public ReadAtLeastImpl(CFG cfg, CodeLocation location, Expression... params) {
			super(cfg, location, "ReadAtLeastImpl",
					GoTypesTuple.getTupleTypeOf(location, GoIntType.INSTANCE, GoErrorType.INSTANCE),	params);
		}

		@Override
		public <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> expressionSemantics(
				InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> state,
				ExpressionSet<SymbolicExpression>[] params, StatementStore<A, H, V> expressions)
				throws SemanticException {
			return state.top();
		}

	}
}