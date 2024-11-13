package it.unive.golisa.cfg.runtime.encoding.base64.function;


import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoInterfaceType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.InterfaceUnit;
import it.unive.lisa.program.Program;

/**
 * A TransactionContextInterface type.
 * 
 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
 */
public class Encoding extends GoInterfaceType {

	/**
	 * Unique instance of {@link Encoding} type.
	 */
	private static Encoding INSTANCE;

	private Encoding(CompilationUnit unit) {
		super("Encoding", unit);
	}

	/**
	 * Yields the {@link Encoding} type.
	 * 
	 * @param program the program to which this type belongs
	 * 
	 * @return the {@link Encoding} type
	 */
	public static Encoding getEncodingType(Program program) {
		
		if (INSTANCE == null) {
			InterfaceUnit encodingUnit = new InterfaceUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION,
					program, "Encoding",
					false);
			Encoding encodingType = new Encoding(encodingUnit);
			
			//Add field
		/*	encodingUnit
			.addInstanceGlobal(new Global(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, encodingUnit,
					"ChannelID", true, GoStringType.INSTANCE));
		 
		 */
			INSTANCE = encodingType;
		}
		return INSTANCE;
	}

	
	
	@Override
	public String toString() {
		return "base64.Encoding";
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
	 * Registers the methods of the {@link Encoding} type.
	 */
	public static void registerMethods() {
		
		CompilationUnit unit = INSTANCE.getUnit();
		unit.addInstanceCodeMember(new DecodeString(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, unit));

		
	}

}
