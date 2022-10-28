package it.unive.golisa.cfg.runtime.tendermint.core.abci.type;

import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.ClassUnit;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Program;

/**
 * A Request of End Block type.
 * 
 * @link https://pkg.go.dev/github.com/tendermint/tendermint/abci/types#RequestEndBlock
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class RequestEndBlock extends GoStructType {

	/**
	 * Unique instance of the {@link RequestEndBlock} type.
	 */
	public static RequestEndBlock INSTANCE;


	private RequestEndBlock(CompilationUnit unit) {
		super("RequestEndBlock", unit);
	}

	public static RequestEndBlock getRequestEndBlockType(Program program) {
		if (INSTANCE == null) {
			ClassUnit abciUnit = new ClassUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, program, "RequestEndBlock",
					false);
			return new RequestEndBlock(abciUnit);
		}
		
		return INSTANCE;
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
