package it.unive.golisa.cfg.runtime.tendermint.core.abci.type;

import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.SourceCodeLocation;

/**
 * A Response of End Block
 * 
 * @link https://pkg.go.dev/github.com/tendermint/tendermint/abci/types#ResponseEndBlock
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class ResponseCommit extends GoStructType {

	public static final ResponseCommit INSTANCE = new ResponseCommit();

	private ResponseCommit() {
		this("ResponseEndBlock", buildRequestEndBlockUnit());
	}

	private ResponseCommit(String name, CompilationUnit unit) {
		super(name, unit);
	}

	private static CompilationUnit buildRequestEndBlockUnit() {
		SourceCodeLocation unknownLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
		CompilationUnit randUnit = new CompilationUnit(unknownLocation, "ResponseEndBlock", false);
		return randUnit;
	}

	public static void registerMethods() {
		//TODO
	}

	@Override
	public String toString() {
		return "abci.types.ResponseEndBlock";
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
