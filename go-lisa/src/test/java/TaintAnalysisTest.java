
import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.checker.TaintChecker;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.LiSAFactory;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.heap.MonolithicHeap;
import it.unive.lisa.interprocedural.ContextBasedAnalysis;
import it.unive.lisa.interprocedural.ReturnTopPolicy;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;
import org.junit.Test;

public class TaintAnalysisTest extends GoAnalysisTestExecutor {

	@Test
	public void taintTest001() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(
						LiSAFactory.getDefaultFor(AbstractState.class, new MonolithicHeap(), new TaintDomain()))
				.addSemanticCheck(new TaintChecker())
				.setInferTypes(true)
				.setJsonOutput(true)
				.setDumpAnalysis(true)
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("taint/t1", "t1.go", conf);
	}

	@Test
	public void taintTest002() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(
						LiSAFactory.getDefaultFor(AbstractState.class, new MonolithicHeap(), new TaintDomain()))
				.addSemanticCheck(new TaintChecker())
				.setInferTypes(true)
				.setJsonOutput(true)
				.setDumpAnalysis(true)
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("taint/t2", "t2.go", conf);
	}

	@Test
	public void taintTest003() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(
						LiSAFactory.getDefaultFor(AbstractState.class, new MonolithicHeap(), new TaintDomain()))
				.addSemanticCheck(new TaintChecker())
				.setInferTypes(true)
				.setJsonOutput(true)
				.setDumpAnalysis(true)
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("taint/t3", "t3.go", conf);
	}

	@Test
	public void taintTest004() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(
						LiSAFactory.getDefaultFor(AbstractState.class, new MonolithicHeap(), new TaintDomain()))
				.addSemanticCheck(new TaintChecker())
				.setInferTypes(true)
				.setJsonOutput(true)
				.setDumpAnalysis(true)
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("taint/t4", "t4.go", conf);
	}

	@Test
	public void taintTest005() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(
						LiSAFactory.getDefaultFor(AbstractState.class, new MonolithicHeap(), new TaintDomain()))
				.addSemanticCheck(new TaintChecker())
				.setInferTypes(true)
				.setJsonOutput(true)
				.setDumpAnalysis(true)
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("taint/t5", "t5.go", conf);
	}

	@Test
	public void taintTest006() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(
						LiSAFactory.getDefaultFor(AbstractState.class, new MonolithicHeap(), new TaintDomain()))
				.addSemanticCheck(new TaintChecker())
				.setInferTypes(true)
				.setJsonOutput(true)
				.setDumpAnalysis(true)
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("taint/t6", "t6.go", conf);
	}

	@Test
	public void taintTest007() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(
						LiSAFactory.getDefaultFor(AbstractState.class, new MonolithicHeap(), new TaintDomain()))
				.addSemanticCheck(new TaintChecker())
				.setInferTypes(true)
				.setJsonOutput(true)
				.setDumpAnalysis(true)
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("taint/t7", "t7.go", conf);
	}

	@Test
	public void taintTest008() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(
						LiSAFactory.getDefaultFor(AbstractState.class, new MonolithicHeap(), new TaintDomain()))
				.addSemanticCheck(new TaintChecker())
				.setInferTypes(true)
				.setJsonOutput(true)
				.setDumpAnalysis(true)
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("taint/t8", "t8.go", conf);
	}

	@Test
	public void taintTest009() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(
						LiSAFactory.getDefaultFor(AbstractState.class, new MonolithicHeap(), new TaintDomain()))
				.addSemanticCheck(new TaintChecker())
				.setInferTypes(true)
				.setJsonOutput(true)
				.setDumpAnalysis(true)
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("taint/t9", "t9.go", conf);
	}
}
