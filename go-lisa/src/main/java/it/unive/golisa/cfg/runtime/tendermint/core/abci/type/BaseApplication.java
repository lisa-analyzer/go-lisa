package it.unive.golisa.cfg.runtime.tendermint.core.abci.type;

import it.unive.golisa.cfg.runtime.tendermint.core.abci.method.BeginBlock;
import it.unive.golisa.cfg.runtime.tendermint.core.abci.method.Commit;
import it.unive.golisa.cfg.runtime.tendermint.core.abci.method.DeliverTx;
import it.unive.golisa.cfg.runtime.tendermint.core.abci.method.EndBlock;
import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.ClassUnit;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Program;

/**
 * A Base Application type.
 * 
 * @link https://pkg.go.dev/github.com/tendermint/tendermint/abci/types#BaseApplication
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class BaseApplication extends GoStructType {

	/**
	 * Unique instance of the {@link BaseApplication} type.
	 */
	// public static final BaseApplication INSTANCE = new BaseApplication();
	//
	// private BaseApplication() {
	// this(, buildBaseApplicationUnit());
	// }

	private BaseApplication(CompilationUnit unit) {
		super("BaseApplication", unit);
	}

	public static BaseApplication etBaseApplicationType(Program program) {
		ClassUnit abciUnit = new ClassUnit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, program, "BaseApplication",
				false);

		// add methods
		abciUnit
				.addInstanceCodeMember(new BeginBlock(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, abciUnit));
		abciUnit
				.addInstanceCodeMember(new DeliverTx(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, abciUnit));
		abciUnit
				.addInstanceCodeMember(new EndBlock(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, abciUnit));
		abciUnit
				.addInstanceCodeMember(new Commit(GoLangUtils.GO_RUNTIME_SOURCECODE_LOCATION, abciUnit));

		return new BaseApplication(abciUnit);
	}

	// /**
	// * Registers the methods of the {@link BaseApplication} type.
	// */
	// public static void registerMethods() {
	// SourceCodeLocation runtimeLocation = new
	// SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
	//
	//
	// }

	@Override
	public String toString() {
		return "abci.types.BaseApplication";
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
