import static it.unive.lisa.LiSAFactory.getDefaultFor;

import org.junit.Test;

import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.heap.pointbased.FieldSensitivePointBasedHeap;
import it.unive.lisa.analysis.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.impl.numeric.Interval;

public class HeapTest extends AnalysisTest {
	
	@Test
	public void fieldInsensitivePointBasedTest() throws AnalysisSetupException {
		perform("heap/field-insensitive", "go-structs.go", false, false, false, getDefaultFor(AbstractState.class, new PointBasedHeap(), new Interval()));
	}
	
	@Test
	public void fieldSensitivepointBasedTest() throws AnalysisSetupException {
		perform("heap/field-sensitive", "go-structs.go", false, false, false, getDefaultFor(AbstractState.class, new FieldSensitivePointBasedHeap(), new Interval()));
	}
}
