
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.pointbased.FieldSensitivePointBasedHeap;
import it.unive.lisa.analysis.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.nonrelational.value.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.numeric.Interval;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.interprocedural.ModularWorstCaseAnalysis;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;
import org.junit.Test;

public class HeapTest extends GoAnalysisTestExecutor {

	@Test
	public void fieldInsensitivePointBasedTest() throws AnalysisSetupException {
		CronConfiguration conf = new CronConfiguration();
		conf.serializeResults = true;
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(), new ValueEnvironment<>(new Interval()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ModularWorstCaseAnalysis<>();
		conf.testDir = "heap/field-insensitive";
		conf.programFile = "go-structs.go";
		perform(conf);
	}

	@Test
	public void fieldSensitivepointBasedTest() throws AnalysisSetupException {
		CronConfiguration conf = new CronConfiguration();
		conf.serializeResults = true;
		conf.abstractState = new SimpleAbstractState<>(new FieldSensitivePointBasedHeap(),
				new ValueEnvironment<>(new Interval()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ModularWorstCaseAnalysis<>();
		conf.testDir = "heap/field-sensitive";
		conf.programFile = "go-structs.go";
		perform(conf);
	}
}
