
import org.junit.Test;

import it.unive.golisa.analysis.heap.GoAbstractState;
import it.unive.golisa.analysis.heap.GoFieldSensitivePointBasedHeap;
import it.unive.golisa.analysis.heap.GoPointBasedHeap;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.LiSAFactory;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.numeric.Interval;
import it.unive.lisa.analysis.value.TypeDomain;

public class HeapTest extends GoAnalysisTestExecutor {

	@Test
	public void fieldInsensitivePointBasedTest() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.serializeResults = true;
		conf.abstractState = new GoAbstractState<>(new GoPointBasedHeap(), new ValueEnvironment<>(new Interval()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		perform("heap/field-insensitive", "go-structs.go", conf);
	}

	@Test
	public void fieldSensitivepointBasedTest() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.serializeResults = true;
		conf.abstractState = new GoAbstractState<>(new GoFieldSensitivePointBasedHeap(),
				new ValueEnvironment<>(new Interval()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		perform("heap/field-sensitive", "go-structs.go", conf);
	}
}
