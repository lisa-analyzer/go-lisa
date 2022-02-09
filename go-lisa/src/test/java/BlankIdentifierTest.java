import static it.unive.lisa.LiSAFactory.getDefaultFor;

import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.LiSAFactory;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.numeric.Interval;
import it.unive.lisa.analysis.types.InferredTypes;
import org.junit.Ignore;
import org.junit.Test;

public class BlankIdentifierTest extends GoAnalysisTestExecutor {

	@Test
	public void blankIdentifierTest() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpAnalysis(true)
				.setAbstractState(getDefaultFor(AbstractState.class, getDefaultFor(HeapDomain.class), new Interval(),
						new InferredTypes()))
				.setDumpAnalysis(true);
		perform("blankidentifier", "blankidentifier.go", conf);
	}

	@Ignore
	@Test
	public void otherTest() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpAnalysis(true)
				.setAbstractState(LiSAFactory.getDefaultFor(AbstractState.class, getDefaultFor(HeapDomain.class),
						new Interval(), new InferredTypes()));
		perform("blankidentifier/others", "others.go", conf);
	}

}