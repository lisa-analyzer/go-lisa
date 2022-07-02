package it.unive.golisa.cfg.runtime.tendermint.core.abci.type;

import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.SourceCodeLocation;

/**
 * A Response of Begin Block type.
 * 
 * @link https://pkg.go.dev/github.com/tendermint/tendermint/abci/types#ResponseBeginBlock
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class ResponseBeginBlock extends GoStructType {

	/**
	 * Unique instance of {@link ResponseBeginBlock} type.
	 */
	public static final ResponseBeginBlock INSTANCE = new ResponseBeginBlock();

	private ResponseBeginBlock() {
		this("ResponseBeginBlock", buildRequestBeginBlockUnit());
	}

	private ResponseBeginBlock(String name, CompilationUnit unit) {
		super(name, unit);
	}

	private static CompilationUnit buildRequestBeginBlockUnit() {
		SourceCodeLocation unknownLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
		CompilationUnit abciUnit = new CompilationUnit(unknownLocation, "ResponseBeginBlock", false);
		return abciUnit;
	}

	/**
	 * Registers methods of the {@link ResponseBeginBlock} type.
	 */
	public static void registerMethods() {
		// TODO
	}

	@Override
	public String toString() {
		return "abci.types.ResponseBeginBlock";
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
