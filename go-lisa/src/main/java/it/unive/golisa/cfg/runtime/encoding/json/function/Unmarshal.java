package it.unive.golisa.cfg.runtime.encoding.json.function;

import java.util.Map;

import it.unive.golisa.analysis.taint.TaintDomainForPrivacyHF;
import it.unive.golisa.cfg.type.GoNilType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoInterfaceType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt8Type;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.nonrelational.value.NonRelationalValueDomain;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
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
import it.unive.lisa.symbolic.heap.HeapReference;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.MemoryPointer;
import it.unive.lisa.symbolic.value.PushAny;

/**
 * func Unmarshal(data []byte, v interface{}) error.
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class Unmarshal extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param jsonUnit the unit to which this native cfg belongs to
	 */
	public Unmarshal(CodeLocation location, CodeUnit jsonUnit) {
		super(new CodeMemberDescriptor(location, jsonUnit, false, "Unmarshal", GoErrorType.INSTANCE,
				new Parameter(location, "data", GoSliceType.lookup(GoUInt8Type.INSTANCE)),
				new Parameter(location, "v", GoInterfaceType.getEmptyInterface())),
				UnmarshalImpl.class);
	}

	/**
	 * The {@link Unmarshal} implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class UnmarshalImpl extends BinaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		@Override
		protected int compareSameClassAndParams(Statement o) {
			return 0; // nothing else to compare
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
		public static UnmarshalImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new UnmarshalImpl(cfg, location, params[0], params[1]);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param left     the left-hand side of this expression
		 * @param right    the right-hand side of this expression
		 */
		public UnmarshalImpl(CFG cfg, CodeLocation location, Expression left, Expression right) {
			super(cfg, location, "UnmarshalImpl", GoErrorType.INSTANCE, left, right);
		}

		@Override
		public <A extends AbstractState<A>> AnalysisState<A> fwdBinarySemantics(
				InterproceduralAnalysis<A> interprocedural, AnalysisState<A> state,
				SymbolicExpression left, SymbolicExpression right, StatementStore<A> expressions)
				throws SemanticException {
			
			AnalysisState<A> ret = state;
			if(left instanceof Identifier && (right instanceof Identifier || (right instanceof HeapReference && ((HeapReference) right).getExpression() instanceof Identifier))) {
				Identifier tmp = right instanceof HeapReference ? (Identifier) ((HeapReference) right).getExpression() : (Identifier) right;

				
				if (state.getState() instanceof SimpleAbstractState<?, ?, ?>) {
					SimpleAbstractState<?, ?, ?> simpleState = (SimpleAbstractState<?, ?, ?>) ret.getState();
					if (simpleState.getValueState() instanceof ValueEnvironment<?>) {
						ValueEnvironment<?> valueEnv = (ValueEnvironment<?>) simpleState.getValueState();
						Map<Identifier, Object> map = (Map<Identifier, Object>) valueEnv.getMap();
						map.put(tmp, map.get(left));
						
						for(SymbolicExpression exp : simpleState.reachableFrom(tmp, original, simpleState)) {
							if(exp instanceof Identifier && !(exp instanceof MemoryPointer))
								map.put((Identifier)exp, map.get(left));
						}

					}
				}
			}
			
			AnalysisState<A> errorValue = ret.lub(state
					.smallStepSemantics(new PushAny(GoErrorType.INSTANCE, getLocation()), original));
			AnalysisState<A> nilValue = state
					.smallStepSemantics(new Constant(GoNilType.INSTANCE, "nil", getLocation()), original);
			return errorValue.lub(nilValue);
		}
	}

}
