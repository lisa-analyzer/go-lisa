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

	/**
	 * Unique instance of a {@link Time} type.
	 */
	private static Time INSTANCE;

	private Time(String name, CompilationUnit unit) {
		super(name, unit);
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

	public static Time getTimeType(Program program) {
		if (INSTANCE == null) {
			ClassUnit timeUnit = new ClassUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, program, "Time", false);
			timeUnit.addGlobal(
					new Global(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, timeUnit, "wall", true,
							GoInt64Type.INSTANCE));
			timeUnit.addGlobal(
					new Global(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, timeUnit, "ext", true,
							GoInt64Type.INSTANCE));
			// TODO: missing field loc *Location

			INSTANCE = new Time("Time", timeUnit);
		}

		return INSTANCE;
	}

	public static void registerMethods() {
		CompilationUnit timeUnit = INSTANCE.getUnit();
		timeUnit.addInstanceCodeMember(new Day(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, timeUnit));
		timeUnit.addInstanceCodeMember(new Month(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, timeUnit));
		timeUnit.addInstanceCodeMember(new Unix(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, timeUnit));
	}

}
