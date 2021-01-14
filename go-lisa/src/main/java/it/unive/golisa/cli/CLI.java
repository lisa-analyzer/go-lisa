package it.unive.golisa.cli;

import static it.unive.lisa.LiSAFactory.getDefaultFor;

import java.io.IOException;

import it.unive.golisa.analysis.RSubs;
import it.unive.lisa.AnalysisException;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSA;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.HeapDomain;
import it.unive.lisa.program.Program;

public class CLI {

	public static void main(String[] args) throws IOException, AnalysisSetupException {
		
		Program program = GoFrontEnd.processFile("go-testcases/example.go");
		LiSA lisa = new LiSA();

		lisa.setProgram(program);
		lisa.setJsonOutput(true);
		lisa.setInferTypes(true);
		lisa.setAbstractState(getDefaultFor(AbstractState.class, getDefaultFor(HeapDomain.class), new RSubs()));
		lisa.setDumpAnalysis(true);
		lisa.setWorkdir("tmp");

		try {
			lisa.run();
		} catch (AnalysisException e) {
			System.err.println(e);
		}
		
	}
}
