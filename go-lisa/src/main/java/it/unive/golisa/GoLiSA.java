package it.unive.golisa;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unive.golisa.analysis.entrypoints.EntryPointsFactory;
import it.unive.golisa.analysis.entrypoints.EntryPointsUtils;
import it.unive.golisa.analysis.hf.privacy.JSONPrivateDataCollectionPolicyParser;
import it.unive.golisa.analysis.hf.privacy.JSONPrivateDataCollectionPolicyParser.PrivateDataPolicy;

import it.unive.golisa.analysis.taint.TaintDomainForPrivacyHF;
import it.unive.golisa.analysis.utilities.PrivacySignatures;
import it.unive.golisa.cfg.utils.CFGUtils;
import it.unive.golisa.checker.ComputePrivateCollectionFromStatementsChecker;
import it.unive.golisa.checker.PrivacyHFChecker;
import it.unive.golisa.frontend.GoFrontEnd;
import it.unive.golisa.interprocedural.RelaxedOpenCallPolicy;
import it.unive.golisa.loader.AnnotationLoader;
import it.unive.golisa.loader.EntryPointLoader;
import it.unive.golisa.loader.annotation.AnnotationSet;
import it.unive.golisa.loader.annotation.CodeAnnotation;
import it.unive.golisa.loader.annotation.MethodAnnotation;
import it.unive.golisa.loader.annotation.MethodParameterAnnotation;
import it.unive.golisa.loader.annotation.sets.CustomTaintAnnotationSet;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSA;
import it.unive.lisa.LiSAReport;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.AnalyzedCFG;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.nonrelational.value.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.string.tarsis.Tarsis;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.checks.semantic.CheckToolWithAnalysisResults;
import it.unive.lisa.checks.warnings.Warning;
import it.unive.lisa.conf.LiSAConfiguration;
import it.unive.lisa.conf.LiSAConfiguration.GraphType;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;
import it.unive.lisa.interprocedural.context.ContextBasedAnalysis;
import it.unive.lisa.interprocedural.context.FullStackToken;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeMember;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.call.CFGCall;
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.program.cfg.statement.call.NativeCall;
import it.unive.lisa.program.cfg.statement.call.Call.CallType;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.type.Type;
import it.unive.lisa.program.cfg.statement.call.OpenCall;
import it.unive.lisa.program.cfg.statement.call.UnresolvedCall;
import it.unive.lisa.util.datastructures.graph.GraphVisitor;

/**
 * The Go frontend for LiSA.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
 */
public class GoLiSA {

	private static final Logger LOG = LogManager.getLogger(GoLiSA.class);
	
	
	 private static final String PRIVATE_INPUT_IN_PUBLIC_STATES  = "PrivateInput-PublicStates";
	 private static final String PUBLIC_INPUT_IN_PRIVATE_STATES  = "PublicInput-PrivateStates";
	 private static final String PUBLIC_STATES_IN_PRIVATE_STATES = "PublicStates-PrivateStates";
	 private static final String PRIVATE_STATES_IN_PUBLIC_STATES  = "PrivateStates-PublicStates";
	 private static final String PRIVATE_STATES_IN_OTHER_PRIVATE_STATES  = "PrivateStates-OtherPrivateStates";
	 
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
		
		
		Option policy_opt = new Option("p", "policy", false, "dump the analysis");
		policy_opt.setRequired(false);
		options.addOption(policy_opt);
		
		

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
		
		GraphType dumpOpt = cmd.hasOption(dump_opt) ? GraphType.HTML_WITH_SUBNODES : GraphType.NONE;
		
		String policyPath = cmd.getOptionValue("policy");


		Program program = null;
		EntryPointLoader entryLoader = new EntryPointLoader();
		try {		
			program = GoFrontEnd.processFile(filePath);
		
			entryLoader.addEntryPoints(EntryPointsFactory.getEntryPoints("hyperledger-fabric"));
			entryLoader.load(program);

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

		if(program != null) {
			/*
			if(satisfyPhaseRequirements(program, PRIVATE_INPUT_IN_PUBLIC_STATES))
				runInformationFlowAnalysis(program, entryLoader, outputDir, dumpOpt, PRIVATE_INPUT_IN_PUBLIC_STATES, PrivacySignatures.privateInputs, PrivacySignatures.publicWriteStatesAndResponsesWithCriticalParams);
			else 
				LOG.info("Program does not contains at least a source and sink for phase " + PRIVATE_INPUT_IN_PUBLIC_STATES);
			
		
			if(satisfyPhaseRequirements(program, PUBLIC_INPUT_IN_PRIVATE_STATES))
				runInformationFlowAnalysis(program, entryLoader, outputDir, dumpOpt, PUBLIC_INPUT_IN_PRIVATE_STATES, PrivacySignatures.publicInputs, PrivacySignatures.privateWriteStatesWithCriticalParams);
			else 
				LOG.info("Program does not contains at least a source and sink for phase " + PUBLIC_INPUT_IN_PRIVATE_STATES);
		
			if(satisfyPhaseRequirements(program, PUBLIC_STATES_IN_PRIVATE_STATES))
				runInformationFlowAnalysis(program, entryLoader, outputDir, dumpOpt, PUBLIC_STATES_IN_PRIVATE_STATES, PrivacySignatures.publicReadStates, PrivacySignatures.privateWriteStatesWithCriticalParams);
			else 
				LOG.info("Program does not contains at least a source and sink for phase " + PUBLIC_STATES_IN_PRIVATE_STATES);
			
			if(satisfyPhaseRequirements(program, PRIVATE_STATES_IN_PUBLIC_STATES))
				runInformationFlowAnalysis(program, entryLoader, outputDir, dumpOpt, PRIVATE_STATES_IN_PUBLIC_STATES, PrivacySignatures.privateInputs, PrivacySignatures.publicWriteStatesAndResponsesWithCriticalParams);
			else 
				LOG.info("Program does not contains at least a source and sink for phase " + PRIVATE_STATES_IN_PUBLIC_STATES);
			*/
			if(satisfyPhaseRequirements(program, PRIVATE_STATES_IN_OTHER_PRIVATE_STATES))
				runAnalysesForPrivateInOtherPrivateStates(program, entryLoader, outputDir, dumpOpt, policyPath);	
		}
	}
	
	

	private static void runInformationFlowAnalysis(Program program, EntryPointLoader entryLoader, String outputDir,
			GraphType dumpOpt, String target, Map<Pair<String, CallType>, Set<String>> sources,
			 Map<Pair<String, CallType>, Set<Pair<String, Integer>>> sinks) {
		
		AnnotationSet annotationSet = new CustomTaintAnnotationSet("hyperledger-fabric", sources, sinks);
		
		AnnotationLoader annotationLoader = new AnnotationLoader();
		annotationLoader.addAnnotationSet(annotationSet);
		
		runInformationFlowAnalysis(program, entryLoader, outputDir, dumpOpt, target, annotationLoader, annotationSet, new HashSet<Call>(), new HashSet<Call>());
		
	}
	

	private static void runInformationFlowAnalysis(Program program, EntryPointLoader entryLoader, String outputDir, GraphType dumpOpt, String target, AnnotationLoader annotationLoader, AnnotationSet annotationSet,
			Set<Call> sourcesRaw, Set<Call> sinksRaw) {
		LiSAConfiguration confPhase = new LiSAConfiguration();

		confPhase.jsonOutput = true;

		confPhase.openCallPolicy = new RelaxedOpenCallPolicy() {

			@Override
			public boolean isSourceForTaint(OpenCall call) {
				return (!target.equals(PRIVATE_STATES_IN_OTHER_PRIVATE_STATES) && GetPhaseSourcesSignatures(target).values().stream().anyMatch(set -> set.stream().anyMatch(source ->  call.getTargetName().equals(source))))
						|| matchesWithRawSource(call, sourcesRaw);
			}

			private boolean matchesWithRawSource(OpenCall call, Set<Call> sourcesRaw) {
				for(Call raw : sourcesRaw) {
					if(raw.getLocation().equals(call.getLocation())
							&& raw.getCallType().equals(call.getCallType())
							&& raw.getTargetName().equals(call.getTargetName())
							&& raw.getParameters().length == call.getParameters().length ) 
							return true;
				}
				return false;
			}};
			
			try {
				confPhase.abstractState = new SimpleAbstractState<>(new PointBasedHeap(), new ValueEnvironment<>(new TaintDomainForPrivacyHF()),
						new TypeEnvironment<>(new InferredTypes()));
				confPhase.semanticChecks.add( new PrivacyHFChecker() {

					@Override
					protected void checkSignature(UnresolvedCall call, CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<TaintDomainForPrivacyHF>, TypeEnvironment<InferredTypes>>> tool) {
						if (call != null) {
								
							Map<Pair<String, CallType>, Set<Pair<String, Integer>>> sinksPhase = GetPhaseSinksSignatures(target);
										
						 if(!target.equals(PRIVATE_STATES_IN_OTHER_PRIVATE_STATES))
							for(Pair<String, CallType> key : sinksPhase.keySet()){
								for(Pair<String, Integer> sink : sinksPhase.get(key) ) {
									boolean[] resultsParam = new boolean[call.getParameters().length];
									if(sink.getLeft().equals(call.getTargetName())
											&& call.getParameters().length > sink.getRight() &&
											(!key.getRight().equals(CallType.STATIC) || (key.getRight().equals(CallType.STATIC) && call.getQualifier().equals(key.getLeft())))) {
										for (AnalyzedCFG<
												SimpleAbstractState<PointBasedHeap, ValueEnvironment<TaintDomainForPrivacyHF>,
														TypeEnvironment<InferredTypes>>> result : tool.getResultOf(call.getCFG())) {
											
												AnalysisState<
												SimpleAbstractState<PointBasedHeap, ValueEnvironment<TaintDomainForPrivacyHF>,
														TypeEnvironment<InferredTypes>>> state = result
																.getAnalysisStateAfter(call.getParameters()[sink.getRight()]);
	
												Set<SymbolicExpression> reachableIds = new HashSet<>();
												for (SymbolicExpression e : state.getComputedExpressions())
													try {
														reachableIds
																.addAll(state.getState().reachableFrom(e, call, state.getState()).elements);
														for (SymbolicExpression s : reachableIds) {
															Set<Type> types = state.getState().getRuntimeTypesOf(s, call, state.getState());
								
															if (types.stream().allMatch(t -> t.isInMemoryType() || t.isPointerType()))
																continue;
								
															ValueEnvironment<TaintDomainForPrivacyHF> valueState = state.getState().getValueState();
															if (valueState.eval((ValueExpression) s, call, state.getState())
																	.isTainted())
																resultsParam[sink.getRight()] = true;
														}
												
													} catch (SemanticException e1) {
														LOG.error("Error during the analysis: " + e1.getMessage());
													}
											
										}
									}
									
									buildWarning(tool, call, null, resultsParam);
								}
							}
							
							for(Call raw : sinksRaw) {
								boolean[] resultsParam = new boolean[raw.getParameters().length];
								if(raw.getLocation().equals(call.getLocation())
										&& raw.getCallType().equals(call.getCallType())
										&& raw.getTargetName().equals(call.getTargetName())
										&& raw.getParameters().length == call.getParameters().length ) {
									for( Entry<Pair<String, CallType>, Set<Pair<String, Integer>>> sinks : sinksPhase.entrySet())
										if(call.getCallType().equals(sinks.getKey().getRight())) {
											for(Pair<String, Integer> sink : sinks.getValue()) {
											if(call.getTargetName().equals(sink.getLeft()) 
													&& call.getParameters().length > sink.getValue())
												for (AnalyzedCFG<
														SimpleAbstractState<PointBasedHeap, ValueEnvironment<TaintDomainForPrivacyHF>,
																TypeEnvironment<InferredTypes>>> result : tool.getResultOf(call.getCFG())) {
													
														AnalysisState<
														SimpleAbstractState<PointBasedHeap, ValueEnvironment<TaintDomainForPrivacyHF>,
																TypeEnvironment<InferredTypes>>> state = result
																		.getAnalysisStateAfter(call.getParameters()[sink.getRight()]);
			
														Set<SymbolicExpression> reachableIds = new HashSet<>();
														try {
															for (SymbolicExpression e : state.getComputedExpressions())
																reachableIds
																		.addAll(state.getState().reachableFrom(e, call, state.getState()).elements);
									
															for (SymbolicExpression s : reachableIds) {
																Set<Type> types = state.getState().getRuntimeTypesOf(s, call, state.getState());
									
																if (types.stream().allMatch(t -> t.isInMemoryType() || t.isPointerType()))
																	continue;
									
																ValueEnvironment<TaintDomainForPrivacyHF> valueState = state.getState().getValueState();
																if (valueState.eval((ValueExpression) s, call, state.getState())
																		.isTainted())
																	resultsParam[sink.getRight()] = true;
															}
														
														} catch (SemanticException e1) {
															LOG.error("Error during the analysis: " + e1.getMessage());
														}
													
												}
											}
										}
									
								}
								buildWarning(tool, call, null, resultsParam);
							}
						}
						
					}
					
				});
			} catch (AnalysisSetupException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}



		confPhase.analysisGraphs = dumpOpt;
		

		File outputdir = new File(outputDir, target + informationflowCounter);
		if (!outputdir.exists())
			outputdir.mkdirs();

		confPhase.workdir = outputdir.getAbsolutePath();
		
		annotationLoader.load(program);
		
		Set<CFG> cfgEntryPoints = new HashSet<>();

		if (!entryLoader.isEntryFound()) {
			Set<Triple<CallType, ? extends CodeAnnotation, CodeMemberDescriptor>> appliedAnnotations = annotationLoader
					.getAppliedAnnotations();

			cfgEntryPoints.addAll(EntryPointsUtils.computeEntryPointSetFromPossibleEntryPointsForAnalysis(program,
					appliedAnnotations,sourcesRaw, annotationSet));
			for (CFG c : cfgEntryPoints)
				program.addEntryPoint(c);

		}
		
		
		if (!program.getEntryPoints().isEmpty()) {
			confPhase.interproceduralAnalysis = new ContextBasedAnalysis<>(FullStackToken.getSingleton());
			confPhase.callGraph = new RTACallGraph();
		} else
			LOG.info("Entry points not found in for this phase " + target);
		
		
		LiSA lisa = new LiSA(confPhase);
		LiSAReport lisaReport = null;
		
		try {
			lisaReport = lisa.run(program);
		} catch (Exception e) {
			// an error occurred during the analysis
			e.printStackTrace();
			return;
		}
		
		if (lisaReport != null && !lisaReport.getWarnings().isEmpty()) {
			System.out.println("The analysis in the phase " + target + " has generated the following warnings:");
			for (Warning warn : lisaReport.getWarnings())
				System.out.println(warn);
		}

		 
		annotationLoader.unload(program);
		 
		removeSpecificAnalysisEntrypoints(program, cfgEntryPoints);

		informationflowCounter++;
		
	}


	private static void removeSpecificAnalysisEntrypoints(Program program, Set<CFG> cfgEntryPoints) {
		for(CFG cfg : cfgEntryPoints) {
			program.getEntryPoints().remove(cfg);
		}
		
	}

	private static void runAnalysesForPrivateInOtherPrivateStates(Program program, EntryPointLoader entryLoader, String outputDir,
			GraphType dumpOpt, String policyPath) {
		

		
		// STRING ANALYSIS
		Pair<Map<Call, Tarsis>, Map<Call, Tarsis>> res = runStringAnalysis(program, entryLoader, outputDir, dumpOpt);
		
		Map<Call, Tarsis> collectionsReadPrivateState = res.getLeft();
		Map<Call, Tarsis> collectionsWritePrivateState = res.getRight();
		
		List<PrivateDataPolicy> policies = policyPath != null ? JSONPrivateDataCollectionPolicyParser.parsePolicies(policyPath) : new ArrayList<>();
		Collection<Pair<Set<Call>, Set<Call>>> conflicts = extractPossiblePrivateCollectionConflicts(collectionsReadPrivateState,collectionsWritePrivateState, policies);
		
		for(Pair<Set<Call>, Set<Call>> c : conflicts) {
			if(!c.getLeft().isEmpty() && !c.getRight().isEmpty()) {
				 runInformationFlowAnalysis(program, entryLoader, outputDir, dumpOpt, PRIVATE_STATES_IN_OTHER_PRIVATE_STATES, c.getLeft(), c.getRight());
			}
		}
		
	}


	private static int informationflowCounter= 0;
	
	private static void runInformationFlowAnalysis(Program program, EntryPointLoader entryLoader, String outputDir,
			GraphType dumpOpt, String target, Set<Call> sources, Set<Call> sinks) {
		
		Set<Triple<CallType, ? extends CodeAnnotation, CodeMemberDescriptor>> specificCodeMemberAnnotations = new HashSet<>() ;
		
		Set<Call> sourcesTocheckRawData = new HashSet<>();
		for(Call source : sources) {
			Set<CodeMemberDescriptor> descriptors = getCodeMemberDescriptors(source);
			if(descriptors.isEmpty())
				sourcesTocheckRawData.add(source);
			else
				for(CodeMemberDescriptor d : descriptors) {
					specificCodeMemberAnnotations.add(Triple.of(source.getCallType(), new MethodAnnotation(TaintDomainForPrivacyHF.TAINTED_ANNOTATION, d.getUnit().getName(), d.getName()), d));
				}
		}
		
		Set<Call> sinksTocheckRawData = new HashSet<>();
		for(Call sink : sinks) {
			Set<CodeMemberDescriptor> descriptors = getCodeMemberDescriptors(sink);
			if(descriptors.isEmpty())
				sinksTocheckRawData.add(sink);
			else
				for(CodeMemberDescriptor d : descriptors) {
					for( Entry<Pair<String, CallType>, Set<Pair<String, Integer>>> e : PrivacySignatures.privateWriteStatesWithCriticalParams.entrySet())
						if(sink.getCallType().equals(e.getKey().getRight())) {
							if(sink.getCallType().equals(CallType.INSTANCE) 
									|| (sink.getCallType().equals(CallType.STATIC) && d.getUnit().getName().equals(e.getKey().getLeft())))
								for(Pair<String, Integer> sign : e.getValue()) {
									if(sign.getKey().equals(d.getName())
											&& d.getFormals().length > sign.getValue())
										specificCodeMemberAnnotations.add(Triple.of(sink.getCallType(), new MethodParameterAnnotation(PrivacyHFChecker.SINK_ANNOTATION, d.getUnit().getName(), d.getName(), sign.getValue()), d));
								}
						}
				}
		}
		
		AnnotationSet annotationSet = new CustomTaintAnnotationSet("hyperledger-fabric", new HashMap<>(), new HashMap<>());
		
		AnnotationLoader annotationLoader = new AnnotationLoader();
		annotationLoader.addSpecificCodeMemberAnnotations(specificCodeMemberAnnotations);
		
		runInformationFlowAnalysis(program, entryLoader, outputDir, dumpOpt, target, annotationLoader, annotationSet, sourcesTocheckRawData, sinksTocheckRawData);
		
	}



	private static Set<CodeMemberDescriptor> getCodeMemberDescriptors(Call call) {
		Set<CodeMemberDescriptor> descriptors = new HashSet<>();
		if (call instanceof NativeCall) {
			NativeCall nativeCfg = (NativeCall) call;
			Collection<CodeMember> nativeCfgs = nativeCfg.getTargets();
			for (CodeMember n : nativeCfgs) {
				descriptors.add(n.getDescriptor());

			}
		} else if (call instanceof CFGCall) {
			CFGCall cfg = (CFGCall) call;
			for (CodeMember n : cfg.getTargets()) {
				descriptors.add(n.getDescriptor());
			}
		} 
		return descriptors;
	}


	private static Collection<Pair<Set<Call>, Set<Call>>> extractPossiblePrivateCollectionConflicts(Map<Call, Tarsis>  collectionsReadPrivateState,
			Map<Call, Tarsis> collectionsWritePrivateState, List<PrivateDataPolicy> policies) {
		Collection<Pair<Set<Call>, Set<Call>>> res = new ArrayList<>();
		
		Map<Tarsis, Set<Call>> readerSetsMapping = new HashMap<>();
		
		for(Call key1 : collectionsReadPrivateState.keySet()) {
			Set<Call> similarCollections = new HashSet<>();
			similarCollections.add(key1);
			Tarsis stringValue1 = collectionsReadPrivateState.get(key1);
			
			if(stringValue1 != null) {
				for(Call key2 :collectionsReadPrivateState.keySet()) {
					Tarsis stringValue2 = collectionsReadPrivateState.get(key2);
					if(stringValue2 != null && !readerSetsMapping.values().stream().anyMatch(s -> s.contains(key1)) 
						&& !key1.equals(key2)
						&& !stringValue1.isTop() && !stringValue2.isTop() 
						 && stringValue1.getAutomaton().isEqualTo(stringValue2.getAutomaton())) {
						similarCollections.add(key2);
					}
				}
				
				if(stringValue1.isTop()) {
					readerSetsMapping.putIfAbsent(new Tarsis(), new HashSet<Call>());
					readerSetsMapping.get(new Tarsis()).add(key1);
				} else 			
					readerSetsMapping.put(stringValue1, similarCollections);
			}
		}
		
		Map<Tarsis, Set<Call>> writerSetsMapping = new HashMap<>();
		
		for(Call key1 : collectionsWritePrivateState.keySet()) {
			Set<Call> similarCollections = new HashSet<>();
			similarCollections.add(key1);
			Tarsis stringValue1 = collectionsWritePrivateState.get(key1);
			if(stringValue1 != null) {
				for(Call key2 :collectionsWritePrivateState.keySet()) {
					Tarsis stringValue2 = collectionsWritePrivateState.get(key2);
					if(stringValue2 != null && !writerSetsMapping.values().stream().anyMatch(s -> s.contains(key1)) 
						&& !key1.equals(key2)
						&& !stringValue1.isTop() && !stringValue2.isTop() 
						&& stringValue1.getAutomaton().isEqualTo(stringValue2.getAutomaton())) {
						similarCollections.add(key2);
					}
				}
				
				if(stringValue1.isTop()) {
					writerSetsMapping.putIfAbsent(new Tarsis(), new HashSet<Call>());
					writerSetsMapping.get(new Tarsis()).add(key1);
				} else
					writerSetsMapping.put(stringValue1, similarCollections);
			}
		}
		
		for(Tarsis stringValueReaders : readerSetsMapping.keySet()) {
			Set<Call> writers = new HashSet<>();
			if(stringValueReaders.isTop()) {
				for(Set<Call> ww : writerSetsMapping.values())
					writers.addAll(ww);
			} else {
				for(Tarsis stringValueWriters : writerSetsMapping.keySet()) {
					if(stringValueWriters.isTop() 
							|| !stringValueReaders.getAutomaton().isEqualTo(stringValueWriters.getAutomaton()))
						writers.addAll(writerSetsMapping.get(stringValueWriters));
				}
			}
			if(!writers.isEmpty())
				res.add(Pair.of(readerSetsMapping.get(stringValueReaders), writers));
		}
		
		
		return res;
	}

	private static Pair<Map<Call, Tarsis>, Map<Call, Tarsis>> runStringAnalysis(Program program, EntryPointLoader entryLoader, String outputDir,
			GraphType dumpOpt) {
		LiSAConfiguration confStringAnalysis = new LiSAConfiguration();

		confStringAnalysis.openCallPolicy = new RelaxedOpenCallPolicy() {

			@Override
			public boolean isSourceForTaint(OpenCall call) {
				return false;
			}
			
		};
		
		
		File outputdir = new File(outputDir, "Tarsis");
		if (!outputdir.exists())
			outputdir.mkdirs();

		confStringAnalysis.workdir = outputdir.getAbsolutePath();
		confStringAnalysis.analysisGraphs = GraphType.HTML_WITH_SUBNODES;
		
		
		AnnotationSet annotationSet = new CustomTaintAnnotationSet("hyperledger-fabric", PrivacySignatures.privateReadStates, PrivacySignatures.privateWriteStatesWithCriticalParams);
		
		AnnotationLoader annotationLoader = new AnnotationLoader();
		annotationLoader.addAnnotationSet(annotationSet);
		annotationLoader.load(program);
		
		Set<CFG> cfgEntryPoints = new HashSet<>();

		if (!entryLoader.isEntryFound()) {
			Set<Triple<CallType, ? extends CodeAnnotation, CodeMemberDescriptor>> appliedAnnotations = annotationLoader
					.getAppliedAnnotations();

			cfgEntryPoints.addAll(EntryPointsUtils.computeEntryPointSetFromPossibleEntryPointsForAnalysis(program,
					appliedAnnotations, null ,annotationSet)); // the idea is to add entry points where there are private read states and private write states
			for (CFG c : cfgEntryPoints)
				program.addEntryPoint(c);

		}
		
		
		if (!program.getEntryPoints().isEmpty()) {
			confStringAnalysis.interproceduralAnalysis = new ContextBasedAnalysis<>(FullStackToken.getSingleton());
			confStringAnalysis.callGraph = new RTACallGraph();
		} else
			LOG.info("Entry points not found in for this phase String Analysis" );
		
		
		ComputePrivateCollectionFromStatementsChecker stringAnalysis = new ComputePrivateCollectionFromStatementsChecker();
		try {
			confStringAnalysis.abstractState = new SimpleAbstractState<>(new PointBasedHeap(), new ValueEnvironment<>(new Tarsis()),
					new TypeEnvironment<>(new InferredTypes()));
			confStringAnalysis.semanticChecks.add(stringAnalysis);
		} catch (AnalysisSetupException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}

		LiSA lisa = new LiSA(confStringAnalysis);

		try {
			lisa.run(program);
		} catch (Exception e) {
			// an error occurred during the analysis
			e.printStackTrace();
			return Pair.of(new HashMap<>(), new HashMap<>());
		}
		
		annotationLoader.unload(program);
		removeSpecificAnalysisEntrypoints(program, cfgEntryPoints);
		
		return Pair.of(stringAnalysis.getReadPrivateStatesInstructions(), stringAnalysis.getWritePrivateStatesInstructions());
		
	}

	private static boolean satisfyPhaseRequirements(Program program, String phase) {
			
		switch(phase) {
		case PRIVATE_INPUT_IN_PUBLIC_STATES:
			if(countCallsMatchingSignatures(program, PrivacySignatures.privateInputs) > 0 && countCallsMatchingSignatures(program, PrivacySignatures.publicWriteStatesAndResponses) > 0 )
				return true;
			break;
		case PUBLIC_INPUT_IN_PRIVATE_STATES:
			if(countCallsMatchingSignatures(program, PrivacySignatures.publicInputs) > 0 && countCallsMatchingSignatures(program, PrivacySignatures.privateWriteStates) > 0 )
				return true;
			break;
		case PUBLIC_STATES_IN_PRIVATE_STATES:
			if(countCallsMatchingSignatures(program, PrivacySignatures.publicReadStates) > 0 && countCallsMatchingSignatures(program, PrivacySignatures.privateWriteStates) > 0 )
				return true;
			break;
		case PRIVATE_STATES_IN_PUBLIC_STATES:
			if(countCallsMatchingSignatures(program, PrivacySignatures.privateReadStates) > 0 && countCallsMatchingSignatures(program, PrivacySignatures.publicWriteStatesAndResponses) > 0 )
				return true;
			break;
		case PRIVATE_STATES_IN_OTHER_PRIVATE_STATES:
			if(countCallsMatchingSignatures(program, PrivacySignatures.privateReadStates) > 0 && countCallsMatchingSignatures(program, PrivacySignatures.privateWriteStates) > 0 )
				return true;
			break;
		default:
			throw new IllegalArgumentException(phase + " is currently a not supported phase");
		}
		return false;
	}

	private static int countCallsMatchingSignatures(Program program, Map<Pair<String, CallType>, Set<String>> signatures) {

		int res = 0;

		SignatureDescriptorMatcher matcher;
		
		for (CFG cfg : program.getAllCFGs()) {
			LinkedList<Statement> possibleEntries = new LinkedList<>();
			matcher = new SignatureDescriptorMatcher(signatures);
			cfg.accept(matcher, possibleEntries);
			if (matcher.isMatched())
				res += matcher.matches;
		}

		for (Unit unit : program.getUnits())
			for (CodeMember cfg : unit.getCodeMembers()) {
				if (cfg instanceof CFG) {
					LinkedList<Statement> possibleEntries = new LinkedList<>();
					matcher = new SignatureDescriptorMatcher(signatures);
					((CFG) cfg).accept(matcher, possibleEntries);
					
					if (matcher.isMatched())
						res += matcher.matches;
				}
			}
		return res;

	}

	private static class SignatureDescriptorMatcher
			implements GraphVisitor<CFG, Statement, Edge, Collection<Statement>> {

		final Map<Pair<String, CallType>, Set<String>> signatures;
		
		private int matches;
		
		public boolean isMatched() {
			return matches > 0;
		}

		public SignatureDescriptorMatcher(Map<Pair<String, CallType>, Set<String>> signatures) {
			this.signatures = signatures;
		}

		@Override
		public boolean visit(Collection<Statement> tool, CFG graph) {
			
			Function<Statement, Boolean> condition = new Function<Statement, Boolean>() {
				
				@Override
				public Boolean apply(Statement t) {
					if (t instanceof Call) {
						Call c = (Call) t;
						if(c.getCallType() == CallType.STATIC) {
							if(signatures.entrySet().stream().anyMatch(set -> set.getValue().stream()
											.anyMatch(s -> 
											(c.getFullTargetName()).equals(set.getKey()+"::"+s) //qualifier::targetName
														))) {
								return true;
							}
						} else if(c.getCallType() == CallType.INSTANCE) {
							if(signatures.entrySet().stream().anyMatch(set -> set.getValue().stream()
									.anyMatch(s -> 
									(c.getTargetName()).equals(s) //targetName
												))) {
								return true;
							}
						} if (c.getCallType() == CallType.UNKNOWN) {
							if(signatures.entrySet().stream().anyMatch(set -> set.getValue().stream()
									.anyMatch(s -> 
									(c.getFullTargetName()).equals(set.getKey()+"::"+s) || c.getTargetName().equals(s)))) {
								return true;
							}
						}
					
					}
					return false;
				}
				
			};
			
			matches += CFGUtils.countMatchInCFGNodes(graph, condition);
			
			return true;
		}

		@Override
		public boolean visit(Collection<Statement> tool, CFG graph, Statement node) {
			return true;
		}

		@Override
		public boolean visit(Collection<Statement> tool, CFG graph, Edge edge) {
			return true;
		}
		
	}
	
	private static Map<Pair<String, CallType>, Set<String>> GetPhaseSourcesSignatures(String phase) {
		
		switch(phase) {
		case PRIVATE_INPUT_IN_PUBLIC_STATES:
				return PrivacySignatures.privateInputs;
		case PUBLIC_INPUT_IN_PRIVATE_STATES:
				return PrivacySignatures.publicInputs;
		case PUBLIC_STATES_IN_PRIVATE_STATES:
				return PrivacySignatures.publicReadStates;
		case PRIVATE_STATES_IN_PUBLIC_STATES:
				return PrivacySignatures.privateReadStates;
		case PRIVATE_STATES_IN_OTHER_PRIVATE_STATES:
				return PrivacySignatures.privateReadStates;
		default:
			throw new IllegalArgumentException(phase + " is currently a not supported phase");
		}
	}
	
private static Map<Pair<String, CallType>, Set<Pair<String, Integer>>> GetPhaseSinksSignatures(String phase) {
		
		switch(phase) {
		case PRIVATE_INPUT_IN_PUBLIC_STATES:
		case PRIVATE_STATES_IN_PUBLIC_STATES:
				return PrivacySignatures.publicWriteStatesAndResponsesWithCriticalParams;
		case PUBLIC_INPUT_IN_PRIVATE_STATES:
		case PUBLIC_STATES_IN_PRIVATE_STATES:
		case PRIVATE_STATES_IN_OTHER_PRIVATE_STATES:
			return PrivacySignatures.privateWriteStatesWithCriticalParams;
		default:
			throw new IllegalArgumentException(phase + " is currently a not supported phase");
		}
	}

}