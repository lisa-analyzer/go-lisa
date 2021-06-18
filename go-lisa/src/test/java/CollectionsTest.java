
import org.junit.Test;

import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.LiSAFactory;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.impl.heap.pointbased.FieldSensitivePointBasedHeap;
import it.unive.lisa.analysis.impl.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.impl.numeric.Interval;
import it.unive.lisa.interprocedural.callgraph.impl.RTACallGraph;
import it.unive.lisa.interprocedural.impl.ContextBasedAnalysis;
import it.unive.lisa.interprocedural.impl.RecursionFreeToken;

public class CollectionsTest extends GoAnalysisTestExecutor {
	
	@Test
	public void structTest() throws  AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpAnalysis(true).setInferTypes(true)
				.setAbstractState(LiSAFactory.getDefaultFor(AbstractState.class, new PointBasedHeap(), new Interval()));
		perform("collections/struct", "struct.go", conf);
	}
	
	
	@Test
	public void arrayTest() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpAnalysis(true).setInferTypes(true)
				.setAbstractState(LiSAFactory.getDefaultFor(AbstractState.class, new PointBasedHeap(), new Interval()));
		perform("collections/array", "array.go", conf);
	}
	
	@Test
	public void interfaceTest1() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setInferTypes(true)
				.setDumpTypeInference(false)
				.setAbstractState(LiSAFactory.getDefaultFor(AbstractState.class, new FieldSensitivePointBasedHeap(), new Interval()))
				.setDumpAnalysis(true)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton()));
		perform("collections/interface/1", "interface.go", conf);
	}	
	
	@Test
	public void interfaceTest2() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setInferTypes(true)
				.setDumpTypeInference(false)
				.setAbstractState(LiSAFactory.getDefaultFor(AbstractState.class, new FieldSensitivePointBasedHeap(), new Interval()))
				.setDumpAnalysis(true)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton()));
		perform("collections/interface/2", "interface.go", conf);
	}
}
