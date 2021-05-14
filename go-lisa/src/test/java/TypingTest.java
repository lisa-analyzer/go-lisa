import org.junit.Test;

import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.interprocedural.callgraph.impl.RTACallGraph;
import it.unive.lisa.interprocedural.impl.ContextBasedAnalysis;

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
