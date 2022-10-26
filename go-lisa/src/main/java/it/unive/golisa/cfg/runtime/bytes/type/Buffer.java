package it.unive.golisa.cfg.runtime.bytes.type;

import java.util.Collection;
import java.util.Collections;

import it.unive.golisa.cfg.expression.literal.GoInteger;
import it.unive.golisa.cfg.runtime.bytes.function.Bytes;
import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.ClassUnit;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;

/**
 * A Duration represents the elapsed time between two instants as an int64
 * nanosecond count. The representation limits the largest representable
 * duration to approximately 290 years.
 * 
 * @link https://pkg.go.dev/time#Duration type Duration int64
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class Buffer extends GoStructType {

//	/**
//	 * Unique instance of the Buffer type.
//	 */
//	public static final Buffer INSTANCE = new Buffer();
//
//	private Buffer() {
//		this("Buffer", buildBufferUnit());
//	}

	private Buffer(String name, CompilationUnit unit) {
		super(name, unit);
	}

//	private static C buildBufferUnit(Program program) {
//		SourceCodeLocation unknownLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
//		ClassUnit bufferUnit = new ClassUnit(unknownLocation, program, "Buffer", false);
//		return bufferUnit;
//	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		return other instanceof Buffer || other.isUntyped();
	}

	@Override
	public Type commonSupertype(Type other) {
		if (other instanceof Buffer || other.isUntyped())
			return other;
		return Untyped.INSTANCE;
	}

	@Override
	public Collection<Type> allInstances() {
		return Collections.singleton(this);
	}

	@Override
	public Expression defaultValue(CFG cfg, SourceCodeLocation location) {
		return new GoInteger(cfg, location, 0);
	}

	@Override
	public String toString() {
		return "bytes.Buffer";
	}

	@Override
	public boolean equals(Object other) {
		return this == other;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}
	
	public static Buffer getBufferType(Program program) {
		// builds the unit
		ClassUnit bufferUnit = new ClassUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, program, "Buffer", false);
		return new Buffer("Buffer", bufferUnit);
	}
	
	/**
	 * Registers the methods of Buffer type.
	 */
	public static void registerMethods() {
		CompilationUnit bufferUnit = GoStructType.get("Buffer").getUnit();
		bufferUnit.addInstanceCodeMember(new Bytes(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, bufferUnit));
	}
}
