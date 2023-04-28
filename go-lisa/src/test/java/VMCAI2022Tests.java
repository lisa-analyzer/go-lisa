import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import it.unive.golisa.analysis.composition.RelTarsis;
import it.unive.golisa.frontend.GoFrontEnd;
import it.unive.lisa.AnalysisException;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSA;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.MonolithicHeap;
import it.unive.lisa.analysis.nonrelational.value.TypeEnvironment;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.conf.LiSAConfiguration;
import it.unive.lisa.interprocedural.ModularWorstCaseAnalysis;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;
import it.unive.lisa.interprocedural.context.ContextBasedAnalysis;
import it.unive.lisa.interprocedural.context.FullStackToken;
import it.unive.lisa.program.Program;

public class VMCAI2022Tests {
	private String tmpDir = "go-outputs/vmcai2022";
	private String sourcePath = "go-testcases/vmcai2022/";

	@Test
	public void codotaTest() throws IOException, AnalysisSetupException {
		Program program = GoFrontEnd.processFile(sourcePath + "codota.go");

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.abstractState = new SimpleAbstractState<>(
				new MonolithicHeap(),
				new RelTarsis(),
				new TypeEnvironment<>(new InferredTypes()));
		conf.serializeResults = true;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(FullStackToken.getSingleton());
		conf.workdir = tmpDir;

		LiSA lisa = new LiSA(conf);
		try {
			lisa.run(program);
		} catch (AnalysisException e) {
			System.err.println(e);
			fail("Analysis terminated with errors");
		}
	}

	@Test
	public void commonLangTest() throws IOException, AnalysisSetupException {
		Program program = GoFrontEnd.processFile(sourcePath + "common-lang.go");

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.abstractState = new SimpleAbstractState<>(
				new MonolithicHeap(),
				new RelTarsis(),
				new TypeEnvironment<>(new InferredTypes()));
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ModularWorstCaseAnalysis<>();
		conf.serializeResults = true;
		conf.workdir = tmpDir;

		LiSA lisa = new LiSA(conf);
		try {
			lisa.run(program);
		} catch (AnalysisException e) {
			System.err.println(e);
			fail("Analysis terminated with errors");
		}
	}

	@Test
	public void icalpTestCases() throws IOException, AnalysisSetupException {
		Program program = GoFrontEnd.processFile(sourcePath + "vmcai2022-testcases.go");

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.abstractState = new SimpleAbstractState<>(
				new MonolithicHeap(),
				new RelTarsis(),
				new TypeEnvironment<>(new InferredTypes()));
		conf.serializeResults = true;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(FullStackToken.getSingleton());
		conf.workdir = tmpDir;

		LiSA lisa = new LiSA(conf);
		try {
			lisa.run(program);
		} catch (AnalysisException e) {
			System.err.println(e);
			fail("Analysis terminated with errors");
		}
	}
}
