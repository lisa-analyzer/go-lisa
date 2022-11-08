
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.LiSAFactory;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.heap.MonolithicHeap;
import it.unive.lisa.analysis.numeric.Interval;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.interprocedural.ContextBasedAnalysis;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;
import org.junit.Test;

public class TypingTest extends GoAnalysisTestExecutor {

	@Test
	public void testTypingDeclaration() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.serializeResults = true;
		conf.abstractState = LiSAFactory.getDefaultFor(AbstractState.class, new MonolithicHeap(), new Interval(),
						new InferredTypes());
		perform("typing", "typing-decl.go", conf);
	}

	@Test
	public void testStringsTypingDeclaration() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.serializeResults = true;
		conf.abstractState = LiSAFactory.getDefaultFor(AbstractState.class, new MonolithicHeap(), new Interval(),
						new InferredTypes());
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>();
		conf.callGraph = new RTACallGraph();
		perform("strings-typing", "strings.go", conf);
	}
}
