
import it.unive.golisa.analysis.heap.GoAbstractState;
import it.unive.golisa.analysis.heap.GoFieldSensitivePointBasedHeap;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.LiSAFactory;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.numeric.Interval;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.interprocedural.ContextBasedAnalysis;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;
import org.junit.Test;

public class BlankIdentifierTest extends GoAnalysisTestExecutor {

	@Test
	public void blankIdentifierTest() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.abstractState = new GoAbstractState<>(new GoFieldSensitivePointBasedHeap(),
				new ValueEnvironment<>(new Interval()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.serializeResults = true;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>();
		perform("blankidentifier", "blankidentifier.go", conf);
	}
}