package it.unive.golisa.cfg.runtime.shim.method;

import it.unive.golisa.analysis.taint.Clean;
import it.unive.golisa.cfg.runtime.shim.type.ChaincodeStub;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.composite.GoTupleType;
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
import it.unive.lisa.program.CompilationUnit;
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

/**
 * func (s *ChaincodeStub) GetPrivateData(collection string, key string)
 * ([]byte, error).
 * https://pkg.go.dev/github.com/hyperledger/fabric-chaincode-go/shim#ChaincodeStub.GetPrivateData
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class GetPrivateData extends NativeCFG {

	/**
	 * Builds the native cfg. func (s *ChaincodeStub) GetPrivateData(collection
	 * string, key string) ([]byte, error)
	 * 
	 * @param location the location where this native cfg is defined
	 * @param shimUnit the unit to which this native cfg belongs to
	 */
	public GetPrivateData(CodeLocation location, CompilationUnit shimUnit) {
		super(new CodeMemberDescriptor(location, shimUnit, true, "GetPrivateData",
				GoTupleType.getTupleTypeOf(location, GoSliceType.lookup(GoUInt8Type.INSTANCE),
						GoErrorType.INSTANCE),
				new Parameter(location, "s", ChaincodeStub.getChaincodeStubType(shimUnit.getProgram())),
				new Parameter(location, "collection", GoStringType.INSTANCE),
				new Parameter(location, "key", GoStringType.INSTANCE)),
				GetPrivateDataImpl.class);
	}

	/**
	 * The {@link GetPrivateData} implementation.
	 */
	public static class GetPrivateDataImpl extends NaryExpression
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
		public static GetPrivateDataImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new GetPrivateDataImpl(cfg, location, params);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param params   the parameters
		 */
		public GetPrivateDataImpl(CFG cfg, CodeLocation location, Expression... params) {
			super(cfg, location, "GetPrivateDataImpl",
					GoTupleType.getTupleTypeOf(location, GoSliceType.lookup(GoUInt8Type.INSTANCE),
							GoErrorType.INSTANCE),
					params);
		}

		@Override
		public <A extends AbstractState<A, H, V, T>,
				H extends HeapDomain<H>,
				V extends ValueDomain<V>,
				T extends TypeDomain<T>> AnalysisState<A, H, V, T> expressionSemantics(
						InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
						ExpressionSet<SymbolicExpression>[] params, StatementStore<A, H, V, T> expressions)
						throws SemanticException {
			return state.smallStepSemantics(
					new Clean(GoTupleType.getTupleTypeOf(getLocation(), GoSliceType.lookup(GoUInt8Type.INSTANCE),
							GoErrorType.INSTANCE), getLocation()),
					original);
		}
	}
}