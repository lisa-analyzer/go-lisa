package it.unive.golisa.cfg.runtime.shim.method;

import it.unive.golisa.analysis.ni.IntegrityNIDomain;
import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.cfg.expression.literal.GoTupleExpression;
import it.unive.golisa.cfg.runtime.shim.type.ChaincodeStub;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.composite.GoTupleType;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
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
import it.unive.lisa.program.cfg.statement.UnaryExpression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.AccessChild;
import it.unive.lisa.symbolic.heap.HeapDereference;
import it.unive.lisa.symbolic.heap.HeapReference;
import it.unive.lisa.symbolic.heap.MemoryAllocation;
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.symbolic.value.Variable;
import it.unive.lisa.type.ReferenceType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;

/**
 * func (s *ChaincodeStub) GetFunctionAndParameters() (function string, params
 * []string).
 * 
 * @link https://pkg.go.dev/github.com/hyperledger/fabric-chaincode-go/shim#ChaincodeStub.GetFunctionAndParameters
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GetFunctionAndParameters extends NativeCFG {

	private final static Annotations anns = new Annotations(TaintDomain.CLEAN_ANNOTATION, IntegrityNIDomain.HIGH_ANNOTATION);

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param shimUnit the unit to which this native cfg belongs to
	 */
	public GetFunctionAndParameters(CodeLocation location, CompilationUnit shimUnit) {
		super(new CodeMemberDescriptor(location, shimUnit, true, "GetFunctionAndParameters",
				GoTupleType.lookup(new Parameter(location, "function", GoStringType.INSTANCE),
						new Parameter(location, "params", GoSliceType.lookup(GoStringType.INSTANCE))),
				anns,
				new Parameter(location, "this", ChaincodeStub.getChaincodeStubType(shimUnit.getProgram()))), GetFunctionAndParametersImpl.class);
	}

	/**
	 * The {@link GetFunctionAndParameters} implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class GetFunctionAndParametersImpl extends UnaryExpression
	implements PluggableStatement {

		@SuppressWarnings("unused")
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
		public static GetFunctionAndParametersImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new GetFunctionAndParametersImpl(cfg, location, params[0]);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param expr     the expression
		 */
		public GetFunctionAndParametersImpl(CFG cfg, CodeLocation location, Expression expr) {
			super(cfg, location, "GetFunctionAndParametersImpl", GoTupleType.lookup(
					new Parameter(location, "function", GoStringType.INSTANCE),
					new Parameter(location, "params", GoSliceType.getSliceOfStrings())),
					expr);
		}

		@Override
		public <A extends AbstractState<A, H, V, T>,
		H extends HeapDomain<H>,
		V extends ValueDomain<V>,
		T extends TypeDomain<T>> AnalysisState<A, H, V, T> unarySemantics(
				InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
				SymbolicExpression expr, StatementStore<A, H, V, T> expressions) throws SemanticException {
			Type sliceOfString = GoSliceType.getSliceOfStrings();
			GoTupleType tupleType = GoTupleType.getTupleTypeOf(getLocation(), GoStringType.INSTANCE, new ReferenceType(sliceOfString));

			// Allocates the new heap allocation
			MemoryAllocation created = new MemoryAllocation(sliceOfString, expr.getCodeLocation(), anns, true);
			AnalysisState<A, H, V, T> allocState = state.smallStepSemantics(created, this);
			
			AnalysisState<A, H, V, T> result = state.bottom();
			for (SymbolicExpression allocId : allocState.getComputedExpressions()) {
				HeapReference ref = new HeapReference(new ReferenceType(sliceOfString), allocId, expr.getCodeLocation());
				HeapDereference deref = new HeapDereference(sliceOfString, ref, expr.getCodeLocation());

				// Assign the len property to this hid
				Variable len = new Variable(Untyped.INSTANCE, "len",
						getLocation());
				AccessChild lenAccess = new AccessChild(GoIntType.INSTANCE, deref,
						len, getLocation());
				AnalysisState<A, H, V, T> lenState = allocState.assign(lenAccess, new PushAny(GoIntType.INSTANCE, getLocation()), this);

				// Assign the cap property to this hid
				Variable cap = new Variable(Untyped.INSTANCE, "cap",
						getLocation());
				AccessChild capAccess = new AccessChild(GoIntType.INSTANCE, deref,
						cap, getLocation());
				AnalysisState<A, H, V, T> capState = lenState.assign(capAccess, new PushAny(GoIntType.INSTANCE, getLocation()), this);

				result = result.lub(GoTupleExpression.allocateTupleExpression(capState, anns, this, getLocation(), tupleType, 
						new PushAny(GoStringType.INSTANCE, getLocation()),
						ref));
			}

			return result;
		}
	}
}
