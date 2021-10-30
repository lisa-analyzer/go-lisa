
import it.unive.golisa.checker.BreakConsensusGoSmartContractChecker;
import it.unive.lisa.LiSAConfiguration;
import java.io.IOException;
import org.junit.Test;

public class NonDetStatementsCheckTest extends GoAnalysisTestExecutor {

	@Test
	public void testSyntacticChecks() throws IOException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpTypeInference(true).setInferTypes(true)
				.addSyntacticCheck(new BreakConsensusGoSmartContractChecker());
		perform("nondet", "nondet.go", conf);
	}
}
