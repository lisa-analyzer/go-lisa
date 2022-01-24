package it.unive.golisa.cfg.runtime.time.function;

import it.unive.golisa.cfg.runtime.time.type.Time;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoTypesTuple;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
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

public class Parse extends NativeCFG {

	public Parse(CodeLocation location, CompilationUnit timeUnit) {
		super(new CFGDescriptor(location, timeUnit, false, timeUnit.getName() + ".Parse",
				new GoTypesTuple(new Parameter(location, "_", Time.INSTANCE),
						new Parameter(location, "_", GoErrorType.INSTANCE)),
				new Parameter(location, "layout", GoStringType.INSTANCE),
				new Parameter(location, "value", GoStringType.INSTANCE)),
				ParseImpl.class);
	}

	public static class ParseImpl extends BinaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static ParseImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new ParseImpl(cfg, location, params[0], params[1]);
		}

		public ParseImpl(CFG cfg, CodeLocation location, Expression left, Expression right) {
			super(cfg, location, "ParseImpl", new GoTypesTuple(new Parameter(location, "_", Time.INSTANCE),
					new Parameter(location, "_", GoErrorType.INSTANCE)), left, right);
		}

		@Override
		protected <A extends AbstractState<A, H, V>,
				H extends HeapDomain<H>,
				V extends ValueDomain<V>> AnalysisState<A, H, V> binarySemantics(
						InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> state,
						SymbolicExpression left,
						SymbolicExpression right) throws SemanticException {
//			GoExpressionsTuple tuple = new GoExpressionsTuple(getCFG(), original.getLocation(), 
//					new PushAny(Caches.types().mkSingletonSet(Time.INSTANCE), original.getLocation()));
			return state.smallStepSemantics(
					new PushAny(Caches.types()
							.mkSingletonSet(new GoTypesTuple(new Parameter(original.getLocation(), "_", Time.INSTANCE),
									new Parameter(original.getLocation(), "_", GoErrorType.INSTANCE))),
							original.getLocation()),
					original);
		}
	}
}
