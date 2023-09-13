package it.unive.golisa.cfg.runtime.io.function;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import it.unive.golisa.cfg.expression.literal.GoTupleExpression;
import it.unive.golisa.cfg.runtime.io.type.Reader;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.composite.GoTupleType;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt8Type;
import it.unive.golisa.checker.TaintChecker.HeapResolver;
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
import it.unive.lisa.symbolic.heap.HeapDereference;
import it.unive.lisa.symbolic.heap.HeapReference;
import it.unive.lisa.symbolic.heap.MemoryAllocation;
import it.unive.lisa.symbolic.value.UnaryExpression;
import it.unive.lisa.symbolic.value.operator.unary.UnaryOperator;
import it.unive.lisa.type.ReferenceType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;
import it.unive.lisa.type.Untyped;

/**
 * func ReadAll(r Reader) ([]byte, error).
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class ReadAll extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param ioUnit   the unit to which this native cfg belongs to
	 */
	public ReadAll(CodeLocation location, CompilationUnit ioUnit) {
		super(new CodeMemberDescriptor(location, ioUnit, false, "ReadAll",
				GoTupleType.getTupleTypeOf(location, GoUInt8Type.INSTANCE,
						GoErrorType.INSTANCE),
				new Parameter(location, "r", Reader.getReaderType(ioUnit.getProgram()))),
				ReadAllImpl.class);
	}

	/**
	 * The {@link ReadAll} implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class ReadAllImpl extends it.unive.lisa.program.cfg.statement.UnaryExpression
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
		public static ReadAllImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new ReadAllImpl(cfg, location, params[0]);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param expr     the expression
		 */
		public ReadAllImpl(CFG cfg, CodeLocation location, Expression expr) {
			super(cfg, location, "ReadAllImpl",
					GoTupleType.getTupleTypeOf(location, GoSliceType.lookup(GoUInt8Type.INSTANCE),
							GoErrorType.INSTANCE),
					expr);
		}

		@Override
		public <A extends AbstractState<A, H, V, T>,
		H extends HeapDomain<H>,
		V extends ValueDomain<V>,
		T extends TypeDomain<T>> AnalysisState<A, H, V, T> unarySemantics(
				InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
				SymbolicExpression expr, StatementStore<A, H, V, T> expressions) throws SemanticException {
			Type sliceOfBytes = GoSliceType.getSliceOfBytes();
			GoTupleType tupleType = GoTupleType.getTupleTypeOf(getLocation(), 
					new ReferenceType(sliceOfBytes), GoErrorType.INSTANCE);

			// Allocates the new heap allocation
			MemoryAllocation created = new MemoryAllocation(sliceOfBytes, expr.getCodeLocation(), new Annotations(), true);
			AnalysisState<A, H, V, T> allocState = state.smallStepSemantics(created, this);

			AnalysisState<A, H, V, T> result = state.bottom();
			for (SymbolicExpression allocId : allocState.getComputedExpressions()) {
				HeapReference ref = new HeapReference(new ReferenceType(sliceOfBytes), allocId, expr.getCodeLocation());
				HeapDereference deref = new HeapDereference(sliceOfBytes, ref, expr.getCodeLocation());
				AnalysisState<A, H, V, T> asg = allocState.bottom();

				// Retrieves all the identifiers reachable from expr
				Collection<SymbolicExpression> reachableIds = HeapResolver.resolve(allocState, expr, this);
				for (SymbolicExpression id : reachableIds) {
					HeapDereference derefId = new HeapDereference(Untyped.INSTANCE, id, expr.getCodeLocation());
					UnaryExpression left = new UnaryExpression(Untyped.INSTANCE, derefId, ReadAllFirstParameter.INSTANCE, getLocation());
					asg = asg.lub(allocState.assign(deref, left, original));
				}

				UnaryExpression rExp = new UnaryExpression(GoErrorType.INSTANCE, expr, ReadAllSecondParameter.INSTANCE, getLocation());

				result = result.lub(GoTupleExpression.allocateTupleExpression(asg, new Annotations(), this, getLocation(), tupleType, 
						ref,
						rExp
						));
			}

			return result;
		}
	}

	public static class ReadAllFirstParameter implements UnaryOperator {

		/**
		 * The singleton instance of this class.
		 */
		public static final ReadAllFirstParameter INSTANCE = new ReadAllFirstParameter();

		/**
		 * Builds the operator. This constructor is visible to allow subclassing:
		 * instances of this class should be unique, and the singleton can be
		 * retrieved through field {@link #INSTANCE}.
		 */
		protected ReadAllFirstParameter() {
		}

		@Override
		public String toString() {
			return "ReadAll_first";
		}	

		@Override
		public Set<Type> typeInference(TypeSystem types, Set<Type> argument) {
			return Collections.singleton(Untyped.INSTANCE);
		}
	}

	public static class ReadAllSecondParameter implements UnaryOperator {

		/**
		 * The singleton instance of this class.
		 */
		public static final ReadAllSecondParameter INSTANCE = new ReadAllSecondParameter();

		/**
		 * Builds the operator. This constructor is visible to allow subclassing:
		 * instances of this class should be unique, and the singleton can be
		 * retrieved through field {@link #INSTANCE}.
		 */
		protected ReadAllSecondParameter() {
		}

		@Override
		public String toString() {
			return "ReadAll_second";
		}	

		@Override
		public Set<Type> typeInference(TypeSystem types, Set<Type> argument) {
			return Collections.singleton(GoErrorType.INSTANCE);
		}
	}
}
