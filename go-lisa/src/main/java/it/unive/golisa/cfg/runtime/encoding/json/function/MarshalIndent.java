package it.unive.golisa.cfg.runtime.encoding.json.function;

import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoInterfaceType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.composite.GoTypesTuple;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt8Type;
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
 * func MarshalIndent(v interface{}, prefix, indent string) ([]byte, error)
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class MarshalIndent extends NativeCFG {

	public MarshalIndent(CodeLocation location, CompilationUnit jsonUnit) {
		super(new CFGDescriptor(location, jsonUnit, false, "MarshalIndent",
				GoTypesTuple.getTupleTypeOf(location, GoSliceType.lookup(new GoSliceType(GoUInt8Type.INSTANCE)),
						GoErrorType.INSTANCE),
				new Parameter(location, "v", GoInterfaceType.getEmptyInterface()),
				new Parameter(location, "prefix", GoStringType.INSTANCE),
				new Parameter(location, "indent", GoStringType.INSTANCE)),
				MarshalIndentImpl.class);
	}

	public static class MarshalIndentImpl extends NaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static MarshalIndentImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new MarshalIndentImpl(cfg, location, params);
		}

		public MarshalIndentImpl(CFG cfg, CodeLocation location, Expression... params) {
			super(cfg, location, "MarshalIndentImpl",
					GoTypesTuple.getTupleTypeOf(location, GoSliceType.lookup(new GoSliceType(GoUInt8Type.INSTANCE)),
							GoErrorType.INSTANCE),
					params);
		}

		@Override
		public <A extends AbstractState<A, H, V>,
				H extends HeapDomain<H>,
				V extends ValueDomain<V>> AnalysisState<A, H, V> expressionSemantics(
						InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> state,
						ExpressionSet<SymbolicExpression>[] params, StatementStore<A, H, V> expressions)
						throws SemanticException {
			return state.top();
		}
	}
}