package it.unive.golisa.cfg.runtime.tendermint.core.abci.type;

import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.SourceCodeLocation;

/**
 * A Request of DeliverTx
 * 
 * @link https://pkg.go.dev/github.com/tendermint/tendermint/abci/types#DeliverTx
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class RequestDeliverTx extends GoStructType {

	public static final RequestDeliverTx INSTANCE = new RequestDeliverTx();

	private RequestDeliverTx() {
		this("RequestDeliverTx", buildRequestDeliverTxUnit());
	}

	private RequestDeliverTx(String name, CompilationUnit unit) {
		super(name, unit);
	}

	private static CompilationUnit buildRequestDeliverTxUnit() {
		SourceCodeLocation unknownLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
		CompilationUnit abciUnit = new CompilationUnit(unknownLocation, "RequestDeliverTx", false);
		return abciUnit;
	}

	public static void registerMethods() {
		// TODO
	}

	@Override
	public String toString() {
		return "abci.types.RequestDeliverTx";
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
