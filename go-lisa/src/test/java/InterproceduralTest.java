
import java.io.IOException;

import org.junit.Test;

import it.unive.golisa.analysis.heap.GoAbstractState;
import it.unive.golisa.analysis.heap.GoFieldSensitivePointBasedHeap;
import it.unive.golisa.analysis.heap.GoPointBasedHeap;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.LiSAFactory;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.numeric.Interval;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.interprocedural.ContextBasedAnalysis;
import it.unive.lisa.interprocedural.RecursionFreeToken;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;

public class InterproceduralTest extends GoAnalysisTestExecutor {

	@Test
	public void testInterproc1() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(
						new GoAbstractState<>(new GoPointBasedHeap(),
								new ValueEnvironment<>(new Interval()),
								LiSAFactory.getDefaultFor(TypeDomain.class)))
				.setSerializeResults(true)
				.setSerializeResults(true)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("interprocedural/interproc1", "interprocedural.go", conf);
	}

	@Test
	public void testInterproc2() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(
						new GoAbstractState<>(new GoPointBasedHeap(),
								new ValueEnvironment<>(new Interval()),
								LiSAFactory.getDefaultFor(TypeDomain.class)))
				.setSerializeResults(true)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("interprocedural/interproc2", "interprocedural.go", conf);
	}

	@Test
	public void testInterproc3() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(
						new GoAbstractState<>(new GoFieldSensitivePointBasedHeap(),
								new ValueEnvironment<>(new Interval()),
								LiSAFactory.getDefaultFor(TypeDomain.class)))
				.setSerializeResults(true)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("interprocedural/interproc3", "interprocedural.go", conf);
	}

	@Test
	public void testInterproc4() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(
						new GoAbstractState<>(new GoFieldSensitivePointBasedHeap(),
								new ValueEnvironment<>(new Interval()),
								LiSAFactory.getDefaultFor(TypeDomain.class)))
				.setSerializeResults(true)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("interprocedural/interproc4", "interprocedural.go", conf);
	}

	@Test
	public void testInterproc5() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(
						new GoAbstractState<>(new GoFieldSensitivePointBasedHeap(),
								new ValueEnvironment<>(new Interval()),
								LiSAFactory.getDefaultFor(TypeDomain.class)))
				.setSerializeResults(true)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("interprocedural/interproc5", "interprocedural.go", conf);
	}

	@Test
	public void testInterproc6() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(
						new GoAbstractState<>(new GoFieldSensitivePointBasedHeap(),
								new ValueEnvironment<>(new Interval()),
								LiSAFactory.getDefaultFor(TypeDomain.class)))
				.setSerializeResults(true)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("interprocedural/interproc6", "interprocedural.go", conf);
	}

	@Test
	public void testInterproc7() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(
						new GoAbstractState<>(new GoFieldSensitivePointBasedHeap(),
								new ValueEnvironment<>(new Interval()),
								LiSAFactory.getDefaultFor(TypeDomain.class)))
				.setSerializeResults(true)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("interprocedural/interproc7", "interprocedural.go", conf);
	}

	@Test
	public void testInteproc8() throws IOException, AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.setJsonOutput(true)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton()))
				.setAbstractState(
						new GoAbstractState<>(new GoFieldSensitivePointBasedHeap(),
								new ValueEnvironment<>(new Interval()),
								LiSAFactory.getDefaultFor(TypeDomain.class)))
				.setSerializeResults(true);

		perform("interprocedural/interproc8", "interprocedural.go", conf);
	}
}