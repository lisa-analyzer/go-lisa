package it.unive.golisa.cfg.runtime.fmt;

import java.util.Set;

import it.unive.golisa.cfg.VarArgsParameter;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.lattices.ExpressionSet;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.CodeUnit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.NativeCFG;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.NaryExpression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.symbolic.value.operator.unary.UnaryOperator;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;
import it.unive.lisa.type.Untyped;

/**
 * func Sprintf(format string, a ...any) string.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class Sprintf extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param fmtUnit  the unit to which this native cfg belongs to
	 */
	public Sprintf(CodeLocation location, CodeUnit fmtUnit) {
		super(new CodeMemberDescriptor(location, fmtUnit, false, "Sprintf", GoStringType.INSTANCE,
				new Parameter(location, "format", GoStringType.INSTANCE),
				new VarArgsParameter(location, "a", GoSliceType.lookup(Untyped.INSTANCE))),
				SprintfImpl.class);
	}

	/**
	 * The {@link Sprintf} implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class SprintfImpl extends NaryExpression implements PluggableStatement {

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
		public static SprintfImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new SprintfImpl(cfg, location, params);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param left     the left expression
		 * @param right    the right expression
		 */
		public SprintfImpl(CFG cfg, CodeLocation location, Expression[] exprs) {
			super(cfg, location, "Sprintf", GoStringType.INSTANCE, exprs);
		}


		@Override
		public <A extends AbstractState<A>> AnalysisState<A> forwardSemanticsAux(
				InterproceduralAnalysis<A> interprocedural, AnalysisState<A> state, ExpressionSet[] params,
				StatementStore<A> expressions) throws SemanticException {
			
			ExpressionSet p1 = params[0];
			AnalysisState<A> res = state.bottom();
			
			if(params.length > 1) {	
				for(SymbolicExpression e1 : p1) {
					for(int i = 1; i< params.length; i++)
						for(SymbolicExpression e2 : params[i]) 
							res = res.lub(state.smallStepSemantics(new it.unive.lisa.symbolic.value.BinaryExpression(getStaticType(), e1,
								e2, SprintfOperator.INSTANCE, getLocation()), original));
				}
			} else {
				for(SymbolicExpression e1 : p1) {
					res = res.lub(state.smallStepSemantics(new it.unive.lisa.symbolic.value.UnaryExpression(getStaticType(),
							e1, (UnaryOperator) SprintfOperatorUnary.INSTANCE, getLocation()), original));
				}
			}
			
			return res;
			
		}
	}

	/**
	 * The Sprintf operator.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class SprintfOperator implements BinaryOperator {

		/**
		 * The singleton instance of this class.
		 */
		public static final SprintfOperator INSTANCE = new SprintfOperator();

		private SprintfOperator() {
		}

		@Override
		public String toString() {
			return "SprintfOperator";
		}

		@Override
		public Set<Type> typeInference(TypeSystem types, Set<Type> left, Set<Type> right) {
			return Set.of(types.getStringType());
		}
	}
	
	/**
	 * The Sprintf operator.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class SprintfOperatorUnary implements UnaryOperator {

		/**
		 * The singleton instance of this class.
		 */
		public static final SprintfOperator INSTANCE = new SprintfOperator();

		private SprintfOperatorUnary() {
		}

		@Override
		public String toString() {
			return "SprintfOperatorUnary";
		}
		
		@Override
		public Set<Type> typeInference(TypeSystem types, Set<Type> argument) {
			return Set.of(types.getStringType());
		}
	}
}
