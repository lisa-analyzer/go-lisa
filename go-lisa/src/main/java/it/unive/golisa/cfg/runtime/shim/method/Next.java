package it.unive.golisa.cfg.runtime.shim.method;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import it.unive.golisa.cfg.expression.literal.GoTupleExpression;
import it.unive.golisa.cfg.runtime.peer.type.Response;
import it.unive.golisa.cfg.runtime.shim.type.StateQueryIterator;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoTupleType;
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
 * func (iter *StateQueryIterator) Next() (*queryresult.KV, error)
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 *
 */
public class Next extends NativeCFG {
	
	
	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param shimUnit the unit to which this native cfg belongs to
	 */
	public Next(CodeLocation location, CompilationUnit shimUnit) {
		super(new CodeMemberDescriptor(location, shimUnit, true, "Next", 
				GoTupleType.getTupleTypeOf(location, Response.getResponseType(shimUnit.getProgram()), GoErrorType.INSTANCE),
				new Parameter(location, "this", StateQueryIterator.getStateQueryIterator(shimUnit.getProgram()))),
				NextImpl.class);
	}
	
	/**
	 * The {@link HasNext} implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class NextImpl extends it.unive.lisa.program.cfg.statement.UnaryExpression
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
		public static NextImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new NextImpl(cfg, location, params[0]);
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
		public NextImpl(CFG cfg, CodeLocation location, Expression e) {
			super(cfg, location, "NextImpl", GoTupleType.getTupleTypeOf(location, Response.getResponseType(null), GoErrorType.INSTANCE), e);
		}

		@Override
		public <A extends AbstractState<A, H, V, T>, H extends HeapDomain<H>, V extends ValueDomain<V>, T extends TypeDomain<T>> AnalysisState<A, H, V, T> unarySemantics(
				InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
				SymbolicExpression expr, StatementStore<A, H, V, T> expressions) throws SemanticException {

			Response responseType = Response.getResponseType(getProgram());
			GoTupleType tupleType = GoTupleType.getTupleTypeOf(getLocation(), 
					new ReferenceType(responseType), GoErrorType.INSTANCE);

			// Allocates the new heap allocation
			MemoryAllocation created = new MemoryAllocation(responseType, expr.getCodeLocation(), new Annotations(), true);
			HeapReference ref = new HeapReference(new ReferenceType(responseType), created, expr.getCodeLocation());
			HeapDereference deref = new HeapDereference(responseType, ref, expr.getCodeLocation());
			AnalysisState<A, H, V, T> asg = state.bottom();

			// Retrieves all the identifiers reachable from expr
			Collection<SymbolicExpression> reachableIds = HeapResolver.resolve(state, expr, this);
			for (SymbolicExpression id : reachableIds) {
				HeapDereference derefId = new HeapDereference(Untyped.INSTANCE, id, expr.getCodeLocation());
				UnaryExpression left = new UnaryExpression(Untyped.INSTANCE, derefId, NextFirstParameter.INSTANCE, getLocation());
				asg = asg.lub(state.assign(deref, left, original));
			}

			UnaryExpression rExp = new UnaryExpression(GoErrorType.INSTANCE, expr, NextSecondParameter.INSTANCE, getLocation());
			
			return GoTupleExpression.allocateTupleExpression(asg, new Annotations(), this, getLocation(), tupleType, 
					ref,
					rExp
					);
		}		
	}
	
	public static class NextFirstParameter implements UnaryOperator {

		/**
		 * The singleton instance of this class.
		 */
		public static final NextFirstParameter INSTANCE = new NextFirstParameter();

		/**
		 * Builds the operator. This constructor is visible to allow subclassing:
		 * instances of this class should be unique, and the singleton can be
		 * retrieved through field {@link #INSTANCE}.
		 */
		protected NextFirstParameter() {
		}

		@Override
		public String toString() {
			return "Next_first";
		}	

		@Override
		public Set<Type> typeInference(TypeSystem types, Set<Type> argument) {
			return Collections.singleton(Untyped.INSTANCE);
		}
	}
	
	public static class NextSecondParameter implements UnaryOperator {

		/**
		 * The singleton instance of this class.
		 */
		public static final NextSecondParameter INSTANCE = new NextSecondParameter();

		/**
		 * Builds the operator. This constructor is visible to allow subclassing:
		 * instances of this class should be unique, and the singleton can be
		 * retrieved through field {@link #INSTANCE}.
		 */
		protected NextSecondParameter() {
		}

		@Override
		public String toString() {
			return "Next_second";
		}	

		@Override
		public Set<Type> typeInference(TypeSystem types, Set<Type> argument) {
			return Collections.singleton(GoErrorType.INSTANCE);
		}
	}
	
}
