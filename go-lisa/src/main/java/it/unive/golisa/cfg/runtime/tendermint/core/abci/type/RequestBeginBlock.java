package it.unive.golisa.cfg.runtime.tendermint.core.abci.type;

import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.ClassUnit;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Program;

/**
 * A Request of Begin Block.
 * 
 * @link https://pkg.go.dev/github.com/tendermint/tendermint/abci/types#RequestBeginBlock
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class RequestBeginBlock extends GoStructType {

	/**
	 * Unique instance of the {@link RequestBeginBlock} type.
	 */
	public static RequestBeginBlock INSTANCE;

	private RequestBeginBlock(CompilationUnit unit) {
		super("RequestBeginBlock", unit);
	}

	/**
	 * Yields the {@link RequestBeginBlock} type.
	 * 
	 * @param program the program to which this type belongs
	 * 
	 * @return the {@link RequestBeginBlock} type
	 */
	public static RequestBeginBlock getRequestBeginBlockType(Program program) {
		if (INSTANCE == null) {
			ClassUnit abciUnit = new ClassUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, program, "RequestBeginBlock",
					false);
			INSTANCE = new RequestBeginBlock(abciUnit);
		}

		return INSTANCE;
	}

	@Override
	public String toString() {
		return "abci.types.RequestBeginBlock";
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
