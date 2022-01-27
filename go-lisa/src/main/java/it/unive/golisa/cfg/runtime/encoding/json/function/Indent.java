package it.unive.golisa.cfg.runtime.encoding.json.function;

import it.unive.golisa.cfg.runtime.bytes.type.Buffer;
import it.unive.golisa.cfg.type.GoByteType;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoPointerType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
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
 * func Indent(dst *bytes.Buffer, src []byte, prefix, indent string) error
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class Indent extends NativeCFG {

	public Indent(CodeLocation location, CompilationUnit jsonUnit) {
		super(new CFGDescriptor(location, jsonUnit, false, "Indent", GoErrorType.INSTANCE,
				new Parameter(location, "dst", new GoPointerType(Buffer.INSTANCE)),
				new Parameter(location, "src", GoSliceType.lookup(new GoSliceType(GoByteType.INSTANCE))),
				new Parameter(location, "prefix", GoStringType.INSTANCE),
				new Parameter(location, "indent", GoStringType.INSTANCE)),
				IndentImpl.class);
	}

	public static class IndentImpl extends NaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static IndentImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new IndentImpl(cfg, location, params);
		}

		public IndentImpl(CFG cfg, CodeLocation location, Expression... params) {
			super(cfg, location, "IndentImpl", GoErrorType.INSTANCE, params);
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
