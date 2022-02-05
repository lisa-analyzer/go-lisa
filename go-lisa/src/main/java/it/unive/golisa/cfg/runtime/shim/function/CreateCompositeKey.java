package it.unive.golisa.cfg.runtime.shim.function;

import it.unive.golisa.cfg.runtime.shim.type.ChaincodeStub;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
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
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.NaryExpression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;

/**
 * func (s *ChaincodeStub) CreateCompositeKey(objectType string, attributes []string) (string, error)
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class CreateCompositeKey extends NativeCFG {

	public CreateCompositeKey(CodeLocation location, CompilationUnit shimUnit) {
		super(new CFGDescriptor(location, shimUnit, true, "CreateCompositeKey",
				GoTypesTuple.getTupleTypeOf(location,GoStringType.INSTANCE,
						GoErrorType.INSTANCE),
				new Parameter(location, "s", ChaincodeStub.INSTANCE),
				new Parameter(location, "objectType", GoStringType.INSTANCE),
				new Parameter(location, "attributes", GoSliceType.lookup(new GoSliceType(GoStringType.INSTANCE)))),
				CreateCompositeKeyImpl.class);
	}

	public static class CreateCompositeKeyImpl extends NaryExpression
	implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static CreateCompositeKeyImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new CreateCompositeKeyImpl(cfg, location, params);
		}

		public CreateCompositeKeyImpl(CFG cfg, CodeLocation location, Expression... params) {
			super(cfg, location, "CreateCompositeKeyImpl", 
					GoTypesTuple.getTupleTypeOf(location, GoStringType.INSTANCE,
							GoErrorType.INSTANCE), params);
		}

		@Override
		public <A extends AbstractState<A, H, V>,
		H extends HeapDomain<H>,
		V extends ValueDomain<V>> AnalysisState<A, H, V> expressionSemantics(
				InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> state,
				ExpressionSet<SymbolicExpression>[] params, StatementStore<A, H, V> expressions)
						throws SemanticException {
			AnalysisState<A, H, V> result = state.bottom();
			for (ExpressionSet<SymbolicExpression> s : params)
				for (SymbolicExpression s1 : s)
					result = result.lub(state.smallStepSemantics(s1, original));

			return result;
		}
	}
}
