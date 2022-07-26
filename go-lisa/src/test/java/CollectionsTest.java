
import it.unive.golisa.analysis.heap.GoAbstractState;
import it.unive.golisa.analysis.heap.GoFieldSensitivePointBasedHeap;
import it.unive.golisa.analysis.heap.GoPointBasedHeap;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.LiSAConfiguration.GraphType;
import it.unive.lisa.LiSAFactory;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.heap.pointbased.FieldSensitivePointBasedHeap;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.numeric.Interval;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.interprocedural.ContextBasedAnalysis;
import it.unive.lisa.interprocedural.RecursionFreeToken;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;
import org.junit.Ignore;
import org.junit.Test;

public class CollectionsTest extends GoAnalysisTestExecutor {

	@Test
	public void structTest() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setSerializeResults(true)
				.setAbstractState(
						new GoAbstractState<>(new GoPointBasedHeap(),
								new ValueEnvironment<>(new Interval()),
								LiSAFactory.getDefaultFor(TypeDomain.class)));
		perform("collections/struct", "struct.go", conf);
	}

	/**
	 * Array tests
	 */
	@Test
	public void fieldInsensitivedPointBasedArrayTest() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setSerializeResults(true)
				.setAbstractState(
						new GoAbstractState<>(new GoPointBasedHeap(),
								new ValueEnvironment<>(new Interval()),
								LiSAFactory.getDefaultFor(TypeDomain.class)));
		perform("collections/array/field-insensitive", "array.go", conf);
	}

	@Test
	public void fieldSensitivePointBasedArrayTest() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setSerializeResults(true)
				.setAbstractState(
						new GoAbstractState<>(new GoFieldSensitivePointBasedHeap(),
								new ValueEnvironment<>(new Interval()),
								LiSAFactory.getDefaultFor(TypeDomain.class)));
		perform("collections/array/field-sensitive", "array.go", conf);
	}

	/**
	 * Map tests
	 */
	@Test
	public void fieldInsensitivedPointBasedMapTest() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpAnalysis(GraphType.HTML)
				.setAbstractState(
						new GoAbstractState<>(new GoPointBasedHeap(),
								new ValueEnvironment<>(new Interval()),
								LiSAFactory.getDefaultFor(TypeDomain.class)));
		perform("collections/map/field-insensitive", "map.go", conf);
	}

	@Test
	public void fieldSensitivedPointBasedMapTest() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpAnalysis(GraphType.HTML)
				.setAbstractState(
						new GoAbstractState<>(new GoFieldSensitivePointBasedHeap(),
								new ValueEnvironment<>(new Interval()),
								LiSAFactory.getDefaultFor(TypeDomain.class)));
		perform("collections/map/field-sensitive", "map.go", conf);
	}

	/**
	 * Slice tests
	 */
	@Test
	public void fieldInsensitivedPointBasedSliceTest() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setSerializeResults(true)
				.setAbstractState(
						new GoAbstractState<>(new GoPointBasedHeap(),
								new ValueEnvironment<>(new Interval()),
								LiSAFactory.getDefaultFor(TypeDomain.class)));
		perform("collections/slice/field-insensitive", "slice.go", conf);
	}

	@Ignore // TODO currently not supported
	public void fieldSensitivePointBasedSliceTest() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setSerializeResults(true)
				.setAbstractState(LiSAFactory.getDefaultFor(AbstractState.class, new FieldSensitivePointBasedHeap(),
						new Interval(), new InferredTypes()));
		perform("collections/slice/field-sensitive", "slice.go", conf);
	}

	@Test
	public void interfaceTest1() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(
						new GoAbstractState<>(new GoFieldSensitivePointBasedHeap(),
								new ValueEnvironment<>(new Interval()),
								LiSAFactory.getDefaultFor(TypeDomain.class)))
				.setSerializeResults(true)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton()));
		perform("collections/interface/1", "interface.go", conf);
	}

	@Test
	public void interfaceTest2() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(
						new GoAbstractState<>(new GoFieldSensitivePointBasedHeap(),
								new ValueEnvironment<>(new Interval()),
								LiSAFactory.getDefaultFor(TypeDomain.class)))
				.setSerializeResults(true)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton()));
		perform("collections/interface/2", "interface.go", conf);
	}

	@Test
	public void interfaceTest3() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(
						new GoAbstractState<>(new GoFieldSensitivePointBasedHeap(),
								new ValueEnvironment<>(new Interval()),
								LiSAFactory.getDefaultFor(TypeDomain.class)))
				.setSerializeResults(true)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton()));
		perform("collections/interface/3", "interface.go", conf);
	}

	@Test
	public void interfaceTest4() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(
						new GoAbstractState<>(new GoFieldSensitivePointBasedHeap(),
								new ValueEnvironment<>(new Interval()),
								LiSAFactory.getDefaultFor(TypeDomain.class)))
				.setSerializeResults(true)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton()));
		perform("collections/interface/4", "interface.go", conf);
	}
}
