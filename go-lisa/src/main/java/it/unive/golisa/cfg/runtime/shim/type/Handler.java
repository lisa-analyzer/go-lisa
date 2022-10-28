package it.unive.golisa.cfg.runtime.shim.type;

import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.ClassUnit;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Program;

/**
 * A Handler type.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class Handler extends GoStructType {

	/**
	 * Unique instance of the {@link Handler} type.
	 */
	private static Handler INSTANCE;

	private Handler(CompilationUnit unit) {
		super("Handler", unit);
	}

	public static Handler getHandlerType(Program program) {
		if (INSTANCE == null) {
			ClassUnit handlerUnit = new ClassUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, program, "Handler",
					false);
			INSTANCE = new Handler(handlerUnit);
		}

		return INSTANCE;
	}

	@Override
	public String toString() {
		return "shim.Handler";
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
