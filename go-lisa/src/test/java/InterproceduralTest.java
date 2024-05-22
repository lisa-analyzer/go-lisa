
import java.io.IOException;

import org.junit.Test;

import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.pointbased.FieldSensitivePointBasedHeap;
import it.unive.lisa.analysis.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.nonrelational.value.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.numeric.Interval;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;
import it.unive.lisa.interprocedural.context.ContextBasedAnalysis;
import it.unive.lisa.interprocedural.context.FullStackToken;

public class InterproceduralTest extends GoAnalysisTestExecutor {

	@Test
	public void testInterproc1() throws AnalysisSetupException {
		CronConfiguration conf = new CronConfiguration();
		conf.jsonOutput = true;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(FullStackToken.getSingleton());
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new ValueEnvironment<>(new Interval()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.serializeResults = true;
		conf.testDir = "interprocedural/interproc1";
		conf.programFile = "interprocedural.go";
		perform(conf);
	}

	@Test
	public void testInterproc2() throws AnalysisSetupException {
		CronConfiguration conf = new CronConfiguration();
		conf.jsonOutput = true;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(FullStackToken.getSingleton());
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new ValueEnvironment<>(new Interval()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.serializeResults = true;
		conf.testDir = "interprocedural/interproc2";
		conf.programFile = "interprocedural.go";
		perform(conf);
	}

	@Test
	public void testInterproc3() throws AnalysisSetupException {
		CronConfiguration conf = new CronConfiguration();
		conf.jsonOutput = true;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(FullStackToken.getSingleton());
		conf.abstractState = new SimpleAbstractState<>(new FieldSensitivePointBasedHeap(),
				new ValueEnvironment<>(new Interval()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.serializeResults = true;
		conf.testDir = "interprocedural/interproc3";
		conf.programFile = "interprocedural.go";
		perform(conf);
	}

	@Test
	public void testInterproc4() throws AnalysisSetupException {
		CronConfiguration conf = new CronConfiguration();
		conf.jsonOutput = true;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(FullStackToken.getSingleton());
		conf.abstractState = new SimpleAbstractState<>(new FieldSensitivePointBasedHeap(),
				new ValueEnvironment<>(new Interval()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.serializeResults = true;
		conf.testDir = "interprocedural/interproc4";
		conf.programFile = "interprocedural.go";
		perform(conf);
	}

	@Test
	public void testInterproc5() throws AnalysisSetupException {
		CronConfiguration conf = new CronConfiguration();
		conf.jsonOutput = true;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(FullStackToken.getSingleton());
		conf.abstractState = new SimpleAbstractState<>(new FieldSensitivePointBasedHeap(),
				new ValueEnvironment<>(new Interval()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.serializeResults = true;
		conf.testDir = "interprocedural/interproc5";
		conf.programFile = "interprocedural.go";
		perform(conf);
	}

	@Test
	public void testInterproc6() throws AnalysisSetupException {
		CronConfiguration conf = new CronConfiguration();
		conf.jsonOutput = true;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(FullStackToken.getSingleton());
		conf.abstractState = new SimpleAbstractState<>(new FieldSensitivePointBasedHeap(),
				new ValueEnvironment<>(new Interval()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.serializeResults = true;
		conf.testDir = "interprocedural/interproc6";
		conf.programFile = "interprocedural.go";
		perform(conf);
	}

	@Test
	public void testInterproc7() throws AnalysisSetupException {
		CronConfiguration conf = new CronConfiguration();
		conf.jsonOutput = true;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(FullStackToken.getSingleton());
		conf.abstractState = new SimpleAbstractState<>(new FieldSensitivePointBasedHeap(),
				new ValueEnvironment<>(new Interval()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.serializeResults = true;
		conf.testDir = "interprocedural/interproc7";
		conf.programFile = "interprocedural.go";
		perform(conf);
	}

	@Test
	public void testInteproc8() throws IOException, AnalysisSetupException {
		CronConfiguration conf = new CronConfiguration();
		conf.jsonOutput = true;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(FullStackToken.getSingleton());
		conf.abstractState = new SimpleAbstractState<>(new FieldSensitivePointBasedHeap(),
				new ValueEnvironment<>(new Interval()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.serializeResults = true;
		conf.testDir = "interprocedural/interproc8";
		conf.programFile = "interprocedural.go";
		perform(conf);
	}

	@Test
	public void testInteproc9() throws IOException, AnalysisSetupException {
		CronConfiguration conf = new CronConfiguration();
		conf.jsonOutput = true;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(FullStackToken.getSingleton());
		conf.abstractState = new SimpleAbstractState<>(new FieldSensitivePointBasedHeap(),
				new ValueEnvironment<>(new Interval()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.serializeResults = true;
		conf.testDir = "interprocedural/interproc9";
		conf.programFile = "interprocedural.go";
		perform(conf);
	}
}