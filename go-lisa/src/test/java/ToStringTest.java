import static it.unive.lisa.LiSAFactory.getDefaultFor;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import it.unive.golisa.analysis.scam.SmashedSum;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.numeric.Interval;
import it.unive.lisa.analysis.string.CharInclusion;
import it.unive.lisa.analysis.string.Prefix;
import it.unive.lisa.analysis.string.Suffix;
import it.unive.lisa.analysis.string.fsa.FSA;
import it.unive.lisa.analysis.string.tarsis.Tarsis;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.interprocedural.ContextBasedAnalysis;
import it.unive.lisa.interprocedural.RecursionFreeToken;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;

public class ToStringTest extends GoAnalysisTestExecutor {
	
	@Test
	public void prefixTest() throws IOException, AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.abstractState = getDefaultFor(AbstractState.class, getDefaultFor(HeapDomain.class), new SmashedSum<Prefix>(new Interval(), new Prefix()),
				new InferredTypes());
		conf.serializeResults = true;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		perform("tarsis/tostring/prefix", "tostring.go", conf);
	}
	
	@Test
	public void suffixTest() throws IOException, AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.abstractState = getDefaultFor(AbstractState.class, getDefaultFor(HeapDomain.class), new SmashedSum<Suffix>(new Interval(), new Suffix()),
				new InferredTypes());
		conf.serializeResults = true;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		perform("tarsis/tostring/suffix", "tostring.go", conf);

	}
	
	@Test
	public void ciTest() throws IOException, AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.abstractState = getDefaultFor(AbstractState.class, getDefaultFor(HeapDomain.class), new SmashedSum<CharInclusion>(new Interval(), new CharInclusion()),
				new InferredTypes());
		conf.serializeResults = true;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		perform("tarsis/tostring/ci", "tostring.go", conf);
		
	}
	
	@Ignore
	public void faTest() throws IOException, AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.abstractState = getDefaultFor(AbstractState.class, getDefaultFor(HeapDomain.class), new SmashedSum<FSA>(new Interval(), new FSA()),
				new InferredTypes());
		conf.serializeResults = true;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		perform("tarsis/tostring/fa", "tostring.go", conf);

	}
	

	@Test
	public void tarsisTest() throws IOException, AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.abstractState = getDefaultFor(AbstractState.class, getDefaultFor(HeapDomain.class), new SmashedSum<Tarsis>(new Interval(), new Tarsis()),
				new InferredTypes());
		conf.serializeResults = true;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		perform("tarsis/tostring/tarsis", "tostring.go", conf);

	}
}