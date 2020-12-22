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

public class CFGTest {

	private String tmpDir = "tmp/";
	private String sourcePath = "go-testcases/decl/";
	private String expDumpPath = "test-outputs/decl/";

	@Test
	public void testSimpleDeclearation() throws IOException {

		for (int i = 1; i < 4; i++) {
			Collection<CFG> cfgs = GoFrontEnd.processFile(sourcePath + "go00" + i + ".go");
			LiSA lisa = new LiSA();

			cfgs.forEach(lisa::addCFG);		
			lisa.setJsonOutput(true);
			lisa.setDumpCFGs(true);
			lisa.setWorkdir(tmpDir);

			try {
				lisa.run();
			} catch (AnalysisException e) {
				System.err.println(e);
				fail("Analysis terminated with errors");
			}

			File actFile = new File(tmpDir + "report.json");
			File expFile = new File(expDumpPath + "go00" + i+ "/report.json");
			JsonReport expected = JsonReport.read(new FileReader(expFile));
			JsonReport actual = JsonReport.read(new FileReader(actFile));
			assertTrue("Results are different",
					JsonReportComparer.compare(expected, actual, expFile.getParentFile(), actFile.getParentFile()));

			for (File f : actFile.getParentFile().listFiles())
				f.delete();
			actFile.getParentFile().delete();
		}
	}

}
