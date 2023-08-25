package it.unive.golisa;

import it.unive.golisa.analysis.entrypoints.EntryPointsFactory;
import it.unive.golisa.analysis.entrypoints.EntryPointsUtils;
import it.unive.golisa.analysis.ni.IntegrityNIDomain;
import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.checker.GoRoutineSourcesChecker;
import it.unive.golisa.checker.IntegrityNIChecker;
import it.unive.golisa.checker.NumericalOverflowChecker;
import it.unive.golisa.checker.TaintChecker;
import it.unive.golisa.checker.readwrite.ReadWritePairChecker;
import it.unive.golisa.checker.readwrite.ReadWritePathChecker;
import it.unive.golisa.frontend.GoFrontEnd;
import it.unive.golisa.interprocedural.RelaxedOpenCallPolicy;
import it.unive.golisa.loader.AnnotationLoader;
import it.unive.golisa.loader.EntryPointLoader;
import it.unive.golisa.loader.annotation.CodeAnnotation;
import it.unive.golisa.loader.annotation.FrameworkNonDeterminismAnnotationSetFactory;
import it.unive.golisa.loader.annotation.sets.NonDeterminismAnnotationSet;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSA;
import it.unive.lisa.LiSAFactory;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.nonrelational.inference.InferenceSystem;
import it.unive.lisa.analysis.nonrelational.value.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.numeric.Interval;
import it.unive.lisa.analysis.string.tarsis.Tarsis;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.conf.LiSAConfiguration;
import it.unive.lisa.conf.LiSAConfiguration.GraphType;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;
import it.unive.lisa.interprocedural.context.ContextBasedAnalysis;
import it.unive.lisa.interprocedural.context.FullStackToken;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
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
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

		Option framework = new Option("f", "framework", true,
				"framework to analyze (hyperledger-fabric, cosmos-sdk, tendermint-core)");
		framework.setRequired(false);
		options.addOption(framework);

		Option analysis_opt = new Option("a", "analysis", true, "the analysis to perform (taint, non-interference)");
		analysis_opt.setRequired(true);
		options.addOption(analysis_opt);

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

		String outputDir = cmd.getOptionValue("output");

		String analysis = cmd.getOptionValue("analysis");

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.workdir = outputDir;
		conf.jsonOutput = true;
		conf.optimize = false;
		//conf.hotspots
		
		ReadWritePairChecker readWritePairChecker = null; 

		switch (analysis) {

		case "taint":
			conf.syntacticChecks.add(new GoRoutineSourcesChecker());
			conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
			conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
					new ValueEnvironment<>(new TaintDomain()),
					LiSAFactory.getDefaultFor(TypeDomain.class));
			conf.semanticChecks.add(new TaintChecker());
			break;
		case "non-interference":
			conf.syntacticChecks.add(new GoRoutineSourcesChecker());
			conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
			conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
					new InferenceSystem<>(new IntegrityNIDomain()),
					LiSAFactory.getDefaultFor(TypeDomain.class));
			conf.semanticChecks.add(new IntegrityNIChecker());
			break;
		case "numerical-overflow":
			conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
			conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
					new ValueEnvironment<>(new Interval()),
					new TypeEnvironment<>(new InferredTypes()));
			conf.semanticChecks.add(new NumericalOverflowChecker());
			break;
		case "read-write":
			conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
			conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
					new ValueEnvironment<>(new Tarsis()),
					new TypeEnvironment<>(new InferredTypes()));
			readWritePairChecker = new ReadWritePairChecker();
			conf.semanticChecks.add(readWritePairChecker);
			break;
		default:
			
		}

		conf.analysisGraphs = cmd.hasOption(dump_opt) ? GraphType.HTML_WITH_SUBNODES : GraphType.NONE;

		Program program = null;

		File theDir = new File(outputDir);
		if (!theDir.exists())
			theDir.mkdirs();

		try {

			NonDeterminismAnnotationSet[] annotationSet = FrameworkNonDeterminismAnnotationSetFactory
					.getAnnotationSets(cmd.getOptionValue("framework"));
			program = GoFrontEnd.processFile(filePath);
			AnnotationLoader annotationLoader = new AnnotationLoader();
			annotationLoader.addAnnotationSet(annotationSet);
			annotationLoader.load(program);

			EntryPointLoader entryLoader = new EntryPointLoader();
			entryLoader.addEntryPoints(EntryPointsFactory.getEntryPoints(cmd.getOptionValue("framework")));
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
				conf.interproceduralAnalysis = new ContextBasedAnalysis<>(FullStackToken.getSingleton());
				conf.callGraph = new RTACallGraph();
			} else
				LOG.info("Entry points not found!");

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
	
		if(analysis.equals("read-write")) {
			//phase 2

			LiSAConfiguration conf2 = new LiSAConfiguration();
			conf2.workdir = outputDir;
			conf2.jsonOutput = true;
			conf2.optimize = false;
			
			conf2.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
			conf2.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
					new ValueEnvironment<>(new Tarsis()),
					new TypeEnvironment<>(new InferredTypes()));
			conf2.semanticChecks.add(new ReadWritePathChecker(readWritePairChecker.getReadAfterWriteCandidates(), readWritePairChecker.getOverWriteCandidates()));
			
			conf2.analysisGraphs = cmd.hasOption(dump_opt) ? GraphType.HTML_WITH_SUBNODES : GraphType.NONE;
			
			if (!program.getEntryPoints().isEmpty()) {
				conf2.interproceduralAnalysis = new ContextBasedAnalysis<>(FullStackToken.getSingleton());
				conf2.callGraph = new RTACallGraph();
			} else
				LOG.info("Entry points not found!");
			
			LiSA lisa2 = new LiSA(conf2);
	
			try {
				lisa2.run(program);
			} catch (Exception e) {
				// an error occurred during the analysis
				e.printStackTrace();
				return;
			}
		}
	}

}
