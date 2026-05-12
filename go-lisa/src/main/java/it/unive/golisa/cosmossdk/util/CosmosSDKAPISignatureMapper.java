package it.unive.golisa.cosmossdk.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import it.unive.golisa.golang.api.signature.ConstGoLangApiSignature;
import it.unive.golisa.golang.api.signature.FuncGoLangApiSignature;
import it.unive.golisa.golang.api.signature.MethodGoLangApiSignature;
import it.unive.golisa.golang.api.signature.TypeGoLangApiSignature;
import it.unive.golisa.golang.api.signature.VarGoLangApiSignature;

public class CosmosSDKAPISignatureMapper {

	/**
	 * The instance of CosmosSDKAPISignatureMapper
	 */
	private static CosmosSDKAPISignatureMapper instance = null;

	/**
	 * Yields the Go API signatures.
	 * @return the signatures
	 */
	public static synchronized CosmosSDKAPISignatureMapper getGoApiSignatures() {
		// singleton pattern
		if (instance == null) {
			instance = new CosmosSDKAPISignatureMapper();
		}
		return instance;
	}

	private final Map<String, Set<ConstGoLangApiSignature>> mapConst;
	private final Map<String, Set<FuncGoLangApiSignature>> mapFunc;
	private final Map<String, Set<MethodGoLangApiSignature>> mapMethod;
	private final Map<String, Set<TypeGoLangApiSignature>> mapType;
	private final Map<String, Set<VarGoLangApiSignature>> mapVar;
	private final Set<String> pkgs;

	private CosmosSDKAPISignatureMapper() {
		long t = System.currentTimeMillis();
		pkgs = new HashSet<>();
		mapConst = new HashMap<String, Set<ConstGoLangApiSignature>>();
		mapFunc = new HashMap<String, Set<FuncGoLangApiSignature>>();
		mapMethod = new HashMap<String, Set<MethodGoLangApiSignature>>();
		mapType = new HashMap<String, Set<TypeGoLangApiSignature>>();
		mapVar = new HashMap<String, Set<VarGoLangApiSignature>>();

		build();

		t = System.currentTimeMillis() - t;
		System.out.println(t);
	}

	/**
	 * Yields the packages.
	 * @return the packages
	 */
	public Set<String> getPackages() {
		return pkgs;
	}

	/**
	 * Yields the mapping of constants.
	 * @return the mapping
	 */
	public Map<String, Set<ConstGoLangApiSignature>> getMapConst() {
		return mapConst;
	}

	/**
	 * Yields the mapping of functions.
	 * @return the mapping
	 */
	public Map<String, Set<FuncGoLangApiSignature>> getMapFunc() {
		return mapFunc;
	}

	/**
	 * Yields the mapping of methods.
	 * @return the mapping
	 */
	public Map<String, Set<MethodGoLangApiSignature>> getMapMethod() {
		return mapMethod;
	}

	/**
	 * Yields the mapping of type.
	 * @return the mapping
	 */
	public Map<String, Set<TypeGoLangApiSignature>> getMapType() {
		return mapType;
	}

	/**
	 * Yields the mapping of variables.
	 * @return the mapping
	 */
	public Map<String, Set<VarGoLangApiSignature>> getMapVar() {
		return mapVar;
	}

	private void build() {
		String pkg = "telemetry";
		pkgs.add(pkg);
		Set<FuncGoLangApiSignature> set = new HashSet<>();
		set.add(new FuncGoLangApiSignature(pkg, "EnableTelemetry", new String[] {""} , new String[] {""}));
		set.add(new FuncGoLangApiSignature(pkg, "IncrCounter", new String[] {"float32", "...string"} , new String[] {""}));
		set.add(new FuncGoLangApiSignature(pkg, "ncrCounterWithLabels ", new String[] {"[]string", "float32", "[]metrics.Label"} , new String[] {""}));
		set.add(new FuncGoLangApiSignature(pkg, "IsTelemetryEnabled", new String[] {""} , new String[] {"bool"}));
		set.add(new FuncGoLangApiSignature(pkg, "MeasureSince", new String[] {"time.Time", "...string"} , new String[] {""}));
		set.add(new FuncGoLangApiSignature(pkg, "ModuleMeasureSince", new String[] {"string", "time.Time", "...string"} , new String[] {""}));
		set.add(new FuncGoLangApiSignature(pkg, "ModuleSetGauge", new String[] {"string", "float32", "...string"} , new String[] {""}));
		set.add(new FuncGoLangApiSignature(pkg, "NewLabel", new String[] {"string", "string"} , new String[] {"metrics.Label"}));
		set.add(new FuncGoLangApiSignature(pkg, "Now", new String[] {""} , new String[] {"time.Time"}));
		set.add(new FuncGoLangApiSignature(pkg, "SetGauge", new String[] {"float32", "...string"} , new String[] {""}));
		set.add(new FuncGoLangApiSignature(pkg, "SetGaugeWithLabels", new String[] {"[]string", "float32", "[]metrics.Label"} , new String[] {""}));
		mapFunc.put(pkg, set);

	}

}