package it.unive.golisa.cfg.runtime.bytes.function;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import it.unive.golisa.cfg.runtime.bytes.type.Buffer;
import it.unive.golisa.cfg.type.composite.GoSliceType;
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

/**
 * func NewBuffer(buf []byte) *Buffer
 * 
 * @see https://pkg.go.dev/bytes#NewBuffer
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class NewBuffer extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param unit     the unit to which this native cfg belongs to
	 */
	public NewBuffer(CodeLocation location, CodeUnit bytesUnit) {
		super(new CodeMemberDescriptor(location, bytesUnit, false, "NewBuffer",
				Buffer.getBufferType(bytesUnit.getProgram()),
				new Parameter(location, "buf", GoSliceType.getSliceOfBytes())),
				NewBufferImpl.class);
	}

	/**
	 * The {@link Bytes} implementation.
	 * 
	 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
	 */
	public static class NewBufferImpl extends it.unive.lisa.program.cfg.statement.UnaryExpression
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
		public static NewBufferImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new NewBufferImpl(cfg, location, params[0]);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param expr     the expr side of this pluggable statement
		 */
		public NewBufferImpl(CFG cfg, CodeLocation location, Expression expr) {
			super(cfg, location, "NewBufferImpl", Buffer.getBufferType(null), expr);
		}


		@Override
		public <A extends AbstractState<A>> AnalysisState<A> fwdUnarySemantics(InterproceduralAnalysis<A> arg0,
				AnalysisState<A> state, SymbolicExpression expr, StatementStore<A> arg3) throws SemanticException {
			Buffer bufferType = Buffer.getBufferType(getProgram());

			// Allocates the new memory for a Buffer object
			MemoryAllocation alloc = new MemoryAllocation(bufferType, getLocation(), new Annotations(), true);
			HeapReference ref = new HeapReference(new ReferenceType(bufferType), alloc, getLocation());
			HeapDereference deref = new HeapDereference(bufferType, ref, getLocation());

			AnalysisState<A> asg = state.bottom();
			Collection<SymbolicExpression> reachableIds = HeapResolver.resolve(state, expr, this);
			for (SymbolicExpression id : reachableIds) {
				HeapDereference derefId = new HeapDereference(Untyped.INSTANCE, id, expr.getCodeLocation());
				UnaryExpression left = new UnaryExpression(Untyped.INSTANCE, derefId, NewBufferOperator.INSTANCE, getLocation());
				asg = asg.lub(state.assign(deref, left, original));
			}

			return asg.smallStepSemantics(ref, original);
		}
	}

	public static class NewBufferOperator implements UnaryOperator {

		/**
		 * The singleton instance of this class.
		 */
		public static final NewBufferOperator INSTANCE = new NewBufferOperator();

		/**
		 * Builds the operator. This constructor is visible to allow subclassing:
		 * instances of this class should be unique, and the singleton can be
		 * retrieved through field {@link #INSTANCE}.
		 */
		protected NewBufferOperator() {
		}

		@Override
		public String toString() {
			return "NewBufferOperator";
		}	

		@Override
		public Set<Type> typeInference(TypeSystem types, Set<Type> argument) {
			return Collections.singleton(Untyped.INSTANCE);
		}
	}

}
