package it.unive.golisa.cfg.runtime.shim.method;

import it.unive.golisa.cfg.runtime.time.type.Time;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.checker.TaintChecker.HeapResolver;
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
import it.unive.lisa.symbolic.heap.HeapDereference;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.MemoryPointer;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;
import it.unive.lisa.type.Untyped;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * func (t Time) Format(layout string) string
 * 
 * @see https://pkg.go.dev/time#Time.Format
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class Format extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param shimUnit the unit to which this native cfg belongs to
	 */
	public Format(CodeLocation location, CompilationUnit shimUnit) {
		super(new CodeMemberDescriptor(location, shimUnit, true, "Format",
				GoStringType.INSTANCE,
				new Parameter(location, "this", Time.getTimeType(shimUnit.getProgram())),
				new Parameter(location, "layout", GoStringType.INSTANCE)),
				FormatImpl.class);
	}

	public static class FormatImpl extends it.unive.lisa.program.cfg.statement.BinaryExpression
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
		public static FormatImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new FormatImpl(cfg, location, params[0], params[1]);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param expr     the expression
		 */
		public FormatImpl(CFG cfg, CodeLocation location, Expression left, Expression right) {
			super(cfg, location, "FormatImpl",
					GoStringType.INSTANCE,
					left, right);
		}

		@Override
		public <A extends AbstractState<A>> AnalysisState<A> fwdBinarySemantics(
				InterproceduralAnalysis<A> interprocedural, AnalysisState<A> state,
				SymbolicExpression left, SymbolicExpression right, StatementStore<A> expressions)
				throws SemanticException {
			// Retrieves all the identifiers reachable from expr
			Collection<SymbolicExpression> reachableIds = HeapResolver.resolve(state, left, this);
			AnalysisState<A> result = state.bottom();
			for (SymbolicExpression id : reachableIds) {
				if (id instanceof MemoryPointer)
					continue;

				Set<Type> idTypes = state.getState().getRuntimeTypesOf(id, this, state.getState());
				for (Type t : idTypes)
					if (t.isPointerType()) {
						HeapDereference derefId = new HeapDereference(Untyped.INSTANCE, id, left.getCodeLocation());
						BinaryExpression lExp = new BinaryExpression(GoStringType.INSTANCE, derefId, right,
								FormatOperator.INSTANCE, getLocation());
						result = result.lub(state.smallStepSemantics(lExp, original));
					} else {
						BinaryExpression lExp = new BinaryExpression(GoStringType.INSTANCE, id, right,
								FormatOperator.INSTANCE, getLocation());
						result = result.lub(state.smallStepSemantics(lExp, original));
					}
			}

			return result;

		}
	}

	public static class FormatOperator implements BinaryOperator {

		/**
		 * The singleton instance of this class.
		 */
		public static final FormatOperator INSTANCE = new FormatOperator();

		/**
		 * Builds the operator. This constructor is visible to allow
		 * subclassing: instances of this class should be unique, and the
		 * singleton can be retrieved through field {@link #INSTANCE}.
		 */
		protected FormatOperator() {
		}

		@Override
		public String toString() {
			return "FormatOperator";
		}

		@Override
		public Set<Type> typeInference(TypeSystem types, Set<Type> left, Set<Type> right) {
			return Collections.singleton(GoStringType.INSTANCE);
		}
	}
}
