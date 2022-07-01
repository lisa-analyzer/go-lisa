package it.unive.golisa.frontend;

import it.unive.golisa.cfg.runtime.bytes.type.Buffer;
import it.unive.golisa.cfg.runtime.cosmos.time.Grant;
import it.unive.golisa.cfg.runtime.cosmossdk.types.errors.function.Wrap;
import it.unive.golisa.cfg.runtime.encoding.json.function.Compact;
import it.unive.golisa.cfg.runtime.encoding.json.function.HtmlEscape;
import it.unive.golisa.cfg.runtime.encoding.json.function.Indent;
import it.unive.golisa.cfg.runtime.encoding.json.function.Marshal;
import it.unive.golisa.cfg.runtime.encoding.json.function.MarshalIndent;
import it.unive.golisa.cfg.runtime.encoding.json.function.Unmarshal;
import it.unive.golisa.cfg.runtime.encoding.json.function.Valid;
import it.unive.golisa.cfg.runtime.fmt.Println;
import it.unive.golisa.cfg.runtime.io.function.Copy;
import it.unive.golisa.cfg.runtime.io.function.CopyBuffer;
import it.unive.golisa.cfg.runtime.io.function.CopyN;
import it.unive.golisa.cfg.runtime.io.function.Pipe;
import it.unive.golisa.cfg.runtime.io.function.ReadAtLeast;
import it.unive.golisa.cfg.runtime.io.function.WriteString;
import it.unive.golisa.cfg.runtime.io.ioutil.function.NopCloser;
import it.unive.golisa.cfg.runtime.io.ioutil.function.ReadAll;
import it.unive.golisa.cfg.runtime.io.ioutil.function.ReadDir;
import it.unive.golisa.cfg.runtime.io.ioutil.function.TempDir;
import it.unive.golisa.cfg.runtime.io.ioutil.function.TempFile;
import it.unive.golisa.cfg.runtime.io.ioutil.function.WriteFile;
import it.unive.golisa.cfg.runtime.io.type.PipeReader;
import it.unive.golisa.cfg.runtime.io.type.PipeWriter;
import it.unive.golisa.cfg.runtime.io.type.Reader;
import it.unive.golisa.cfg.runtime.io.type.Writer;
import it.unive.golisa.cfg.runtime.math.rand.function.ExpFloat64;
import it.unive.golisa.cfg.runtime.math.rand.function.Float32;
import it.unive.golisa.cfg.runtime.math.rand.function.Float64;
import it.unive.golisa.cfg.runtime.math.rand.function.Int;
import it.unive.golisa.cfg.runtime.math.rand.function.Int31;
import it.unive.golisa.cfg.runtime.math.rand.function.Int31n;
import it.unive.golisa.cfg.runtime.math.rand.function.Int63;
import it.unive.golisa.cfg.runtime.math.rand.function.Int63n;
import it.unive.golisa.cfg.runtime.math.rand.function.Intn;
import it.unive.golisa.cfg.runtime.math.rand.function.NormFloat64;
import it.unive.golisa.cfg.runtime.math.rand.function.Perm;
import it.unive.golisa.cfg.runtime.math.rand.function.Read;
import it.unive.golisa.cfg.runtime.math.rand.function.UInt32;
import it.unive.golisa.cfg.runtime.math.rand.function.UInt64;
import it.unive.golisa.cfg.runtime.math.rand.method.Seed;
import it.unive.golisa.cfg.runtime.math.rand.method.Shuffle;
import it.unive.golisa.cfg.runtime.math.rand.type.Rand;
import it.unive.golisa.cfg.runtime.os.file.function.Create;
import it.unive.golisa.cfg.runtime.os.file.function.CreateTemp;
import it.unive.golisa.cfg.runtime.os.file.function.NewFile;
import it.unive.golisa.cfg.runtime.os.file.function.Open;
import it.unive.golisa.cfg.runtime.os.file.function.OpenFile;
import it.unive.golisa.cfg.runtime.os.function.Executable;
import it.unive.golisa.cfg.runtime.os.function.Exit;
import it.unive.golisa.cfg.runtime.os.function.Getenv;
import it.unive.golisa.cfg.runtime.os.function.IsNotExist;
import it.unive.golisa.cfg.runtime.os.function.MkdirAll;
import it.unive.golisa.cfg.runtime.os.function.ReadFile;
import it.unive.golisa.cfg.runtime.os.function.RemoveAll;
import it.unive.golisa.cfg.runtime.os.function.Setenv;
import it.unive.golisa.cfg.runtime.os.function.Unsetenv;
import it.unive.golisa.cfg.runtime.os.type.File;
import it.unive.golisa.cfg.runtime.os.type.FileMode;
import it.unive.golisa.cfg.runtime.path.filepath.function.Dir;
import it.unive.golisa.cfg.runtime.path.filepath.function.Join;
import it.unive.golisa.cfg.runtime.pkg.statebased.function.NewStateEP;
import it.unive.golisa.cfg.runtime.pkg.statebased.type.KeyEndorsementPolicy;
import it.unive.golisa.cfg.runtime.shim.function.Start;
import it.unive.golisa.cfg.runtime.shim.function.Success;
import it.unive.golisa.cfg.runtime.shim.type.Chaincode;
import it.unive.golisa.cfg.runtime.shim.type.ChaincodeStub;
import it.unive.golisa.cfg.runtime.shim.type.ChaincodeStubInterface;
import it.unive.golisa.cfg.runtime.shim.type.CommonIteratorInterface;
import it.unive.golisa.cfg.runtime.shim.type.Handler;
import it.unive.golisa.cfg.runtime.shim.type.TLSProperties;
import it.unive.golisa.cfg.runtime.strconv.Atoi;
import it.unive.golisa.cfg.runtime.strconv.Itoa;
import it.unive.golisa.cfg.runtime.strings.Contains;
import it.unive.golisa.cfg.runtime.strings.HasPrefix;
import it.unive.golisa.cfg.runtime.strings.HasSuffix;
import it.unive.golisa.cfg.runtime.strings.Index;
import it.unive.golisa.cfg.runtime.strings.IndexRune;
import it.unive.golisa.cfg.runtime.strings.Len;
import it.unive.golisa.cfg.runtime.strings.Replace;
import it.unive.golisa.cfg.runtime.strings.ToLower;
import it.unive.golisa.cfg.runtime.time.function.Now;
import it.unive.golisa.cfg.runtime.time.function.Parse;
import it.unive.golisa.cfg.runtime.time.function.Since;
import it.unive.golisa.cfg.runtime.time.method.Day;
import it.unive.golisa.cfg.runtime.time.method.Month;
import it.unive.golisa.cfg.runtime.time.method.Unix;
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

/**
 * The interface managing the loading of runtimes.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public interface GoRuntimeLoader {
	SourceCodeLocation runtimeLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);

	default void loadRuntime(String module, Program program, GoLangAPISignatureMapper mapper) {
		if (module.equals("strings"))
			loadStrings(program);
		else if (module.equals("fmt"))
			loadFmt(program);
		else if (module.equals("math/rand"))
			loadMathRand(program);
		else if (module.equals("io"))
			loadIO(program);
		else if (module.equals("io/ioutil"))
			loadIoutil(program);
		else if (module.equals("crypto/rand"))
			loadCryptoRand(program);
		else if (module.equals("url"))
			loadUrl(program);
		else if (module.equals("path/filepath"))
			loadFilePath(program);
		else if (module.equals("strconv"))
			loadStrconv(program);
		else if (module.equals("time"))
			loadTime(program);
		else if (module.equals("os"))
			loadOs(program);
		else if (module.equals("bytes"))
			loadBytes(program);
		else if (module.equals("encoding/json"))
			loadJson(program);
		else if (module.startsWith("github.com/hyperledger")) {
			if (module.endsWith("/shim"))
				loadShim(program);
			if (module.endsWith("pkg/statebased"))
				loadStateBased(program);
		} else if (module.startsWith("github.com/cosmos/cosmos-sdk")) {
			loadCosmosTypes(program);
			if (module.endsWith("/errors"))
				loadCosmosErrors(program);
		} else
			loadUnhandledLib(module, program, mapper);
	}

	private void loadCosmosErrors(Program program) {
		CompilationUnit sdkerrors = new CompilationUnit(runtimeLocation, "sdkerrors", false);
		// adding functions
		sdkerrors.addConstruct(new Wrap(runtimeLocation, sdkerrors));
		// adding compilation units to program
		program.addCompilationUnit(sdkerrors);
	}

	private void loadCosmosTypes(Program program) {
		program.registerType(Grant.INSTANCE);
		GoStructType.lookup(Grant.INSTANCE.getUnit().getName(), Grant.INSTANCE.getUnit());
	}

	private void loadFilePath(Program program) {
		CompilationUnit filepath = new CompilationUnit(runtimeLocation, "filepath", false);

		// adding functions
		filepath.addConstruct(new Dir(runtimeLocation, filepath));
		filepath.addConstruct(new Join(runtimeLocation, filepath));

		// adding compilation units to program
		program.addCompilationUnit(filepath);
	}

	private void loadIO(Program program) {
		CompilationUnit io = new CompilationUnit(runtimeLocation, "io", false);

		// adding functions
		io.addConstruct(new Copy(runtimeLocation, io));
		io.addConstruct(new CopyBuffer(runtimeLocation, io));
		io.addConstruct(new CopyN(runtimeLocation, io));
		io.addConstruct(new Pipe(runtimeLocation, io));
		io.addConstruct(new ReadAll(runtimeLocation, io));
		io.addConstruct(new ReadAtLeast(runtimeLocation, io));
		io.addConstruct(new WriteString(runtimeLocation, io));

		// adding compilation units to program
		program.addCompilationUnit(io);
	}

	private void loadIoutil(Program program) {
		CompilationUnit ioutil = new CompilationUnit(runtimeLocation, "ioutil", false);

		// adding functions
		ioutil.addConstruct(new NopCloser(runtimeLocation, ioutil));
		ioutil.addConstruct(new ReadAll(runtimeLocation, ioutil));
		ioutil.addConstruct(new ReadDir(runtimeLocation, ioutil));
		ioutil.addConstruct(new ReadFile(runtimeLocation, ioutil));
		ioutil.addConstruct(new TempDir(runtimeLocation, ioutil));
		ioutil.addConstruct(new TempFile(runtimeLocation, ioutil));
		ioutil.addConstruct(new WriteFile(runtimeLocation, ioutil));

		// adding types
		program.registerType(PipeReader.INSTANCE);
		PipeReader.registerMethods();

		program.registerType(PipeWriter.INSTANCE);
		PipeWriter.registerMethods();

		program.registerType(Reader.INSTANCE);
		Reader.registerMethods();

		program.registerType(Writer.INSTANCE);
		Writer.registerMethods();

		// adding compilation units to program
		program.addCompilationUnit(ioutil);
	}

	private void loadOs(Program program) {
		CompilationUnit os = new CompilationUnit(runtimeLocation, "os", false);

		// adding functions

		// os/file
		os.addConstruct(new Create(runtimeLocation, os));
		os.addConstruct(new CreateTemp(runtimeLocation, os));
		os.addConstruct(new NewFile(runtimeLocation, os));
		os.addConstruct(new Open(runtimeLocation, os));
		os.addConstruct(new OpenFile(runtimeLocation, os));

		// os
		os.addConstruct(new Executable(runtimeLocation, os));
		os.addConstruct(new Exit(runtimeLocation, os));
		os.addConstruct(new Getenv(runtimeLocation, os));
		os.addConstruct(new IsNotExist(runtimeLocation, os));
		os.addConstruct(new MkdirAll(runtimeLocation, os));
		os.addConstruct(new ReadFile(runtimeLocation, os));
		os.addConstruct(new RemoveAll(runtimeLocation, os));
		os.addConstruct(new Setenv(runtimeLocation, os));
		os.addConstruct(new Unsetenv(runtimeLocation, os));

		// adding types
		program.registerType(File.INSTANCE);
		program.registerType(FileMode.INSTANCE);
		File.registerMethods();

		// adding compilation unit to program
		program.addCompilationUnit(os);
		program.addCompilationUnit(File.INSTANCE.getUnit());
		program.addCompilationUnit(FileMode.INSTANCE.getUnit());

	}

	private void loadCryptoRand(Program program) {
		CompilationUnit cryptoRand = new CompilationUnit(runtimeLocation, "rand", false);

		// adding functions
		cryptoRand.addConstruct(new it.unive.golisa.cfg.runtime.crypto.rand.function.Int(runtimeLocation, cryptoRand));
		cryptoRand.addConstruct(new it.unive.golisa.cfg.runtime.crypto.rand.function.Read(runtimeLocation, cryptoRand));
		cryptoRand
				.addConstruct(new it.unive.golisa.cfg.runtime.crypto.rand.function.Prime(runtimeLocation, cryptoRand));

		// adding compilation units to program
		program.addCompilationUnit(cryptoRand);
	}

	private void loadJson(Program program) {
		CompilationUnit jsonUnit = new CompilationUnit(runtimeLocation, "json", false);

		// adding functions
		jsonUnit.addConstruct(new Compact(runtimeLocation, jsonUnit));
		jsonUnit.addConstruct(new HtmlEscape(runtimeLocation, jsonUnit));
		jsonUnit.addConstruct(new Indent(runtimeLocation, jsonUnit));
		jsonUnit.addConstruct(new Marshal(runtimeLocation, jsonUnit));
		jsonUnit.addConstruct(new MarshalIndent(runtimeLocation, jsonUnit));
		jsonUnit.addConstruct(new Unmarshal(runtimeLocation, jsonUnit));
		jsonUnit.addConstruct(new Valid(runtimeLocation, jsonUnit));

		program.addCompilationUnit(jsonUnit);
	}

	private void loadMathRand(Program program) {
		CompilationUnit mathRand = new CompilationUnit(runtimeLocation, "rand", false);

		// adding functions
		mathRand.addConstruct(new ExpFloat64(runtimeLocation, mathRand));
		mathRand.addConstruct(
				new Float32(runtimeLocation, mathRand));
		mathRand.addConstruct(
				new Float64(runtimeLocation, mathRand));
		mathRand.addConstruct(
				new Int(runtimeLocation, mathRand));
		mathRand.addConstruct(
				new Int31(runtimeLocation, mathRand));
		mathRand.addConstruct(
				new Int31n(runtimeLocation, mathRand));
		mathRand.addConstruct(
				new Int63(runtimeLocation, mathRand));
		mathRand.addConstruct(
				new Int63n(runtimeLocation, mathRand));
		mathRand.addConstruct(
				new Intn(runtimeLocation, mathRand));
		mathRand.addConstruct(new NormFloat64(runtimeLocation,
				mathRand));
		mathRand.addConstruct(
				new Perm(runtimeLocation, mathRand));
		mathRand.addConstruct(
				new UInt32(runtimeLocation, mathRand));
		mathRand.addConstruct(
				new UInt64(runtimeLocation, mathRand));
		mathRand.addConstruct(
				new Read(runtimeLocation, mathRand));
		mathRand.addConstruct(
				new Seed(runtimeLocation, mathRand));
		mathRand.addConstruct(
				new Shuffle(runtimeLocation, mathRand));

		// adding types
		program.registerType(Rand.INSTANCE);
		Rand.registerMethods();

		// adding compilation units to program
		program.addCompilationUnit(mathRand);
	}

	private void loadBytes(Program program) {
		CompilationUnit bytes = new CompilationUnit(runtimeLocation, "bytes", false);

		// adding functions and methods

		// adding types
		program.registerType(Buffer.INSTANCE);

		program.addCompilationUnit(bytes);
	}

	private void loadStateBased(Program program) {
		CompilationUnit statebased = new CompilationUnit(runtimeLocation, "statebased", false);

		// adding functions
		statebased.addConstruct(new NewStateEP(runtimeLocation, statebased));

		// adding types
		program.registerType(KeyEndorsementPolicy.INSTANCE);

		// adding compilation unit to program
		program.addCompilationUnit(statebased);
	}

	private void loadShim(Program program) {
		CompilationUnit shim = new CompilationUnit(runtimeLocation, "shim", false);

		// adding functions
		shim.addConstruct(new Start(runtimeLocation, shim));
		shim.addConstruct(new it.unive.golisa.cfg.runtime.shim.function.Error(runtimeLocation, shim));
		shim.addConstruct(new Success(runtimeLocation, shim));

		// adding types
		program.registerType(ChaincodeStub.INSTANCE);
		ChaincodeStub.registerMethods();

		program.addCompilationUnit(ChaincodeStubInterface.INSTANCE.getUnit());
		ChaincodeStubInterface.registerMethods();

		program.registerType(CommonIteratorInterface.INSTANCE);
		program.registerType(Handler.INSTANCE);
		program.registerType(TLSProperties.INSTANCE);
		program.registerType(ChaincodeStubInterface.INSTANCE);
		program.registerType(Chaincode.INSTANCE);

		// adding compilation unit to program
		program.addCompilationUnit(shim);
		program.addCompilationUnit(Chaincode.INSTANCE.getUnit());
		program.addCompilationUnit(ChaincodeStub.INSTANCE.getUnit());
		program.addCompilationUnit(CommonIteratorInterface.INSTANCE.getUnit());
		program.addCompilationUnit(TLSProperties.INSTANCE.getUnit());
		program.addCompilationUnit(Handler.INSTANCE.getUnit());
	}

	private void loadUrl(Program program) {
		CompilationUnit url = new CompilationUnit(runtimeLocation, "url", false);
		url.addConstruct(new UrlQueryEscape(runtimeLocation, url));
		url.addConstruct(new UrlPathEscape(runtimeLocation, url));

		program.addCompilationUnit(url);
	}

	private void loadStrings(Program program) {
		CompilationUnit str = new CompilationUnit(runtimeLocation, "strings", false);
		str.addConstruct(new HasPrefix(runtimeLocation, str));
		str.addConstruct(new HasSuffix(runtimeLocation, str));
		str.addConstruct(new Contains(runtimeLocation, str));
		str.addConstruct(new Replace(runtimeLocation, str));
		str.addConstruct(new Index(runtimeLocation, str));
		str.addConstruct(new IndexRune(runtimeLocation, str));
		str.addConstruct(new Len(runtimeLocation, str));
		str.addConstruct(new ToLower(runtimeLocation, str));

		program.addCompilationUnit(str);
	}

	private void loadStrconv(Program program) {
		CompilationUnit strconv = new CompilationUnit(runtimeLocation, "strconv", false);
		strconv.addConstruct(new Atoi(runtimeLocation, strconv));
		strconv.addConstruct(new Itoa(runtimeLocation, strconv));

		program.addCompilationUnit(strconv);
	}

	private void loadFmt(Program program) {
		CompilationUnit fmt = new CompilationUnit(runtimeLocation, "fmt", false);
		fmt.addConstruct(new Println(runtimeLocation, fmt));

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
		time.addInstanceConstruct(new Unix(runtimeLocation, time));

		// adding types
		program.registerType(Time.INSTANCE);
		GoStructType.lookup(Time.INSTANCE.getUnit().getName(), time);

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
