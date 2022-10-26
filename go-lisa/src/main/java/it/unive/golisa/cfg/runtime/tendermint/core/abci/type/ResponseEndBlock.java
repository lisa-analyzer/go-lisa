package it.unive.golisa.cfg.runtime.tendermint.core.abci.type;

import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.ClassUnit;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Program;

/**
 * A Response of end block type.
 * 
 * @link https://pkg.go.dev/github.com/tendermint/tendermint/abci/types#ResponseEndBlock
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class ResponseEndBlock extends GoStructType {

	/**
	 * Unique instance of {@link ResponseEndBlock} type.
	 */
//	public static final ResponseEndBlock INSTANCE = new ResponseEndBlock();

//	private ResponseEndBlock() {
//		this("ResponseEndBlock", buildRequestEndBlockUnit());
//	}

	private ResponseEndBlock(CompilationUnit unit) {
		super("ResponseEndBlock", unit);
	}

	public static ResponseEndBlock getRequestEndBlockType(Program program) {
		ClassUnit abciUnit = new ClassUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, program, "ResponseEndBlock",
				false);
		return new ResponseEndBlock(abciUnit);
	}

	/**
	 * Registers methods of the {@link ResponseEndBlock} type.
	 */
	public static void registerMethods() {
		// TODO
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
