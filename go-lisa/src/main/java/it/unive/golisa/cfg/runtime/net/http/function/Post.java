package it.unive.golisa.cfg.runtime.net.http.function;

import it.unive.golisa.cfg.runtime.net.http.type.Response;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoPointerType;
import it.unive.golisa.cfg.type.composite.GoTupleType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.CodeUnit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.NativeCFG;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.TernaryExpression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.type.Untyped;

/**
 * func Post(url, contentType string, body io.Reader) (resp *Response, err error)
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class Post extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location    the location where this native cfg is defined
	 * @param httpUnit the unit to which this native cfg belongs to
	 */
	public Post(CodeLocation location, CodeUnit httpUnit) {
		super(new CodeMemberDescriptor(location, httpUnit, false, "Post", 
				GoTupleType.getTupleTypeOf(location, GoPointerType.lookup(Response.getResponseType(httpUnit.getProgram())), GoErrorType.INSTANCE),
				new Parameter(location, "url", GoStringType.INSTANCE),
				new Parameter(location, "contentType", GoStringType.INSTANCE),
				new Parameter(location, "body", Untyped.INSTANCE)),
				PostImpl.class);
	}

	/**
	 * The ParseInt implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class PostImpl extends TernaryExpression implements PluggableStatement {

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
		public static PostImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new PostImpl(cfg, location, params[0], params[1], params[2]);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param left     the left expression
		 * @param middle     the middle expression
		 * @param right     the right expression
		 */
		public PostImpl(CFG cfg, CodeLocation location, Expression left, Expression middle, Expression right) {
			super(cfg, location, "PostImpl", GoTupleType.getTupleTypeOf(location, GoPointerType.lookup(Response.getResponseType(null)), GoErrorType.INSTANCE), left, middle, right);
		}

		@Override
		public <A extends AbstractState<A, H, V, T>, H extends HeapDomain<H>, V extends ValueDomain<V>, T extends TypeDomain<T>> AnalysisState<A, H, V, T> ternarySemantics(
				InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
				SymbolicExpression left, SymbolicExpression middle, SymbolicExpression right,
				StatementStore<A, H, V, T> expressions) throws SemanticException {
			
			AnalysisState<A, H, V, T> result = state.bottom();
			result = result.lub(state.smallStepSemantics(left, original));
			result = result.lub(state.smallStepSemantics(middle, original));
			result = result.lub(state.smallStepSemantics(right, original));
			return result;
		}	
	}
}