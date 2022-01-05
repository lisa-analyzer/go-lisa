package it.unive.golisa.cli;

import it.unive.golisa.analysis.Taint;
import it.unive.golisa.checker.TaintChecker;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSA;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.MonolithicHeap;
import it.unive.lisa.analysis.nonrelational.inference.InferenceSystem;
import it.unive.lisa.interprocedural.ContextBasedAnalysis;
import it.unive.lisa.interprocedural.RecursionFreeToken;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;
import it.unive.lisa.program.Program;
import java.io.File;
import java.io.IOException;
import org.antlr.v4.runtime.misc.ParseCancellationException;

public class GoLiSA {

	public static void main(String[] args) throws AnalysisSetupException {
		if (args == null || args[0] == null) {
			System.err.println("Input file is missing. Exiting.");
			return;
		}

		String filePath = args[0];

		if (args.length < 2) {
			System.err.println("Output directory is missing. Exiting.");
			return;
		}

		String outputDir = args[1];
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.setWorkdir(outputDir);
		conf.setJsonOutput(true);

		conf.setJsonOutput(true)
				.setDumpAnalysis(true)
				.setInferTypes(true)
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton()))
				.setCallGraph(new RTACallGraph())
				.setAbstractState(new SimpleAbstractState<>(new MonolithicHeap(), new InferenceSystem<>(new Taint())))
				.addSemanticCheck(new TaintChecker());

		Program program = null;

		File theDir = new File(outputDir);
		if (!theDir.exists())
			theDir.mkdirs();

		try {
			program = GoFrontEnd.processFile(filePath);
		} catch (ParseCancellationException e) {
			// a parsing error occurred
			System.err.println("Parsing error.");
			return;
		} catch (IOException e) {
			// the file does not exists
			System.err.println("File " + filePath + "does not exist.");
			return;
		} catch (UnsupportedOperationException e1) {
			// an unsupported operations has been encountered
			System.err.println(e1 + " " + e1.getStackTrace()[0].toString());
			e1.printStackTrace();
			return;
		} catch (Exception e2) {
			// other exception
			e2.printStackTrace();
			System.err.println(e2 + " " + e2.getStackTrace()[0].toString());
			return;
		}

		LiSA lisa = new LiSA(conf);

		try {
			lisa.run(program);
		} catch (Exception e) {
			// an error occurred during the analysis
			e.printStackTrace();
			return;
		}
	}
}
