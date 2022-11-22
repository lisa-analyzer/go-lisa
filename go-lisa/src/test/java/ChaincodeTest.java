
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
	
	@Test
	public void testAkm4() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new TaintChecker());
		perform("cc/akm4", "taint", "insurance_chaincode_state.go", conf, annSet);
	}
	
	@Test
	public void testAkm4NI() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/akm4", "ni", "insurance_chaincode_state.go", conf, annSet);
	}

	@Test
	public void testEshita53() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new TaintChecker());
		perform("cc/eshida", "taint", "fabcar.go", conf, annSet);
	}
	
	@Test
	public void testEshita53NI() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/eshida", "ni", "fabcar.go", conf, annSet);
	}
	

	@Test
	public void testJuniorug() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new TaintChecker());
		perform("cc/juniorug", "taint", "foodSCM.go", conf, annSet);
	}
	
	@Test
	public void testJuniorugNI() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/juniorug", "ni", "foodSCM.go", conf, annSet);
	}

	@Test
	public void testKompiTech() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new TaintChecker());
		perform("cc/KompiTech", "taint", "mockstub.go", conf, annSet);
	}
	
	@Test
	public void testKompiTechNI() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/KompiTech", "ni", "mockstub.go", conf, annSet);
	}
	

	@Test
	public void testLakshay2395() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new TaintChecker());
		perform("cc/lakshay2395", "taint", "schoolsChaincode.go", conf, annSet);
	}
	
	@Test
	public void testLakshay2395NI() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/lakshay2395", "ni", "schoolsChaincode.go", conf, annSet);
	}
	

	@Test
	public void testSachinCpu() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new TaintChecker());
		perform("cc/sachin-ngpws", "taint", "cpu-use.go", conf, annSet);
	}
	
	@Test
	public void testSachinCpuNI() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/sachin-ngpws", "ni", "cpu-use.go", conf, annSet);
	}
	

	@Test
	public void testTkalai25298Cpu() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new TaintChecker());
		perform("cc/tkalai25298", "taint", "cpu-use.go", conf, annSet);
	}
	
	@Test
	public void testTkalai25298CpuNI() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/tkalai25298", "ni", "cpu-use.go", conf, annSet);
	}
	

	@Test
	public void testTrustContract() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new TaintChecker());
		perform("cc/Trust-Contract", "taint", "cargo-chaincode.go", conf, annSet);
	}
	
	@Test
	public void testTrustContractNI() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/Trust-Contract", "ni", "cargo-chaincode.go", conf, annSet);
	}
	

	@Test
	public void testVmorris() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new TaintChecker());
		perform("cc/vmorris", "taint", "procon.go", conf, annSet);
	}
	
	@Test
	public void testVmorrisNI() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/vmorris", "ni", "procon.go", conf, annSet);
	}
	

	@Test
	public void testXuansonha17031991D() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new TaintChecker());
		perform("cc/xuansonha17031991/drug_information", "taint", "drug_information.go", conf, annSet);
	}
	
	@Test
	public void testXuansonha17031991DNI() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/xuansonha17031991/drug_information", "ni", "drug_information.go", conf, annSet);
	}
	
	@Test
	public void testXuansonha17031991H() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new TaintChecker());
		perform("cc/xuansonha17031991/hospital_fees", "taint", "hospital_fees.go", conf, annSet);
	}
	
	@Test
	public void testXuansonha17031991HNI() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/xuansonha17031991/hospital_fees", "ni", "hospital_fees.go", conf, annSet);
	}
	
	@Test
	public void testXuansonha17031991M() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new TaintChecker());
		perform("cc/xuansonha17031991/medical_record", "taint", "medical_record.go", conf, annSet);
	}
	
	@Test
	public void testXuansonha17031991MNI() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/xuansonha17031991/medical_record", "ni", "medical_record.go", conf, annSet);
	}

	@Test
	public void testXuansonha17031991P() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new TaintChecker());
		perform("cc/xuansonha17031991/patient_inormation", "taint", "patient_inormation.go", conf, annSet);
	}
	
	@Test
	public void testXuansonha17031991PNI() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/xuansonha17031991/patient_inormation", "ni", "patient_inormation.go", conf, annSet);
	}
	
}
