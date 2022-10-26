
import org.junit.Test;

import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.LiSAFactory;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.heap.MonolithicHeap;
import it.unive.lisa.analysis.numeric.Interval;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.interprocedural.ContextBasedAnalysis;
import it.unive.lisa.interprocedural.ReturnTopPolicy;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;

public class ProjectsAnalysisTest extends GoAnalysisTestExecutor {

	@Test
	public void testProjectAnalysis001() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(LiSAFactory.getDefaultFor(AbstractState.class, new MonolithicHeap(), new Interval(),
						new InferredTypes()))
				.setSerializeResults(true)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>())
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE);
		perform("go-projects", "a.go", conf);
	}
}
