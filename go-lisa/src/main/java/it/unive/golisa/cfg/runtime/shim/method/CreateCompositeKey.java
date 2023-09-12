package it.unive.golisa.cfg.runtime.shim.method;

import java.util.Collections;
import java.util.Set;

import it.unive.golisa.cfg.expression.literal.GoTupleExpression;
import it.unive.golisa.cfg.runtime.shim.type.ChaincodeStub;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.composite.GoTupleType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
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
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;

/**
 * func CreateCompositeKey(objectType string, attributes []string) (string,
 * error).
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
	public static class CreateCompositeKeyImpl extends it.unive.lisa.program.cfg.statement.BinaryExpression
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
			return new CreateCompositeKeyImpl(cfg, location, params);
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
					params[0], params[1]);
		}

	

		@Override
		public <A extends AbstractState<A, H, V, T>, H extends HeapDomain<H>, V extends ValueDomain<V>, T extends TypeDomain<T>> AnalysisState<A, H, V, T> binarySemantics(
				InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
				SymbolicExpression left, SymbolicExpression right, StatementStore<A, H, V, T> expressions)
				throws SemanticException {
			GoTupleType tupleType = GoTupleType.getTupleTypeOf(getLocation(), GoStringType.INSTANCE,
					GoErrorType.INSTANCE);
			BinaryExpression leftExp = new BinaryExpression(GoStringType.INSTANCE, left, right, CreateCompositeKeyFirstParameter.INSTANCE, getLocation());
			BinaryExpression rightExp = new BinaryExpression(GoErrorType.INSTANCE, left, right, CreateCompositeKeySecondParameter.INSTANCE, getLocation());
			return GoTupleExpression.allocateTupleExpression(state, new Annotations(), original, getLocation(), tupleType, 
					leftExp,
					rightExp);
		}
	}

	public static class CreateCompositeKeyFirstParameter implements BinaryOperator {

		/**
		 * The singleton instance of this class.
		 */
		public static final CreateCompositeKeyFirstParameter INSTANCE = new CreateCompositeKeyFirstParameter();

		/**
		 * Builds the operator. This constructor is visible to allow subclassing:
		 * instances of this class should be unique, and the singleton can be
		 * retrieved through field {@link #INSTANCE}.
		 */
		protected CreateCompositeKeyFirstParameter() {
		}

		@Override
		public String toString() {
			return "CreateCompositeKey_first";
		}

		@Override
		public Set<Type> typeInference(TypeSystem types, Set<Type> left, Set<Type> right) {
			return Collections.singleton(GoStringType.INSTANCE);
		}
	}

	public static class CreateCompositeKeySecondParameter implements BinaryOperator {

		/**
		 * The singleton instance of this class.
		 */
		public static final CreateCompositeKeySecondParameter INSTANCE = new CreateCompositeKeySecondParameter();

		/**
		 * Builds the operator. This constructor is visible to allow subclassing:
		 * instances of this class should be unique, and the singleton can be
		 * retrieved through field {@link #INSTANCE}.
		 */
		protected CreateCompositeKeySecondParameter() {
		}

		@Override
		public String toString() {
			return "CreateCompositeKey_second";
		}

		@Override
		public Set<Type> typeInference(TypeSystem types, Set<Type> left, Set<Type> right) {
			return Collections.singleton(GoErrorType.INSTANCE);
		}
	}
}
