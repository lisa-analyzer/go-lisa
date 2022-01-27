package it.unive.golisa.cli;

import it.unive.golisa.cfg.runtime.bytes.type.Buffer;
import it.unive.golisa.cfg.runtime.fmt.GoPrintln;
import it.unive.golisa.cfg.runtime.math.rand.method.ExpFloat64;
import it.unive.golisa.cfg.runtime.math.rand.method.Float32;
import it.unive.golisa.cfg.runtime.math.rand.method.Float64;
import it.unive.golisa.cfg.runtime.math.rand.method.Int;
import it.unive.golisa.cfg.runtime.math.rand.method.Int31;
import it.unive.golisa.cfg.runtime.math.rand.method.Int31n;
import it.unive.golisa.cfg.runtime.math.rand.method.Int63;
import it.unive.golisa.cfg.runtime.math.rand.method.Int63n;
import it.unive.golisa.cfg.runtime.math.rand.method.Intn;
import it.unive.golisa.cfg.runtime.math.rand.method.NormFloat64;
import it.unive.golisa.cfg.runtime.math.rand.method.Perm;
import it.unive.golisa.cfg.runtime.math.rand.method.UInt32;
import it.unive.golisa.cfg.runtime.math.rand.method.UInt64;
import it.unive.golisa.cfg.runtime.math.rand.type.Rand;
import it.unive.golisa.cfg.runtime.shim.function.Start;
import it.unive.golisa.cfg.runtime.shim.method.DelPrivateData;
import it.unive.golisa.cfg.runtime.shim.method.DelState;
import it.unive.golisa.cfg.runtime.shim.method.GetFunctionAndParameters;
import it.unive.golisa.cfg.runtime.shim.method.GetState;
import it.unive.golisa.cfg.runtime.shim.method.PutPrivateData;
import it.unive.golisa.cfg.runtime.shim.method.PutState;
import it.unive.golisa.cfg.runtime.shim.type.Chaincode;
import it.unive.golisa.cfg.runtime.shim.type.ChaincodeStub;
import it.unive.golisa.cfg.runtime.shim.type.ChaincodeStubInterface;
import it.unive.golisa.cfg.runtime.shim.type.CommonIteratorInterface;
import it.unive.golisa.cfg.runtime.shim.type.Handler;
import it.unive.golisa.cfg.runtime.shim.type.TLSProperties;
import it.unive.golisa.cfg.runtime.strconv.GoAtoi;
import it.unive.golisa.cfg.runtime.strconv.GoItoa;
import it.unive.golisa.cfg.runtime.strings.GoContains;
import it.unive.golisa.cfg.runtime.strings.GoHasPrefix;
import it.unive.golisa.cfg.runtime.strings.GoHasSuffix;
import it.unive.golisa.cfg.runtime.strings.GoIndex;
import it.unive.golisa.cfg.runtime.strings.GoIndexRune;
import it.unive.golisa.cfg.runtime.strings.GoLen;
import it.unive.golisa.cfg.runtime.strings.GoReplace;
import it.unive.golisa.cfg.runtime.time.function.Now;
import it.unive.golisa.cfg.runtime.time.function.Parse;
import it.unive.golisa.cfg.runtime.time.function.Since;
import it.unive.golisa.cfg.runtime.time.method.Day;
import it.unive.golisa.cfg.runtime.time.method.Month;
import it.unive.golisa.cfg.runtime.time.type.Duration;
import it.unive.golisa.cfg.runtime.time.type.Time;
import it.unive.golisa.cfg.runtime.url.UrlPathEscape;
import it.unive.golisa.cfg.runtime.url.UrlQueryEscape;
import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangAPISignatureMapper;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.SourceCodeLocation;

public interface GoRuntimeLoader {
	SourceCodeLocation runtimeLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);

	default void loadRuntime(String module, Program program, GoLangAPISignatureMapper mapper) {
		if (module.equals("strings"))
			loadStrings(program);
		else if (module.equals("fmt"))
			loadFmt(program);
		else if (module.equals("math/rand"))
			loadMathRand(program);
		else if (module.equals("url"))
			loadUrl(program);
		else if (module.equals("strconv"))
			loadStrconv(program);
		else if (module.equals("time"))
			loadTime(program);
		else if (module.equals("bytes"))
			loadBytes(program);
		else if (module.startsWith("github.com/hyperledger")) {
			if (module.endsWith("/shim"))
				loadShim(program);
		} else
			loadUnhandledLib(module, program, mapper);
	}

	private void loadMathRand(Program program) {
		CompilationUnit mathRand = new CompilationUnit(runtimeLocation, "rand", false);

		// adding functions
		mathRand.addConstruct(new it.unive.golisa.cfg.runtime.math.rand.function.ExpFloat64(runtimeLocation, mathRand));
		mathRand.addConstruct(
				new it.unive.golisa.cfg.runtime.math.rand.function.Float32(runtimeLocation, Rand.INSTANCE.getUnit()));
		mathRand.addConstruct(
				new it.unive.golisa.cfg.runtime.math.rand.function.Float64(runtimeLocation, Rand.INSTANCE.getUnit()));
		mathRand.addConstruct(
				new it.unive.golisa.cfg.runtime.math.rand.function.Int(runtimeLocation, Rand.INSTANCE.getUnit()));
		mathRand.addConstruct(
				new it.unive.golisa.cfg.runtime.math.rand.function.Int31(runtimeLocation, Rand.INSTANCE.getUnit()));
		mathRand.addConstruct(
				new it.unive.golisa.cfg.runtime.math.rand.function.Int31n(runtimeLocation, Rand.INSTANCE.getUnit()));
		mathRand.addConstruct(
				new it.unive.golisa.cfg.runtime.math.rand.function.Int63(runtimeLocation, Rand.INSTANCE.getUnit()));
		mathRand.addConstruct(
				new it.unive.golisa.cfg.runtime.math.rand.function.Int63n(runtimeLocation, Rand.INSTANCE.getUnit()));
		mathRand.addConstruct(
				new it.unive.golisa.cfg.runtime.math.rand.function.Intn(runtimeLocation, Rand.INSTANCE.getUnit()));
		mathRand.addConstruct(new it.unive.golisa.cfg.runtime.math.rand.function.NormFloat64(runtimeLocation,
				Rand.INSTANCE.getUnit()));
		mathRand.addConstruct(
				new it.unive.golisa.cfg.runtime.math.rand.function.Perm(runtimeLocation, Rand.INSTANCE.getUnit()));
		mathRand.addConstruct(
				new it.unive.golisa.cfg.runtime.math.rand.function.UInt32(runtimeLocation, Rand.INSTANCE.getUnit()));
		mathRand.addConstruct(
				new it.unive.golisa.cfg.runtime.math.rand.function.UInt64(runtimeLocation, Rand.INSTANCE.getUnit()));

		// adding types
		program.registerType(Rand.INSTANCE);

		// adding rand.Rand methods
		Rand.INSTANCE.getUnit().addInstanceConstruct(new ExpFloat64(runtimeLocation, Rand.INSTANCE.getUnit()));
		Rand.INSTANCE.getUnit().addInstanceConstruct(new Float32(runtimeLocation, Rand.INSTANCE.getUnit()));
		Rand.INSTANCE.getUnit().addInstanceConstruct(new Float64(runtimeLocation, Rand.INSTANCE.getUnit()));
		Rand.INSTANCE.getUnit().addInstanceConstruct(new Int(runtimeLocation, Rand.INSTANCE.getUnit()));
		Rand.INSTANCE.getUnit().addInstanceConstruct(new Int31(runtimeLocation, Rand.INSTANCE.getUnit()));
		Rand.INSTANCE.getUnit().addInstanceConstruct(new Int31n(runtimeLocation, Rand.INSTANCE.getUnit()));
		Rand.INSTANCE.getUnit().addInstanceConstruct(new Int63(runtimeLocation, Rand.INSTANCE.getUnit()));
		Rand.INSTANCE.getUnit().addInstanceConstruct(new Int63n(runtimeLocation, Rand.INSTANCE.getUnit()));
		Rand.INSTANCE.getUnit().addInstanceConstruct(new Intn(runtimeLocation, Rand.INSTANCE.getUnit()));
		Rand.INSTANCE.getUnit().addInstanceConstruct(new NormFloat64(runtimeLocation, Rand.INSTANCE.getUnit()));
		Rand.INSTANCE.getUnit().addInstanceConstruct(new Perm(runtimeLocation, Rand.INSTANCE.getUnit()));
		Rand.INSTANCE.getUnit().addInstanceConstruct(new UInt32(runtimeLocation, Rand.INSTANCE.getUnit()));
		Rand.INSTANCE.getUnit().addInstanceConstruct(new UInt64(runtimeLocation, Rand.INSTANCE.getUnit()));

		program.addCompilationUnit(mathRand);
	}

	private void loadBytes(Program program) {
		CompilationUnit bytes = new CompilationUnit(runtimeLocation, "bytes", false);

		// adding functions and methods
		bytes.addConstruct(new Start(runtimeLocation, bytes));

		// adding types
		program.registerType(Buffer.INSTANCE);

		program.addCompilationUnit(bytes);
	}

	private void loadShim(Program program) {
		CompilationUnit shim = new CompilationUnit(runtimeLocation, "shim", false);

		// adding functions
		shim.addConstruct(new Start(runtimeLocation, shim));

		// adding types
		program.registerType(Chaincode.INSTANCE);
		program.registerType(ChaincodeStub.INSTANCE);
		program.registerType(ChaincodeStubInterface.INSTANCE);
		ChaincodeStubInterface.registerMethods();

		program.registerType(CommonIteratorInterface.INSTANCE);
		program.registerType(Handler.INSTANCE);
		program.registerType(TLSProperties.INSTANCE);

		// adding ChainCodeStub methods
		ChaincodeStub.INSTANCE.getUnit()
				.addInstanceConstruct(new DelPrivateData(runtimeLocation, ChaincodeStub.INSTANCE.getUnit()));
		ChaincodeStub.INSTANCE.getUnit()
				.addInstanceConstruct(new DelState(runtimeLocation, ChaincodeStub.INSTANCE.getUnit()));
		ChaincodeStub.INSTANCE.getUnit()
				.addInstanceConstruct(new GetFunctionAndParameters(runtimeLocation, ChaincodeStub.INSTANCE.getUnit()));
		ChaincodeStub.INSTANCE.getUnit()
				.addInstanceConstruct(new PutPrivateData(runtimeLocation, ChaincodeStub.INSTANCE.getUnit()));
		ChaincodeStub.INSTANCE.getUnit()
				.addInstanceConstruct(new PutState(runtimeLocation, ChaincodeStub.INSTANCE.getUnit()));
		ChaincodeStub.INSTANCE.getUnit()
				.addInstanceConstruct(new GetState(runtimeLocation, ChaincodeStub.INSTANCE.getUnit()));

		program.addCompilationUnit(shim);
	}

	private void loadUrl(Program program) {
		CompilationUnit url = new CompilationUnit(runtimeLocation, "url", false);
		url.addConstruct(new UrlQueryEscape(runtimeLocation, url));
		url.addConstruct(new UrlPathEscape(runtimeLocation, url));

		program.addCompilationUnit(url);
	}

	private void loadStrings(Program program) {
		CompilationUnit str = new CompilationUnit(runtimeLocation, "strings", false);
		str.addConstruct(new GoHasPrefix(runtimeLocation, str));
		str.addConstruct(new GoHasSuffix(runtimeLocation, str));
		str.addConstruct(new GoContains(runtimeLocation, str));
		str.addConstruct(new GoReplace(runtimeLocation, str));
		str.addConstruct(new GoIndex(runtimeLocation, str));
		str.addConstruct(new GoIndexRune(runtimeLocation, str));
		str.addConstruct(new GoLen(runtimeLocation, str));

		program.addCompilationUnit(str);
	}

	private void loadStrconv(Program program) {
		CompilationUnit strconv = new CompilationUnit(runtimeLocation, "strconv", false);
		strconv.addConstruct(new GoAtoi(runtimeLocation, strconv));
		strconv.addConstruct(new GoItoa(runtimeLocation, strconv));

		program.addCompilationUnit(strconv);
	}

	private void loadFmt(Program program) {
		CompilationUnit fmt = new CompilationUnit(runtimeLocation, "fmt", false);
		fmt.addConstruct(new GoPrintln(runtimeLocation, fmt));

		program.addCompilationUnit(fmt);
	}

	private void loadTime(Program program) {
		CompilationUnit time = new CompilationUnit(runtimeLocation, "time", false);

		// adding functions and methods
		time.addConstruct(new Now(runtimeLocation, time));
		time.addConstruct(new Since(runtimeLocation, time));
		time.addConstruct(new Day(runtimeLocation, time));
		time.addConstruct(new Month(runtimeLocation, time));
		time.addConstruct(new Parse(runtimeLocation, time));

		// adding types
		program.registerType(Time.INSTANCE);
		GoStructType.lookup(Time.INSTANCE.getUnit().getName(), Time.INSTANCE.getUnit());

		program.registerType(it.unive.golisa.cfg.runtime.time.type.Month.INSTANCE);
		program.registerType(Duration.INSTANCE);

		program.addCompilationUnit(time);
	}

	private void loadUnhandledLib(String lib, Program program, GoLangAPISignatureMapper mapper) {

		SourceCodeLocation runTimeSourceLocation;
		if (mapper.getPackages().contains(lib))
			// it is a package contained in Go APIs
			runTimeSourceLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
		else
			runTimeSourceLocation = new SourceCodeLocation("unknown", 0, 0);

		CompilationUnit cu = new CompilationUnit(runTimeSourceLocation, lib, false);
		program.addCompilationUnit(cu);
	}

}
