package it.unive.golisa.cfg.runtime.tendermint.core.abci.type;

import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.ClassUnit;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Program;

/**
 * A Response of DeliverTx type.
 * 
 * @link https://pkg.go.dev/github.com/tendermint/tendermint/abci/types#ResponseDeliverTx
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class ResponseDeliverTx extends GoStructType {

	/**
	 * Unique instance of the {@link ResponseDeliverTx} type.
	 */
	private static ResponseDeliverTx INSTANCE;

	private ResponseDeliverTx(CompilationUnit unit) {
		super("ResponseDeliverTx", unit);
	}

	public static ResponseDeliverTx getResponseDeliverTxType(Program program) {
		if (INSTANCE == null) {
			ClassUnit abciUnit = new ClassUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, program, "ResponseDeliverTx",
					false);
			INSTANCE = new ResponseDeliverTx(abciUnit);
		}
		
		return INSTANCE;
	}

	@Override
	public String toString() {
		return "abci.types.ResponseDeliverTx";
	}

	@Override
	public boolean equals(Object other) {
		return this == other;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}
}
