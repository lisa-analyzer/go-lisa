
import it.unive.golisa.checker.BreakConsensusGoSmartContractChecker;
import it.unive.lisa.LiSAConfiguration;
import java.io.IOException;
import org.junit.Test;

public class NonDetStatementsCheckTest extends GoAnalysisTestExecutor {

	@Test
	public void testSyntacticChecks() throws IOException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpTypeInference(true).setInferTypes(true)
				.addSyntacticCheck(new BreakConsensusGoSmartContractChecker());
//		for (int i = 0; i < 50; i++) {
//			System.err.println(i);
		// FIXME this nondeterministically fails (1/5-6) because inside Invoke 
		// an untyped is added
		perform("nondet", "nondet.go", conf); 
//		}
	}
}
