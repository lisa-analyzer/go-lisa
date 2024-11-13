package it.unive.golisa.cfg.runtime.encoding.base64.function;

import it.unive.golisa.cfg.expression.literal.GoTupleExpression;
import it.unive.golisa.cfg.type.GoNilType;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.composite.GoTupleType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.lattices.ExpressionSet;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.annotations.Annotation;
import it.unive.lisa.program.annotations.Annotations;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.CodeMember;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.NativeCFG;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.UnaryExpression;
import it.unive.lisa.program.cfg.statement.call.ResolvedCall;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.type.Type;

/**
 * func (enc *Encoding) DecodeString(s string) ([]byte, error)
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class DecodeString extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param unit the unit to which this native cfg belongs to
	 */
	public DecodeString(CodeLocation location, CompilationUnit unit) {
		super(new CodeMemberDescriptor(location, unit, false, "DecodeString", GoErrorType.INSTANCE,
				new Parameter(location, "s", GoStringType.INSTANCE)),
				DecodeStringImpl.class);
	}

	/**
	 * The {@link DecodeString} implementation.
	 * 
	 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
	 */
	public static class DecodeStringImpl extends UnaryExpression
			implements PluggableStatement {

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
		 * @param par   the parameter
		 * 
		 * @return the pluggable statement
		 */
		public static DecodeStringImpl build(CFG cfg, CodeLocation location, Expression par) {
			return new DecodeStringImpl(cfg, location, par);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param par     the par of this expression
		 */
		public DecodeStringImpl(CFG cfg, CodeLocation location, Expression par) {
			super(cfg, location, "DecodeStringImpl", GoErrorType.INSTANCE, par);
		}


		@Override
		public <A extends AbstractState<A>> AnalysisState<A> fwdUnarySemantics(
				InterproceduralAnalysis<A> interprocedural, AnalysisState<A> state, SymbolicExpression expr,
				StatementStore<A> expressions) throws SemanticException {
			Type  sliceBytes= GoSliceType.getSliceOfBytes();

			GoTupleType tupleType = GoTupleType.getTupleTypeOf(original.getLocation(), sliceBytes, GoErrorType.INSTANCE);

			Annotations annots = new Annotations();
			if (original instanceof ResolvedCall)
				for (CodeMember target : ((ResolvedCall) original).getTargets())
					for (Annotation ann : target.getDescriptor().getAnnotations())
						annots.addAnnotation(ann);
			
			
			AnalysisState<A> pState = state.smallStepSemantics(expr, original);
			
			ExpressionSet computeExprs = pState.getComputedExpressions();
			AnalysisState<A> ret = state.bottom();
			
			for(SymbolicExpression exp : pState.getState().rewrite(computeExprs, original, state.getState())) {
				if(exp instanceof Identifier) {
					Identifier v = (Identifier) exp;
					for (Annotation ann : annots)
						v.addAnnotation(ann);
				}
				ret = ret.lub(GoTupleExpression.allocateTupleExpression(pState, 
					annots, 
					this,
					getLocation(), 
					tupleType,
					exp,
					new Constant(GoErrorType.INSTANCE, "error", getLocation())));
			}			
			

			return ret;
		}
	}
}
