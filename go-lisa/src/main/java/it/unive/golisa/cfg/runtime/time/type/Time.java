package it.unive.golisa.cfg.runtime.time.type;

import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.cfg.type.numeric.signed.GoInt64Type;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.SourceCodeLocation;

public class Time extends GoStructType {

	public static final Time INSTANCE = new Time();

	private Time() {
		this("Time", buildTimeUnit());
	}

	private Time(String name, CompilationUnit unit) {
		super(name, unit);
	}

	private static CompilationUnit buildTimeUnit() {
		SourceCodeLocation unknownLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
		CompilationUnit timeType = new CompilationUnit(unknownLocation, "Time", false);
		timeType.addGlobal(new Global(unknownLocation, "wall", GoInt64Type.INSTANCE));
		timeType.addGlobal(new Global(unknownLocation, "ext", GoInt64Type.INSTANCE));
		// TODO: missing field loc *Location
		return timeType;
	}

	@Override
	public String toString() {
		return "time.Time";
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
