package it.unive.golisa.cfg.runtime.shim.method;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import it.unive.golisa.cfg.expression.literal.GoTupleExpression;
import it.unive.golisa.cfg.runtime.shim.type.ChaincodeStub;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.composite.GoTupleType;
import it.unive.golisa.checker.TaintChecker.HeapResolver;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.CompilationUnit;
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
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.MemoryPointer;
import it.unive.lisa.symbolic.value.TernaryExpression;
import it.unive.lisa.symbolic.value.operator.ternary.TernaryOperator;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;

/**
 * func (s *ChaincodeStub) CreateCompositeKey(objectType string, attributes
 * []string) (string, error)
 * 
 * @link https://pkg.go.dev/github.com/hyperledger/fabric-chaincode-go/shim#CreateCompositeKey
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class CreateCompositeKey extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param shimUnit the unit to which this native cfg belongs to
	 */
	public CreateCompositeKey(CodeLocation location, CompilationUnit shimUnit) {
		super(new CodeMemberDescriptor(location, shimUnit, true, "CreateCompositeKey",
				GoTupleType.getTupleTypeOf(location, GoStringType.INSTANCE,
						GoErrorType.INSTANCE),
				new Parameter(location, "this", ChaincodeStub.getChaincodeStubType(shimUnit.getProgram())),
				new Parameter(location, "objectType", GoStringType.INSTANCE),
				new Parameter(location, "attributes", GoSliceType.lookup(GoStringType.INSTANCE))),
				CreateCompositeKeyImpl.class);
	}

	/**
	 * The CreateCompositeKey implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class CreateCompositeKeyImpl extends it.unive.lisa.program.cfg.statement.TernaryExpression
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
		public static CreateCompositeKeyImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new CreateCompositeKeyImpl(cfg, location, params[0], params[1], params[2]);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param params   the parameters
		 */
		public CreateCompositeKeyImpl(CFG cfg, CodeLocation location, Expression... params) {
			super(cfg, location, "CreateCompositeKeyImpl",
					GoTupleType.getTupleTypeOf(location, GoStringType.INSTANCE,
							GoErrorType.INSTANCE),
					params[0], params[1], params[2]);
		}

		@Override
		public <A extends AbstractState<A>> AnalysisState<A> fwdTernarySemantics(
				InterproceduralAnalysis<A> interprocedural, AnalysisState<A> state,
				SymbolicExpression left, SymbolicExpression middle, SymbolicExpression right,
				StatementStore<A> expressions) throws SemanticException {
			GoTupleType tupleType = GoTupleType.getTupleTypeOf(getLocation(), GoStringType.INSTANCE,
					GoErrorType.INSTANCE);
			AnalysisState<A> result = state.bottom();
			// Retrieves all the identifiers reachable from expr
			Collection<SymbolicExpression> reachableIds = HeapResolver.resolve(state, left, this);
			for (SymbolicExpression id : reachableIds) {
				if (id instanceof MemoryPointer)
					continue;
				Set<Type> idTypes = state.getState().getRuntimeTypesOf(id, this, state.getState());
				for (Type t : idTypes) {
					if (t.isPointerType()) {
						HeapDereference derefId = new HeapDereference(t.asPointerType().getInnerType(), id,
								getLocation());
						TernaryExpression leftExp = new TernaryExpression(GoStringType.INSTANCE,
								new Constant(getStaticType(), 1, getLocation()), middle,
								new Constant(getStaticType(), 1, getLocation()),
								CreateCompositeKeyFirstParameter.INSTANCE, getLocation());
						TernaryExpression rightExp = new TernaryExpression(GoErrorType.INSTANCE,
								new Constant(getStaticType(), 1, getLocation()), middle,
								new Constant(getStaticType(), 1, getLocation()),
								CreateCompositeKeySecondParameter.INSTANCE, getLocation());
						AnalysisState<A> tupleState = GoTupleExpression.allocateTupleExpression(state,
								new Annotations(), original, getLocation(), tupleType,
								leftExp,
								rightExp);

						result = result.lub(tupleState);
					} else {
						TernaryExpression leftExp = new TernaryExpression(GoStringType.INSTANCE,
								new Constant(getStaticType(), 1, getLocation()), middle,
								new Constant(getStaticType(), 1, getLocation()),
								CreateCompositeKeyFirstParameter.INSTANCE, getLocation());
						TernaryExpression rightExp = new TernaryExpression(GoErrorType.INSTANCE,
								new Constant(getStaticType(), 1, getLocation()), middle,
								new Constant(getStaticType(), 1, getLocation()),
								CreateCompositeKeySecondParameter.INSTANCE, getLocation());
						AnalysisState<A> tupleState = GoTupleExpression.allocateTupleExpression(state,
								new Annotations(), original, getLocation(), tupleType,
								leftExp,
								rightExp);

						result = result.lub(tupleState);
					}
				}
			}

			return result;
		}
	}

	public static class CreateCompositeKeyFirstParameter implements TernaryOperator {

		/**
		 * The singleton instance of this class.
		 */
		public static final CreateCompositeKeyFirstParameter INSTANCE = new CreateCompositeKeyFirstParameter();

		/**
		 * Builds the operator. This constructor is visible to allow
		 * subclassing: instances of this class should be unique, and the
		 * singleton can be retrieved through field {@link #INSTANCE}.
		 */
		protected CreateCompositeKeyFirstParameter() {
		}

		@Override
		public String toString() {
			return "CreateCompositeKey_first";
		}

		@Override
		public Set<Type> typeInference(TypeSystem types, Set<Type> left, Set<Type> middle, Set<Type> right) {
			return Collections.singleton(GoStringType.INSTANCE);
		}
	}

	public static class CreateCompositeKeySecondParameter implements TernaryOperator {

		/**
		 * The singleton instance of this class.
		 */
		public static final CreateCompositeKeySecondParameter INSTANCE = new CreateCompositeKeySecondParameter();

		/**
		 * Builds the operator. This constructor is visible to allow
		 * subclassing: instances of this class should be unique, and the
		 * singleton can be retrieved through field {@link #INSTANCE}.
		 */
		protected CreateCompositeKeySecondParameter() {
		}

		@Override
		public String toString() {
			return "CreateCompositeKey_second";
		}

		@Override
		public Set<Type> typeInference(TypeSystem types, Set<Type> left, Set<Type> middle, Set<Type> right) {
			return Collections.singleton(GoErrorType.INSTANCE);
		}
	}
}
