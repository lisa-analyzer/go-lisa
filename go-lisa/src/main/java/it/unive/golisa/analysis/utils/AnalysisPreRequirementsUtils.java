package it.unive.golisa.analysis.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import it.unive.golisa.cfg.expression.binary.GoChannelSend;
import it.unive.golisa.cfg.expression.binary.GoDiv;
import it.unive.golisa.cfg.expression.unary.GoChannelReceive;
import it.unive.golisa.cfg.expression.unary.GoRange;
import it.unive.golisa.cfg.statement.GoRoutine;
import it.unive.golisa.cfg.utils.CFGUtils;
import it.unive.golisa.golang.api.signature.FuncGoLangApiSignature;
import it.unive.golisa.golang.util.GoLangAPISignatureLoader;
import it.unive.golisa.loader.annotation.sets.GoNonDeterminismAnnotationSet;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeMember;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.util.datastructures.graph.GraphVisitor;

/**
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class AnalysisPreRequirementsUtils {


	public static boolean satisfyPrerequirements(Program program, String analysis) {
			switch (analysis) {

			case "non-determinism":
			case "non-determinism-ni":
				// at least a non-det function	or goroutine or channel or global variables
				InputStream input = GoNonDeterminismAnnotationSet.class
				.getResourceAsStream("/for-analysis/nondeterm_sources.txt");

				Map<String, Set<String>> nondetcalls = new HashMap<>();
		
				GoLangAPISignatureLoader loader;
				try {
					loader = new GoLangAPISignatureLoader(input);
					
					for (Entry<String, ? extends Set<FuncGoLangApiSignature>> e : loader.getFunctionAPIs().entrySet())
						for (FuncGoLangApiSignature sig : e.getValue()) {
							nondetcalls.putIfAbsent(e.getKey(), new HashSet<>());
							nondetcalls.get(e.getKey()).add(sig.getName());
						}
				} catch (IOException e) {
					e.printStackTrace();
				}
		
		
				if( countCallsMatchingSignatures(program, nondetcalls) > 0 
						|| countStatementsMatchingType(program, GoRoutine.class, GoRange.class, GoChannelSend.class, GoChannelReceive.class)  > 0
						|| program.getGlobals().size() > 0)
					return true;
				//break;
			case "phantom-read":
				// at least a range query
				Map<String, Set<String>> rangequeries = new HashMap<>();
				rangequeries.put("ChaincodeStub", Set.of("GetQueryResult", "GetHistoryForKey", "GetPrivateDataQueryResult"));
				rangequeries.put("ChaincodeStubInterface",
						Set.of("GetQueryResult", "GetHistoryForKey", "GetPrivateDataQueryResult"));
				
				if(countCallsMatchingSignatures(program, rangequeries) > 0)
					return true;
				break;
			case "ucci":
				// at least a cross-contract invocation
				Map<String, Set<String>> CCIs = new HashMap<>();
				CCIs.put("ChaincodeStub", Set.of("InvokeChaincode"));
				CCIs.put("ChaincodeStubInterface", Set.of("InvokeChaincode"));
				if(countCallsMatchingSignatures(program, CCIs) > 0)
					return true;
				break;
			case "dcci":
				// at least two cross-contract invocation
				Map<String, Set<String>> dCCIs = new HashMap<>();
				dCCIs.put("ChaincodeStub", Set.of("InvokeChaincode"));
				dCCIs.put("ChaincodeStubInterface", Set.of("InvokeChaincode"));
				if(countCallsMatchingSignatures(program, dCCIs) > 1)
					return true;
				break;
			case "div-by-zero":
				if( countStatementsMatchingType(program, GoDiv.class) > 0)
					return true;
			case "read-write":
			case "unhandled-errors":
			case "numerical-issues":
				//no particular pre-requirements
				return true;
			default:
				throw new IllegalArgumentException("No pre-requirements set for the analysis \""+analysis+"\"");
			}
			
			return false;
	}

	private static int countStatementsMatchingType(Program program, Class<?> ...classes ) {
		int res = 0;

		ClassDescriptorMatcher matcher;
		
		for (CFG cfg : program.getAllCFGs()) {
			LinkedList<Statement> possibleEntries = new LinkedList<>();
			matcher = new ClassDescriptorMatcher(classes);
			cfg.accept(matcher, possibleEntries);
			if (matcher.isMatched())
				res += matcher.matches;
		}

		for (Unit unit : program.getUnits())
			for (CodeMember cfg : unit.getCodeMembers()) {
				if (cfg instanceof CFG) {
					LinkedList<Statement> possibleEntries = new LinkedList<>();
					matcher = new ClassDescriptorMatcher(classes);
					((CFG) cfg).accept(matcher, possibleEntries);
					
					if (matcher.isMatched())
						res += matcher.matches;
				}
			}
		return res;
	}

	private static int countCallsMatchingSignatures(Program program, Map<String, Set<String>> signatures) {

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

		final Map<String, Set<String>> signatures;
		
		private int matches;
		
		public boolean isMatched() {
			return matches > 0;
		}

		public SignatureDescriptorMatcher(Map<String, Set<String>> signatures) {
			this.signatures = signatures;
		}

		@Override
		public boolean visit(Collection<Statement> tool, CFG graph) {
			
			Function<Statement, Boolean> condition = new Function<Statement, Boolean>() {
				
				@Override
				public Boolean apply(Statement t) {
					if (t instanceof Call)
						if(signatures.entrySet().stream().anyMatch(set -> set.getValue().stream()
										.anyMatch(s -> (((Call) t).getFullTargetName()).equals(set.getKey()+"::"+s) //qualifier::targetName
													))) {
							return true;
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
	

	private static class ClassDescriptorMatcher
			implements GraphVisitor<CFG, Statement, Edge, Collection<Statement>> {

		final Class<?>[] classes;
		
		private int matches;
		
		public boolean isMatched() {
			return matches > 0;
		}

		public ClassDescriptorMatcher( Class<?> ...classes) {
			this.classes = classes;
		}

		@Override
		public boolean visit(Collection<Statement> tool, CFG graph) {
			
			Function<Statement, Boolean> condition = new Function<Statement, Boolean>() {
				
				@Override
				public Boolean apply(Statement t) {
					for(Class<?> c : classes)
						if (t.getClass() == c || t.getClass().isInstance(c))
							return true;
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
}
