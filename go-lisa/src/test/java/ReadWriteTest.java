
import it.unive.golisa.checker.hf.readwrite.ReadWritePairChecker;
import it.unive.golisa.checker.hf.readwrite.ReadWritePathChecker;
import it.unive.golisa.interprocedural.RelaxedOpenCallPolicy;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.nonrelational.value.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.string.tarsis.Tarsis;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;
import it.unive.lisa.interprocedural.context.ContextBasedAnalysis;
import it.unive.lisa.program.cfg.statement.call.OpenCall;

import org.junit.Test;

public class ReadWriteTest extends GoChaincodeTestExecutor {

	protected void run(String testDir, String programFile) {
		CronConfiguration conf1 = new CronConfiguration();
		conf1.openCallPolicy = new RelaxedOpenCallPolicy() {
			
			@Override
			public boolean isSourceForTaint(OpenCall call) {
				return false;
			}
		};
		conf1.abstractState = new SimpleAbstractState<>(new PointBasedHeap(), new ValueEnvironment<>(new Tarsis()),
				new TypeEnvironment<>(new InferredTypes()));
		conf1.semanticChecks.add(new ReadWritePairChecker());
		conf1.jsonOutput = true;
		conf1.callGraph = new RTACallGraph();
		conf1.interproceduralAnalysis = new ContextBasedAnalysis<>();
		conf1.compareWithOptimization = false;
		conf1.testDir = testDir;
		conf1.programFile = programFile;

		CronConfiguration conf2 = new CronConfiguration();
		conf2.openCallPolicy = new RelaxedOpenCallPolicy() {
			
			@Override
			public boolean isSourceForTaint(OpenCall call) {
				return false;
			}
		};
		conf2.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new ValueEnvironment<>(new Tarsis()),
				new TypeEnvironment<>(new InferredTypes()));
		conf2.jsonOutput = true;
		conf2.callGraph = new RTACallGraph();
		conf2.interproceduralAnalysis = new ContextBasedAnalysis<>();
		conf2.compareWithOptimization = false;
		conf2.testDir = testDir;
		conf2.programFile = programFile;

		performAnalysisIn2Phases(conf1, conf2);
	}

	@Override
	protected void prePhase2(CronConfiguration confPhase1, CronConfiguration confPhase2) {
		ReadWritePairChecker readwritePhase1 = (ReadWritePairChecker) confPhase1.semanticChecks.iterator().next();
		confPhase2.semanticChecks.add(new ReadWritePathChecker(readwritePhase1.getReadAfterWriteCandidates(),
				readwritePhase1.getOverWriteCandidates(), false));
		super.prePhase2(confPhase1, confPhase2);
	}

	@Test
	public void testSingle1() throws AnalysisSetupException {
		run("hf/read-write/single/single1", "single1.go");
	}

	@Test
	public void testSingle2() throws AnalysisSetupException {
		run("hf/read-write/single/single2", "single2.go");
	}

	@Test
	public void testSingle3() throws AnalysisSetupException {
		run("hf/read-write/single/single3", "single3.go");
	}

	@Test
	public void testSingle4() throws AnalysisSetupException {
		run("hf/read-write/single/single4", "single4.go");
	}

	@Test
	public void testSingle5() throws AnalysisSetupException {
		run("hf/read-write/single/single5", "single5.go");
	}

	@Test
	public void testSingle6() throws AnalysisSetupException {
		run("hf/read-write/single/single6", "single6.go");
	}

	@Test
	public void testSingle7() throws AnalysisSetupException {
		run("hf/read-write/single/single7", "single7.go");
	}

	@Test
	public void testRange1() throws AnalysisSetupException {
		run("hf/read-write/range/range1", "range1.go");
	}

	@Test
	public void testRange2() throws AnalysisSetupException {
		run("hf/read-write/range/range2", "range2.go");
	}

	@Test
	public void testRange3() throws AnalysisSetupException {
		run("hf/read-write/range/range3", "range3.go");
	}

	@Test
	public void testRange4() throws AnalysisSetupException {
		run("hf/read-write/range/range4", "range4.go");
	}

	@Test
	public void testInterproc1() throws AnalysisSetupException {
		run("hf/read-write/interproc/inter1", "inter1.go");
	}

	@Test
	public void testInterproc2() throws AnalysisSetupException {
		run("hf/read-write/interproc/inter2", "inter2.go");
	}

	@Test
	public void testInterproc3() throws AnalysisSetupException {
		run("hf/read-write/interproc/inter3", "inter3.go");
	}

	@Test
	public void testDeferInterproc1() throws AnalysisSetupException {
		run("hf/read-write/interproc/defer-inter1", "defer-inter1.go");
	}

	@Test
	public void testDeferInterproc2() throws AnalysisSetupException {
		run("hf/read-write/interproc/defer-inter2", "defer-inter2.go");
	}

	@Test
	public void testDeferInterproc3() throws AnalysisSetupException {
		run("hf/read-write/interproc/defer-inter3", "defer-inter3.go");
	}

	@Test
	public void testDeferInterproc4() throws AnalysisSetupException {
		run("hf/read-write/interproc/defer-inter4", "defer-inter4.go");
	}

	@Test
	public void testDeferInterproc5() throws AnalysisSetupException {
		run("hf/read-write/interproc/defer-inter5", "defer-inter5.go");
	}

	@Test
	public void testDeferInterproc6() throws AnalysisSetupException {
		run("hf/read-write/interproc/defer-inter6", "defer-inter6.go");
	}

	@Test
	public void testDeferInterproc7() throws AnalysisSetupException {
		run("hf/read-write/interproc/defer-inter7", "defer-inter7.go");
	}

	@Test
	public void testDeferInterproc8() throws AnalysisSetupException {
		run("hf/read-write/interproc/defer-inter8", "defer-inter8.go");
	}

	@Test
	public void testDeferInterproc9() throws AnalysisSetupException {
		run("hf/read-write/interproc/defer-inter9", "defer-inter9.go");
	}

	@Test
	public void testDeferInterproc10() throws AnalysisSetupException {
		run("hf/read-write/interproc/defer-inter10", "defer-inter10.go");
	}

}
