import static it.unive.lisa.LiSAFactory.getDefaultFor;

import org.junit.Test;

import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.impl.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.impl.numeric.Interval;

public class CollectionsTest extends AnalysisTest {
	
	@Test
	public void structTest() throws AnalysisSetupException {
		perform("collections/struct", "struct.go", true, false, false, getDefaultFor(AbstractState.class, new PointBasedHeap(), new Interval()));
	}
	
	
	@Test
	public void arrayTest() throws AnalysisSetupException {
		perform("collections/array", "array.go", true, false, false, getDefaultFor(AbstractState.class, new PointBasedHeap(), new Interval()));
	}
}
