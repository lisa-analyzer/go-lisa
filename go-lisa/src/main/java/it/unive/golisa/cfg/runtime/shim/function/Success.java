package it.unive.golisa.cfg.runtime.shim.function;

import java.util.Collections;
import java.util.Set;

import it.unive.golisa.cfg.runtime.peer.type.Response;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt8Type;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.CodeUnit;
import it.unive.lisa.program.annotations.Annotations;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.NativeCFG;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.HeapDereference;
import it.unive.lisa.symbolic.heap.HeapReference;
import it.unive.lisa.symbolic.heap.MemoryAllocation;
import it.unive.lisa.symbolic.value.UnaryExpression;
import it.unive.lisa.symbolic.value.operator.unary.UnaryOperator;
import it.unive.lisa.type.ReferenceType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;

/**
 * Success response chaincodes. func Success(payload []byte) pb.Response
 *
 * @link https://pkg.go.dev/github.com/hyperledger/fabric-chaincode-go/shim#Success
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class Success extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param shimUnit the unit to which this native cfg belongs to
	 */
	public Success(CodeLocation location, CodeUnit shimUnit) {
		super(new CodeMemberDescriptor(location, shimUnit, false, "Success",
				Response.getResponseType(shimUnit.getProgram()),
				new Parameter(location, "payload", GoSliceType.lookup(GoUInt8Type.INSTANCE))),
				SuccessImpl.class);
	}

	/**
	 * The Success implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class SuccessImpl extends it.unive.lisa.program.cfg.statement.UnaryExpression
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
		public static SuccessImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new SuccessImpl(cfg, location, params[0]);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param expr     the expression
		 */
		public SuccessImpl(CFG cfg, CodeLocation location, Expression expr) {
			super(cfg, location, "SuccessImpl", Response.getResponseType(null), expr);
		}

		@Override
		public <A extends AbstractState<A>> AnalysisState<A> fwdUnarySemantics(
				InterproceduralAnalysis<A> interprocedural, AnalysisState<A> state,
				SymbolicExpression expr, StatementStore<A> expressions) throws SemanticException {

			Response responseType = Response.getResponseType(null);

			// Allocates the new memory for a Time object
			MemoryAllocation alloc = new MemoryAllocation(responseType, getLocation(), new Annotations(), true);
			AnalysisState<A> allocState = state.smallStepSemantics(alloc, this);

			// Assigns an unknown object to each allocation identifier
			HeapReference ref = new HeapReference(new ReferenceType(responseType), alloc, getLocation());
			HeapDereference deref = new HeapDereference(responseType, ref, getLocation());
			UnaryExpression un = new UnaryExpression(GoSliceType.getSliceOfBytes(), expr, SuccessOperator.INSTANCE, getLocation());
			AnalysisState<A> asg = allocState.assign(deref, un, this);				
			return asg.smallStepSemantics(ref, original);
		}
	}

	public static class SuccessOperator implements UnaryOperator {

		/**
		 * The singleton instance of this class.
		 */
		public static final SuccessOperator INSTANCE = new SuccessOperator();

		/**
		 * Builds the operator. This constructor is visible to allow subclassing:
		 * instances of this class should be unique, and the singleton can be
		 * retrieved through field {@link #INSTANCE}.
		 */
		protected SuccessOperator() {
		}

		@Override
		public String toString() {
			return "SuccessOperator";
		}	

		@Override
		public Set<Type> typeInference(TypeSystem types, Set<Type> argument) {
			return Collections.singleton(GoSliceType.getSliceOfBytes());
		}
	}
}