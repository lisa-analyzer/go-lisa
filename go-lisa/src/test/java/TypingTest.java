
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.interprocedural.ContextBasedAnalysis;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;
import org.junit.Test;

public class TypingTest extends GoAnalysisTestExecutor {

	@Test
	public void testTypingDeclaration() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpTypeInference(true).setInferTypes(true);
		perform("typing", "typing-decl.go", conf);
	}

	@Test
	public void testStringsTypingDeclaration() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpTypeInference(true).setInferTypes(true)
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>())
				.setCallGraph(new RTACallGraph());
		perform("strings-typing", "strings.go", conf);
	}
}
