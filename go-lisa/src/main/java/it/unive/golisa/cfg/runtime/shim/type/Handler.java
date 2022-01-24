package it.unive.golisa.cfg.runtime.shim.type;

import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.SourceCodeLocation;

public class Handler extends GoStructType {
	public static final Handler INSTANCE = new Handler();

	private Handler() {
		this("Handler", buildHandlerUnit());
	}

	private Handler(String name, CompilationUnit unit) {
		super(name, unit);
	}

	private static CompilationUnit buildHandlerUnit() {
		SourceCodeLocation unknownLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
		CompilationUnit handlerUnit = new CompilationUnit(unknownLocation, "Handler", false);
		return handlerUnit;
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
