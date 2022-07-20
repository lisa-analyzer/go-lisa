import org.junit.Test;

import it.unive.golisa.analysis.heap.GoAbstractState;
import it.unive.golisa.analysis.heap.GoPointBasedHeap;
import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.checker.TaintChecker;
import it.unive.golisa.loader.annotation.sets.HyperledgerFabricNonDeterminismAnnotationSet;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.LiSAConfiguration.GraphType;
import it.unive.lisa.LiSAFactory;
import it.unive.lisa.analysis.nonrelational.inference.InferenceSystem;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.interprocedural.ContextBasedAnalysis;
import it.unive.lisa.interprocedural.ReturnTopPolicy;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;

public class NonDeterminismTest extends GoChaincodeTestExecutor {


	@Test
	public void testMapIteration() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(
						new GoAbstractState<>(new GoPointBasedHeap(),
								new InferenceSystem<>(new TaintDomain()),
								LiSAFactory.getDefaultFor(TypeDomain.class)))
				.addSemanticCheck(new TaintChecker())
				.setJsonOutput(true)
				.setDumpAnalysis(GraphType.HTML)
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("non-det", "MapIteration.go", conf, new HyperledgerFabricNonDeterminismAnnotationSet());
		
	}
	
	@Test
	public void testChannel() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(
						new GoAbstractState<>(new GoPointBasedHeap(),
								new ValueEnvironment<>(new TaintDomain()),
								LiSAFactory.getDefaultFor(TypeDomain.class)))
				.addSemanticCheck(new TaintChecker())
				.setJsonOutput(true)
				.setDumpAnalysis(GraphType.HTML)
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("non-det", "Channel.go", conf, new HyperledgerFabricNonDeterminismAnnotationSet());
		
	}
}
