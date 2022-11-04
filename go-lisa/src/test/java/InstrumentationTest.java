
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.LiSAFactory;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.numeric.Interval;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.interprocedural.ReturnTopPolicy;
import org.junit.Test;

public class InstrumentationTest extends GoAnalysisTestExecutor {

	@Test
	public void returnStatementInstrumentationTest() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setSerializeResults(true)
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setAbstractState(LiSAFactory.getDefaultFor(AbstractState.class, new PointBasedHeap(), new Interval(),
						new InferredTypes()));
		perform("instrumentation/return-statement", "instrumented-returns.go", conf);
	}
}
