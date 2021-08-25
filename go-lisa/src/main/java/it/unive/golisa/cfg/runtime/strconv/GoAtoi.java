package it.unive.golisa.cfg.runtime.strconv;

import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.caches.Caches;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CFGDescriptor;
import it.unive.lisa.program.cfg.NativeCFG;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.UnaryNativeCall;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.PushAny;

public class GoAtoi extends NativeCFG {

	public GoAtoi(SourceCodeLocation location, CompilationUnit strconvUnit) {
		super(new CFGDescriptor(location, strconvUnit, false, "Atoi", GoIntType.INSTANCE,
				new Parameter(location, "this", GoStringType.INSTANCE)),
				Atoi.class);
	}
	
	public static class Atoi extends UnaryNativeCall implements PluggableStatement {
		
		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}
		
		public Atoi(CFG cfg, SourceCodeLocation location, Expression exp1) {
			super(cfg, location, "Atoi", GoStringType.INSTANCE, exp1);
		}

		@Override
		protected <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> unarySemantics(
				AnalysisState<A, H, V> entryState, InterproceduralAnalysis<A, H, V> interprocedural,
				AnalysisState<A, H, V> exprState, SymbolicExpression expr) throws SemanticException {
			if (!expr.getDynamicType().isStringType() && !expr.getDynamicType().isUntyped())
				return entryState.bottom();
			
			return exprState.smallStepSemantics(new PushAny(Caches.types().mkSingletonSet(GoIntType.INSTANCE), getLocation()), original);

		}
	}
}

