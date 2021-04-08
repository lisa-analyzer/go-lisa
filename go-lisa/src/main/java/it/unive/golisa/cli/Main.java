package it.unive.golisa.cli;

import static it.unive.lisa.LiSAFactory.getDefaultFor;

import java.io.IOException;

import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSA;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.impl.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.impl.numeric.Interval;
import it.unive.lisa.program.Program;

public class Main {

	public static void main(String[] args) throws IOException, AnalysisSetupException {

		String filePath = "example.go";
		String outputDir = "output";

		Program program = GoFrontEnd.processFile(filePath);

		LiSAConfiguration conf = new LiSAConfiguration();

		LiSA lisa = new LiSA(conf);
		conf.setWorkdir(outputDir).setInferTypes(true)
		.setAbstractState(getDefaultFor(AbstractState.class, new PointBasedHeap(), new Interval()))
		.setDumpAnalysis(true);

		try {
			lisa.run(program);
		} catch (Exception e) {
			// an error occurred during the analysis
			e.printStackTrace();
			return;
		} 
	}
}
