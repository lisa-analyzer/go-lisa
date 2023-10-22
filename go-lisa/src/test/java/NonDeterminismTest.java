
import org.junit.Test;

import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.checker.TaintChecker;
import it.unive.golisa.loader.annotation.sets.HyperledgerFabricNonDeterminismAnnotationSet;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.nonrelational.value.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.interprocedural.ReturnTopPolicy;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;
import it.unive.lisa.interprocedural.context.ContextBasedAnalysis;

public class NonDeterminismTest extends GoChaincodeTestExecutor {

	private static final HyperledgerFabricNonDeterminismAnnotationSet annSet = new HyperledgerFabricNonDeterminismAnnotationSet();

	@Test
	public void testMapIteration() throws AnalysisSetupException {
		CronConfiguration conf = new CronConfiguration();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.semanticChecks.add(new TaintChecker());
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>();
		conf.compareWithOptimization = false;
		conf.testDir = "non-det/map-iter";
		conf.programFile = "MapIteration.go";
		conf.annSet = annSet;
		perform(conf);
	}

	@Test
	public void testChannel() throws AnalysisSetupException {
		CronConfiguration conf = new CronConfiguration();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.semanticChecks.add(new TaintChecker());
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>();
		conf.compareWithOptimization = false;
		conf.testDir = "non-det/channel";
		conf.programFile = "Channel.go";
		conf.annSet = annSet;

		perform(conf);
	}

	@Test
	public void testGoRoutine() throws AnalysisSetupException {
		CronConfiguration conf = new CronConfiguration();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.semanticChecks.add(new TaintChecker());
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>();
		conf.compareWithOptimization = false;
		conf.testDir = "non-det/goroutines";
		conf.programFile = "GoRoutines.go";
		conf.annSet = annSet;
		perform(conf);
	}
}
