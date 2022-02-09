
import java.io.IOException;

import org.junit.Test;

import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.analysis.taint.annotation.AnnotationSet;
import it.unive.golisa.analysis.taint.annotation.NonDeterminismAnnotationSet;
import it.unive.golisa.checker.TaintChecker;
import it.unive.lisa.AnalysisException;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.LiSAFactory;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.nonrelational.inference.InferenceSystem;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.interprocedural.ContextBasedAnalysis;
import it.unive.lisa.interprocedural.RecursionFreeToken;
import it.unive.lisa.interprocedural.ReturnTopPolicy;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;

public class ChaincodeTest extends GoChaincodeTestExecutor {
	
	private final AnnotationSet annSet = new NonDeterminismAnnotationSet();
	
	@Test
	public void testBoleto() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setJsonOutput(true)

				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton()))
				.setCallGraph(new RTACallGraph())
				.setAbstractState(
						new SimpleAbstractState<>(new PointBasedHeap(), new InferenceSystem<>(new TaintDomain()),
								LiSAFactory.getDefaultFor(TypeDomain.class)))
				.addSemanticCheck(new TaintChecker());
		perform("cc/boleto", "boleto.go", conf, annSet);
	}

	@Test
	public void testMarblesChaincode() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setJsonOutput(true)

				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton()))
				.setCallGraph(new RTACallGraph())
				.setAbstractState(
						new SimpleAbstractState<>(new PointBasedHeap(), new InferenceSystem<>(new TaintDomain()),
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
						new SimpleAbstractState<>(new PointBasedHeap(), new InferenceSystem<>(new TaintDomain()),
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
						new SimpleAbstractState<>(new PointBasedHeap(), new InferenceSystem<>(new TaintDomain()),
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
						new SimpleAbstractState<>(new PointBasedHeap(), new InferenceSystem<>(new TaintDomain()),
								LiSAFactory.getDefaultFor(TypeDomain.class)))
				.addSemanticCheck(new TaintChecker());
		perform("cc/cpu-use", "cpu-use.go", conf, annSet);
	}
}
