package it.unive.golisa.cfg.runtime.strconv;

import it.unive.golisa.analysis.taint.Clean;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoTupleType;
import it.unive.golisa.cfg.type.numeric.floating.GoFloat64Type;
import it.unive.golisa.cfg.type.numeric.signed.GoInt64Type;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.CodeUnit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.NativeCFG;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.BinaryExpression;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;

/**
 * func ParseFloat(s string, bitSize int) (float64, error) *
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class ParseFloat extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location    the location where this native cfg is defined
	 * @param strconvUnit the unit to which this native cfg belongs to
	 */
	public ParseFloat(CodeLocation location, CodeUnit strconvUnit) {
		super(new CodeMemberDescriptor(location, strconvUnit, false, "ParseFloat", 
				GoTupleType.getTupleTypeOf(location, GoFloat64Type.INSTANCE, GoErrorType.INSTANCE),
				new Parameter(location, "s", GoStringType.INSTANCE),
				new Parameter(location, "bitSize", GoIntType.INSTANCE)),
				ParseFloatImpl.class);
	}

	/**
	 * The ParseFloat implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class ParseFloatImpl extends BinaryExpression implements PluggableStatement {

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
		public static ParseFloatImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new ParseFloatImpl(cfg, location, params[0], params[1]);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param expr     the expression
		 */
		public ParseFloatImpl(CFG cfg, CodeLocation location, Expression left, Expression right) {
			super(cfg, location, "ParseFloat", GoTupleType.getTupleTypeOf(location, GoFloat64Type.INSTANCE, GoErrorType.INSTANCE), left, right);
		}

		@Override
		public <A extends AbstractState<A, H, V, T>, H extends HeapDomain<H>, V extends ValueDomain<V>, T extends TypeDomain<T>> AnalysisState<A, H, V, T> binarySemantics(
				InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
				SymbolicExpression left, SymbolicExpression right, StatementStore<A, H, V, T> expressions)
				throws SemanticException {
			return state.smallStepSemantics(new Clean(GoInt64Type.INSTANCE, getLocation()), original);
		}
	}
}