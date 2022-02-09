package it.unive.golisa.cfg.runtime.io.function;

import it.unive.golisa.cfg.runtime.io.type.Reader;
import it.unive.golisa.cfg.runtime.io.type.Writer;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoTypesTuple;
import it.unive.golisa.cfg.type.numeric.signed.GoInt64Type;
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
 * func CopyN(dst Writer, src Reader, n int64) (written int64, err error)
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class CopyN extends NativeCFG {

	public CopyN(CodeLocation location, CompilationUnit ioUnit) {
		super(new CFGDescriptor(location, ioUnit, false, "CopyN",
				GoTypesTuple.getTupleTypeOf(location, GoInt64Type.INSTANCE,
						GoErrorType.INSTANCE),
				new Parameter(location, "dsr", Writer.INSTANCE),
				new Parameter(location, "src", Reader.INSTANCE),
				new Parameter(location, "n", GoInt64Type.INSTANCE)),
				CopyNImpl.class);
	}

	public static class CopyNImpl extends NaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static CopyNImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new CopyNImpl(cfg, location, params);
		}

		public CopyNImpl(CFG cfg, CodeLocation location, Expression... params) {
			super(cfg, location, "CopyNImpl",
					GoTypesTuple.getTupleTypeOf(location, GoInt64Type.INSTANCE, GoErrorType.INSTANCE),	params);
		}

		@Override
		public <A extends AbstractState<A, H, V, T>, H extends HeapDomain<H>, V extends ValueDomain<V>, T extends TypeDomain<T>> AnalysisState<A, H, V, T> expressionSemantics(
				InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
				ExpressionSet<SymbolicExpression>[] params, StatementStore<A, H, V, T> expressions)
				throws SemanticException {
			return state.top();
		}

	}
}
