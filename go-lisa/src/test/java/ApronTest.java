import org.junit.Test;

import it.unive.golisa.analysis.apron.Apron;
import it.unive.golisa.analysis.apron.Apron.ApronDomain;
import it.unive.golisa.checker.OverflowChecker;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.LiSAFactory;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.impl.heap.MonolithicHeap;
import it.unive.lisa.interprocedural.callgraph.impl.RTACallGraph;
import it.unive.lisa.interprocedural.impl.ContextBasedAnalysis;

public class ApronTest extends GoAnalysisTestExecutor {
	
	
	@Test
	public void testSm2Interval() throws AnalysisSetupException {
		Apron.setManager(ApronDomain.Box);
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(LiSAFactory.getDefaultFor(AbstractState.class, new MonolithicHeap(), new Apron()))
				.addSemanticCheck(new OverflowChecker())
				.setInferTypes(true)
				.setJsonOutput(true)
				.setDumpAnalysis(true)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("apron/sm2/non-protected/box", "sm.go", conf);

	}

	@Test
	public void testSm2Oct() throws AnalysisSetupException {
		Apron.setManager(ApronDomain.Octagon);
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(LiSAFactory.getDefaultFor(AbstractState.class, new MonolithicHeap(), new Apron()))
				.addSemanticCheck(new OverflowChecker())
				.setInferTypes(true)
				.setJsonOutput(true)
				.setDumpAnalysis(true)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("apron/sm2/non-protected/oct", "sm.go", conf);

	}
	
	@Test
	public void testSm2Ppl() throws AnalysisSetupException {
		Apron.setManager(ApronDomain.PplPoly);
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(LiSAFactory.getDefaultFor(AbstractState.class, new MonolithicHeap(), new Apron()))
				.addSemanticCheck(new OverflowChecker())
				.setInferTypes(true)
				.setJsonOutput(true)
				.setDumpAnalysis(true)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("apron/sm2/non-protected/ppl", "sm.go", conf);

	}
	
	@Test
	public void testSm2IntervalProtected() throws AnalysisSetupException {
		Apron.setManager(ApronDomain.Box);
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(LiSAFactory.getDefaultFor(AbstractState.class, new MonolithicHeap(), new Apron()))
				.addSemanticCheck(new OverflowChecker())
				.setInferTypes(true)
				.setJsonOutput(true)
				.setDumpAnalysis(true)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("apron/sm2/protected/box", "sm-protected.go", conf);
	}
	
	@Test
	public void testSm2OctProtected() throws AnalysisSetupException {
		Apron.setManager(ApronDomain.Octagon);
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(LiSAFactory.getDefaultFor(AbstractState.class, new MonolithicHeap(), new Apron()))
				.addSemanticCheck(new OverflowChecker())
				.setInferTypes(true)
				.setJsonOutput(true)
				.setDumpAnalysis(true)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("apron/sm2/protected/oct", "sm-protected.go", conf);
	}

	@Test
	public void testSm2PplProtected() throws AnalysisSetupException {
		Apron.setManager(ApronDomain.PplPoly);
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(LiSAFactory.getDefaultFor(AbstractState.class, new MonolithicHeap(), new Apron()))
				.addSemanticCheck(new OverflowChecker())
				.setInferTypes(true)
				.setJsonOutput(true)
				.setDumpAnalysis(true)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("apron/sm2/protected/ppl", "sm-protected.go", conf);
	}
}
