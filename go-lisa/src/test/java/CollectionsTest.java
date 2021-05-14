
import org.junit.Test;

import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.LiSAFactory;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.impl.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.impl.numeric.Interval;

public class CollectionsTest extends GoAnalysisTestExecutor {
	
	@Test
	public void structTest() throws  AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpAnalysis(true).setInferTypes(true)
				.setAbstractState(LiSAFactory.getDefaultFor(AbstractState.class, new PointBasedHeap(), new Interval()));
		perform("collections/struct", "struct.go", conf);
	}
	
	
	@Test
	public void arrayTest() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpAnalysis(true).setInferTypes(true)
				.setAbstractState(LiSAFactory.getDefaultFor(AbstractState.class, new PointBasedHeap(), new Interval()));
		perform("collections/array", "array.go", conf);
	}
}
