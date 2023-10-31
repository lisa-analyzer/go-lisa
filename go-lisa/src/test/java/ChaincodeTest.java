
import it.unive.golisa.analysis.ni.IntegrityNIDomain;
import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.checker.IntegrityNIChecker;
import it.unive.golisa.checker.TaintChecker;
import it.unive.golisa.interprocedural.RelaxedOpenCallPolicy;
import it.unive.golisa.loader.annotation.AnnotationSet;
import it.unive.golisa.loader.annotation.sets.HyperledgerFabricNonDeterminismAnnotationSet;
import it.unive.lisa.AnalysisException;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.nonrelational.inference.InferenceSystem;
import it.unive.lisa.analysis.nonrelational.value.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;
import it.unive.lisa.interprocedural.context.ContextBasedAnalysis;
import it.unive.lisa.interprocedural.context.FullStackToken;
import java.io.IOException;
import org.junit.Ignore;
import org.junit.Test;

public class ChaincodeTest extends GoChaincodeTestExecutor {

	private final AnnotationSet annSet = new HyperledgerFabricNonDeterminismAnnotationSet();

	@Test
	public void testBoleto() throws AnalysisException, IOException {
		CronConfiguration conf = new CronConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(FullStackToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.semanticChecks.add(new TaintChecker());
		conf.compareWithOptimization = false;
		conf.testDir = "cc/boleto";
		conf.testSubDir = "taint";
		conf.programFile = "boleto.go";
		conf.annSet = annSet;
		perform(conf);
	}

	@Test
	public void testBoletoNI() throws AnalysisException, IOException {
		CronConfiguration conf = new CronConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(FullStackToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.semanticChecks.add(new IntegrityNIChecker());
		conf.compareWithOptimization = false;
		conf.testDir = "cc/boleto";
		conf.testSubDir = "ni";
		conf.programFile = "boleto.go";
		conf.annSet = annSet;
		perform(conf);
	}

	@Test
	public void testMarblesChaincode() throws AnalysisException, IOException {
		CronConfiguration conf = new CronConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(FullStackToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.semanticChecks.add(new TaintChecker());
		conf.compareWithOptimization = false;
		conf.testDir = "cc/marbles-chaincode";
		conf.testSubDir = "taint";
		conf.programFile = "marbles_chaincode.go";
		conf.annSet = annSet;
		perform(conf);
	}

	@Test
	public void testHighThroughput() throws AnalysisException, IOException {
		CronConfiguration conf = new CronConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(FullStackToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.semanticChecks.add(new TaintChecker());
		conf.compareWithOptimization = false;
		conf.testDir = "cc/high-throughput";
		conf.programFile = "high-throughput.go";
		conf.annSet = annSet;
		perform(conf);
	}

	@Test
	public void testMarblesChaincodeNI() throws AnalysisException, IOException {
		CronConfiguration conf = new CronConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(FullStackToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.semanticChecks.add(new IntegrityNIChecker());
		conf.compareWithOptimization = false;
		conf.testDir = "cc/marbles-chaincode";
		conf.testSubDir = "ni";
		conf.programFile = "marbles_chaincode.go";
		conf.annSet = annSet;
		perform(conf);
	}

	@Test
	public void testTommyStarkNI() throws AnalysisException, IOException {
		CronConfiguration conf = new CronConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(FullStackToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.semanticChecks.add(new IntegrityNIChecker());
		conf.compareWithOptimization = false;
		conf.testDir = "cc/tommystark";
		conf.testSubDir = "ni";
		conf.programFile = "contract_chaincode.go";
		conf.annSet = annSet;
		perform(conf);
	}

	@Test
	public void testTommyStark() throws AnalysisException, IOException {
		CronConfiguration conf = new CronConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(FullStackToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.semanticChecks.add(new TaintChecker());
		conf.compareWithOptimization = false;
		conf.testDir = "cc/tommystark";
		conf.testSubDir = "taint";
		conf.programFile = "contract_chaincode.go";
		conf.annSet = annSet;
		perform(conf);
	}

	@Test
	public void testSacc() throws AnalysisException, IOException {
		CronConfiguration conf = new CronConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(FullStackToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.semanticChecks.add(new TaintChecker());
		conf.compareWithOptimization = false;
		conf.testDir = "cc/sacc";
		conf.programFile = "sacc.go";
		conf.annSet = annSet;
		perform(conf);
	}

	@Test
	public void testMyCC() throws AnalysisException, IOException {
		CronConfiguration conf = new CronConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(FullStackToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.semanticChecks.add(new TaintChecker());
		conf.compareWithOptimization = false;
		conf.testDir = "cc/mycc";
		conf.programFile = "mycc.go";
		conf.annSet = annSet;
		perform(conf);
	}

	@Test
	public void testChaincodeNI() throws AnalysisException, IOException {
		CronConfiguration conf = new CronConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(FullStackToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.semanticChecks.add(new IntegrityNIChecker());
		conf.compareWithOptimization = false;
		conf.testDir = "cc/chaincode";
		conf.programFile = "chaincode.go";
		conf.annSet = annSet;
		perform(conf);
	}

	@Test
	public void testImplicit() throws AnalysisException, IOException {
		CronConfiguration conf = new CronConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(FullStackToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.semanticChecks.add(new TaintChecker());
		conf.compareWithOptimization = false;
		conf.testDir = "cc/implicit-flow";
		conf.testSubDir = "taint";
		conf.programFile = "implicit.go";
		conf.annSet = annSet;
		perform(conf);
	}

	@Test
	public void testImplicitNI() throws AnalysisException, IOException {
		CronConfiguration conf = new CronConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(FullStackToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.semanticChecks.add(new IntegrityNIChecker());
		conf.compareWithOptimization = false;
		conf.testDir = "cc/implicit-flow";
		conf.testSubDir = "ni";
		conf.programFile = "implicit.go";
		conf.annSet = annSet;
		perform(conf);
	}

	@Ignore
	public void testCpuUse() throws AnalysisException, IOException {
		CronConfiguration conf = new CronConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(FullStackToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.semanticChecks.add(new TaintChecker());
		conf.compareWithOptimization = false;
		conf.testDir = "cc/cpu-use";
		conf.testSubDir = "taint";
		conf.programFile = "cpu-use.go";
		conf.annSet = annSet;
		perform(conf);
	}

	@Ignore
	public void testCpuUseNI() throws AnalysisException, IOException {
		CronConfiguration conf = new CronConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(FullStackToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.semanticChecks.add(new IntegrityNIChecker());
		conf.compareWithOptimization = false;
		conf.testDir = "cc/cpu-use";
		conf.testSubDir = "ni";
		conf.programFile = "cpu-use.go";
		conf.annSet = annSet;
		perform(conf);
	}
}
