package it.unive.golisa.cfg.runtime.time.type;

import it.unive.golisa.cfg.runtime.time.method.Day;
import it.unive.golisa.cfg.runtime.time.method.Month;
import it.unive.golisa.cfg.runtime.time.method.Unix;
import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.cfg.type.numeric.signed.GoInt64Type;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.ClassUnit;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.Program;

/**
 * A Time type.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class Time extends GoStructType {

//	/**
//	 * Unique instance of a {@link Time} type.
//	 */
//	public static final Time INSTANCE = new Time();
//
//	private Time() {
//		this("Time", buildTimeUnit());
//	}

	private Time(String name, CompilationUnit unit) {
		super(name, unit);
	}

//	private static CompilationUnit buildTimeUnit() {
//		SourceCodeLocation unknownLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
//		
//	}

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
	
	public static Time getTimeType(Program program) {
		ClassUnit timeUnit = new ClassUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, program, "Time", false);
		timeUnit.addGlobal(new Global(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, timeUnit, "wall", true, GoInt64Type.INSTANCE));
		timeUnit.addGlobal(new Global(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, timeUnit, "ext", true, GoInt64Type.INSTANCE));
		// TODO: missing field loc *Location
		
		return new Time("Time", timeUnit);
	}
	
	public static void registerMethods() {
		CompilationUnit timeUnit = GoStructType.get("Time").getUnit();
		timeUnit.addInstanceCodeMember(new Day(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, timeUnit));
		timeUnit.addInstanceCodeMember(new Month(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, timeUnit));
		timeUnit.addInstanceCodeMember(new Unix(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, timeUnit));
	}
	
}
