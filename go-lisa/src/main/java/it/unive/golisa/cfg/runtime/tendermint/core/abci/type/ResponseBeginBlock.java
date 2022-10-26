package it.unive.golisa.cfg.runtime.tendermint.core.abci.type;

import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.ClassUnit;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Program;

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
//	public static final ResponseBeginBlock INSTANCE = new ResponseBeginBlock();
//
//	private ResponseBeginBlock() {
//		this("", buildRequestBeginBlockUnit());
//	}

	private ResponseBeginBlock(CompilationUnit unit) {
		super("ResponseBeginBlock", unit);
	}

	public static ResponseBeginBlock getRequestBeginBlockType(Program program) {
		ClassUnit abciUnit = new ClassUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, program, "ResponseBeginBlock",
				false);
		return new ResponseBeginBlock(abciUnit);
	}

	/**
	 * // * Registers methods of the {@link ResponseBeginBlock} type. //
	 */
//	public static void registerMethods() {
//		// TODO
//	}

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
