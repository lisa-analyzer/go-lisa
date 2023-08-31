package it.unive.golisa.cfg.runtime.crypto.x509.function;

import it.unive.golisa.analysis.taint.Clean;
import it.unive.golisa.cfg.runtime.crypto.x509.type.Certificate;
import it.unive.golisa.cfg.runtime.shim.method.GetState;
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
import it.unive.lisa.program.cfg.statement.UnaryExpression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.type.Untyped;

public class ParseCertificate extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param x509Unit the unit to which this native cfg belongs to
	 */
	public ParseCertificate(CodeLocation location, CodeUnit x509Unit) {
		super(new CodeMemberDescriptor(location, x509Unit, false, "ParseCertificate",
				GoTupleType.getTupleTypeOf(location, GoPointerType.lookup(Certificate.getCertificateFile(x509Unit.getProgram())),
						GoErrorType.INSTANCE),
				new Parameter(location, "s", Untyped.INSTANCE)),
				ParseCertificateImpl.class);
	}

	/**
	 * The {@link GetState} implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class ParseCertificateImpl extends UnaryExpression
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
		public static ParseCertificateImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new ParseCertificateImpl(cfg, location, params[0]);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param params   the parameters
		 */
		public ParseCertificateImpl(CFG cfg, CodeLocation location, Expression exp) {
			super(cfg, location, "ParseCertificateImpl", GoTupleType.getTupleTypeOf(location, GoPointerType.lookup(Certificate.getCertificateFile(null)), GoErrorType.INSTANCE), exp);
		}



		@Override
		public <A extends AbstractState<A, H, V, T>, H extends HeapDomain<H>, V extends ValueDomain<V>, T extends TypeDomain<T>> AnalysisState<A, H, V, T> unarySemantics(
				InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
				SymbolicExpression expr, StatementStore<A, H, V, T> expressions) throws SemanticException {
			return state.smallStepSemantics(new Clean(getStaticType(), getLocation()), original);
		}
	}
}
