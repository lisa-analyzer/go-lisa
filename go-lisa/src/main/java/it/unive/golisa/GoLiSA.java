package it.unive.golisa;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unive.golisa.analysis.entrypoints.EntryPointsFactory;
import it.unive.golisa.analysis.entrypoints.EntryPointsUtils;
import it.unive.golisa.analysis.heap.GoAbstractState;
import it.unive.golisa.analysis.heap.GoPointBasedHeap;
import it.unive.golisa.analysis.taint.TaintDomainForPhase1;
import it.unive.golisa.analysis.taint.TaintDomainForPhase2;
import it.unive.golisa.checker.UCCICheckerPhase1;
import it.unive.golisa.checker.UCCICheckerPhase2;
import it.unive.golisa.frontend.GoFrontEnd;
import it.unive.golisa.interprocedural.GoContextBasedAnalysis;
import it.unive.golisa.interprocedural.RelaxedOpenCallPolicy;
import it.unive.golisa.loader.AnnotationLoader;
import it.unive.golisa.loader.EntryPointLoader;
import it.unive.golisa.loader.annotation.CodeAnnotation;
import it.unive.golisa.loader.annotation.sets.HyperledgerFabricUCCIAnnotationSet;
import it.unive.golisa.loader.annotation.sets.UCCIAnnotationSet;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSA;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.LiSAConfiguration.GraphType;
import it.unive.lisa.LiSAFactory;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.checks.warnings.Warning;
import it.unive.lisa.interprocedural.RecursionFreeToken;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;

/**
 * The Go frontend for LiSA.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoLiSA {

	private static final Logger LOG = LogManager.getLogger(GoLiSA.class);

	/**
	 * Entry point of {@link GoLiSA}.
	 * 
	 * @param args the arguments
	 * 
	 * @throws AnalysisSetupException if something goes wrong with the analysis
	 */
	public static void main(String[] args) throws AnalysisSetupException {

		Options options = new Options();

		Option input = new Option("i", "input", true, "input file path");
		input.setRequired(true);
		options.addOption(input);

		Option output = new Option("o", "output", true, "output file path");
		output.setRequired(true);
		options.addOption(output);

		Option dump_opt = new Option("d", "dumpAnalysis", false, "dump the analysis");
		dump_opt.setRequired(false);
		options.addOption(dump_opt);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("help", options);

			System.exit(1);
		}

		String filePath = cmd.getOptionValue("input");

		try {
			if(!FileUtils.readFileToString(new File(filePath)).contains("InvokeChaincode")) {
				System.out.println("File "+filePath+" does not contain cross-contract invocations");
				System.exit(0);
			}
		} catch (IOException e3) {
			e3.printStackTrace();
		}
		
		String outputDir = cmd.getOptionValue("output");

		LiSAConfiguration confPhase1 = new LiSAConfiguration();

		confPhase1.jsonOutput = true;

		confPhase1.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		confPhase1.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new ValueEnvironment<>(new TaintDomainForPhase1()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		confPhase1.semanticChecks.add(new UCCICheckerPhase1());

		confPhase1.analysisGraphs = cmd.hasOption(dump_opt) ? GraphType.HTML_WITH_SUBNODES : GraphType.NONE;

		Program program = null;

		File theDirP1 = new File(outputDir, "Phase1");
		if (!theDirP1.exists())
			theDirP1.mkdirs();

		confPhase1.workdir = theDirP1.getAbsolutePath();
		
		try {

			UCCIAnnotationSet[] annotationSet = new UCCIAnnotationSet[] {new HyperledgerFabricUCCIAnnotationSet()};
			program = GoFrontEnd.processFile(filePath);
			AnnotationLoader annotationLoader = new AnnotationLoader();
			annotationLoader.addAnnotationSet(annotationSet);
			annotationLoader.load(program);

			EntryPointLoader entryLoader = new EntryPointLoader();
			entryLoader.addEntryPoints(EntryPointsFactory.getEntryPoints("hyperledger-fabric"));
			entryLoader.load(program);

			if (!entryLoader.isEntryFound()) {
				Set<Pair<CodeAnnotation, CodeMemberDescriptor>> appliedAnnotations = annotationLoader
						.getAppliedAnnotations();

				// if(EntryPointsUtils.containsPossibleEntryPointsForAnalysis(appliedAnnotations,
				// annotationSet)) {
				Set<CFG> cfgs = EntryPointsUtils.computeEntryPointSetFromPossibleEntryPointsForAnalysis(program,
						appliedAnnotations, annotationSet);
				for (CFG c : cfgs)
					program.addEntryPoint(c);
				// }
			}

			if (!program.getEntryPoints().isEmpty()) {
				confPhase1.interproceduralAnalysis = new GoContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
				confPhase1.callGraph = new RTACallGraph();
			} else
				LOG.info("Entry points not found in Phase 1!");

		} catch (ParseCancellationException e) {
			// a parsing error occurred
			System.err.println("Parsing error.");
			return;
		} catch (IOException e) {
			// the file does not exists
			System.err.println("File " + filePath + " does not exist.");
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

		LiSA lisaP1 = new LiSA(confPhase1);

		try {
			lisaP1.run(program);
		} catch (Exception e) {
			// an error occurred during the analysis
			e.printStackTrace();
			return;
		}
		
		if (!lisaP1.getWarnings().isEmpty()) {
			System.out.println("The analysis in the Phase 1 has generated the following warnings:");
			for (Warning warn : lisaP1.getWarnings())
				System.out.println(warn);
		}
		
		if(lisaP1.getWarnings().isEmpty())
			return;
		
		LiSAConfiguration confPhase2 = new LiSAConfiguration();
		
		confPhase2.jsonOutput = true;

		confPhase2.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		confPhase2.abstractState = new GoAbstractState<>(new GoPointBasedHeap(),
				new ValueEnvironment<>(new TaintDomainForPhase2()),
				LiSAFactory.getDefaultFor(TypeDomain.class));
		confPhase2.semanticChecks.add(new UCCICheckerPhase2());

		confPhase2.analysisGraphs = cmd.hasOption(dump_opt) ? GraphType.HTML_WITH_SUBNODES : GraphType.NONE;

		File theDirP2 = new File(outputDir, "Phase2");
		if (!theDirP2.exists())
			theDirP2.mkdirs();

		confPhase2.workdir = theDirP2.getAbsolutePath();
		
		try {

			UCCIAnnotationSet[] annotationSet = new UCCIAnnotationSet[] {new HyperledgerFabricUCCIAnnotationSet()};
			
			program = GoFrontEnd.processFile(filePath);
			AnnotationLoader annotationLoader = new AnnotationLoader();
			annotationLoader.addAnnotationSet(annotationSet);
			annotationLoader.load(program);

			EntryPointLoader entryLoader = new EntryPointLoader();
			entryLoader.addEntryPoints(EntryPointsFactory.getEntryPoints("hyperledger-fabric"));
			entryLoader.load(program);

			if (!entryLoader.isEntryFound()) {
				Set<Pair<CodeAnnotation, CodeMemberDescriptor>> appliedAnnotations = annotationLoader
						.getAppliedAnnotations();

				// if(EntryPointsUtils.containsPossibleEntryPointsForAnalysis(appliedAnnotations,
				// annotationSet)) {
				Set<CFG> cfgs = EntryPointsUtils.computeEntryPointSetFromPossibleEntryPointsForAnalysis(program,
						appliedAnnotations, annotationSet);
				for (CFG c : cfgs)
					program.addEntryPoint(c);
				// }
			}

			if (!program.getEntryPoints().isEmpty()) {
				confPhase2.interproceduralAnalysis = new GoContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
				confPhase2.callGraph = new RTACallGraph();
			} else
				LOG.info("Entry points not found in Phase 1!");

		} catch (ParseCancellationException e) {
			// a parsing error occurred
			System.err.println("Parsing error.");
			return;
		} catch (IOException e) {
			// the file does not exists
			System.err.println("File " + filePath + " does not exist.");
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

		LiSA lisaP2 = new LiSA(confPhase2);

		try {
			lisaP2.run(program);
		} catch (Exception e) {
			// an error occurred during the analysis
			e.printStackTrace();
			return;
		}
		
		if (!lisaP2.getWarnings().isEmpty()) {
			System.out.println("The analysis in the Phase 2 has generated the following warnings:");
			for (Warning warn : lisaP2.getWarnings())
				System.out.println(warn);
		}
	}

}
