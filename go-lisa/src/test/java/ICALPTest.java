import static it.unive.lisa.LiSAFactory.getDefaultFor;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;

import it.unive.golisa.analysis.rsubs.RelationalSubstringDomain;
import it.unive.golisa.cli.GoFrontEnd;
import it.unive.lisa.AnalysisException;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSA;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.HeapDomain;
import it.unive.lisa.analysis.impl.numeric.Sign;
import it.unive.lisa.outputs.JsonReport;
import it.unive.lisa.outputs.compare.JsonReportComparer;
import it.unive.lisa.program.Program;

public class ICALPTest {

	private String tmpDir = "tmp/";
	private String sourcePath = "go-testcases/icalp/";
	private String expDumpPath = "test-outputs/icalp/";

	@Test
	public void testDeclaration() throws IOException, AnalysisSetupException {
		Program program = GoFrontEnd.processFile(sourcePath + "subs.go");
		LiSA lisa = new LiSA();

		lisa.setProgram(program);
		lisa.setJsonOutput(true);
		lisa.setInferTypes(true);
		lisa.setAbstractState(getDefaultFor(AbstractState.class, getDefaultFor(HeapDomain.class), new RelationalSubstringDomain()));
		lisa.setDumpAnalysis(true);
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
