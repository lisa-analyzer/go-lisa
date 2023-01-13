
import java.io.IOException;

import org.junit.Test;

import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.pointbased.FieldSensitivePointBasedHeap;
import it.unive.lisa.analysis.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.nonrelational.value.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.numeric.Interval;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.interprocedural.ContextBasedAnalysis;
import it.unive.lisa.interprocedural.RecursionFreeToken;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;

public class InterproceduralTest extends GoAnalysisTestExecutor {

	@Test
	public void testInterproc1() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new ValueEnvironment<>(new Interval()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.serializeResults = true;
		perform("interprocedural/interproc1", "interprocedural.go", conf);
	}

	@Test
	public void testInterproc2() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new ValueEnvironment<>(new Interval()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.serializeResults = true;
		perform("interprocedural/interproc2", "interprocedural.go", conf);
	}

	@Test
	public void testInterproc3() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.abstractState = new SimpleAbstractState<>(new FieldSensitivePointBasedHeap(),
				new ValueEnvironment<>(new Interval()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.serializeResults = true;
		perform("interprocedural/interproc3", "interprocedural.go", conf);
	}

	@Test
	public void testInterproc4() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.abstractState = new SimpleAbstractState<>(new FieldSensitivePointBasedHeap(),
				new ValueEnvironment<>(new Interval()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.serializeResults = true;
		perform("interprocedural/interproc4", "interprocedural.go", conf);
	}

	@Test
	public void testInterproc5() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.abstractState = new SimpleAbstractState<>(new FieldSensitivePointBasedHeap(),
				new ValueEnvironment<>(new Interval()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.serializeResults = true;
		perform("interprocedural/interproc5", "interprocedural.go", conf);
	}

	@Test
	public void testInterproc6() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.abstractState = new SimpleAbstractState<>(new FieldSensitivePointBasedHeap(),
				new ValueEnvironment<>(new Interval()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.serializeResults = true;
		perform("interprocedural/interproc6", "interprocedural.go", conf);
	}

	@Test
	public void testInterproc7() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.abstractState = new SimpleAbstractState<>(new FieldSensitivePointBasedHeap(),
				new ValueEnvironment<>(new Interval()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.serializeResults = true;
		perform("interprocedural/interproc7", "interprocedural.go", conf);
	}

	@Test
	public void testInteproc8() throws IOException, AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.abstractState = new SimpleAbstractState<>(new FieldSensitivePointBasedHeap(),
				new ValueEnvironment<>(new Interval()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.serializeResults = true;

		perform("interprocedural/interproc8", "interprocedural.go", conf);
	}
}