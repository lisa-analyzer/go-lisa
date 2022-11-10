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
	private static ResponseBeginBlock INSTANCE;

	private ResponseBeginBlock(CompilationUnit unit) {
		super("ResponseBeginBlock", unit);
	}

	/**
	 * Yields the {@link ResponseBeginBlock} type.
	 * 
	 * @param program the program to which this type belongs
	 * 
	 * @return the {@link ResponseBeginBlock} type
	 */
	public static ResponseBeginBlock getRequestBeginBlockType(Program program) {
		if (INSTANCE == null) {
			ClassUnit abciUnit = new ClassUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, program,
					"ResponseBeginBlock",
					false);
			INSTANCE = new ResponseBeginBlock(abciUnit);
		}

		return INSTANCE;
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
