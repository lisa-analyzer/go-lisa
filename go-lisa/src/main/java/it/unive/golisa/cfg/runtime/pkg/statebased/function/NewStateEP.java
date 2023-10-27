package it.unive.golisa.cfg.runtime.pkg.statebased.function;

import it.unive.golisa.cfg.expression.literal.GoTupleExpression;
import it.unive.golisa.cfg.runtime.pkg.statebased.type.KeyEndorsementPolicy;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.composite.GoTupleType;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt8Type;
import it.unive.golisa.checker.TaintChecker.HeapResolver;
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
import it.unive.lisa.type.Untyped;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * func NewStateEP(policy []byte) (KeyEndorsementPolicy, error).
 * 
 * @link https://pkg.go.dev/github.com/hyperledger/fabric-chaincode-go/pkg/statebased#NewStateEP
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class NewStateEP extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location          the location where this native cfg is defined
	 * @param statebasedPackage the unit to which this native cfg belongs to
	 */
	public NewStateEP(CodeLocation location, CodeUnit statebasedPackage) {
		super(new CodeMemberDescriptor(location, statebasedPackage, false, "NewStateEP",
				GoTupleType.getTupleTypeOf(location,
						KeyEndorsementPolicy.getKeyEndorsementPolicyType(statebasedPackage.getProgram()),
						GoErrorType.INSTANCE),
				new Parameter(location, "policy", GoSliceType.lookup(GoUInt8Type.INSTANCE))),
				NewStateEPImpl.class);
	}

	/**
	 * The NewStateEP implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class NewStateEPImpl extends it.unive.lisa.program.cfg.statement.UnaryExpression
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
		public static NewStateEPImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new NewStateEPImpl(cfg, location, params[0]);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param expr     the expression
		 */
		public NewStateEPImpl(CFG cfg, CodeLocation location, Expression expr) {
			super(cfg, location, "NewStateEPImpl",
					GoTupleType.getTupleTypeOf(location, KeyEndorsementPolicy.getKeyEndorsementPolicyType(null),
							GoErrorType.INSTANCE),
					expr);
		}

		@Override
		public <A extends AbstractState<A>> AnalysisState<A> fwdUnarySemantics(
				InterproceduralAnalysis<A> interprocedural, AnalysisState<A> state,
				SymbolicExpression expr, StatementStore<A> expressions) throws SemanticException {

			Type kepType = KeyEndorsementPolicy.getKeyEndorsementPolicyType(getProgram());
			GoTupleType tupleType = GoTupleType.getTupleTypeOf(getLocation(),
					new ReferenceType(kepType), GoErrorType.INSTANCE);

			// Allocates the new heap allocation
			MemoryAllocation created = new MemoryAllocation(kepType, expr.getCodeLocation(), new Annotations(), true);
			AnalysisState<A> allocState = state.smallStepSemantics(created, this);

			AnalysisState<A> result = state.bottom();
			for (SymbolicExpression allocId : allocState.getComputedExpressions()) {
				HeapReference ref = new HeapReference(new ReferenceType(kepType), allocId, expr.getCodeLocation());
				HeapDereference deref = new HeapDereference(kepType, ref, expr.getCodeLocation());
				AnalysisState<A> asg = allocState.bottom();

				// Retrieves all the identifiers reachable from expr
				Collection<SymbolicExpression> reachableIds = HeapResolver.resolve(allocState, expr, this);
				for (SymbolicExpression id : reachableIds) {
					HeapDereference derefId = new HeapDereference(Untyped.INSTANCE, id, expr.getCodeLocation());
					it.unive.lisa.symbolic.value.UnaryExpression left = new UnaryExpression(Untyped.INSTANCE, derefId,
							KeyEndorsementPolicyFirstParameter.INSTANCE, getLocation());
					asg = asg.lub(allocState.assign(deref, left, original));
				}

				UnaryExpression rightRes = new UnaryExpression(GoErrorType.INSTANCE, expr,
						KeyEndorsementPolicySecondParameter.INSTANCE, getLocation());

				result = result.lub(GoTupleExpression.allocateTupleExpression(asg, new Annotations(), this,
						getLocation(), tupleType,
						ref,
						rightRes));
			}

			return result;
		}
	}

	public static class KeyEndorsementPolicyFirstParameter implements UnaryOperator {

		/**
		 * The singleton instance of this class.
		 */
		public static final KeyEndorsementPolicyFirstParameter INSTANCE = new KeyEndorsementPolicyFirstParameter();

		/**
		 * Builds the operator. This constructor is visible to allow
		 * subclassing: instances of this class should be unique, and the
		 * singleton can be retrieved through field {@link #INSTANCE}.
		 */
		protected KeyEndorsementPolicyFirstParameter() {
		}

		@Override
		public String toString() {
			return "KeyEndorsementPolicy_first";
		}

		@Override
		public Set<Type> typeInference(TypeSystem types, Set<Type> argument) {
			return Collections.singleton(Untyped.INSTANCE);
		}
	}

	public static class KeyEndorsementPolicySecondParameter implements UnaryOperator {

		/**
		 * The singleton instance of this class.
		 */
		public static final KeyEndorsementPolicySecondParameter INSTANCE = new KeyEndorsementPolicySecondParameter();

		/**
		 * Builds the operator. This constructor is visible to allow
		 * subclassing: instances of this class should be unique, and the
		 * singleton can be retrieved through field {@link #INSTANCE}.
		 */
		protected KeyEndorsementPolicySecondParameter() {
		}

		@Override
		public String toString() {
			return "CreateCompositeKey_second";
		}

		@Override
		public Set<Type> typeInference(TypeSystem types, Set<Type> argument) {
			return Collections.singleton(GoErrorType.INSTANCE);
		}
	}
}
