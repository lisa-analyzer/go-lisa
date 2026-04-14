package it.unive.golisa;

import it.unive.golisa.analysis.GoIntervalDomain;
import it.unive.golisa.analysis.DummyDomain;
import it.unive.golisa.analysis.entrypoints.EntryPointsFactory;
import it.unive.golisa.analysis.entrypoints.EntryPointsUtils;
import it.unive.golisa.analysis.ni.IntegrityNIDomain;
import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.analysis.utils.AnalysisPreRequirementsUtils;
import it.unive.golisa.analysis.utils.FileInfo;
import it.unive.golisa.checker.DivByZeroChecker;
import it.unive.golisa.checker.GoRoutineSourcesChecker;
import it.unive.golisa.checker.IntegrityNIChecker;
import it.unive.golisa.checker.NumericalOverflowOfVariablesChecker;
import it.unive.golisa.checker.TaintChecker;
import it.unive.golisa.checker.hf.CchiUtils;
import it.unive.golisa.checker.cosmos.panic.ABCIPanicChecker;
import it.unive.golisa.checker.hf.CrossChannelInvocationsIssuesChecker;
import it.unive.golisa.checker.hf.CrossChannelInvocationsWriteOpsChecker;
import it.unive.golisa.checker.hf.UnhandledErrorsChecker;
import it.unive.golisa.checker.hf.cci.CrossContractInvocationInformation;
import it.unive.golisa.checker.hf.readwrite.ReadWritePairChecker;
import it.unive.golisa.checker.hf.readwrite.ReadWritePathChecker;
import it.unive.golisa.frontend.GoFrontEnd;
import it.unive.golisa.interprocedural.RelaxedOpenCallPolicy;
import it.unive.golisa.loader.AnnotationLoader;
import it.unive.golisa.loader.EntryPointLoader;
import it.unive.golisa.loader.annotation.AnnotationSet;
import it.unive.golisa.loader.annotation.CodeAnnotation;
import it.unive.golisa.loader.annotation.FrameworkNonDeterminismAnnotationSetFactory;
import it.unive.golisa.loader.annotation.sets.PhantomReadAnnotationSet;
import it.unive.golisa.loader.annotation.sets.UCCIPhase1AnnotationSet;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSA;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.nonrelational.inference.InferenceSystem;
import it.unive.lisa.analysis.nonrelational.value.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.string.tarsis.Tarsis;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.conf.LiSAConfiguration;
import it.unive.lisa.conf.LiSAConfiguration.GraphType;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;
import it.unive.lisa.interprocedural.context.ContextBasedAnalysis;
import it.unive.lisa.interprocedural.context.FullStackToken;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.statement.Statement;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a> and
 *             <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
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
		
		Option contractInfo = new Option("ci", "contractinfo", true, "the deployment name and channel of the contract to analyze (Hyperledger Fabric only)");
		contractInfo.setRequired(false);
		options.addOption(contractInfo);
		contractInfo.setArgs(2);

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
		
		Option crosscontract_opt = new Option("xc", "crosscontract", false, "set cross-contract analysis (Hyperledger Fabric only)");
		crosscontract_opt.setRequired(false);
		options.addOption(crosscontract_opt);

		Option dump_opt = new Option("d", "dumpAnalysis", false, "dump the analysis");
		dump_opt.setRequired(false);
		options.addOption(dump_opt);

		Option dumpAdditionalAnalysisInfo = new Option("u", "dumpAdditionalAnalysisInfo", false,
				"dump additional info to improve user experience if allowed by the analysis");
		dumpAdditionalAnalysisInfo.setRequired(false);
		options.addOption(dumpAdditionalAnalysisInfo);

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
		
		List<FileInfo> fileInfos = new ArrayList<>();

        String[] arguments = args;
        String currentInput = null, currentName = null, currentChannel = null;

        for (int i = 0; i < arguments.length; i++) {
            switch (arguments[i]) {
                case "-i":
                case "--input":
                    if (currentInput != null && currentName != null && currentChannel != null) {
                    	fileInfos.add(new FileInfo(currentInput, currentName, currentChannel));
                    }
                    currentInput = arguments[++i];
                    currentName = null;
                    currentChannel = null;
                    break;
                case "-ci":
                case "--contractinfo":
                    currentName = arguments[++i];
                    currentChannel = arguments[++i];
                    break;
            }
        }

        // Add the last set of arguments if complete
        if (currentInput != null) {
        	fileInfos.add(new FileInfo(currentInput, currentName, currentChannel));
        }
        

		String outputDir = cmd.getOptionValue("output");

		String analysis = cmd.getOptionValue("analysis");

		boolean crossContractAnalysis = cmd.hasOption(crosscontract_opt);
		
		for(FileInfo fInfo : fileInfos) {
			
			LiSAConfiguration conf = new LiSAConfiguration();
			conf.workdir = outputDir + File.separatorChar + "Result"+fileInfos.hashCode();
		switch (analysis) {

		case "non-determinism":
			annotationSet = FrameworkNonDeterminismAnnotationSetFactory
					.getAnnotationSets(cmd.getOptionValue("framework"));
			conf.syntacticChecks.add(new GoRoutineSourcesChecker());
			conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
			conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
					new ValueEnvironment<>(new TaintDomain()), new TypeEnvironment<>(new InferredTypes()));
			conf.semanticChecks.add(new TaintChecker("Possible issue of non-determinism."));
			break;
		case "non-determinism-ni":
			annotationSet = FrameworkNonDeterminismAnnotationSetFactory
					.getAnnotationSets(cmd.getOptionValue("framework"));
			conf.syntacticChecks.add(new GoRoutineSourcesChecker());
			conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
			conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
					new InferenceSystem<>(new IntegrityNIDomain()), new TypeEnvironment<>(new InferredTypes()));
			conf.semanticChecks.add(new IntegrityNIChecker());
			break;
		case "phantom-read":
			annotationSet = new AnnotationSet[] { new PhantomReadAnnotationSet() };
			conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
			conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
					new ValueEnvironment<>(new TaintDomain()), new TypeEnvironment<>(new InferredTypes()));
			conf.semanticChecks.add(new TaintChecker("Possible phantom read."));
			break;
		case "ucci":
			// TODO: add configuration for UCCI analysis
			// TODO: support 2nd phase
			// require2Phase = true;
			/*
			 * dirPhase1 = new File(outputDir, "Phase1"); if
			 * (!dirPhase1.exists()) dirPhase1.mkdirs(); conf.workdir =
			 * dirPhase1.getAbsolutePath(); break;
			 */
			annotationSet = new AnnotationSet[] { new UCCIPhase1AnnotationSet() };
			conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
			conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
					new ValueEnvironment<>(new TaintDomain()), new TypeEnvironment<>(new InferredTypes()));
			conf.semanticChecks.add(new TaintChecker("Possible untrusted cross-contract invocation."));
			break;
		case "cchi":
			conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
			conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(), new ValueEnvironment<>(new Tarsis()),
					new TypeEnvironment<>(new InferredTypes()));
			conf.semanticChecks.add(new CrossChannelInvocationsIssuesChecker());
			break;
		case "read-write":

			require2Phase = true;

			conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
			conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(), new ValueEnvironment<>(new Tarsis()),
					new TypeEnvironment<>(new InferredTypes()));
			readwritePhase1 = new ReadWritePairChecker();
			conf.semanticChecks.add(readwritePhase1);

			dirPhase1 = new File(outputDir, "Phase1");
			if (!dirPhase1.exists())
				dirPhase1.mkdirs();

			conf.workdir = dirPhase1.getAbsolutePath();
			break;
		case "unhandled-errors":
			conf.syntacticChecks.add(new UnhandledErrorsChecker());
			conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(), new ValueEnvironment<>(new DummyDomain()),
					new TypeEnvironment<>(new InferredTypes()));
			conf.semanticChecks.add(new ABCIPanicChecker());
			break;
		case "numerical-issues":
			conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
			conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(), new ValueEnvironment<>(new GoIntervalDomain()),
					new TypeEnvironment<>(new InferredTypes()));
			conf.semanticChecks.add(new NumericalOverflowOfVariablesChecker());
			conf.semanticChecks.add(new DivByZeroChecker());			
		default:

		}

		File theDir = new File(outputDir);
		if (!theDir.exists())
			theDir.mkdirs();

		lisaExecution(filePath, annotationSet, cmd.getOptionValue("framework"), analysis, conf);

		File dirPhase2 = new File(outputDir, "Phase2");

		if (require2Phase) {

			conf = new LiSAConfiguration();
			conf.workdir = outputDir;
			conf.jsonOutput = true;
			conf.optimize = false;

			conf.analysisGraphs = cmd.hasOption(dump_opt) ? GraphType.HTML_WITH_SUBNODES : GraphType.NONE;
			
			AnnotationSet[] annotationSet = new AnnotationSet[] {};
			boolean require2Phase = false;
	
			ReadWritePairChecker readwritePhase1 = null;
			CrossChannelInvocationsIssuesChecker cchiChecker = null;
			
			File dirPhase1 = null;
			
			switch (analysis) {
	
				case "non-determinism":
					annotationSet = FrameworkNonDeterminismAnnotationSetFactory
							.getAnnotationSets(cmd.getOptionValue("framework"));
					conf.syntacticChecks.add(new GoRoutineSourcesChecker());
					conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
					conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
							new ValueEnvironment<>(new TaintDomain()), new TypeEnvironment<>(new InferredTypes()));
					conf.semanticChecks.add(new TaintChecker("Possible issue of non-determinism."));
					break;
				case "non-determinism-ni":
					annotationSet = FrameworkNonDeterminismAnnotationSetFactory
							.getAnnotationSets(cmd.getOptionValue("framework"));
					conf.syntacticChecks.add(new GoRoutineSourcesChecker());
					conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
					conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
							new InferenceSystem<>(new IntegrityNIDomain()), new TypeEnvironment<>(new InferredTypes()));
					conf.semanticChecks.add(new IntegrityNIChecker());
					break;
				case "phantom-read":
					annotationSet = new AnnotationSet[] { new PhantomReadAnnotationSet() };
					conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
					conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
							new ValueEnvironment<>(new TaintDomain()), new TypeEnvironment<>(new InferredTypes()));
					conf.semanticChecks.add(new TaintChecker("Possible phantom read."));
					break;
				case "ucci":
					// TODO: add configuration for UCCI analysis
					// TODO: support 2nd phase
					// require2Phase = true;
					/*
					 * dirPhase1 = new File(outputDir, "Phase1"); if
					 * (!dirPhase1.exists()) dirPhase1.mkdirs(); conf.workdir =
					 * dirPhase1.getAbsolutePath(); break;
					 */
					annotationSet = new AnnotationSet[] { new UCCIPhase1AnnotationSet() };
					conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
					conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
							new ValueEnvironment<>(new TaintDomain()), new TypeEnvironment<>(new InferredTypes()));
					conf.semanticChecks.add(new TaintChecker("Possible untrusted cross-contract invocation."));
					break;
				case "cchi":
					conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
					conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(), new ValueEnvironment<>(new Tarsis()),
							new TypeEnvironment<>(new InferredTypes()));
					cchiChecker = new CrossChannelInvocationsIssuesChecker(!crossContractAnalysis, fInfo.getChannel());
					conf.semanticChecks.add(cchiChecker);
					break;
				case "read-write":
		
					require2Phase = true;
		
					conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
					conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(), new ValueEnvironment<>(new Tarsis()),
							new TypeEnvironment<>(new InferredTypes()));
					readwritePhase1 = new ReadWritePairChecker();
					conf.semanticChecks.add(readwritePhase1);
		
					dirPhase1 = new File(outputDir, "Phase1");
					if (!dirPhase1.exists())
						dirPhase1.mkdirs();
		
					conf.workdir = dirPhase1.getAbsolutePath();
					break;
				case "unhandled-errors":
					conf.syntacticChecks.add(new UnhandledErrorsChecker());
					break;
				case "numerical-issues":
					conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
					conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(), new ValueEnvironment<>(new GoIntervalDomain()),
							new TypeEnvironment<>(new InferredTypes()));
					conf.semanticChecks.add(new NumericalOverflowOfVariablesChecker());
					conf.semanticChecks.add(new DivByZeroChecker());			
				default:
	
			}
	
			File theDir = new File(outputDir);
			if (!theDir.exists())
				theDir.mkdirs();
	
			Program program = lisaExecution(fInfo.getInput(), annotationSet, cmd.getOptionValue("framework"), analysis, conf);
			
			if(crossContractAnalysis && program != null) {
				if(analysis.equals("cchi")) {
					//program.getAllCFGs().
				}
			}
			
			
			File dirPhase2 = new File(outputDir, "Phase2");
	
			if (require2Phase) {
	
				conf = new LiSAConfiguration();
				conf.workdir = outputDir;
				conf.jsonOutput = true;
				conf.optimize = false;
	
				dirPhase2 = new File(outputDir, "Phase2");
				if (!dirPhase2.exists())
					dirPhase2.mkdirs();
	
				conf.workdir = dirPhase2.getAbsolutePath();
				conf.analysisGraphs = cmd.hasOption(dump_opt) ? GraphType.HTML_WITH_SUBNODES : GraphType.NONE;
	
				switch (analysis) {
				case "ucci":
					// TODO: add configuration for UCCI analysis
					// break;
					throw new IllegalArgumentException("The 2nd phase of UCCI analysis is currently not supported");
				case "read-write":
					conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
					conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
							new ValueEnvironment<>(new Tarsis()),
							new TypeEnvironment<>(new InferredTypes()));
					conf.semanticChecks.add(new ReadWritePathChecker(readwritePhase1.getReadAfterWriteCandidates(),
							readwritePhase1.getOverWriteCandidates(), cmd.hasOption(dumpAdditionalAnalysisInfo)));
					break;
				default:
	
				}
				lisaExecution(fInfo.getInput(), annotationSet, cmd.getOptionValue("framework"), analysis, conf);
			}
			
			if(crossContractAnalysis) {
				switch (analysis) {
				
					case "cchi":
						Map<Statement, CrossContractInvocationInformation> cchis = cchiChecker.getCrossChannelInvocations();
						
						for (FileInfo fi : fileInfos) {
							Set<Statement> cchisToCheck;
							
							LiSAConfiguration cchis2 = new LiSAConfiguration();
							cchis2.workdir = conf.workdir + File.separatorChar +"xcontract" + File.separatorChar+ "Result"+fileInfos.hashCode();
							cchis2.jsonOutput = true;
							cchis2.optimize = false;

							cchis2.analysisGraphs = cmd.hasOption(dump_opt) ? GraphType.HTML_WITH_SUBNODES : GraphType.NONE;
							
							cchis2.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
							cchis2.abstractState = new SimpleAbstractState<>(new PointBasedHeap(), new ValueEnvironment<>(new Tarsis()),
									new TypeEnvironment<>(new InferredTypes()));
							
							cchisToCheck = CchiUtils.computeCchisToCheck(fi, cchis);
							
							if(cchisToCheck != null && !cchisToCheck.isEmpty()) {
								cchis2.semanticChecks.add(new CrossChannelInvocationsWriteOpsChecker(cchisToCheck,  cmd.hasOption(dumpAdditionalAnalysisInfo)));
								lisaExecution(fi.getInput(), annotationSet, cmd.getOptionValue("framework"), "cchi-write", cchis2);
							}
						} 

					default:
						
				}
			}
		}

	}

	private static Program lisaExecution(String filePath, AnnotationSet[] annotationSet, String framework, String analysis,
			LiSAConfiguration conf) {
		Program program = null;
		
		try {

			program = GoFrontEnd.processFile(filePath);
			
			AnnotationLoader annotationLoader = new AnnotationLoader();
			annotationLoader.addAnnotationSet(annotationSet);
			annotationLoader.load(program);

			EntryPointLoader entryLoader = new EntryPointLoader();
			entryLoader.addEntryPoints(EntryPointsFactory.getEntryPoints(framework));
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
			
			if(analysis.equals("numerical-issues") || analysis.equals("div-by-zero"))
				for (CFG c : program.getAllCFGs())
					program.addEntryPoint(c);
			
			if(analysis.equals("unhandled-errors") && framework.equalsIgnoreCase("COSMOS-SDK"))
				for (CFG c : program.getAllCFGs())
					if(c.getDescriptor().getSignature().contains("BeginBlocker") 
							|| c.getDescriptor().getSignature().contains("EndBlocker"))
					program.addEntryPoint(c);

			if (!program.getEntryPoints().isEmpty()) {
				conf.interproceduralAnalysis = new ContextBasedAnalysis<>(FullStackToken.getSingleton());
				conf.callGraph = new RTACallGraph();
			} else
				LOG.info("Entry points not found!");

		} catch (ParseCancellationException e) {
			// a parsing error occurred
			System.err.println("Parsing error.");
			return null;
		} catch (IOException e) {
			// the file does not exists
			System.err.println("File " + filePath + "does not exist.");
			return null;
		} catch (UnsupportedOperationException e1) {
			// an unsupported operations has been encountered
			System.err.println(e1 + " " + e1.getStackTrace()[0].toString());
			e1.printStackTrace();
			return null;
		} catch (Exception e2) {
			// other exception
			e2.printStackTrace();
			System.err.println(e2 + " " + e2.getStackTrace()[0].toString());
			return null;
		}
		
		if (program != null) {
			if(AnalysisPreRequirementsUtils.satisfyPrerequirements(program, analysis)) {
				LiSA lisa = new LiSA(conf);
				try {
					lisa.run(program);
				} catch (Exception e) {
					// an error occurred during the analysis
					e.printStackTrace();
					return program;
				}
			} else {
				System.out.println("Pre-requirements for the analysis not found!");
			}
		}
		return program;
	}

}
