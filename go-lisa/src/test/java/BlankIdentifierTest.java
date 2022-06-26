import static it.unive.lisa.LiSAFactory.getDefaultFor;

import org.junit.Test;

import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.numeric.Interval;
import it.unive.lisa.analysis.types.InferredTypes;

public class BlankIdentifierTest extends GoAnalysisTestExecutor {

	@Test
	public void blankIdentifierTest() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpAnalysis(true)
				.setAbstractState(getDefaultFor(AbstractState.class, getDefaultFor(HeapDomain.class), new Interval(),
						new InferredTypes()))
				.setDumpAnalysis(true);
		perform("blankidentifier", "blankidentifier.go", conf);
	}
}