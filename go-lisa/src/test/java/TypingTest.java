import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;

import org.junit.Test;

import it.unive.golisa.cli.GoFrontEnd;
import it.unive.lisa.AnalysisException;
import it.unive.lisa.LiSA;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.outputs.JsonReport;
import it.unive.lisa.outputs.compare.JsonReportComparer;

public class TypingTest {

	private String tmpDir = "tmp/";
	private String sourcePath = "go-testcases/typing/";
	private String expDumpPath = "test-outputs/typing/";

	@Test
	public void testTypingDeclaration() throws IOException {
		Collection<CFG> cfgs = GoFrontEnd.processFile(sourcePath + "typing-decl.go");
		LiSA lisa = new LiSA();

		cfgs.forEach(lisa::addCFG);		
		lisa.setJsonOutput(true);
		lisa.setInferTypes(true);
		lisa.setDumpTypeInference(true);
		lisa.setWorkdir(tmpDir);

		try {
			lisa.run();
		} catch (AnalysisException e) {
			System.err.println(e);
			fail("Analysis terminated with errors");
		}

		File actFile = new File(tmpDir + "report.json");
		File expFile = new File(expDumpPath + "report.json");
		JsonReport expected = JsonReport.read(new FileReader(expFile));
		JsonReport actual = JsonReport.read(new FileReader(actFile));
		assertTrue("Results are different",
				JsonReportComparer.compare(expected, actual, expFile.getParentFile(), actFile.getParentFile()));

		for (File f : actFile.getParentFile().listFiles())
			f.delete();
		actFile.getParentFile().delete();
	}
}
