
import it.unive.golisa.analysis.heap.GoAbstractState;
import it.unive.golisa.analysis.heap.GoPointBasedHeap;
import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.checker.TaintChecker;
import it.unive.golisa.interprocedural.RelaxedOpenCallPolicy;
import it.unive.golisa.loader.annotation.sets.HyperledgerFabricNonDeterminismAnnotationSet;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.LiSAConfiguration.GraphType;
import it.unive.lisa.LiSAFactory;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.interprocedural.ContextBasedAnalysis;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;
import org.junit.Test;

public class NonDeterminismTest extends GoChaincodeTestExecutor {

	@Test
	public void testMapIteration() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(
						new GoAbstractState<>(new GoPointBasedHeap(),
								new ValueEnvironment<>(new TaintDomain()),
								LiSAFactory.getDefaultFor(TypeDomain.class)))
				.addSemanticCheck(new TaintChecker())
				.setJsonOutput(true)
				.setOpenCallPolicy(RelaxedOpenCallPolicy.INSTANCE)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("non-det/map-iter", "MapIteration.go", conf, new HyperledgerFabricNonDeterminismAnnotationSet());

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
				.setOpenCallPolicy(RelaxedOpenCallPolicy.INSTANCE)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("non-det/channel", "Channel.go", conf, new HyperledgerFabricNonDeterminismAnnotationSet());

	}

	@Test
	public void testGoRoutine() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(
						new GoAbstractState<>(new GoPointBasedHeap(),
								new ValueEnvironment<>(new TaintDomain()),
								LiSAFactory.getDefaultFor(TypeDomain.class)))
				.addSemanticCheck(new TaintChecker())
				.setJsonOutput(true)
				.setOpenCallPolicy(RelaxedOpenCallPolicy.INSTANCE)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("non-det/goroutines", "GoRoutines.go", conf, new HyperledgerFabricNonDeterminismAnnotationSet());
	}
}
