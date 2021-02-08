import static it.unive.lisa.LiSAFactory.getDefaultFor;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import it.unive.golisa.analysis.composition.RelTarsis;
import it.unive.golisa.cli.GoFrontEnd;
import it.unive.lisa.AnalysisException;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSA;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.HeapDomain;
import it.unive.lisa.program.Program;

public class ICALPTest {
	private String tmpDir = "tmp/";
	private String sourcePath = "go-testcases/icalp/";

	@Test
	public void codotaTest() throws IOException, AnalysisSetupException {
		Program program = GoFrontEnd.processFile(sourcePath + "codota.go");
		LiSA lisa = new LiSA();

		lisa.setProgram(program);
		lisa.setJsonOutput(true);
		lisa.setInferTypes(true);
		lisa.setAbstractState(getDefaultFor(AbstractState.class, getDefaultFor(HeapDomain.class), new RelTarsis()));
		lisa.setDumpAnalysis(true);
		lisa.setWorkdir(tmpDir);

		try {
			lisa.run();
		} catch (AnalysisException e) {
			System.err.println(e);
			fail("Analysis terminated with errors");
		}
	}
	
	@Test
	public void commonLangTest() throws IOException, AnalysisSetupException {
		Program program = GoFrontEnd.processFile(sourcePath + "common-lang.go");
		LiSA lisa = new LiSA();

		lisa.setProgram(program);
		lisa.setJsonOutput(true);
		lisa.setInferTypes(true);
		lisa.setAbstractState(getDefaultFor(AbstractState.class, getDefaultFor(HeapDomain.class), new RelTarsis()));
		lisa.setDumpAnalysis(true);
		lisa.setWorkdir(tmpDir);

		try {
			lisa.run();
		} catch (AnalysisException e) {
			System.err.println(e);
			fail("Analysis terminated with errors");
		}
	}
	
	@Test
	public void icalpTestCases() throws IOException, AnalysisSetupException {
		Program program = GoFrontEnd.processFile(sourcePath + "icalp-testcases.go");
		LiSA lisa = new LiSA();

		lisa.setProgram(program);
		lisa.setJsonOutput(true);
		lisa.setInferTypes(true);
		lisa.setAbstractState(getDefaultFor(AbstractState.class, getDefaultFor(HeapDomain.class), new RelTarsis()));
		lisa.setDumpAnalysis(true);
		lisa.setWorkdir(tmpDir);

		try {
			lisa.run();
		} catch (AnalysisException e) {
			System.err.println(e);
			fail("Analysis terminated with errors");
		}
	}
}
