
import it.unive.golisa.checker.hf.UnhandledErrorsChecker;
import it.unive.lisa.AnalysisSetupException;
import org.junit.Test;

public class UnhandledErrorsTest extends GoChaincodeTestExecutor {


	@Test
	public void testUnhandledErrors() throws AnalysisSetupException {
		CronConfiguration conf = new CronConfiguration();
		conf.syntacticChecks.add(new UnhandledErrorsChecker());
		conf.jsonOutput = true;
		conf.compareWithOptimization = false;
		conf.testDir = "unhandled-errors/hf-unhandled-errors";
		conf.programFile = "hf-unhandled-errors.go";
		perform(conf);
	}
}
