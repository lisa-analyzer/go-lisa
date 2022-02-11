import static it.unive.lisa.outputs.compare.JsonReportComparer.compare;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import it.unive.golisa.frontend.GoFrontEnd;
import it.unive.golisa.loader.Loader;
import it.unive.lisa.AnalysisException;
import it.unive.lisa.LiSA;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.outputs.JsonReport;
import it.unive.lisa.program.Program;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;

public abstract class GoAnalysisTestExecutor {

	protected static final String EXPECTED_RESULTS_DIR = "go-testcases";
	protected static final String ACTUAL_RESULTS_DIR = "go-outputs";

	protected void perform(String folder, String source, LiSAConfiguration configuration, Loader... loaders) {
		System.out.println("Testing " + getCaller());
		performAux(folder, null, source, configuration, loaders);
	}

	protected void perform(String folder, String subfolder, String source, LiSAConfiguration configuration,
			Loader... loaders) {
		System.out.println("Testing " + getCaller());
		performAux(folder, subfolder, source, configuration, loaders);
	}

	private void performAux(String folder, String subfolder, String source, LiSAConfiguration configuration,
			Loader... loaders) {
		Path expectedPath = Paths.get(EXPECTED_RESULTS_DIR, folder);
		Path actualPath = Paths.get(ACTUAL_RESULTS_DIR, folder);
		Path target = Paths.get(expectedPath.toString(), source);

		Program program = null;
		try {
			program = GoFrontEnd.processFile(target.toString());
			for (Loader loader : loaders)
				loader.load(program);
		} catch (IOException e) {
			e.printStackTrace(System.err);
			fail("Exception while parsing '" + target + "': " + e.getMessage());
		}

		if (subfolder != null) {
			expectedPath = Paths.get(expectedPath.toString(), subfolder);
			actualPath = Paths.get(actualPath.toString(), subfolder);
		}

		File workdir = actualPath.toFile();
		if (workdir.exists()) {
			System.out.println(workdir + " already exists: deleting...");
			try {
				FileUtils.forceDelete(workdir);
			} catch (IOException e) {
				e.printStackTrace(System.err);
				fail("Cannot delete working directory '" + workdir + "': " + e.getMessage());
			}
		}
		configuration.setWorkdir(workdir.toString());

		configuration.setJsonOutput(true);

		LiSA lisa = new LiSA(configuration);
		try {
			lisa.run(program);
		} catch (AnalysisException e) {
			e.printStackTrace(System.err);
			fail("Analysis terminated with errors");
		}

		File expFile = Paths.get(expectedPath.toString(), "report.json").toFile();
		File actFile = Paths.get(actualPath.toString(), "report.json").toFile();
		try (FileReader l = new FileReader(expFile); FileReader r = new FileReader(actFile)) {
			JsonReport expected = JsonReport.read(l);
			JsonReport actual = JsonReport.read(r);
			assertTrue("Results are different", compare(expected, actual, expectedPath.toFile(), actualPath.toFile()));
		} catch (FileNotFoundException e) {
			e.printStackTrace(System.err);
			fail("Unable to find report file");
		} catch (IOException e) {
			e.printStackTrace(System.err);
			fail("Unable to compare reports");
		}
	}

	private String getCaller() {
		StackTraceElement[] trace = Thread.getAllStackTraces().get(Thread.currentThread());
		// 0: java.lang.Thread.dumpThreads()
		// 1: java.lang.Thread.getAllStackTraces()
		// 2: it.unive.lisa.test.AnalysisTest.getCaller()
		// 3: it.unive.lisa.test.AnalysisTest.perform()
		// 4: caller
		return trace[4].getClassName() + "::" + trace[4].getMethodName();
	}
}
