package it.unive.golisa.cfg.runtime.math.rand.method;

import it.unive.golisa.cfg.runtime.math.rand.type.Rand;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.composite.GoTypesTuple;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
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
import it.unive.lisa.program.cfg.statement.BinaryExpression;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;

/**
 * func (r *Rand) Read(p []byte) (n int, err error)
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class Read extends NativeCFG {

	public Read(CodeLocation location, CompilationUnit randUnit) {
		super(new CFGDescriptor(location, randUnit, true, "Read",
				GoTypesTuple.getTupleTypeOf(location, GoIntType.INSTANCE, GoErrorType.INSTANCE),
				new Parameter(location, "this", Rand.INSTANCE),
				new Parameter(location, "p", GoSliceType.lookup(new GoSliceType(GoIntType.INSTANCE)))),
				ReadImpl.class);
	}

	public static class ReadImpl extends BinaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static ReadImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new ReadImpl(cfg, location, params[0], params[1]);
		}

		public ReadImpl(CFG cfg, CodeLocation location, Expression expr, Expression expr2) {
			super(cfg, location, "ReadImpl",
					GoTypesTuple.getTupleTypeOf(location, GoIntType.INSTANCE, GoErrorType.INSTANCE), expr, expr2);
		}

		@Override
		protected <A extends AbstractState<A, H, V, T>, H extends HeapDomain<H>, V extends ValueDomain<V>, T extends TypeDomain<T>> AnalysisState<A, H, V, T> binarySemantics(
				InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
				SymbolicExpression left, SymbolicExpression right, StatementStore<A, H, V, T> expressions)
				throws SemanticException {
			return state.top();
		}
	}
}
