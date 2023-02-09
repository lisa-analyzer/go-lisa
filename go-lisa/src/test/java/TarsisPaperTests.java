import static it.unive.lisa.LiSAFactory.getDefaultFor;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import it.unive.golisa.analysis.scam.SmashedSum;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.nonrelational.value.BaseNonRelationalValueDomain;
import it.unive.lisa.analysis.numeric.Interval;
import it.unive.lisa.analysis.string.CharInclusion;
import it.unive.lisa.analysis.string.Prefix;
import it.unive.lisa.analysis.string.Suffix;
import it.unive.lisa.analysis.string.bricks.Bricks;
import it.unive.lisa.analysis.string.fsa.FSA;
import it.unive.lisa.analysis.string.tarsis.Tarsis;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.interprocedural.ContextBasedAnalysis;
import it.unive.lisa.interprocedural.RecursionFreeToken;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;

public class TarsisPaperTests extends GoAnalysisTestExecutor {

	public static <S extends BaseNonRelationalValueDomain<S>> LiSAConfiguration baseConf(S stringDomain)
			throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.abstractState = getDefaultFor(AbstractState.class, getDefaultFor(HeapDomain.class),
				new SmashedSum<>(new Interval(), stringDomain),
				new InferredTypes());
		conf.serializeResults = true;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		return conf;
	}

	@Test
	public void toStringPrefixTest() throws IOException, AnalysisSetupException {
		perform("tarsis/tostring/prefix", "tostring.go", baseConf(new Prefix()));
	}

	@Test
	public void toStringSuffixTest() throws IOException, AnalysisSetupException {
		perform("tarsis/tostring/suffix", "tostring.go", baseConf(new Suffix()));
	}

	@Test
	public void toStringCiTest() throws IOException, AnalysisSetupException {
		perform("tarsis/tostring/ci", "tostring.go", baseConf(new CharInclusion()));
	}

	@Test
	public void toStringBricksTest() throws IOException, AnalysisSetupException {
		perform("tarsis/tostring/bricks", "tostring.go", baseConf(new Bricks()));
	}

	@Ignore
	public void toStringFaTest() throws IOException, AnalysisSetupException {
		perform("tarsis/tostring/fa", "tostring.go", baseConf(new FSA()));
	}

	@Test
	public void toStringTarsisTest() throws IOException, AnalysisSetupException {
		perform("tarsis/tostring/tarsis", "tostring.go", baseConf(new Tarsis()));
	}

	@Test
	public void substringPrefixTest() throws IOException, AnalysisSetupException {
		perform("tarsis/substring/prefix", "subs.go", baseConf(new Prefix()));
	}

	@Test
	public void substringSuffixTest() throws IOException, AnalysisSetupException {
		perform("tarsis/substring/suffix", "subs.go", baseConf(new Suffix()));
	}

	@Test
	public void substringCiTest() throws IOException, AnalysisSetupException {
		perform("tarsis/substring/ci", "subs.go", baseConf(new CharInclusion()));
	}

	@Test
	public void substringBricksTest() throws IOException, AnalysisSetupException {
		perform("tarsis/substring/bricks", "subs.go", baseConf(new Bricks()));
	}

	@Ignore
	public void substringFaTest() throws IOException, AnalysisSetupException {
		perform("tarsis/substring/fa", "subs.go", baseConf(new FSA()));
	}

	@Test
	public void substringTarsisTest() throws IOException, AnalysisSetupException {
		perform("tarsis/substring/tarsis", "subs.go", baseConf(new Tarsis()));
	}

	@Test
	public void loopPrefixTest() throws IOException, AnalysisSetupException {
		perform("tarsis/loop/prefix", "loop.go", baseConf(new Prefix()));
	}

	@Test
	public void loopSuffixTest() throws IOException, AnalysisSetupException {
		perform("tarsis/loop/suffix", "loop.go", baseConf(new Suffix()));
	}

	@Test
	public void loopCiTest() throws IOException, AnalysisSetupException {
		perform("tarsis/loop/ci", "loop.go", baseConf(new CharInclusion()));
	}

	@Test
	public void loopBricksTest() throws IOException, AnalysisSetupException {
		perform("tarsis/loop/bricks", "loop.go", baseConf(new Bricks()));
	}

	@Ignore
	public void loopFaTest() throws IOException, AnalysisSetupException {
		perform("tarsis/loop/fa", "loop.go", baseConf(new FSA()));
	}

	@Test
	public void loopTarsisTest() throws IOException, AnalysisSetupException {
		perform("tarsis/loop/tarsis", "loop.go", baseConf(new Tarsis()));
	}

	@Test
	public void cmPrefixTest() throws IOException, AnalysisSetupException {
		perform("tarsis/count/prefix", "count.go", baseConf(new Prefix()));
	}

	@Test
	public void cmSuffixTest() throws IOException, AnalysisSetupException {
		perform("tarsis/count/suffix", "count.go", baseConf(new Suffix()));
	}

	@Test
	public void cmCiTest() throws IOException, AnalysisSetupException {
		perform("tarsis/count/ci", "count.go", baseConf(new CharInclusion()));
	}

	@Test
	public void cmBricksTest() throws IOException, AnalysisSetupException {
		perform("tarsis/count/bricks", "count.go", baseConf(new Bricks()));
	}

	@Ignore
	public void cmFaTest() throws IOException, AnalysisSetupException {
		perform("tarsis/count/fa", "count.go", baseConf(new FSA()));
	}

	@Test
	public void cmTarsisTest() throws IOException, AnalysisSetupException {
		perform("tarsis/count/tarsis", "count.go", baseConf(new Tarsis()));
	}
}
