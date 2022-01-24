package it.unive.golisa.cli;

import it.unive.golisa.cfg.runtime.fmt.GoPrintln;
import it.unive.golisa.cfg.runtime.shim.function.Start;
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
import it.unive.golisa.golang.util.GoLangAPISignatureMapper;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.SourceCodeLocation;

public interface GoRuntimeLoader {
	SourceCodeLocation runtimeLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);

	default void loadRuntime(String module, Program program, GoLangAPISignatureMapper mapper) {
		switch (module) {
		case "strings":
			loadStrings(program);
			break;
		case "fmt":
			loadFmt(program);
			break;
		case "url":
			loadUrl(program);
			break;
		case "strconv":
			loadStrconv(program);
			break;
		case "time":
			loadTime(program);
			break;
		case "github.com/hyperledger/fabric/core/chaincode/shim":
			loadShim(program);
		default:
			loadUnhandledLib(module, program, mapper);
			break;
		}
	}

	private void loadShim(Program program) {
		CompilationUnit shim = new CompilationUnit(runtimeLocation, "shim", false);

		// adding functions and methods
		shim.addConstruct(new Start(runtimeLocation, shim));

		// adding types
		program.registerType(Chaincode.INSTANCE);
		program.registerType(ChaincodeStub.INSTANCE);
		program.registerType(ChaincodeStubInterface.INSTANCE);
		program.registerType(CommonIteratorInterface.INSTANCE);
		program.registerType(Handler.INSTANCE);
		program.registerType(TLSProperties.INSTANCE);

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
