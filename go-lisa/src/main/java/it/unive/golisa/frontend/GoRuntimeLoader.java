package it.unive.golisa.frontend;

import it.unive.golisa.cfg.runtime.bytes.type.Buffer;
import it.unive.golisa.cfg.runtime.container.list.type.List;
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
import it.unive.golisa.cfg.runtime.fmt.Sprint;
import it.unive.golisa.cfg.runtime.io.fs.type.FileInfo;
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
import it.unive.golisa.cfg.runtime.math.rand.function.Seed;
import it.unive.golisa.cfg.runtime.math.rand.function.Shuffle;
import it.unive.golisa.cfg.runtime.math.rand.function.UInt32;
import it.unive.golisa.cfg.runtime.math.rand.function.UInt64;
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
import it.unive.golisa.cfg.runtime.peer.type.Response;
import it.unive.golisa.cfg.runtime.pkg.statebased.function.NewStateEP;
import it.unive.golisa.cfg.runtime.pkg.statebased.type.KeyEndorsementPolicy;
import it.unive.golisa.cfg.runtime.shim.function.Start;
import it.unive.golisa.cfg.runtime.shim.function.Success;
import it.unive.golisa.cfg.runtime.shim.type.Chaincode;
import it.unive.golisa.cfg.runtime.shim.type.ChaincodeServer;
import it.unive.golisa.cfg.runtime.shim.type.ChaincodeStub;
import it.unive.golisa.cfg.runtime.shim.type.ChaincodeStubInterface;
import it.unive.golisa.cfg.runtime.shim.type.CommonIteratorInterface;
import it.unive.golisa.cfg.runtime.shim.type.Handler;
import it.unive.golisa.cfg.runtime.shim.type.TLSProperties;
import it.unive.golisa.cfg.runtime.strconv.Atoi;
import it.unive.golisa.cfg.runtime.strconv.Itoa;
import it.unive.golisa.cfg.runtime.strconv.ParseFloat;
import it.unive.golisa.cfg.runtime.strconv.ParseInt;
import it.unive.golisa.cfg.runtime.strings.Contains;
import it.unive.golisa.cfg.runtime.strings.HasPrefix;
import it.unive.golisa.cfg.runtime.strings.HasSuffix;
import it.unive.golisa.cfg.runtime.strings.Index;
import it.unive.golisa.cfg.runtime.strings.IndexRune;
import it.unive.golisa.cfg.runtime.strings.Len;
import it.unive.golisa.cfg.runtime.strings.Replace;
import it.unive.golisa.cfg.runtime.strings.ToLower;
import it.unive.golisa.cfg.runtime.strings.ToUpper;
import it.unive.golisa.cfg.runtime.time.function.Now;
import it.unive.golisa.cfg.runtime.time.function.Parse;
import it.unive.golisa.cfg.runtime.time.function.Since;
import it.unive.golisa.cfg.runtime.time.type.Duration;
import it.unive.golisa.cfg.runtime.time.type.Month;
import it.unive.golisa.cfg.runtime.time.type.Time;
import it.unive.golisa.cfg.runtime.url.PathEscape;
import it.unive.golisa.cfg.runtime.url.QueryEscape;
import it.unive.golisa.cfg.type.composite.GoInterfaceType;
import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.golang.util.GoLangAPISignatureMapper;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CodeUnit;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.SourceCodeLocation;

/**
 * The interface managing the loading of runtimes.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public interface GoRuntimeLoader {

	/**
	 * The source code location of runtimes.
	 */
	SourceCodeLocation runtimeLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);

	/**
	 * Loads a package.
	 * 
	 * @param module  the name of the package to load
	 * @param program the program
	 * @param mapper  a mapper
	 */
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
		else if (module.equals("container/list"))
			loadList(program);
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
		CodeUnit sdkerrors = new CodeUnit(runtimeLocation, program, "sdkerrors");

		// adding functions
		sdkerrors.addCodeMember(new Wrap(runtimeLocation, sdkerrors));
		// adding units to program
		program.addUnit(sdkerrors);
	}

	private void loadCosmosTypes(Program program) {
		GoStructType.registerType(Grant.getGrantType(program));
	}

	private void loadFilePath(Program program) {
		CodeUnit filepath = new CodeUnit(runtimeLocation, program, "filepath");

		// adding functions
		filepath.addCodeMember(new Dir(runtimeLocation, filepath));
		filepath.addCodeMember(new Join(runtimeLocation, filepath));

		// adding compilation units to program
		program.addUnit(filepath);
	}

	private void loadIO(Program program) {
		CodeUnit io = new CodeUnit(runtimeLocation, program, "io");

		GoStructType.registerType(PipeReader.getPipeReaderType(program));
		GoStructType.registerType(PipeWriter.getPipeWriterType(program));
		GoStructType.registerType(Reader.getReaderType(program));
		GoStructType.registerType(Writer.getWriterType(program));
		GoStructType.registerType(FileInfo.getFileInfoType(program));

		// adding functions
		io.addCodeMember(new Copy(runtimeLocation, io));
		io.addCodeMember(new CopyBuffer(runtimeLocation, io));
		io.addCodeMember(new CopyN(runtimeLocation, io));
		io.addCodeMember(new Pipe(runtimeLocation, io));
		io.addCodeMember(new ReadAll(runtimeLocation, io));
		io.addCodeMember(new ReadAtLeast(runtimeLocation, io));
		io.addCodeMember(new WriteString(runtimeLocation, io));

		// adding compilation units to program
		program.addUnit(io);
	}

	private void loadIoutil(Program program) {
		CodeUnit ioutil = new CodeUnit(runtimeLocation, program, "ioutil");
		loadIO(program);

		// adding types
		GoStructType.registerType(PipeReader.getPipeReaderType(program));
		GoStructType.registerType(PipeWriter.getPipeWriterType(program));
		GoStructType.registerType(Reader.getReaderType(program));
		GoStructType.registerType(Writer.getWriterType(program));

		// adding functions
		ioutil.addCodeMember(new NopCloser(runtimeLocation, ioutil));
		ioutil.addCodeMember(new ReadAll(runtimeLocation, ioutil));
		ioutil.addCodeMember(new ReadDir(runtimeLocation, ioutil));
		ioutil.addCodeMember(new ReadFile(runtimeLocation, ioutil));
		ioutil.addCodeMember(new TempDir(runtimeLocation, ioutil));
		ioutil.addCodeMember(new TempFile(runtimeLocation, ioutil));
		ioutil.addCodeMember(new WriteFile(runtimeLocation, ioutil));

		// adding compilation units to program
		program.addUnit(ioutil);
	}

	private void loadOs(Program program) {
		CodeUnit os = new CodeUnit(runtimeLocation, program, "os");

		GoStructType.registerType(File.getFileType(program));
		GoStructType.registerType(FileMode.getFileModeType(program));

		// os/file
		os.addCodeMember(new Create(runtimeLocation, os));
		os.addCodeMember(new CreateTemp(runtimeLocation, os));
		os.addCodeMember(new NewFile(runtimeLocation, os));
		os.addCodeMember(new Open(runtimeLocation, os));
		os.addCodeMember(new OpenFile(runtimeLocation, os));

		// os
		os.addCodeMember(new Executable(runtimeLocation, os));
		os.addCodeMember(new Exit(runtimeLocation, os));
		os.addCodeMember(new Getenv(runtimeLocation, os));
		os.addCodeMember(new IsNotExist(runtimeLocation, os));
		os.addCodeMember(new MkdirAll(runtimeLocation, os));
		os.addCodeMember(new ReadFile(runtimeLocation, os));
		os.addCodeMember(new RemoveAll(runtimeLocation, os));
		os.addCodeMember(new Setenv(runtimeLocation, os));
		os.addCodeMember(new Unsetenv(runtimeLocation, os));

		// adding compilation unit to program
		program.addUnit(File.getFileType(program).getUnit());
		program.addUnit(FileMode.getFileModeType(program).getUnit());
		program.addUnit(os);
	}

	private void loadCryptoRand(Program program) {
		CodeUnit cryptoRand = new CodeUnit(runtimeLocation, program, "rand");

		// adding functions
		cryptoRand.addCodeMember(new it.unive.golisa.cfg.runtime.crypto.rand.function.Int(runtimeLocation, cryptoRand));
		cryptoRand
				.addCodeMember(new it.unive.golisa.cfg.runtime.crypto.rand.function.Read(runtimeLocation, cryptoRand));
		cryptoRand
				.addCodeMember(new it.unive.golisa.cfg.runtime.crypto.rand.function.Prime(runtimeLocation, cryptoRand));

		// adding compilation units to program
		program.addUnit(cryptoRand);
	}

	private void loadJson(Program program) {
		CodeUnit jsonUnit = new CodeUnit(runtimeLocation, program, "json");
		// FIXME:
		loadBytes(program);
		// adding functions
		jsonUnit.addCodeMember(new Compact(runtimeLocation, jsonUnit));
		jsonUnit.addCodeMember(new HtmlEscape(runtimeLocation, jsonUnit));
		jsonUnit.addCodeMember(new Indent(runtimeLocation, jsonUnit));
		jsonUnit.addCodeMember(new Marshal(runtimeLocation, jsonUnit));
		jsonUnit.addCodeMember(new MarshalIndent(runtimeLocation, jsonUnit));
		jsonUnit.addCodeMember(new Unmarshal(runtimeLocation, jsonUnit));
		jsonUnit.addCodeMember(new Valid(runtimeLocation, jsonUnit));

		program.addUnit(jsonUnit);
	}

	private void loadList(Program program) {

		CodeUnit listUnit = new CodeUnit(runtimeLocation, program, "container/list");

		List list = it.unive.golisa.cfg.runtime.container.list.type.List.getListType(program);

		// adding types
		program.getTypes().registerType(list);
		GoStructType.registerType(list);

		// registers methods
		List.registerMethods();

		program.addUnit(list.getUnit());

		// adding functions

		program.addUnit(listUnit);
	}

	private void loadMathRand(Program program) {
		CodeUnit mathRand = new CodeUnit(runtimeLocation, program, "rand");

		// registers types of rand package
		GoStructType.registerType(Rand.getRandType(program));

		// adding functions and static methods
		mathRand.addCodeMember(new ExpFloat64(runtimeLocation, mathRand));
		mathRand.addCodeMember(
				new Float32(runtimeLocation, mathRand));
		mathRand.addCodeMember(
				new Float64(runtimeLocation, mathRand));
		mathRand.addCodeMember(
				new Int(runtimeLocation, mathRand));
		mathRand.addCodeMember(
				new Int31(runtimeLocation, mathRand));
		mathRand.addCodeMember(
				new Int31n(runtimeLocation, mathRand));
		mathRand.addCodeMember(
				new Int63(runtimeLocation, mathRand));
		mathRand.addCodeMember(
				new Int63n(runtimeLocation, mathRand));
		mathRand.addCodeMember(
				new Intn(runtimeLocation, mathRand));
		mathRand.addCodeMember(new NormFloat64(runtimeLocation,
				mathRand));
		mathRand.addCodeMember(
				new Perm(runtimeLocation, mathRand));
		mathRand.addCodeMember(
				new UInt32(runtimeLocation, mathRand));
		mathRand.addCodeMember(
				new UInt64(runtimeLocation, mathRand));
		mathRand.addCodeMember(
				new Read(runtimeLocation, mathRand));
		mathRand.addCodeMember(
				new Seed(runtimeLocation, mathRand));
		mathRand.addCodeMember(
				new Shuffle(runtimeLocation, mathRand));

		Rand.registerMethods();

		// adding compilation units to program
		program.addUnit(mathRand);
	}

	private void loadBytes(Program program) {
		CodeUnit bytes = new CodeUnit(runtimeLocation, program, "bytes");

		Buffer bufferType = Buffer.getBufferType(program);

		// adding types
		program.getTypes().registerType(bufferType);
		GoStructType.registerType(bufferType);

		// registers methods
		Buffer.registerMethods();
		program.addUnit(bytes);

		program.addUnit(bufferType.getUnit());
	}

	private void loadStateBased(Program program) {
		CodeUnit statebased = new CodeUnit(runtimeLocation, program, "statebased");
		GoInterfaceType.registerType(KeyEndorsementPolicy.getKeyEndorsementPolicyType(program));

		// adding functions
		statebased.addCodeMember(new NewStateEP(runtimeLocation, statebased));

		// adding compilation unit to program
		program.addUnit(statebased);
	}

	private void loadShim(Program program) {
		CodeUnit shim = new CodeUnit(runtimeLocation, program, "shim");

		// FIXME
		GoStructType.registerType(Buffer.getBufferType(program));
		GoStructType.registerType(Reader.getReaderType(program));

		GoInterfaceType.registerType(ChaincodeStubInterface.getChainCodeStubInterfaceType(program));
		GoInterfaceType.registerType(Chaincode.getChaincodeType(program));
		GoInterfaceType.registerType(CommonIteratorInterface.getCommonIteratorInterfaceType(program));
		GoStructType.registerType(Handler.getHandlerType(program));
		GoStructType.registerType(TLSProperties.getTLSPropertiesType(program));
		GoStructType.registerType(ChaincodeStub.getChaincodeStubType(program));
		GoStructType.registerType(ChaincodeServer.getChaincodeServerType(program));
		GoStructType.registerType(Response.getResponseType(program));

		// adding functions
		shim.addCodeMember(new Start(runtimeLocation, shim));
		shim.addCodeMember(new it.unive.golisa.cfg.runtime.shim.function.Error(runtimeLocation, shim));
		shim.addCodeMember(new Success(runtimeLocation, shim));

		// register methods
		ChaincodeStub.registerMethods();
		ChaincodeServer.registerMethods();

		ChaincodeStub.getChaincodeStubType(program).getUnit()
				.addAncestor(ChaincodeStubInterface.getChainCodeStubInterfaceType(program).getUnit());

		// FIXME: we should register this type just in GoInterfaceType
		program.getTypes().registerType(ChaincodeStubInterface.getChainCodeStubInterfaceType(program));

		// adding compilation unit to program
		program.addUnit(shim);
		program.addUnit(ChaincodeStubInterface.getChainCodeStubInterfaceType(program).getUnit());
		program.addUnit(Chaincode.getChaincodeType(program).getUnit());
		program.addUnit(CommonIteratorInterface.getCommonIteratorInterfaceType(program).getUnit());

		program.addUnit(ChaincodeStub.getChaincodeStubType(program).getUnit());
		program.addUnit(TLSProperties.getTLSPropertiesType(program).getUnit());

	}

	private void loadUrl(Program program) {
		CodeUnit url = new CodeUnit(runtimeLocation, program, "url");
		url.addCodeMember(new QueryEscape(runtimeLocation, url));
		url.addCodeMember(new PathEscape(runtimeLocation, url));

		program.addUnit(url);
	}

	private void loadStrings(Program program) {
		CodeUnit str = new CodeUnit(runtimeLocation, program, "strings");
		str.addCodeMember(new HasPrefix(runtimeLocation, str));
		str.addCodeMember(new HasSuffix(runtimeLocation, str));
		str.addCodeMember(new Contains(runtimeLocation, str));
		str.addCodeMember(new Replace(runtimeLocation, str));
		str.addCodeMember(new Index(runtimeLocation, str));
		str.addCodeMember(new IndexRune(runtimeLocation, str));
		str.addCodeMember(new Len(runtimeLocation, str));
		str.addCodeMember(new ToLower(runtimeLocation, str));
		str.addCodeMember(new ToUpper(runtimeLocation, str));

		program.addUnit(str);
	}

	private void loadStrconv(Program program) {
		CodeUnit strconv = new CodeUnit(runtimeLocation, program, "strconv");
		strconv.addCodeMember(new Atoi(runtimeLocation, strconv));
		strconv.addCodeMember(new Itoa(runtimeLocation, strconv));
		strconv.addCodeMember(new ParseFloat(runtimeLocation, strconv));
		strconv.addCodeMember(new ParseInt(runtimeLocation, strconv));

		program.addUnit(strconv);
	}

	private void loadFmt(Program program) {
		CodeUnit fmt = new CodeUnit(runtimeLocation, program, "fmt");
		fmt.addCodeMember(new Println(runtimeLocation, fmt));
		fmt.addCodeMember(new Sprint(runtimeLocation, fmt));

		program.addUnit(fmt);
	}

	private void loadTime(Program program) {
		CodeUnit time = new CodeUnit(runtimeLocation, program, "time");

		Time timeType = Time.getTimeType(program);
		GoStructType.registerType(timeType);
		program.getTypes().registerType(timeType);
		program.getTypes().registerType(Month.INSTANCE);
		program.getTypes().registerType(Duration.INSTANCE);

		// adding static functions
		time.addCodeMember(new Now(runtimeLocation, time));
		time.addCodeMember(new Since(runtimeLocation, time));
		time.addCodeMember(new Parse(runtimeLocation, time));

		Time.registerMethods();

		program.addUnit(time);
	}

	private void loadUnhandledLib(String lib, Program program, GoLangAPISignatureMapper mapper) {

		SourceCodeLocation runTimeSourceLocation;
		if (mapper.getPackages().contains(lib))
			// it is a package contained in Go APIs
			runTimeSourceLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
		else
			runTimeSourceLocation = new SourceCodeLocation("unknown", 0, 0);

		CodeUnit cu = new CodeUnit(runTimeSourceLocation, program, lib);
		program.addUnit(cu);
	}

}
