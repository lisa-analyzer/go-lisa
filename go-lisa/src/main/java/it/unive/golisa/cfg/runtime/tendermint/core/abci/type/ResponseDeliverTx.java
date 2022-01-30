package it.unive.golisa.cfg.runtime.tendermint.core.abci.type;

import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.SourceCodeLocation;

/**
 * A Response of DeliverTx
 * 
 * @link https://pkg.go.dev/github.com/tendermint/tendermint/abci/types#ResponseDeliverTx
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class ResponseDeliverTx extends GoStructType {

	public static final ResponseDeliverTx INSTANCE = new ResponseDeliverTx();

	private ResponseDeliverTx() {
		this("ResponseDeliverTx", buildResponseDeliverTxUnit());
	}

	private ResponseDeliverTx(String name, CompilationUnit unit) {
		super(name, unit);
	}

	private static CompilationUnit buildResponseDeliverTxUnit() {
		SourceCodeLocation unknownLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
		CompilationUnit randUnit = new CompilationUnit(unknownLocation, "ResponseDeliverTx", false);
		return randUnit;
	}

	public static void registerMethods() {
		//TODO
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
