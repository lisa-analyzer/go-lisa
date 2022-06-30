package it.unive.golisa.cfg.runtime.io.function;

import it.unive.golisa.cfg.runtime.io.type.PipeReader;
import it.unive.golisa.cfg.runtime.io.type.PipeWriter;
import it.unive.golisa.cfg.type.composite.GoPointerType;
import it.unive.golisa.cfg.type.composite.GoTupleType;
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
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.NaryExpression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.Skip;

/**
 * func Pipe() (*PipeReader, *PipeWriter).
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class Pipe extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param ioUnit   the unit to which this native cfg belongs to
	 */
	public Pipe(CodeLocation location, CompilationUnit ioUnit) {
		super(new CFGDescriptor(location, ioUnit, false, "Pipe",
				GoTupleType.getTupleTypeOf(location, new GoPointerType(PipeReader.INSTANCE),
						new GoPointerType(PipeWriter.INSTANCE))),
				PipeImpl.class);
	}

	public static class PipeImpl extends NaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static PipeImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new PipeImpl(cfg, location, params);
		}

		public PipeImpl(CFG cfg, CodeLocation location, Expression... params) {
			super(cfg, location, "PipeImpl",
					GoTupleType.getTupleTypeOf(location, new GoPointerType(PipeReader.INSTANCE),
							new GoPointerType(PipeWriter.INSTANCE)),
					params);
		}

		@Override
		public <A extends AbstractState<A, H, V, T>,
				H extends HeapDomain<H>,
				V extends ValueDomain<V>,
				T extends TypeDomain<T>> AnalysisState<A, H, V, T> expressionSemantics(
						InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
						ExpressionSet<SymbolicExpression>[] params, StatementStore<A, H, V, T> expressions)
						throws SemanticException {
			return state.smallStepSemantics(new Skip(getLocation()), original);
		}

	}
}
