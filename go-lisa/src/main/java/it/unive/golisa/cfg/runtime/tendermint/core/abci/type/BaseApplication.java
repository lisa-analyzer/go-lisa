package it.unive.golisa.cfg.runtime.tendermint.core.abci.type;

import it.unive.golisa.cfg.runtime.tendermint.core.abci.method.BeginBlock;
import it.unive.golisa.cfg.runtime.tendermint.core.abci.method.Commit;
import it.unive.golisa.cfg.runtime.tendermint.core.abci.method.DeliverTx;
import it.unive.golisa.cfg.runtime.tendermint.core.abci.method.EndBlock;
import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.SourceCodeLocation;

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
	public static final BaseApplication INSTANCE = new BaseApplication();

	private BaseApplication() {
		this("BaseApplication", buildBaseApplicationUnit());
	}

	private BaseApplication(String name, CompilationUnit unit) {
		super(name, unit);
	}

	private static CompilationUnit buildBaseApplicationUnit() {
		SourceCodeLocation unknownLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
		CompilationUnit abciUnit = new CompilationUnit(unknownLocation, "BaseApplication", false);
		return abciUnit;
	}

	/**
	 * Registers the methods of the {@link BaseApplication} type.
	 */
	public static void registerMethods() {
		SourceCodeLocation runtimeLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);

		BaseApplication.INSTANCE.getUnit()
				.addInstanceConstruct(new BeginBlock(runtimeLocation, BaseApplication.INSTANCE.getUnit()));
		BaseApplication.INSTANCE.getUnit()
				.addInstanceConstruct(new DeliverTx(runtimeLocation, BaseApplication.INSTANCE.getUnit()));
		BaseApplication.INSTANCE.getUnit()
				.addInstanceConstruct(new EndBlock(runtimeLocation, BaseApplication.INSTANCE.getUnit()));
		BaseApplication.INSTANCE.getUnit()
				.addInstanceConstruct(new Commit(runtimeLocation, BaseApplication.INSTANCE.getUnit()));
	}

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
