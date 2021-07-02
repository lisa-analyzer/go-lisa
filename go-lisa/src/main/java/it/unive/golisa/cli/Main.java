package it.unive.golisa.cli;

import java.io.IOException;

import it.unive.golisa.analysis.ModularWorstCaseWithNativeCalls;
import it.unive.golisa.analysis.composition.RelTarsis;
import it.unive.golisa.analysis.tarsis.TarsisIntv;
import it.unive.lisa.AnalysisException;
import it.unive.lisa.LiSA;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.LiSAFactory;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.impl.heap.MonolithicHeap;
import it.unive.lisa.analysis.impl.heap.pointbased.FieldSensitivePointBasedHeap;
import it.unive.lisa.interprocedural.callgraph.impl.RTACallGraph;
import it.unive.lisa.interprocedural.impl.ContextBasedAnalysis;
import it.unive.lisa.interprocedural.impl.RecursionFreeToken;
import it.unive.lisa.program.Program;

public class Main {
	public static void main(String[] args) throws IOException, AnalysisException {
		Program program = GoFrontEnd.processFile("example.go");

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.setJsonOutput(true)
		.setInferTypes(true)
		.setDumpTypeInference(true)
		.setAbstractState(LiSAFactory.getDefaultFor(AbstractState.class, new MonolithicHeap(), new RelTarsis()))
		.setDumpAnalysis(true)
		.setCallGraph(new RTACallGraph())
		.setInterproceduralAnalysis(new ContextBasedAnalysis<>())
		.setWorkdir("output");

		new LiSA(conf).run(program);
	}
}
