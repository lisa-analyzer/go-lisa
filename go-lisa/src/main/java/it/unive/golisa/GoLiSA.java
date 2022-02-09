package it.unive.golisa;

import java.io.File;
import java.io.IOException;

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import it.unive.golisa.analysis.entrypoints.EntryPointsFactory;
import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.analysis.taint.annotation.NonDeterminismAnnotationSet;
import it.unive.golisa.checker.TaintChecker;
import it.unive.golisa.frontend.GoFrontEnd;
import it.unive.golisa.loader.AnnotationLoader;
import it.unive.golisa.loader.EntryPointLoader;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSA;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.LiSAFactory;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.nonrelational.inference.InferenceSystem;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.interprocedural.ContextBasedAnalysis;
import it.unive.lisa.interprocedural.RecursionFreeToken;
import it.unive.lisa.interprocedural.ReturnTopPolicy;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;
import it.unive.lisa.program.Program;

public class GoLiSA {

	public static void main(String[] args) throws AnalysisSetupException {
		
        Options options = new Options();

        Option input = new Option("i", "input", true, "input file path");
        input.setRequired(true);
        options.addOption(input);

        Option output = new Option("o", "output", true, "output file path");
        output.setRequired(true);
        options.addOption(output);
        
        Option framework = new Option("f", "framework", true, "framework to analyze (hyperledger-fabric, cosmos-sdk, tendermint-core)");
        framework.setRequired(false);
        options.addOption(framework);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
        }

		String filePath = cmd.getOptionValue("input");

		String outputDir = cmd.getOptionValue("output");
		
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.setWorkdir(outputDir);
		conf.setJsonOutput(true);

		conf.setJsonOutput(true)
				.setDumpAnalysis(true)
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton()))
				.setCallGraph(new RTACallGraph())
				.setAbstractState(
						new SimpleAbstractState<>(new PointBasedHeap(), new InferenceSystem<>(new TaintDomain()), LiSAFactory.getDefaultFor(TypeDomain.class)))
				.addSemanticCheck(new TaintChecker());

		Program program = null;

		File theDir = new File(outputDir);
		if (!theDir.exists())
			theDir.mkdirs();

		try {

			program = GoFrontEnd.processFile(filePath);
			AnnotationLoader annotationLoader = new AnnotationLoader();
			annotationLoader.addAnnotationSet(new NonDeterminismAnnotationSet());
			annotationLoader.load(program);
			
			EntryPointLoader entryLoader = new EntryPointLoader();
			entryLoader.addEntryPoints( EntryPointsFactory.getEntryPoints(cmd.getOptionValue("framework")));
			entryLoader.load(program);
			
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
