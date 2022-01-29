package it.unive.golisa.cfg.runtime.os.function;

import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoTypesTuple;
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
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.NaryExpression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;

/**
 * 
 * func Executable() (string, error)
 * 
 * @link https://pkg.go.dev/os#Executable
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class Executable extends NativeCFG {

	public Executable(CodeLocation location, CompilationUnit osUnit) {
		super(new CFGDescriptor(location, osUnit, false, "Executable", 
				GoTypesTuple.getTupleTypeOf(location, GoStringType.INSTANCE,
				GoErrorType.INSTANCE)),
				ExecutableImpl.class);
	}

	public static class ExecutableImpl extends NaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static ExecutableImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new ExecutableImpl(cfg, location);
		}

		public ExecutableImpl(CFG cfg, CodeLocation location) {
			super(cfg, location, "ReadFileImpl", GoTypesTuple.getTupleTypeOf(location, GoStringType.INSTANCE,
					GoErrorType.INSTANCE));
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