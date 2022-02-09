
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.LiSAFactory;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.heap.pointbased.FieldSensitivePointBasedHeap;
import it.unive.lisa.analysis.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.numeric.Interval;
import it.unive.lisa.analysis.types.InferredTypes;

import org.junit.Test;

public class HeapTest extends GoAnalysisTestExecutor {

	@Test
	public void fieldInsensitivePointBasedTest() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpAnalysis(true)
				.setAbstractState(LiSAFactory.getDefaultFor(AbstractState.class, new PointBasedHeap(), new Interval(), new InferredTypes()));
		perform("heap/field-insensitive", "go-structs.go", conf);
	}

	@Test
	public void fieldSensitivepointBasedTest() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpAnalysis(true)
				.setAbstractState(LiSAFactory.getDefaultFor(AbstractState.class, new FieldSensitivePointBasedHeap(),
						new Interval(), new InferredTypes()));
		perform("heap/field-sensitive", "go-structs.go", conf);
	}
}
