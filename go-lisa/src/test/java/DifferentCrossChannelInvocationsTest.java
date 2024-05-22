
import org.junit.Test;

import it.unive.golisa.checker.hf.DifferentCrossChannelInvocationsChecker;
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
import it.unive.lisa.program.cfg.statement.call.OpenCall;

public class DifferentCrossChannelInvocationsTest extends GoChaincodeTestExecutor {

	@Test
	public void testDCCIs() throws AnalysisSetupException {
		CronConfiguration conf = new CronConfiguration();
		conf.openCallPolicy = new RelaxedOpenCallPolicy() {
			
			@Override
			public boolean isSourceForTaint(OpenCall call) {
				return false;
			}
		};
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(), new ValueEnvironment<>(new Tarsis()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.semanticChecks.add(new DifferentCrossChannelInvocationsChecker());
		conf.jsonOutput = true;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>();
		conf.compareWithOptimization = false;
		conf.testDir = "hf/dcci";
		conf.programFile = "dcci.go";

		perform(conf);
	}
}
