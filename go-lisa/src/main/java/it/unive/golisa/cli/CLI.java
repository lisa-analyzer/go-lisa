package it.unive.golisa.cli;

import static it.unive.lisa.LiSAFactory.getDefaultFor;

import java.io.IOException;

import it.unive.golisa.analysis.tarsis.Tarsis;
import it.unive.lisa.AnalysisException;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSA;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.HeapDomain;
import it.unive.lisa.program.Program;

public class CLI {

	public static void main(String[] args) throws  AnalysisSetupException, IOException {
		String filePath = "go-testcases/strings.go";
		String outputDir = "tmp";

		Program program = GoFrontEnd.processFile(filePath);

		LiSA lisa = new LiSA();

		lisa.setProgram(program);
		lisa.setJsonOutput(true);
		lisa.setWorkdir(outputDir);
		lisa.setInferTypes(true);
		lisa.setAbstractState(getDefaultFor(AbstractState.class, getDefaultFor(HeapDomain.class), new Tarsis()));
		lisa.setDumpAnalysis(true);
		lisa.setDumpTypeInference(true);


		try {
			lisa.run();
		} catch (AnalysisException e) {
			// an error occurred during the analysis
			e.printStackTrace();
		} catch (UnsupportedOperationException e) {
			// an unsupported operations has been analyzed
			e.printStackTrace();
		}	
	}
}
