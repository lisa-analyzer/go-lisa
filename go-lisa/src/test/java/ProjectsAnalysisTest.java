
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.MonolithicHeap;
import it.unive.lisa.analysis.nonrelational.value.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.numeric.Interval;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.interprocedural.ContextBasedAnalysis;
import it.unive.lisa.interprocedural.ReturnTopPolicy;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;
import org.junit.Test;

public class ProjectsAnalysisTest extends GoAnalysisTestExecutor {

	@Test
	public void testProjectAnalysis001() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.abstractState = new SimpleAbstractState<>(new MonolithicHeap(),
				new ValueEnvironment<>(new Interval()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.serializeResults = true;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>();
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		perform("go-projects", "a.go", conf);
	}
}
