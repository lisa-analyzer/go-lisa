import java.io.IOException;

import org.junit.Test;

import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.checker.TaintChecker;
import it.unive.lisa.AnalysisException;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.nonrelational.inference.InferenceSystem;
import it.unive.lisa.interprocedural.ContextBasedAnalysis;
import it.unive.lisa.interprocedural.RecursionFreeToken;
import it.unive.lisa.interprocedural.ReturnTopPolicy;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;

public class ChaincodeTest extends GoChaincodeTestExecutor {

	@Test
	public void testBoleto() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setJsonOutput(true)
				.setInferTypes(true)
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton()))
				.setCallGraph(new RTACallGraph())
				.setAbstractState(
						new SimpleAbstractState<>(new PointBasedHeap(), new InferenceSystem<>(new TaintDomain())))
				.addSemanticCheck(new TaintChecker());
		perform("cc/boleto", "boleto.go", conf);
	}
	
	@Test
	public void testMarblesChaincode() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setJsonOutput(true)
				.setInferTypes(true)
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton()))
				.setCallGraph(new RTACallGraph())
				.setAbstractState(
						new SimpleAbstractState<>(new PointBasedHeap(), new InferenceSystem<>(new TaintDomain())))
				.addSemanticCheck(new TaintChecker());
		perform("cc/marbles-chaincode", "marbles_chaincode.go", conf);
	}
	
	@Test
	public void testHighThroughput() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setJsonOutput(true)
				.setInferTypes(true)
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton()))
				.setCallGraph(new RTACallGraph())
				.setAbstractState(
						new SimpleAbstractState<>(new PointBasedHeap(), new InferenceSystem<>(new TaintDomain())))
				.addSemanticCheck(new TaintChecker());
		perform("cc/high-throughput", "high-throughput.go", conf);
	}
	
	@Test
	public void testMarbles02() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setJsonOutput(true)
				.setInferTypes(true)
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton()))
				.setCallGraph(new RTACallGraph())
				.setAbstractState(
						new SimpleAbstractState<>(new PointBasedHeap(), new InferenceSystem<>(new TaintDomain())))
				.addSemanticCheck(new TaintChecker());
		perform("cc/marbles02", "marbles02.go", conf);
	}
	
	@Test
	public void testCpuUse() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setJsonOutput(true)
				.setInferTypes(true)
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton()))
				.setCallGraph(new RTACallGraph())
				.setAbstractState(
						new SimpleAbstractState<>(new PointBasedHeap(), new InferenceSystem<>(new TaintDomain())))
				.addSemanticCheck(new TaintChecker());
		perform("cc/cpu-use", "cpu-use.go", conf);
	}
}
