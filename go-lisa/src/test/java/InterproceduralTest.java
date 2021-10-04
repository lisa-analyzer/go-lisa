import org.junit.Test;

import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.LiSAFactory;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.heap.pointbased.FieldSensitivePointBasedHeap;
import it.unive.lisa.analysis.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.numeric.Interval;
import it.unive.lisa.interprocedural.ContextBasedAnalysis;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;

public class InterproceduralTest extends GoAnalysisTestExecutor {

	@Test
	public void testInterproc1() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setDumpTypeInference(false)
				.setInferTypes(true)
				.setAbstractState(LiSAFactory.getDefaultFor(AbstractState.class, new PointBasedHeap(), new Interval()))
				.setDumpAnalysis(true)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("interprocedural/interproc1", "interprocedural.go", conf);
	}

	@Test
	public void testInterproc2() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setDumpTypeInference(false)
				.setInferTypes(true)
				.setAbstractState(LiSAFactory.getDefaultFor(AbstractState.class, new PointBasedHeap(), new Interval()))
				.setDumpAnalysis(true)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("interprocedural/interproc2", "interprocedural.go", conf);
	}

	@Test
	public void testInterproc3() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setDumpTypeInference(false)
				.setInferTypes(true)
				.setAbstractState(LiSAFactory.getDefaultFor(AbstractState.class, new FieldSensitivePointBasedHeap(), new Interval()))
				.setDumpAnalysis(true)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("interprocedural/interproc3", "interprocedural.go", conf);
	}

	@Test
	public void testInterproc4() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setDumpTypeInference(false)
				.setInferTypes(true)
				.setAbstractState(LiSAFactory.getDefaultFor(AbstractState.class, new FieldSensitivePointBasedHeap(), new Interval()))
				.setDumpAnalysis(true)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("interprocedural/interproc4", "interprocedural.go", conf);
	}

	@Test
	public void testInterproc5() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setDumpTypeInference(false)
				.setInferTypes(true)
				.setAbstractState(LiSAFactory.getDefaultFor(AbstractState.class, new FieldSensitivePointBasedHeap(), new Interval()))
				.setDumpAnalysis(true)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("interprocedural/interproc5", "interprocedural.go", conf);
	}

	@Test	
	public void testInterproc6() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setDumpTypeInference(false)
				.setInferTypes(true)
				.setAbstractState(LiSAFactory.getDefaultFor(AbstractState.class, new FieldSensitivePointBasedHeap(), new Interval()))
				.setDumpAnalysis(true)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("interprocedural/interproc6", "interprocedural.go", conf);
	}

	@Test
	public void testInterproc7() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setDumpTypeInference(false)
				.setInferTypes(true)
				.setAbstractState(LiSAFactory.getDefaultFor(AbstractState.class, new FieldSensitivePointBasedHeap(), new Interval()))
				.setDumpAnalysis(true)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("interprocedural/interproc7", "interprocedural.go", conf);
	}
}