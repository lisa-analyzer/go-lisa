

import java.io.IOException;

import org.junit.Test;

import it.unive.golisa.analysis.BreakConsensusChaincodeChecker;
import it.unive.lisa.LiSAConfiguration;

public class NonDetStatementsCheckTest extends GoAnalysisTestExecutor {

	@Test
	public void testSyntacticChecks() throws IOException{
		LiSAConfiguration conf = new LiSAConfiguration().setDumpTypeInference(true).setInferTypes(true).addSyntacticCheck(new BreakConsensusChaincodeChecker());
		perform("nondet", "nondet.go", conf);
	}
}
