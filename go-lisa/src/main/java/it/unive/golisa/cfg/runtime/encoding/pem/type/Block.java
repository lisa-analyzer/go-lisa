package it.unive.golisa.cfg.runtime.encoding.pem.type;

import it.unive.golisa.cfg.runtime.encoding.pem.function.Decode;
import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.ClassUnit;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Program;

/**
 * A Block type.
 * 
 * @link https://pkg.go.dev/encoding/pem#Block
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class Block extends GoStructType {

	/**
	 * Unique instance of {@link Block} type.
	 */
	public static Block INSTANCE;

	private Block(String name, CompilationUnit unit) {
		super(name, unit);
	}

	@Override
	public String toString() {
		return "pem.Block";
	}

	@Override
	public boolean equals(Object other) {
		return this == other;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}

	/**
	 * Yields the {@link Block} type.
	 * 
	 * @param program the program to which this type belongs
	 * 
	 * @return the {@link Block} type
	 */
	public static Block getBlockType(Program program) {
		if (INSTANCE == null) {
			ClassUnit fileInfoUnit = new ClassUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, program, "Block",
					false);
			INSTANCE = new Block("Block", fileInfoUnit);
			return INSTANCE;
		}

		return INSTANCE;
	}
	

	/**
	 * Registers the methods of List type.
	 */
	public static void registerMethods() {
		CompilationUnit unit = INSTANCE.getUnit();
		unit.addInstanceCodeMember(new Decode(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, unit));
	}
}
