import static it.unive.lisa.LiSAFactory.getDefaultFor;

import org.junit.Test;

import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.impl.numeric.Interval;

public class HeapTest extends AnalysisTest {
	
	@Test
	public void pointBasedTest() throws AnalysisSetupException {
		perform("heap", "go-structs.go", false, false, false, getDefaultFor(AbstractState.class, new PointBasedHeap(), new Interval()));
	}
}
