package it.unive.golisa.cfg.runtime.encoding.json.function;

import it.unive.golisa.cfg.runtime.bytes.type.Buffer;
import it.unive.golisa.cfg.type.GoNilType;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoPointerType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt8Type;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.lattices.ExpressionSet;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.CodeUnit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.NativeCFG;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.NaryExpression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.PushAny;

/**
 * func Indent(dst *bytes.Buffer, src []byte, prefix, indent string) error.
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class Indent extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param jsonUnit the unit to which this native cfg belongs to
	 */
	public Indent(CodeLocation location, CodeUnit jsonUnit) {
		super(new CodeMemberDescriptor(location, jsonUnit, false, "Indent", GoErrorType.INSTANCE,
				new Parameter(location, "dst", GoPointerType.lookup(Buffer.getBufferType(jsonUnit.getProgram()))),
				new Parameter(location, "src", GoSliceType.lookup(GoSliceType.lookup(GoUInt8Type.INSTANCE))),
				new Parameter(location, "prefix", GoStringType.INSTANCE),
				new Parameter(location, "indent", GoStringType.INSTANCE)),
				IndentImpl.class);
	}

	/**
	 * The {@link Indent} implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class IndentImpl extends NaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param params   the parameters
		 * 
		 * @return the pluggable statement
		 */
		public static IndentImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new IndentImpl(cfg, location, params);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param params   the parameters
		 */
		public IndentImpl(CFG cfg, CodeLocation location, Expression... params) {
			super(cfg, location, "IndentImpl", GoErrorType.INSTANCE, params);
		}

		@Override
		public <A extends AbstractState<A, H, V, T>,
				H extends HeapDomain<H>,
				V extends ValueDomain<V>,
				T extends TypeDomain<T>> AnalysisState<A, H, V, T> expressionSemantics(
						InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
						ExpressionSet<SymbolicExpression>[] params, StatementStore<A, H, V, T> expressions)
						throws SemanticException {
			AnalysisState<A, H, V, T> errorValue = state
					.smallStepSemantics(new PushAny(GoErrorType.INSTANCE, getLocation()), original);
			AnalysisState<A, H, V, T> nilValue = state
					.smallStepSemantics(new Constant(GoNilType.INSTANCE, "nil", getLocation()), original);
			return errorValue.lub(nilValue);
		}
	}
}
