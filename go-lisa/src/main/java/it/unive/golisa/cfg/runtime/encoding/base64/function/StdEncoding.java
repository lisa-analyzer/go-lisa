package it.unive.golisa.cfg.runtime.encoding.base64.function;


import it.unive.golisa.cfg.type.composite.GoInterfaceType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.InterfaceUnit;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.Expression;

/**
 * A TransactionContextInterface type.
 * 
 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
 */
public class StdEncoding extends GoInterfaceType {

	/**
	 * Unique instance of {@link StdEncoding} type.
	 */
	private static StdEncoding INSTANCE;

	private StdEncoding(CompilationUnit unit) {
		super("StdEncoding", unit);
	}

	/**
	 * Yields the {@link StdEncoding} type.
	 * 
	 * @param program the program to which this type belongs
	 * 
	 * @return the {@link StdEncoding} type
	 */
	public static StdEncoding getStdEncodingType(Program program) {
		
		if (INSTANCE == null) {
			InterfaceUnit stdEncodingUnit = new InterfaceUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION,
					program, "StdEncoding",
					false);
			StdEncoding stdEncodingType = new StdEncoding(stdEncodingUnit);

			INSTANCE = stdEncodingType;
		}
		return INSTANCE;
	}

	
	
	@Override
	public String toString() {
		return "base64.StdEncoding";
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
	 * Registers the methods of the {@link StdEncoding} type.
	 */
	public static void registerMethods() {
		
		CompilationUnit unit = INSTANCE.getUnit();
		unit.addInstanceCodeMember(new DecodeString(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, unit));

		
	}

	@Override
	public Expression defaultValue(CFG cfg, CodeLocation location) {
		// TODO Auto-generated method stub
		return super.defaultValue(cfg, location);
	}
	
	

}
