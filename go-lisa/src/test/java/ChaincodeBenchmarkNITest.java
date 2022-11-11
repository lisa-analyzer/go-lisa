
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import it.unive.golisa.analysis.ni.IntegrityNIDomain;
import it.unive.golisa.checker.IntegrityNIChecker;
import it.unive.golisa.loader.annotation.AnnotationSet;
import it.unive.golisa.loader.annotation.sets.HyperledgerFabricNonDeterminismAnnotationSet;
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

@Ignore
public class ChaincodeBenchmarkNITest extends GoChaincodeTestExecutor {

	private final AnnotationSet annSet = new HyperledgerFabricNonDeterminismAnnotationSet();

	@Test
	public void test_1() throws AnalysisException, IOException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());

		perform("cc/benchmark/2cluster/", "contract.go", conf, annSet);

	}

	@Test
	public void test_2() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());

		perform("cc/benchmark/abbeydabiri/", "bank.go", conf, annSet);

	}

	@Test
	public void test_3() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());

		perform("cc/benchmark/abbeydabiri/", "bankbranch.go", conf, annSet);

	}

	@Test
	public void test_4() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abbeydabiri/", "bankbranch_test.go", conf, annSet);

	}

	@Test
	public void test_5() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abbeydabiri/", "bank_test.go", conf, annSet);

	}

	@Test
	public void test_6() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abbeydabiri/", "branchconfig.go", conf, annSet);

	}

	@Test
	public void test_7() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abbeydabiri/", "branchconfig_test.go", conf, annSet);

	}

	@Test
	public void test_8() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abbeydabiri/", "buyer.go", conf, annSet);

	}

	@Test
	public void test_9() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abbeydabiri/", "buyer_test.go", conf, annSet);

	}

	@Test
	public void test_10() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abbeydabiri/", "init.go", conf, annSet);

	}

	@Test
	public void test_11() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abbeydabiri/", "invoke.go", conf, annSet);

	}

	@Test
	public void test_12() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abbeydabiri/", "loan.go", conf, annSet);

	}

	@Test
	public void test_13() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abbeydabiri/", "loanbuyer.go", conf, annSet);

	}

	@Test
	public void test_14() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abbeydabiri/", "loanbuyer_test.go", conf, annSet);

	}

	@Test
	public void test_15() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abbeydabiri/", "loandoc.go", conf, annSet);

	}

	@Test
	public void test_16() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abbeydabiri/", "loandoc_test.go", conf, annSet);

	}

	@Test
	public void test_17() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abbeydabiri/", "loanmarketshare.go", conf, annSet);

	}

	@Test
	public void test_18() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abbeydabiri/", "loanmarketshare_test.go", conf, annSet);

	}

	@Test
	public void test_19() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abbeydabiri/", "loanrating.go", conf, annSet);

	}

	@Test
	public void test_20() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abbeydabiri/", "loanrating_test.go", conf, annSet);

	}

	@Test
	public void test_21() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abbeydabiri/", "loan_test.go", conf, annSet);

	}

	@Test
	public void test_22() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abbeydabiri/", "main.go", conf, annSet);

	}

	@Test
	public void test_23() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abbeydabiri/", "main_test.go", conf, annSet);

	}

	@Test
	public void test_24() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abbeydabiri/", "message.go", conf, annSet);

	}

	@Test
	public void test_25() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abbeydabiri/", "message_test.go", conf, annSet);

	}

	@Test
	public void test_26() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abbeydabiri/", "permissions.go", conf, annSet);

	}

	@Test
	public void test_27() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abbeydabiri/", "permissions_test.go", conf, annSet);

	}

	@Test
	public void test_28() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abbeydabiri/", "property.go", conf, annSet);

	}

	@Test
	public void test_29() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abbeydabiri/", "property_test.go", conf, annSet);

	}

	@Test
	public void test_30() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abbeydabiri/", "role.go", conf, annSet);

	}

	@Test
	public void test_31() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abbeydabiri/", "role_test.go", conf, annSet);

	}

	@Test
	public void test_32() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abbeydabiri/", "seller.go", conf, annSet);

	}

	@Test
	public void test_33() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abbeydabiri/", "seller_test.go", conf, annSet);

	}

	@Test
	public void test_34() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abbeydabiri/", "transactions.go", conf, annSet);

	}

	@Test
	public void test_35() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abbeydabiri/", "transactions_test.go", conf, annSet);

	}

	@Test
	public void test_36() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abbeydabiri/", "user.go", conf, annSet);

	}

	@Test
	public void test_37() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abbeydabiri/", "usercategory.go", conf, annSet);

	}

	@Test
	public void test_38() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abbeydabiri/", "usercategory_test.go", conf, annSet);

	}

	@Test
	public void test_39() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abbeydabiri/", "user_test.go", conf, annSet);

	}

	@Test
	public void test_40() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abbeydabiri/", "utils.go", conf, annSet);

	}

	@Test
	public void test_41() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abegpatel/", "chaincode.go", conf, annSet);

	}

	@Test
	public void test_42() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abegpatel/", "chaincode_sample.go", conf, annSet);

	}

	@Test
	public void test_43() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abegpatel/", "chaincode_test.go", conf, annSet);

	}

	@Test
	public void test_44() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abegpatel/", "main.go", conf, annSet);

	}

	@Test
	public void test_45() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abegpatel/", "map.go", conf, annSet);

	}

	@Test
	public void test_46() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abegpatel/", "mapkeys.go", conf, annSet);

	}

	@Test
	public void test_47() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abegpatel/", "map_private.go", conf, annSet);

	}

	@Test
	public void test_48() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/abegpatel/", "marbles_chaincode.go", conf, annSet);

	}

	@Test
	public void test_49() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/acloudfan/", "chaincode_example02.go", conf, annSet);

	}

	@Test
	public void test_50() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/arthurmsouza/", "boleto.go", conf, annSet);

	}

	@Test
	public void test_51() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/arthurmsouza/", "erc20Token.go", conf, annSet);

	}

	@Test
	public void test_52() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/arthurmsouza/", "fmt.go", conf, annSet);

	}

	@Test
	public void test_53() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/arthurmsouza/", "io.go", conf, annSet);

	}

	@Test
	public void test_54() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/atirikt/", "chaincode.go", conf, annSet);

	}

	@Test
	public void test_55() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/atirikt/", "chaincodeserver.go", conf, annSet);

	}

	@Test
	public void test_56() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/atirikt/", "config_test.go", conf, annSet);

	}

	@Test
	public void test_57() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/atirikt/", "context.go", conf, annSet);

	}

	@Test
	public void test_58() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/atirikt/", "contract_chaincode.go", conf, annSet);

	}

	@Test
	public void test_59() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/atirikt/", "contract_chaincode_test.go", conf, annSet);

	}

	@Test
	public void test_60() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/atirikt/", "errors.go", conf, annSet);

	}

	@Test
	public void test_61() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/atirikt/", "flag.go", conf, annSet);

	}

	@Test
	public void test_62() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/atirikt/", "handler_test.go", conf, annSet);

	}

	@Test
	public void test_63() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/atirikt/", "marbles_chaincode.go", conf, annSet);

	}

	@Test
	public void test_64() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/atirikt/", "mockstub.go", conf, annSet);

	}

	@Test
	public void test_65() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/atirikt/", "mockstub_test.go", conf, annSet);

	}

	@Test
	public void test_66() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/atirikt/", "sacc.go", conf, annSet);

	}

	@Test
	public void test_67() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/atirikt/", "sacc_test.go", conf, annSet);

	}

	@Test
	public void test_68() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/atirikt/", "shim.go", conf, annSet);

	}

	@Test
	public void test_69() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/atirikt/", "shim_test.go", conf, annSet);

	}

	@Test
	public void test_70() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/atirikt/", "sort.go", conf, annSet);

	}

	@Test
	public void test_71() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/atirikt/", "step_definitions_test.go", conf, annSet);

	}

	@Test
	public void test_72() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/atirikt/", "stub_test.go", conf, annSet);

	}

	@Test
	public void test_73() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/atirikt/", "transaction_context.go", conf, annSet);

	}

	@Test
	public void test_74() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/atirikt/", "transaction_context_test.go", conf, annSet);

	}

	@Test
	public void test_75() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/atirikt/", "unicode.go", conf, annSet);

	}

	@Test
	public void test_76() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/braduf/", "mockccstub.go", conf, annSet);

	}

	@Test
	public void test_77() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/braduf/", "mockcid.go", conf, annSet);

	}

	@Test
	public void test_78() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/braduf/", "mocktxcontext.go", conf, annSet);

	}

	@Test
	public void test_79() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/CONUN-Global/", "chaincodeserver.go", conf, annSet);

	}

	@Test
	public void test_80() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/CONUN-Global/", "chaincodestub.go", conf, annSet);

	}

	@Test
	public void test_81() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/CONUN-Global/", "contract_chaincode.go", conf, annSet);

	}

	@Test
	public void test_82() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/CONUN-Global/", "errors.go", conf, annSet);

	}

	@Test
	public void test_83() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/CONUN-Global/", "shim.go", conf, annSet);

	}

	@Test
	public void test_84() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/CONUN-Global/", "smartcontract_test.go", conf, annSet);

	}

	@Test
	public void test_85() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/CONUN-Global/", "token_contract_test.go", conf, annSet);

	}

	@Test
	public void test_86() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/CONUN-Global/", "transaction.go", conf, annSet);

	}

	@Test
	public void test_87() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/CONUN-Global/", "transaction_context.go", conf, annSet);

	}

	@Test
	public void test_88() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/CONUN-Global/", "user_data_test.go", conf, annSet);

	}

	@Test
	public void test_89() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/davidkhala/", "cid.go", conf, annSet);

	}

	@Test
	public void test_90() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/davidkhala/", "commonCC.go", conf, annSet);

	}

	@Test
	public void test_91() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/davidkhala/", "compositeKey.go", conf, annSet);

	}

	@Test
	public void test_92() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/davidkhala/", "fabric.go", conf, annSet);

	}

	@Test
	public void test_93() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/davidkhala/", "fabriclib.go", conf, annSet);

	}

	@Test
	public void test_94() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/davidkhala/", "fabric_test.go", conf, annSet);

	}

	@Test
	public void test_95() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/davidkhala/", "main.go", conf, annSet);

	}

	@Test
	public void test_96() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/davidkhala/", "richQuery.go", conf, annSet);

	}

	@Test
	public void test_97() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/davidkhala/", "sideDB.go", conf, annSet);

	}

	@Test
	public void test_98() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/davidkhala/", "stateIterator.go", conf, annSet);

	}

	@Test
	public void test_99() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/davidkhala/", "stress_test.go", conf, annSet);

	}

	@Test
	public void test_100() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/davidkhala/", "struct.go", conf, annSet);

	}

	@Test
	public void test_101() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/dovetail-lab/", "trigger.go", conf, annSet);

	}

	@Test
	public void test_102() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/dovetail-lab/", "util.go", conf, annSet);

	}

	@Test
	public void test_103() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/elonazoulayB/", "chaincode.go", conf, annSet);

	}

	@Test
	public void test_104() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/elonazoulayB/", "chaincodeserver.go", conf, annSet);

	}

	@Test
	public void test_105() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/elonazoulayB/", "config_test.go", conf, annSet);

	}

	@Test
	public void test_106() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/elonazoulayB/", "handler_test.go", conf, annSet);

	}

	@Test
	public void test_107() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/elonazoulayB/", "mockstub.go", conf, annSet);

	}

	@Test
	public void test_108() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/elonazoulayB/", "mockstub_test.go", conf, annSet);

	}

	@Test
	public void test_109() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/elonazoulayB/", "server_test.go", conf, annSet);

	}

	@Test
	public void test_110() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/elonazoulayB/", "shim.go", conf, annSet);

	}

	@Test
	public void test_111() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/elonazoulayB/", "shim_test.go", conf, annSet);

	}

	@Test
	public void test_112() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/elonazoulayB/", "stub_test.go", conf, annSet);

	}

	@Test
	public void test_113() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/gabrielodi/", "chaincode.go", conf, annSet);

	}

	@Test
	public void test_114() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/gabrielodi/", "chaincodeserver.go", conf, annSet);

	}

	@Test
	public void test_115() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/gabrielodi/", "config_test.go", conf, annSet);

	}

	@Test
	public void test_116() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/gabrielodi/", "handler_test.go", conf, annSet);

	}

	@Test
	public void test_117() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/gabrielodi/", "mockstub.go", conf, annSet);

	}

	@Test
	public void test_118() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/gabrielodi/", "mockstub_test.go", conf, annSet);

	}

	@Test
	public void test_119() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/gabrielodi/", "server_test.go", conf, annSet);

	}

	@Test
	public void test_120() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/gabrielodi/", "shim.go", conf, annSet);

	}

	@Test
	public void test_121() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/gabrielodi/", "shim_test.go", conf, annSet);

	}

	@Test
	public void test_122() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/gabrielodi/", "stub_test.go", conf, annSet);

	}

	@Test
	public void test_123() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/hyperledger/", "chaincode.go", conf, annSet);

	}

	@Test
	public void test_124() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/hyperledger/", "chaincodeserver.go", conf, annSet);

	}

	@Test
	public void test_125() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/hyperledger/", "chaincodestub.go", conf, annSet);

	}

	@Test
	public void test_126() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/hyperledger/", "chaincode_test.go", conf, annSet);

	}

	@Test
	public void test_127() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/hyperledger/", "config_test.go", conf, annSet);

	}

	@Test
	public void test_128() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/hyperledger/", "contract_chaincode.go", conf, annSet);

	}

	@Test
	public void test_129() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/hyperledger/", "contract_chaincode_test.go", conf, annSet);

	}

	@Test
	public void test_130() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/hyperledger/", "ecc.go", conf, annSet);

	}

	@Test
	public void test_131() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/hyperledger/", "enclave.go", conf, annSet);

	}

	@Test
	public void test_132() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/hyperledger/", "handler_test.go", conf, annSet);

	}

	@Test
	public void test_133() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/hyperledger/", "mockstub.go", conf, annSet);

	}

	@Test
	public void test_134() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/hyperledger/", "mockstub_test.go", conf, annSet);

	}

	@Test
	public void test_135() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/hyperledger/", "mock_enclave.go", conf, annSet);

	}

	@Test
	public void test_136() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/hyperledger/", "registry_test.go", conf, annSet);

	}

	@Test
	public void test_137() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/hyperledger/", "server_test.go", conf, annSet);

	}

	@Test
	public void test_138() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/hyperledger/", "shim.go", conf, annSet);

	}

	@Test
	public void test_139() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/hyperledger/", "shim_test.go", conf, annSet);

	}

	@Test
	public void test_140() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/hyperledger/", "step_definitions_test.go", conf, annSet);

	}

	@Test
	public void test_141() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/hyperledger/", "stub.go", conf, annSet);

	}

	@Test
	public void test_142() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/hyperledger/", "stub_test.go", conf, annSet);

	}

	@Test
	public void test_143() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/hyperledger/", "transaction.go", conf, annSet);

	}

	@Test
	public void test_144() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/hyperledger/", "transaction_context.go", conf, annSet);

	}

	@Test
	public void test_145() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/hyperledger/", "transaction_context_test.go", conf, annSet);

	}

	@Test
	public void test_146() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/hyperledger/", "validation.go", conf, annSet);

	}

	@Test
	public void test_147() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/hysn19/", "chaincodeserver.go", conf, annSet);

	}

	@Test
	public void test_148() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/hysn19/", "errors.go", conf, annSet);

	}

	@Test
	public void test_149() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/hysn19/", "mockstub.go", conf, annSet);

	}

	@Test
	public void test_150() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/hysn19/", "sacc.go", conf, annSet);

	}

	@Test
	public void test_151() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/hysn19/", "sacc_test.go", conf, annSet);

	}

	@Test
	public void test_152() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/hysn19/", "shim.go", conf, annSet);

	}

	@Test
	public void test_153() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/imran-iitp/", "abac.go", conf, annSet);

	}

	@Test
	public void test_154() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/imran-iitp/", "abac_test.go", conf, annSet);

	}

	@Test
	public void test_155() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/imran-iitp/", "auctionQueries.go", conf, annSet);

	}

	@Test
	public void test_156() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/imran-iitp/", "chaincode.go", conf, annSet);

	}

	@Test
	public void test_157() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/imran-iitp/", "high-throughput.go", conf, annSet);

	}

	@Test
	public void test_158() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/imran-iitp/", "marbles_chaincode.go", conf, annSet);

	}

	@Test
	public void test_159() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/imran-iitp/", "sacc.go", conf, annSet);

	}

	@Test
	public void test_160() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/imran-iitp/", "sacc_test.go", conf, annSet);

	}

	@Test
	public void test_161() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/imran-iitp/", "utils.go", conf, annSet);

	}

	@Test
	public void test_162() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/KarthikSamaganam550/", "abac.go", conf, annSet);

	}

	@Test
	public void test_163() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/KarthikSamaganam550/", "abac_test.go", conf, annSet);

	}

	@Test
	public void test_164() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/KarthikSamaganam550/", "marbles_chaincode.go", conf, annSet);

	}

	@Test
	public void test_165() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/KarthikSamaganam550/", "sacc.go", conf, annSet);

	}

	@Test
	public void test_166() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/KarthikSamaganam550/", "sacc_test.go", conf, annSet);

	}

	@Test
	public void test_167() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/koakh/", "bytes.go", conf, annSet);

	}

	@Test
	public void test_168() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/koakh/", "chaincodeserver.go", conf, annSet);

	}

	@Test
	public void test_169() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/koakh/", "chaincode_stub.go", conf, annSet);

	}

	@Test
	public void test_170() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/koakh/", "chaincode_support_test.go", conf, annSet);

	}

	@Test
	public void test_171() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/koakh/", "configure.go", conf, annSet);

	}

	@Test
	public void test_172() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/koakh/", "configure_test.go", conf, annSet);

	}

	@Test
	public void test_173() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/koakh/", "context.go", conf, annSet);

	}

	@Test
	public void test_174() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/koakh/", "endorser.go", conf, annSet);

	}

	@Test
	public void test_175() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/koakh/", "errors.go", conf, annSet);

	}

	@Test
	public void test_176() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/koakh/", "flag.go", conf, annSet);

	}

	@Test
	public void test_177() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/koakh/", "fmt.go", conf, annSet);

	}

	@Test
	public void test_178() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/koakh/", "init_private_data.go", conf, annSet);

	}

	@Test
	public void test_179() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/koakh/", "init_public_data.go", conf, annSet);

	}

	@Test
	public void test_180() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/koakh/", "ledger_shim.go", conf, annSet);

	}

	@Test
	public void test_181() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/koakh/", "lscc.go", conf, annSet);

	}

	@Test
	public void test_182() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/koakh/", "lscc_suite_test.go", conf, annSet);

	}

	@Test
	public void test_183() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/koakh/", "lscc_test.go", conf, annSet);

	}

	@Test
	public void test_184() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/koakh/", "marbles_chaincode.go", conf, annSet);

	}

	@Test
	public void test_185() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/koakh/", "mockstub.go", conf, annSet);

	}

	@Test
	public void test_186() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/koakh/", "net.go", conf, annSet);

	}

	@Test
	public void test_187() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/koakh/", "os.go", conf, annSet);

	}

	@Test
	public void test_188() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/koakh/", "regexp.go", conf, annSet);

	}

	@Test
	public void test_189() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/koakh/", "scc.go", conf, annSet);

	}

	@Test
	public void test_190() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/koakh/", "selfdescribingsyscc.go", conf, annSet);

	}

	@Test
	public void test_191() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/koakh/", "shim.go", conf, annSet);

	}

	@Test
	public void test_192() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/koakh/", "sync.go", conf, annSet);

	}

	@Test
	public void test_193() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/koakh/", "time.go", conf, annSet);

	}

	@Test
	public void test_194() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/koakh/", "unicode.go", conf, annSet);

	}

	@Test
	public void test_195() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/KompiTech/", "context_interface.go", conf, annSet);

	}

	@Test
	public void test_196() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/KompiTech/", "entry.go", conf, annSet);

	}

	@Test
	public void test_197() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/KompiTech/", "from_bytes.go", conf, annSet);

	}

	@Test
	public void test_198() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/KompiTech/", "identity.go", conf, annSet);

	}

	@Test
	public void test_199() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/KompiTech/", "iterator.go", conf, annSet);

	}

	@Test
	public void test_200() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/KompiTech/", "matcher.go", conf, annSet);

	}

	@Test
	public void test_201() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/KompiTech/", "mockstub.go", conf, annSet);

	}

	@Test
	public void test_202() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/KompiTech/", "registry.go", conf, annSet);

	}

	@Test
	public void test_203() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/KompiTech/", "response.go", conf, annSet);

	}

	@Test
	public void test_204() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/liuhuanyuxcx/", "e2e.go", conf, annSet);

	}

	@Test
	public void test_205() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/liuhuanyuxcx/", "fabcar.go", conf, annSet);

	}

	@Test
	public void test_206() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/liuhuanyuxcx/", "marbles02.go", conf, annSet);

	}

	@Test
	public void test_207() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/MeganDoman/", "TweetChaincode.go", conf, annSet);

	}

	@Test
	public void test_208() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/miguomi/", "mycc.go", conf, annSet);

	}

	@Test
	public void test_209() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/miguomi/", "mycc_test.go", conf, annSet);

	}

	@Test
	public void test_210() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/nmix/", "blank.go", conf, annSet);

	}

	@Test
	public void test_211() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/nmix/", "calc.go", conf, annSet);

	}

	@Test
	public void test_212() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/nmix/", "caller.go", conf, annSet);

	}

	@Test
	public void test_213() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/nmix/", "cid.go", conf, annSet);

	}

	@Test
	public void test_214() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/nmix/", "range.go", conf, annSet);

	}

	@Test
	public void test_215() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/nmix/", "token.go", conf, annSet);

	}

	@Test
	public void test_216() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/nmix/", "tokenv5.go", conf, annSet);

	}

	@Test
	public void test_217() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/open-dovetail/", "activity_test.go", conf, annSet);

	}

	@Test
	public void test_218() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/open-dovetail/", "shim.go", conf, annSet);

	}

	@Test
	public void test_219() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/open-dovetail/", "trigger.go", conf, annSet);

	}

	@Test
	public void test_220() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/open-dovetail/", "trigger_test.go", conf, annSet);

	}

	@Test
	public void test_221() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/open-dovetail/", "util.go", conf, annSet);

	}

	@Test
	public void test_222() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/open-dovetail/", "util_test.go", conf, annSet);

	}

	@Test
	public void test_223() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/PaulSchevelenbos/", "chaincode.go", conf, annSet);

	}

	@Test
	public void test_224() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/peikiuar/", "couchdb.go", conf, annSet);

	}

	@Test
	public void test_225() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/peikiuar/", "mockccstub.go", conf, annSet);

	}

	@Test
	public void test_226() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/peikiuar/", "mockcid.go", conf, annSet);

	}

	@Test
	public void test_227() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/peikiuar/", "mocktxcontext.go", conf, annSet);

	}

	@Test
	public void test_228() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/richardfelkl/", "main.go", conf, annSet);

	}

	@Test
	public void test_229() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/s7techlab/", "cars_proxy.go", conf, annSet);

	}

	@Test
	public void test_230() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/s7techlab/", "cert_identity.go", conf, annSet);

	}

	@Test
	public void test_231() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/s7techlab/", "chaincode.go", conf, annSet);

	}

	@Test
	public void test_232() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/s7techlab/", "client.go", conf, annSet);

	}

	@Test
	public void test_233() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/s7techlab/", "context.go", conf, annSet);

	}

	@Test
	public void test_234() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/s7techlab/", "data.go", conf, annSet);

	}

	@Test
	public void test_235() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/s7techlab/", "entry.go", conf, annSet);

	}

	@Test
	public void test_236() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/s7techlab/", "from_bytes.go", conf, annSet);

	}

	@Test
	public void test_237() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/s7techlab/", "invoke.go", conf, annSet);

	}

	@Test
	public void test_238() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/s7techlab/", "invoker.go", conf, annSet);

	}

	@Test
	public void test_239() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/s7techlab/", "invoke_insurance.go", conf, annSet);

	}

	@Test
	public void test_240() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/s7techlab/", "invoke_police.go", conf, annSet);

	}

	@Test
	public void test_241() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/s7techlab/", "invoke_repairshop.go", conf, annSet);

	}

	@Test
	public void test_242() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/s7techlab/", "invoke_shop.go", conf, annSet);

	}

	@Test
	public void test_243() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/s7techlab/", "key.go", conf, annSet);

	}

	@Test
	public void test_244() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/s7techlab/", "mocked_peer.go", conf, annSet);

	}

	@Test
	public void test_245() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/s7techlab/", "mockstub.go", conf, annSet);

	}

	@Test
	public void test_246() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/s7techlab/", "mockstub_test.go", conf, annSet);

	}

	@Test
	public void test_247() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/s7techlab/", "pagination.go", conf, annSet);

	}

	@Test
	public void test_248() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/s7techlab/", "pagination_test.go", conf, annSet);

	}

	@Test
	public void test_249() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/s7techlab/", "response.go", conf, annSet);

	}

	@Test
	public void test_250() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/s7techlab/", "router.go", conf, annSet);

	}

	@Test
	public void test_251() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/s7techlab/", "router_test.go", conf, annSet);

	}

	@Test
	public void test_252() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/s7techlab/", "state_cached.go", conf, annSet);

	}

	@Test
	public void test_253() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/s7techlab/", "state_list.go", conf, annSet);

	}

	@Test
	public void test_254() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/s7techlab/", "testing.go", conf, annSet);

	}

	@Test
	public void test_255() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/s7techlab/", "util.go", conf, annSet);

	}

	@Test
	public void test_256() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/sachin-ngpws/", "cpu-use.go", conf, annSet);

	}

	@Test
	public void test_257() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/sachin-ngpws/", "cpu-use_test.go", conf, annSet);

	}

	@Test
	public void test_258() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/sarkershantonuZ/", "contract_chaincode.go", conf, annSet);

	}

	@Test
	public void test_259() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/sarkershantonuZ/", "contract_chaincode_test.go", conf, annSet);

	}

	@Test
	public void test_260() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/sarkershantonuZ/", "step_definitions_test.go", conf, annSet);

	}

	@Test
	public void test_261() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/sarkershantonuZ/", "transaction_context.go", conf, annSet);

	}

	@Test
	public void test_262() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/sarkershantonuZ/", "transaction_context_test.go", conf, annSet);

	}

	@Test
	public void test_263() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/Substra/", "ledger_db.go", conf, annSet);

	}

	@Test
	public void test_264() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/Substra/", "main.go", conf, annSet);

	}

	@Test
	public void test_265() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/Substra/", "mockstub_test.go", conf, annSet);

	}

	@Test
	public void test_266() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/Substra/", "utils.go", conf, annSet);

	}

	@Test
	public void test_267() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/Sudhanmanoharan/", "chaincodestub.go", conf, annSet);

	}

	@Test
	public void test_268() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/Sudhanmanoharan/", "movieTicket_test.go", conf, annSet);

	}

	@Test
	public void test_269() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/Sudhanmanoharan/", "transaction.go", conf, annSet);

	}

	@Test
	public void test_270() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/tinywell/", "handler.go", conf, annSet);

	}

	@Test
	public void test_271() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/TommyStarK/", "chaincode.go", conf, annSet);

	}

	@Test
	public void test_272() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/TommyStarK/", "chaincodeserver.go", conf, annSet);

	}

	@Test
	public void test_273() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/TommyStarK/", "chaincode_test.go", conf, annSet);

	}

	@Test
	public void test_274() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/TommyStarK/", "contract_chaincode.go", conf, annSet);

	}

	@Test
	public void test_275() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/TommyStarK/", "errors.go", conf, annSet);

	}

	@Test
	public void test_276() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/TommyStarK/", "ledger.go", conf, annSet);

	}

	@Test
	public void test_277() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/TommyStarK/", "ledger_test.go", conf, annSet);

	}

	@Test
	public void test_278() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/TommyStarK/", "log.go", conf, annSet);

	}

	@Test
	public void test_279() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/TommyStarK/", "main.go", conf, annSet);

	}

	@Test
	public void test_280() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/TommyStarK/", "mockstub.go", conf, annSet);

	}

	@Test
	public void test_281() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/TommyStarK/", "shim.go", conf, annSet);

	}

	@Test
	public void test_282() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/TommyStarK/", "smart_contract_test.go", conf, annSet);

	}

	@Test
	public void test_283() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/TommyStarK/", "transaction_context.go", conf, annSet);

	}

	@Test
	public void test_284() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/vanitas92/", "fabcar.go", conf, annSet);

	}

	@Test
	public void test_285() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/vanitas92/", "marbles02.go", conf, annSet);

	}

	@Test
	public void test_286() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/vitorduarte/", "basicChaincode.go", conf, annSet);

	}

	@Test
	public void test_287() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/VoneChain-CS/", "chaincodeserver.go", conf, annSet);

	}

	@Test
	public void test_288() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/VoneChain-CS/", "contract_chaincode.go", conf, annSet);

	}

	@Test
	public void test_289() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/VoneChain-CS/", "errors.go", conf, annSet);

	}

	@Test
	public void test_290() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/VoneChain-CS/", "mockstub.go", conf, annSet);

	}

	@Test
	public void test_291() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/VoneChain-CS/", "shim.go", conf, annSet);

	}

	@Test
	public void test_292() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/VoneChain-CS/", "strings.go", conf, annSet);

	}

	@Test
	public void test_293() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/VoneChain-CS/", "transaction_context.go", conf, annSet);

	}

	@Test
	public void test_294() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/yunmosehc/", "chaincodeserver.go", conf, annSet);

	}

	@Test
	public void test_295() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/yunmosehc/", "contract_chaincode.go", conf, annSet);

	}

	@Test
	public void test_296() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/yunmosehc/", "errors.go", conf, annSet);

	}

	@Test
	public void test_297() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/yunmosehc/", "shim.go", conf, annSet);

	}

	@Test
	public void test_298() throws AnalysisException, IOException {

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		conf.callGraph = new RTACallGraph();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new InferenceSystem<>(new IntegrityNIDomain()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		conf.semanticChecks.add(new IntegrityNIChecker());
		perform("cc/benchmark/yunmosehc/", "transaction_context.go", conf, annSet);

	}

}
