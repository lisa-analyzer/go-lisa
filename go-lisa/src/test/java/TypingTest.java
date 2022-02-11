
import org.junit.Test;

import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.LiSAFactory;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.heap.MonolithicHeap;
import it.unive.lisa.analysis.numeric.Interval;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.interprocedural.ContextBasedAnalysis;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;

public class TypingTest extends GoAnalysisTestExecutor {

	@Test
	public void testTypingDeclaration() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpTypeInference(true)
				.setAbstractState(LiSAFactory.getDefaultFor(AbstractState.class, new MonolithicHeap(), new Interval(), new InferredTypes()));
		perform("typing", "typing-decl.go", conf);
	}

	@Test
	public void testStringsTypingDeclaration() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpTypeInference(true)
				.setAbstractState(LiSAFactory.getDefaultFor(AbstractState.class, new MonolithicHeap(), new Interval(), new InferredTypes()))
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>())
				.setCallGraph(new RTACallGraph());
		perform("strings-typing", "strings.go", conf);
	}
}
