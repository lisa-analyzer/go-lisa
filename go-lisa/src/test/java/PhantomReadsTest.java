import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.checker.TaintChecker;
import it.unive.golisa.interprocedural.RelaxedOpenCallPolicy;
import it.unive.golisa.loader.annotation.sets.PhantomReadAnnotationSet;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.nonrelational.value.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;
import it.unive.lisa.interprocedural.context.ContextBasedAnalysis;
import it.unive.lisa.interprocedural.context.FullStackToken;

import org.junit.Test;

public class PhantomReadsTest extends GoChaincodeTestExecutor {

	private static final PhantomReadAnnotationSet annSet = new PhantomReadAnnotationSet();

	public void run(String testDir, String programFile) throws AnalysisSetupException {
		CronConfiguration conf = new CronConfiguration();
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()), new TypeEnvironment<>(new InferredTypes()));
		conf.semanticChecks.add(new TaintChecker());
		conf.jsonOutput = true;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(FullStackToken.getSingleton());
		conf.compareWithOptimization = false;
		conf.testDir = testDir;
		conf.programFile = programFile;
		conf.annSet = annSet;
		
		perform(conf);
	}
	
	@Test
	public void testSingle1() throws AnalysisSetupException {
		run("hf/phantom-read/single/single1", "single1.go");
	}
	
	@Test
	public void testSingle2() throws AnalysisSetupException {
		run("hf/phantom-read/single/single2", "single2.go");
	}
	
	@Test
	public void testSingle3() throws AnalysisSetupException {
		run("hf/phantom-read/single/single3", "single3.go");
	}
	
	@Test
	public void testSingle4() throws AnalysisSetupException {
		run("hf/phantom-read/single/single4", "single4.go");
	}
	
	@Test
	public void testInterproc1() throws AnalysisSetupException {
		run("hf/phantom-read/interproc/inter1", "inter1.go");
	}
}
