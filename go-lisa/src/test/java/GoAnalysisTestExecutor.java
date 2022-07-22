import static it.unive.lisa.outputs.compare.JsonReportComparer.compare;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import it.unive.golisa.frontend.GoFrontEnd;
import it.unive.golisa.loader.Loader;
import it.unive.lisa.AnalysisException;
import it.unive.lisa.LiSA;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.outputs.compare.JsonReportComparer.DiffReporter;
import it.unive.lisa.outputs.compare.JsonReportComparer.REPORTED_COMPONENT;
import it.unive.lisa.outputs.compare.JsonReportComparer.REPORT_TYPE;
import it.unive.lisa.outputs.json.JsonReport;
import it.unive.lisa.outputs.json.JsonReport.JsonWarning;
import it.unive.lisa.program.Program;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.HashSet;
import org.apache.commons.io.FileUtils;

public abstract class GoAnalysisTestExecutor {

	protected static final String EXPECTED_RESULTS_DIR = "go-testcases";
	protected static final String ACTUAL_RESULTS_DIR = "go-outputs";

	protected void perform(String folder, String source, LiSAConfiguration configuration, Loader... loaders) {
		System.out.println("Testing " + getCaller());
		performAux(folder, null, source, configuration, false, loaders);
	}

	protected void perform(String folder, String subfolder, String source, LiSAConfiguration configuration,
			Loader... loaders) {
		System.out.println("Testing " + getCaller());
		performAux(folder, subfolder, source, configuration, false, loaders);
	}

	protected void perform(String folder, String source, LiSAConfiguration configuration, boolean update,
			Loader... loaders) {
		System.out.println("Testing " + getCaller());
		performAux(folder, null, source, configuration, update, loaders);
	}

	protected void perform(String folder, String subfolder, String source, LiSAConfiguration configuration,
			boolean update,
			Loader... loaders) {
		System.out.println("Testing " + getCaller());
		performAux(folder, subfolder, source, configuration, update, loaders);
	}

	protected void performAux(String folder, String subfolder, String source, LiSAConfiguration configuration,
			Loader... loaders) {
		System.out.println("Testing " + getCaller());
		performAux(folder, subfolder, source, configuration, false, loaders);
	}

	protected void performAux(String folder, String subfolder, String source, LiSAConfiguration configuration,
			boolean update,
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
			Accumulator acc = new Accumulator(expectedPath);
			if (!update)
				assertTrue("Results are different", compare(expected, actual, expectedPath.toFile(), actualPath.toFile()));
			else {
				boolean compare = compare(expected, actual, expectedPath.toFile(), actualPath.toFile(), acc);
				if (!compare)
					regen(expectedPath, actualPath, expFile, actFile, acc);
				assertTrue("Results are different", compare);
			}
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

	private void regen(Path expectedPath, Path actualPath, File expFile, File actFile, Accumulator acc)
			throws IOException {
		boolean updateReport = !acc.addedWarning.isEmpty() || !acc.removedWarning.isEmpty()
				|| !acc.addedFilePaths.isEmpty() || !acc.removedFilePaths.isEmpty()
				|| !acc.changedFileName.isEmpty();
		if (updateReport) {
			Files.copy(actFile.toPath(), expFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			System.err.println("- Updated report.json");
		}
		for (Path f : acc.removedFilePaths) {
			Files.delete(Paths.get(expectedPath.toString(), f.toString()));
			System.err.println("- Deleted " + f);
		}
		for (Path f : acc.addedFilePaths) {
			Files.copy(Paths.get(actualPath.toString(), f.toString()),
					Paths.get(expectedPath.toString(), f.toString()));
			System.err.println("- Copied (new) " + f);
		}
		for (Path f : acc.changedFileName) {
			Path fresh = Paths.get(expectedPath.toString(), f.toString());
			Files.copy(
					Paths.get(actualPath.toString(), f.toString()),
					fresh,
					StandardCopyOption.REPLACE_EXISTING);
			System.err.println("- Copied (update) " + fresh);
		}
	}

	private class Accumulator implements DiffReporter {

		private final Collection<Path> changedFileName = new HashSet<>();
		private final Collection<Path> addedFilePaths = new HashSet<>();
		private final Collection<Path> removedFilePaths = new HashSet<>();
		private final Collection<JsonWarning> addedWarning = new HashSet<>();
		private final Collection<JsonWarning> removedWarning = new HashSet<>();
		private final Path exp;

		public Accumulator(Path exp) {
			this.exp = exp;
		}

		@Override
		public void report(REPORTED_COMPONENT component, REPORT_TYPE type, Collection<?> reported) {
			switch (type) {
			case ONLY_FIRST:
				switch (component) {
				case FILES:
					reported.forEach(e -> removedFilePaths.add(Paths.get((String) e)));
					break;
				case WARNINGS:
					reported.forEach(e -> removedWarning.add((JsonWarning) e));
					break;
				default:
					break;
				}
				break;
			case ONLY_SECOND:
				switch (component) {
				case FILES:
					reported.forEach(e -> addedFilePaths.add(Paths.get((String) e)));
					break;
				case WARNINGS:
					reported.forEach(e -> addedWarning.add((JsonWarning) e));
					break;
				default:
					break;
				}
				break;
			case COMMON:
			default:
				break;

			}
		}

		@Override
		public void fileDiff(String first, String second, String message) {
			Path file = Paths.get(first);
			changedFileName.add(exp.relativize(file));
		}
	}
}
