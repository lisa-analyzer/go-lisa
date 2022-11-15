
import java.io.IOException;

import org.junit.Test;

import it.unive.golisa.analysis.heap.GoAbstractState;
import it.unive.golisa.analysis.heap.GoPointBasedHeap;
import it.unive.golisa.analysis.ni.IntegrityNIDomain;
import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.checker.IntegrityNIChecker;
import it.unive.golisa.checker.TaintChecker;
import it.unive.golisa.interprocedural.RelaxedOpenCallPolicy;
import it.unive.golisa.loader.annotation.FrameworkNonDeterminismAnnotationSetFactory;
import it.unive.golisa.loader.annotation.sets.NonDeterminismAnnotationSet;
import it.unive.lisa.AnalysisException;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.LiSAFactory;
import it.unive.lisa.analysis.nonrelational.inference.InferenceSystem;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.interprocedural.ContextBasedAnalysis;
import it.unive.lisa.interprocedural.RecursionFreeToken;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;

public class ChaincodeTest extends GoChaincodeTestExecutor {

	private final NonDeterminismAnnotationSet[] annSet = FrameworkNonDeterminismAnnotationSetFactory.getAnnotationSets("HYPERLEDGER-FABRIC");

	@Test
	public void testBoleto() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new TaintChecker());
		perform("cc/boleto", "taint", "boleto.go", conf, annSet);
	}

	@Test
	public void testBoletoNI() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/boleto", "ni", "boleto.go", conf, annSet);
	}

	@Test
	public void testMarblesChaincode() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new TaintChecker());
		perform("cc/marbles-chaincode", "marbles_chaincode.go", conf, annSet);
	}

	@Test
	public void testHighThroughput() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new TaintChecker());
		perform("cc/high-throughput", "high-throughput.go", conf, annSet);
	}

	@Test
	public void testMarbles02() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new TaintChecker());
		perform("cc/marbles02", "marbles02.go", conf, annSet);
	}

	@Test
	public void testCpuUse() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new TaintChecker());
		perform("cc/cpu-use", "cpu-use.go", conf, annSet);
	}

	@Test
	public void testCpuUseNI() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/cpu-use", "cpu-use.go", conf, annSet);
	}

	@Test
	public void testMarblesChaincodeNI() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/marbles-chaincode", "marbles_chaincode.go", conf, annSet);
	}

	@Test
	public void testMarbles02NI() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/marbles02", "marbles02.go", conf, annSet);
	}

	@Test
	public void testTommyStarkNI() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/tommystark", "contract_chaincode.go", conf, annSet);
	}

	@Test
	public void testSacc() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new TaintChecker());
		perform("cc/sacc", "sacc.go", conf, annSet);
	}

	@Test
	public void testMyCC() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new TaintChecker());
		perform("cc/mycc", "mycc.go", conf, annSet);
	}

	@Test
	public void testChaincode() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/chaincode", "chaincode.go", conf, annSet);
	}

	@Test
	public void testImplicit() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new TaintChecker());
		perform("cc/implicit-flow", "taint", "implicit.go", conf, annSet);
	}

	@Test
	public void testImplicitNI() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/implicit-flow", "ni", "implicit.go", conf, annSet);
	}
	
	@Test
	public void testAdaephon() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new TaintChecker());
		perform("cc/adaephon-ben", "taint", "main.go", conf, annSet);
	}
	
	@Test
	public void testAdaephonNI() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/adaephon-ben", "ni", "main.go", conf, annSet);
	}
	
	@Test
	public void testCristianoAndrade() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new TaintChecker());
		perform("cc/cristiano-an", "taint", "chaincode-test.go", conf, annSet);
	}
	
	@Test
	public void testCristianoAndradeNI() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/cristiano-an", "ni", "chaincode-test.go", conf, annSet);
	}
}
