package it.unive.golisa;

import it.unive.golisa.analysis.GoIntervalDomain;
import it.unive.golisa.analysis.entrypoints.EntryPointSet;
import it.unive.golisa.analysis.entrypoints.EntryPointsFactory;
import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.checker.TaintChecker;
import it.unive.golisa.frontend.GoFrontEnd;
import it.unive.golisa.interprocedural.RelaxedOpenCallPolicy;
import it.unive.golisa.loader.EntryPointLoader;
import it.unive.golisa.loader.annotation.AnnotationSet;
import it.unive.golisa.loader.annotation.CodeAnnotation;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSA;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.nonrelational.value.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.numeric.Parity;
import it.unive.lisa.analysis.numeric.Pentagon;
import it.unive.lisa.analysis.numeric.Sign;
import it.unive.lisa.analysis.string.Prefix;
import it.unive.lisa.analysis.string.Suffix;
import it.unive.lisa.analysis.string.tarsis.Tarsis;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.conf.LiSAConfiguration;
import it.unive.lisa.conf.LiSAConfiguration.GraphType;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;
import it.unive.lisa.interprocedural.context.ContextBasedAnalysis;
import it.unive.lisa.interprocedural.context.FullStackToken;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeMember;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.Parameter;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
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

		Option output = new Option("o", "output", true, "output file path");
		output.setRequired(true);
		options.addOption(output);

		Option analysis_opt = new Option("a", "analysis", true, "the analysis to perform (taint, non-interference)");
		analysis_opt.setRequired(true);
		options.addOption(analysis_opt);
		

		Option entrypointss_opt = new Option("e", "entrypoints", true, "path of file containg the list of entrypoints (method names) ");
		entrypointss_opt.setRequired(false);
		options.addOption(entrypointss_opt);
		
		Option sinks_opt = new Option("sinks", "sinks", true, "path of file containg the list of sinks (method names) for taint analysis");
		sinks_opt.setRequired(false);
		options.addOption(sinks_opt);
		
		Option sources_opt = new Option("sources", "sources", true, "path of file containg the list of sources (method names) for taint analysis");
		sources_opt.setRequired(false);
		options.addOption(sources_opt);
		
		Option sanitizers_opt = new Option("sanitizers", "sanitizers", true, "path of file containg the list of sanitizers (method names) for taint analysis");
		sanitizers_opt.setRequired(false);
		options.addOption(sanitizers_opt);


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

		conf.analysisGraphs = GraphType.HTML_WITH_SUBNODES;
		
		switch (analysis) {
		case "sign":
			conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
			conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(), new ValueEnvironment<>(new Sign()),
					new TypeEnvironment<>(new InferredTypes()));
		case "parity":
			conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
			conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(), new ValueEnvironment<>(new Parity()),
					new TypeEnvironment<>(new InferredTypes()));
		case "intervals":
			conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
			conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(), new ValueEnvironment<>(new GoIntervalDomain()),
					new TypeEnvironment<>(new InferredTypes()));
		case "pentagons":
			conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
			conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(), new Pentagon(),
					new TypeEnvironment<>(new InferredTypes()));
		case "prefix":
			conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
			conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(), new ValueEnvironment<>(new Prefix()),
					new TypeEnvironment<>(new InferredTypes()));
			break;
		case "suffix":
			conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
			conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(), new ValueEnvironment<>(new Suffix()),
					new TypeEnvironment<>(new InferredTypes()));
			break;
		case "tarsis":
			conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
			conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(), new ValueEnvironment<>(new Tarsis()),
					new TypeEnvironment<>(new InferredTypes()));
			break;
		case "taint":
			conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
			conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
					new ValueEnvironment<>(new TaintDomain()), new TypeEnvironment<>(new InferredTypes()));
			conf.semanticChecks.add(new TaintChecker());
			break;
			
		default:

		}

		File theDir = new File(outputDir);
		if (!theDir.exists())
			theDir.mkdirs();

		Program program = null;

		try {

			program = GoFrontEnd.processFile(filePath);
			
			if(analysis.equals("taint")) {
				applyAnnotationForTaint(program, entrypointss_opt, entrypointss_opt, entrypointss_opt);
			}

			EntryPointLoader entryLoader = new EntryPointLoader();
			
			EntryPointSet entrypoints = EntryPointsFactory.getEntryPoints(null); // Considers main method
			
			if(entrypointss_opt.hasOptionalArg()) {
				// Considers provided user entrypoints
				entrypoints = getEntryPointsFromList(entrypointss_opt.getValue());
			}
			entryLoader.addEntryPoints(entrypoints);
			entryLoader.load(program);

			if (!entryLoader.isEntryFound()) {
			
				// Considers all methods
				for (CFG c : program.getAllCFGs())
					program.addEntryPoint(c);
		
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
		
		if (program != null) {
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

	private static void applyAnnotationForTaint(Program program, Option sources_opt, Option sinks_opt,
			Option sanitizers_opt) {
		
		Collection<CodeMember> codeMembers = program.getCodeMembers();
		Set<String> sources = Set.of();
		Set<String> sinks = Set.of();
		Set<String> sanitizers = Set.of();
		try {
			if(sources_opt.hasOptionalArg())
				sources.addAll(FileUtils.readLines(new File(sources_opt.getValue())));
			if(sinks_opt.hasOptionalArg())
				sinks.addAll(FileUtils.readLines(new File(sinks_opt.getValue())));
			if(sanitizers_opt.hasOptionalArg())
				sanitizers.addAll(FileUtils.readLines(new File(sanitizers_opt.getValue())));
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (CodeMember cm : codeMembers)
				checkAndAddAnnotation(cm.getDescriptor(), sources,sinks, sanitizers);

		for (Unit unit : program.getUnits()) {
			for (CodeMember cm : unit.getCodeMembers()) {
				checkAndAddAnnotation(cm.getDescriptor(), sources,sinks, sanitizers);
			}

			if (unit instanceof CompilationUnit) {
				CompilationUnit cUnit = (CompilationUnit) unit;

				for (CodeMember cm : cUnit.getInstanceCodeMembers(true)) {
					checkAndAddAnnotation(cm.getDescriptor(), sources,sinks, sanitizers);
				}
			}
		}
		
	}

	private static void checkAndAddAnnotation(CodeMemberDescriptor descriptor, Set<String> sources, Set<String> sinks,
			Set<String> sanitizers) {
		String name = descriptor.getName();
		if(sources.contains(name))
			descriptor.getAnnotations().addAnnotation(TaintDomain.TAINTED_ANNOTATION);
		if(sanitizers.contains(name))
			descriptor.getAnnotations().addAnnotation(TaintDomain.CLEAN_ANNOTATION);
		if(sinks.contains(name))
			for(Parameter param : descriptor.getFormals())
				param.addAnnotation(TaintChecker.SINK_ANNOTATION);
		
	}

	private static EntryPointSet getEntryPointsFromList(String path) {
		
		return new EntryPointSet() {

			@Override
			protected void build(Set<String> entryPoints) {
				try {
					List<String> names = FileUtils.readLines(new File(path));
					entryPoints.addAll(names);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		};
	}



}
