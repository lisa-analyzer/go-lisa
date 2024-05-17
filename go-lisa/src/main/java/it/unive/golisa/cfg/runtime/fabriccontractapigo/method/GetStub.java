package it.unive.golisa.cfg.runtime.fabriccontractapigo.method;

import it.unive.golisa.cfg.runtime.fabriccontractapigo.type.TransactionContext;
import it.unive.golisa.cfg.runtime.shim.type.ChaincodeStub;
import it.unive.golisa.cfg.runtime.shim.type.ChaincodeStubInterface;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.NativeCFG;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.UnaryExpression;
import it.unive.lisa.symbolic.value.operator.unary.UnaryOperator;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;
import java.util.HashSet;
import java.util.Set;

/**
 * func (ctx *TransactionContext) GetStub() shim.ChaincodeStubInterface.
 * https://pkg.go.dev/github.com/hyperledger/fabric-contract-api-go/contractapi#TransactionContext.GetStub
 * 
 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
 */
public class GetStub extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param unit the unit to which this native cfg belongs to
	 */
	public GetStub(CodeLocation location, CompilationUnit unit) {
		super(new CodeMemberDescriptor(location, unit, true, "GetStub",
				ChaincodeStubInterface.getChainCodeStubInterfaceType(unit.getProgram()),
				new Parameter(location, "ctx", TransactionContext.getTransactionContextType(unit.getProgram()))),
				GetStubImpl.class);
	}

	/**
	 * The {@link GetStub} implementation.
	 * 
	 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
	 */
	public static class GetStubImpl extends it.unive.lisa.program.cfg.statement.UnaryExpression
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
		public static GetStubImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new GetStubImpl(cfg, location, params[0]);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param left     the left argument
		 * @param right    the right argument
		 */
		public GetStubImpl(CFG cfg, CodeLocation location, Expression par) {
			super(cfg, location, "GetStubImpl", ChaincodeStubInterface.getChainCodeStubInterfaceType(par.getProgram()), par);
		}


		@Override
		public <A extends AbstractState<A>> AnalysisState<A> fwdUnarySemantics(
				InterproceduralAnalysis<A> interprocedural, AnalysisState<A> state, SymbolicExpression expr,
				StatementStore<A> expressions) throws SemanticException {
			return state.smallStepSemantics(new UnaryExpression(getStaticType(), expr, new UnaryOperator() {
				
				@Override
				public Set<Type> typeInference(TypeSystem types, Set<Type> argument) {
					Set<Type> res = new HashSet<>();
					res.add(ChaincodeStub.getChaincodeStubType(getProgram()));
					res.add(ChaincodeStubInterface.getChainCodeStubInterfaceType(getProgram()));
					return res;
				}
			}, getLocation()), original);
		}
	}
}
