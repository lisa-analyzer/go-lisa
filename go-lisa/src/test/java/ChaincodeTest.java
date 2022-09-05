
import it.unive.golisa.analysis.heap.GoAbstractState;
import it.unive.golisa.analysis.heap.GoPointBasedHeap;
import it.unive.golisa.analysis.ni.IntegrityNIDomain;
import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.checker.IntegrityNIChecker;
import it.unive.golisa.checker.TaintChecker;
import it.unive.golisa.loader.annotation.AnnotationSet;
import it.unive.golisa.loader.annotation.sets.HyperledgerFabricNonDeterminismAnnotationSet;
import it.unive.lisa.AnalysisException;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.LiSAConfiguration.GraphType;
import it.unive.lisa.LiSAFactory;
import it.unive.lisa.analysis.nonrelational.inference.InferenceSystem;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.interprocedural.ContextBasedAnalysis;
import it.unive.lisa.interprocedural.RecursionFreeToken;
import it.unive.lisa.interprocedural.ReturnTopPolicy;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;
import java.io.IOException;
import org.junit.Test;

public class ChaincodeTest extends GoChaincodeTestExecutor {

	private final AnnotationSet annSet = new HyperledgerFabricNonDeterminismAnnotationSet();

	@Test
	public void testBoleto() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setJsonOutput(true)
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton()))
				.setCallGraph(new RTACallGraph())
				.setAbstractState(
						new GoAbstractState<>(new GoPointBasedHeap(),
								new ValueEnvironment<>(new TaintDomain()),
								LiSAFactory.getDefaultFor(TypeDomain.class)))
				.addSemanticCheck(new TaintChecker());
		perform("cc/boleto", "taint", "boleto.go", conf, annSet);
	}

	@Test
	public void testBoletoNI() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setJsonOutput(true)
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton()))
				.setCallGraph(new RTACallGraph())
				.setCallGraph(new RTACallGraph())
				.setAbstractState(
						new GoAbstractState<>(new GoPointBasedHeap(),
								new InferenceSystem<>(new IntegrityNIDomain()),
								LiSAFactory.getDefaultFor(TypeDomain.class)))
				.addSemanticCheck(new IntegrityNIChecker());
		perform("cc/boleto", "ni", "boleto.go", conf, annSet);
	}

	@Test
	public void testMarblesChaincode() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setJsonOutput(true)
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton()))
				.setCallGraph(new RTACallGraph())
				.setAbstractState(
						new GoAbstractState<>(new GoPointBasedHeap(),
								new ValueEnvironment<>(new TaintDomain()),
								LiSAFactory.getDefaultFor(TypeDomain.class)))
				.addSemanticCheck(new TaintChecker());
		perform("cc/marbles-chaincode", "marbles_chaincode.go", conf, annSet);
	}

	@Test
	public void testHighThroughput() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setJsonOutput(true)

				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton()))
				.setCallGraph(new RTACallGraph())
				.setAbstractState(
						new GoAbstractState<>(new GoPointBasedHeap(),
								new ValueEnvironment<>(new TaintDomain()),
								LiSAFactory.getDefaultFor(TypeDomain.class)))
				.addSemanticCheck(new TaintChecker());
		perform("cc/high-throughput", "high-throughput.go", conf, annSet);
	}

	@Test
	public void testMarbles02() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setJsonOutput(true)

				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton()))
				.setCallGraph(new RTACallGraph())
				.setAbstractState(
						new GoAbstractState<>(new GoPointBasedHeap(),
								new ValueEnvironment<>(new TaintDomain()),
								LiSAFactory.getDefaultFor(TypeDomain.class)))
				.addSemanticCheck(new TaintChecker());
		perform("cc/marbles02", "marbles02.go", conf, annSet);
	}

	@Test
	public void testCpuUse() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setJsonOutput(true)
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton()))
				.setCallGraph(new RTACallGraph())
				.setAbstractState(
						new GoAbstractState<>(new GoPointBasedHeap(),
								new ValueEnvironment<>(new TaintDomain()),
								LiSAFactory.getDefaultFor(TypeDomain.class)))
				.addSemanticCheck(new TaintChecker());
		perform("cc/cpu-use", "cpu-use.go", conf, annSet);
	}

	@Test
	public void testCpuUseNI() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setJsonOutput(true)
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton()))
				.setCallGraph(new RTACallGraph())
				.setAbstractState(
						new GoAbstractState<>(new GoPointBasedHeap(),
								new InferenceSystem<>(new IntegrityNIDomain()),
								LiSAFactory.getDefaultFor(TypeDomain.class)))
				.addSemanticCheck(new IntegrityNIChecker());
		perform("cc/cpu-use", "cpu-use.go", conf, annSet);
	}

	@Test
	public void testMarblesChaincodeNI() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setJsonOutput(true)
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton()))
				.setCallGraph(new RTACallGraph())
				.setAbstractState(
						new GoAbstractState<>(new GoPointBasedHeap(),
								new InferenceSystem<>(new IntegrityNIDomain()),
								LiSAFactory.getDefaultFor(TypeDomain.class)))
				.addSemanticCheck(new IntegrityNIChecker());
		perform("cc/marbles-chaincode", "marbles_chaincode.go", conf, annSet);
	}

	@Test
	public void testMarbles02NI() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setJsonOutput(true)
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton()))
				.setCallGraph(new RTACallGraph())
				.setAbstractState(
						new GoAbstractState<>(new GoPointBasedHeap(),
								new InferenceSystem<>(new IntegrityNIDomain()),
								LiSAFactory.getDefaultFor(TypeDomain.class)))
				.addSemanticCheck(new IntegrityNIChecker());
		perform("cc/marbles02", "marbles02.go", conf, annSet);
	}

	@Test
	public void testTommyStarkNI() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setJsonOutput(true)
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton()))
				.setCallGraph(new RTACallGraph())
				.setAbstractState(
						new GoAbstractState<>(new GoPointBasedHeap(),
								new InferenceSystem<>(new IntegrityNIDomain()),
								LiSAFactory.getDefaultFor(TypeDomain.class)))
				.addSemanticCheck(new IntegrityNIChecker());
		perform("cc/tommystark", "contract_chaincode.go", conf, annSet);
	}

	@Test
	public void testSacc() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setJsonOutput(true)
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton()))
				.setCallGraph(new RTACallGraph())
				.setAbstractState(
						new GoAbstractState<>(new GoPointBasedHeap(),
								new ValueEnvironment<>(new TaintDomain()),
								LiSAFactory.getDefaultFor(TypeDomain.class)))
				.addSemanticCheck(new TaintChecker());
		perform("cc/sacc", "sacc.go", conf, annSet);
	}

	@Test
	public void testMyCC() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setJsonOutput(true)
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton()))
				.setCallGraph(new RTACallGraph())
				.setAbstractState(
						new GoAbstractState<>(new GoPointBasedHeap(),
								new ValueEnvironment<>(new TaintDomain()),
								LiSAFactory.getDefaultFor(TypeDomain.class)))
				.addSemanticCheck(new TaintChecker());
		perform("cc/mycc", "mycc.go", conf, annSet);
	}

	@Test
	public void testChaincode() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setJsonOutput(true)
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton()))
				.setCallGraph(new RTACallGraph())
				.setAbstractState(
						new GoAbstractState<>(new GoPointBasedHeap(),
								new ValueEnvironment<>(new TaintDomain()),
								LiSAFactory.getDefaultFor(TypeDomain.class)))
				.addSemanticCheck(new TaintChecker());
		perform("cc/chaincode", "chaincode.go", conf, annSet);
	}

	@Test
	public void testImplicit() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setJsonOutput(true)
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton()))
				.setCallGraph(new RTACallGraph())
				.setAbstractState(
						new GoAbstractState<>(new GoPointBasedHeap(),
								new ValueEnvironment<>(new TaintDomain()),
								LiSAFactory.getDefaultFor(TypeDomain.class)))
				.addSemanticCheck(new TaintChecker());
		perform("cc/implicit-flow", "taint", "implicit.go", conf, annSet);
	}

	@Test
	public void testImplicitNI() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setJsonOutput(true)
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton()))
				.setCallGraph(new RTACallGraph())
				.setAbstractState(
						new GoAbstractState<>(new GoPointBasedHeap(),
								new InferenceSystem<>(new IntegrityNIDomain()),
								LiSAFactory.getDefaultFor(TypeDomain.class)))
				.addSemanticCheck(new IntegrityNIChecker());
		perform("cc/implicit-flow", "ni", "implicit.go", conf, annSet);
	}
}
