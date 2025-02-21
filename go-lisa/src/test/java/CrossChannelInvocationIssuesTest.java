
import it.unive.golisa.checker.hf.CrossChannelInvocationsIssuesChecker;
import it.unive.golisa.interprocedural.RelaxedOpenCallPolicy;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.nonrelational.value.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.string.tarsis.Tarsis;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;
import it.unive.lisa.interprocedural.context.ContextBasedAnalysis;
import org.junit.Test;

public class CrossChannelInvocationIssuesTest extends GoChaincodeTestExecutor {

	@Test
	public void testSingleCchi1() throws AnalysisSetupException {
		CronConfiguration conf = new CronConfiguration();
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(), new ValueEnvironment<>(new Tarsis()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.semanticChecks.add(new CrossChannelInvocationsIssuesChecker());
		conf.jsonOutput = true;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>();
		conf.compareWithOptimization = false;
		conf.testDir = "hf/cchi/cchi1";
		conf.programFile = "cchi1.go";

		perform(conf);
	}
	
	@Test
	public void testSingleCchi2() throws AnalysisSetupException {
		CronConfiguration conf = new CronConfiguration();
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(), new ValueEnvironment<>(new Tarsis()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.semanticChecks.add(new CrossChannelInvocationsIssuesChecker());
		conf.jsonOutput = true;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>();
		conf.compareWithOptimization = false;
		conf.testDir = "hf/cchi/cchi2";
		conf.programFile = "cchi2.go";

		perform(conf);
	}
	
	@Test
	public void testSingleCchi3() throws AnalysisSetupException {
		CronConfiguration conf = new CronConfiguration();
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(), new ValueEnvironment<>(new Tarsis()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.semanticChecks.add(new CrossChannelInvocationsIssuesChecker());
		conf.jsonOutput = true;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>();
		conf.compareWithOptimization = false;
		conf.testDir = "hf/cchi/cchi3";
		conf.programFile = "cchi3.go";

		perform(conf);
	}
	
	@Test
	public void testSingleCchi4() throws AnalysisSetupException {
		CronConfiguration conf = new CronConfiguration();
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(), new ValueEnvironment<>(new Tarsis()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.semanticChecks.add(new CrossChannelInvocationsIssuesChecker());
		conf.jsonOutput = true;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>();
		conf.compareWithOptimization = false;
		conf.testDir = "hf/cchi/cchi4";
		conf.programFile = "cchi4.go";

		perform(conf);
	}
	
	@Test
	public void testSingleCchi5() throws AnalysisSetupException {
		CronConfiguration conf = new CronConfiguration();
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(), new ValueEnvironment<>(new Tarsis()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.semanticChecks.add(new CrossChannelInvocationsIssuesChecker());
		conf.jsonOutput = true;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>();
		conf.compareWithOptimization = false;
		conf.testDir = "hf/cchi/cchi5";
		conf.programFile = "cchi5.go";

		perform(conf);
	}
	
}
