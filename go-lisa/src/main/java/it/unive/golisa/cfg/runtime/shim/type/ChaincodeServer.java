package it.unive.golisa.cfg.runtime.shim.type;

import it.unive.golisa.cfg.runtime.shim.method.Start;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoInterfaceType;
import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.ClassUnit;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.Program;

/**
 * A ChaincodeServer type.
 * 
 * @link https://pkg.go.dev/github.com/hyperledger/fabric-chaincode-go/shim#ChaincodeServer
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class ChaincodeServer extends GoStructType {

	/**
	 * Unique instance of the {@link ChaincodeServer} type.
	 */
	private static ChaincodeServer INSTANCE;

	private ChaincodeServer(CompilationUnit unit) {
		super("ChaincodeServer", unit);
	}

	public static ChaincodeServer getChaincodeServerType(Program program) {
		if (INSTANCE == null) {
			ClassUnit chaincodeServerUnit = new ClassUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, program,
					"ChaincodeServer", false);

			// Add globals
			chaincodeServerUnit.addGlobal(new Global(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeServerUnit,
					"CCID", true, GoStringType.INSTANCE));
			chaincodeServerUnit.addGlobal(new Global(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeServerUnit,
					"Address", true, GoStringType.INSTANCE));
			chaincodeServerUnit.addGlobal(new Global(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeServerUnit, "CC",
					true, GoInterfaceType.get("Chaincode")));
			chaincodeServerUnit.addGlobal(new Global(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeServerUnit,
					"TLSProps", true, TLSProperties.getTLSPropertiesType(program)));

			// TODO: missing KaOpts *keepalive.ServerParameters
			INSTANCE = new ChaincodeServer(chaincodeServerUnit);
		}
		
		return INSTANCE;
	}

	/**
	 * Registers the methods of the {@link ChaincodeStub} type.
	 */
	public static void registerMethods() {
		CompilationUnit chaincodeServerUnit = INSTANCE.getUnit();
		chaincodeServerUnit
		.addInstanceCodeMember(new Start(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, chaincodeServerUnit));
	}

	@Override
	public String toString() {
		return "shim.ChaincodeServer";
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
