
import org.junit.Test;

import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.checker.TaintChecker;
import it.unive.golisa.loader.annotation.sets.HyperledgerFabricNonDeterminismAnnotationSet;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.nonrelational.value.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.interprocedural.ContextBasedAnalysis;
import it.unive.lisa.interprocedural.ReturnTopPolicy;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;

public class NonDeterminismTest extends GoChaincodeTestExecutor {

	@Test
	public void testMapIteration() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.semanticChecks.add(new TaintChecker());
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>();
		perform("non-det/map-iter", "MapIteration.go", conf, new HyperledgerFabricNonDeterminismAnnotationSet());

	}

	@Test
	public void testChannel() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.semanticChecks.add(new TaintChecker());
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>();
		perform("non-det/channel", "Channel.go", conf, new HyperledgerFabricNonDeterminismAnnotationSet());

	}

	@Test
	public void testGoRoutine() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.semanticChecks.add(new TaintChecker());
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>();
		perform("non-det/goroutines", "GoRoutines.go", conf, new HyperledgerFabricNonDeterminismAnnotationSet());
	}
}
