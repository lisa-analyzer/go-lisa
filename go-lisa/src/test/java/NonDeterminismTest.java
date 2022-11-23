
import org.junit.Test;

import it.unive.golisa.analysis.heap.GoAbstractState;
import it.unive.golisa.analysis.heap.GoPointBasedHeap;
import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.checker.TaintChecker;
import it.unive.golisa.loader.annotation.FrameworkNonDeterminismAnnotationSetFactory;
import it.unive.golisa.loader.annotation.sets.NonDeterminismAnnotationSet;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.LiSAConfiguration.GraphType;
import it.unive.lisa.LiSAFactory;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.interprocedural.ContextBasedAnalysis;
import it.unive.lisa.interprocedural.ReturnTopPolicy;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;

public class NonDeterminismTest extends GoChaincodeTestExecutor {

	private final NonDeterminismAnnotationSet[] annSet = FrameworkNonDeterminismAnnotationSetFactory
			.getAnnotationSets("HYPERLEDGER-FABRIC");

	@Test
	public void testMapIteration() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new TaintChecker());
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>();
		perform("non-det/map-iter", "MapIteration.go", conf, annSet);

	}

	@Test
	public void testChannel() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new TaintChecker());
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>();
		perform("non-det/channel", "Channel.go", conf, annSet);

	}

	@Test
	public void testGoRoutine() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new TaintChecker());
		conf.jsonOutput = true;
		conf.analysisGraphs=GraphType.HTML_WITH_SUBNODES;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>();
		perform("non-det/goroutines", "GoRoutines.go", conf, annSet);
	}
}
