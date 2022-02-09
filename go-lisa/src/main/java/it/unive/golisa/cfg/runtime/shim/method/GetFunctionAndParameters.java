package it.unive.golisa.cfg.runtime.shim.method;

import it.unive.golisa.analysis.taint.Clean;
import it.unive.golisa.cfg.runtime.shim.type.ChaincodeStub;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.composite.GoTypesTuple;
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

public class GetFunctionAndParameters extends NativeCFG {

	public GetFunctionAndParameters(CodeLocation location, CompilationUnit shimPackage) {
		super(new CFGDescriptor(location, shimPackage, true, "GetFunctionAndParameters",
				GoTypesTuple.lookup(new GoTypesTuple(new Parameter(location, "function", GoStringType.INSTANCE),
						new Parameter(location, "params", GoSliceType.lookup(new GoSliceType(GoStringType.INSTANCE))))),
				new Parameter(location, "this", ChaincodeStub.INSTANCE)),
				GetFunctionAndParametersImpl.class);
	}

	public static class GetFunctionAndParametersImpl extends UnaryExpression
	implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static GetFunctionAndParametersImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new GetFunctionAndParametersImpl(cfg, location, params[0]);
		}

		public GetFunctionAndParametersImpl(CFG cfg, CodeLocation location, Expression e) {
			super(cfg, location, "GetFunctionAndParametersImpl", GoTypesTuple.lookup(new GoTypesTuple(
					new Parameter(location, "function", GoStringType.INSTANCE),
					new Parameter(location, "params", GoSliceType.lookup(new GoSliceType(GoStringType.INSTANCE))))), e);
		}

		@Override
		protected <A extends AbstractState<A, H, V, T>, H extends HeapDomain<H>, V extends ValueDomain<V>, T extends TypeDomain<T>> AnalysisState<A, H, V, T> unarySemantics(
				InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
				SymbolicExpression expr, StatementStore<A, H, V, T> expressions) throws SemanticException {
			return state.smallStepSemantics(new Clean(getLocation()), original);
		}
	}
}
