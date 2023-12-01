import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.checker.TaintChecker;
import it.unive.golisa.interprocedural.RelaxedOpenCallPolicy;
import it.unive.golisa.loader.annotation.sets.PhantomReadAnnotationSet;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.nonrelational.value.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;
import it.unive.lisa.interprocedural.context.ContextBasedAnalysis;
import org.junit.Test;

public class PhantomReadsTest extends GoChaincodeTestExecutor {

	private static final PhantomReadAnnotationSet annSet = new PhantomReadAnnotationSet();

	@Test
	public void testPhantomReads() throws AnalysisSetupException {
		CronConfiguration conf = new CronConfiguration();
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()), new TypeEnvironment<>(new InferredTypes()));
		conf.semanticChecks.add(new TaintChecker());
		conf.jsonOutput = true;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>();
		conf.compareWithOptimization = false;
		conf.testDir = "hf/phantom-read";
		conf.programFile = "phantom-read.go";
		conf.annSet = annSet;
		
		perform(conf);
	}
}
