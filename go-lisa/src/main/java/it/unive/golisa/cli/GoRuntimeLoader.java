package it.unive.golisa.cli;

import it.unive.golisa.cfg.runtime.fmt.GoPrintln;
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
		default:
			loadUnhandledLib(module, program, mapper);
			break;
		}
	}

	private void loadUnhandledLib(String lib, Program program, GoLangAPISignatureMapper mapper) {

		SourceCodeLocation unknownLocation;
		if (mapper.getPackages().contains(lib))
			// it is a package contained in Go APIs
			unknownLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
		else
			unknownLocation = new SourceCodeLocation("unknown", 0, 0);

		CompilationUnit cu = new CompilationUnit(unknownLocation, lib, false);
		program.addCompilationUnit(cu);
	}

	private void loadUrl(Program program) {
		SourceCodeLocation unknownLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
		CompilationUnit url = new CompilationUnit(unknownLocation, "url", false);
		url.addConstruct(new UrlQueryEscape(unknownLocation, url));
		url.addConstruct(new UrlPathEscape(unknownLocation, url));

		program.addCompilationUnit(url);
	}

	private void loadStrings(Program program) {
		SourceCodeLocation unknownLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
		CompilationUnit str = new CompilationUnit(unknownLocation, "strings", false);
		str.addConstruct(new GoHasPrefix(unknownLocation, str));
		str.addConstruct(new GoHasSuffix(unknownLocation, str));
		str.addConstruct(new GoContains(unknownLocation, str));
		str.addConstruct(new GoReplace(unknownLocation, str));
		str.addConstruct(new GoIndex(unknownLocation, str));
		str.addConstruct(new GoIndexRune(unknownLocation, str));
		str.addConstruct(new GoLen(unknownLocation, str));

		program.addCompilationUnit(str);
	}

	private void loadStrconv(Program program) {
		SourceCodeLocation unknownLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
		CompilationUnit strconv = new CompilationUnit(unknownLocation, "strconv", false);
		strconv.addConstruct(new GoAtoi(unknownLocation, strconv));
		strconv.addConstruct(new GoItoa(unknownLocation, strconv));

		program.addCompilationUnit(strconv);
	}

	private void loadFmt(Program program) {
		SourceCodeLocation unknownLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
		CompilationUnit fmt = new CompilationUnit(unknownLocation, "fmt", false);
		fmt.addConstruct(new GoPrintln(unknownLocation, fmt));

		program.addCompilationUnit(fmt);
	}

	private void loadTime(Program program) {
		SourceCodeLocation unknownLocation = new SourceCodeLocation(GoLangUtils.GO_RUNTIME_SOURCE, 0, 0);
		CompilationUnit time = new CompilationUnit(unknownLocation, "time", false);

		// adding functions and methods
		time.addConstruct(new Now(unknownLocation, time));
		time.addConstruct(new Since(unknownLocation, time));
		time.addConstruct(new Day(unknownLocation, time));
		time.addConstruct(new Month(unknownLocation, time));

		// adding types
		program.registerType(Time.INSTANCE);
		program.registerType(it.unive.golisa.cfg.runtime.time.type.Month.INSTANCE);
		program.registerType(Duration.INSTANCE);
	}

}
