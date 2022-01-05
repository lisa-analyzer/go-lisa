
import it.unive.golisa.analysis.apron.Apron;
import it.unive.golisa.analysis.apron.Apron.ApronDomain;
import it.unive.golisa.checker.DivisionByZeroChecker;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.LiSAFactory;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.heap.MonolithicHeap;
import org.junit.Test;

public class DivByZeroTest extends GoAnalysisTestExecutor {

	@Test
	public void testDivByZeroBox() throws AnalysisSetupException {
		Apron.setManager(ApronDomain.Box);
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(LiSAFactory.getDefaultFor(AbstractState.class, new MonolithicHeap(), new Apron()))
				.addSemanticCheck(new DivisionByZeroChecker())
				.setInferTypes(true)
				.setJsonOutput(true)
				.setDumpAnalysis(true);
		perform("div-by-zero/box", "example.go", conf);

	}

	@Test
	public void testDivByZeroOct() throws AnalysisSetupException {
		Apron.setManager(ApronDomain.Octagon);
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(LiSAFactory.getDefaultFor(AbstractState.class, new MonolithicHeap(), new Apron()))
				.addSemanticCheck(new DivisionByZeroChecker())
				.setInferTypes(true)
				.setJsonOutput(true)
				.setDumpAnalysis(true);
		perform("div-by-zero/oct", "example.go", conf);

	}

	@Test
	public void testDivByZeroPpl() throws AnalysisSetupException {
		Apron.setManager(ApronDomain.PplPoly);
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(LiSAFactory.getDefaultFor(AbstractState.class, new MonolithicHeap(), new Apron()))
				.addSemanticCheck(new DivisionByZeroChecker())
				.setInferTypes(true)
				.setJsonOutput(true)
				.setDumpAnalysis(true);
		perform("div-by-zero/ppl", "example.go", conf);
	}
}
