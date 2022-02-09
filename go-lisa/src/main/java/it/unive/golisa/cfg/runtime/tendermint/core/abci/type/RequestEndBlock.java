package it.unive.golisa.cfg.runtime.tendermint.core.abci.type;

import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.SourceCodeLocation;

/**
 * A Request of End Block
 * 
 * @link https://pkg.go.dev/github.com/tendermint/tendermint/abci/types#RequestEndBlock
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class RequestEndBlock extends GoStructType {

	public static final RequestEndBlock INSTANCE = new RequestEndBlock();

	private RequestEndBlock() {
		this("RequestEndBlock", buildRequestEndBlockUnit());
	}

	private RequestEndBlock(String name, CompilationUnit unit) {
		super(name, unit);
	}

	private static CompilationUnit buildRequestEndBlockUnit() {
		SourceCodeLocation unknownLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
		CompilationUnit abciUnit = new CompilationUnit(unknownLocation, "RequestEndBlock", false);
		return abciUnit;
	}

	public static void registerMethods() {
		// TODO
	}

	@Override
	public String toString() {
		return "abci.types.RequestEndBlock";
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
