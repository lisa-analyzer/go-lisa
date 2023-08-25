package it.unive.golisa.checker.readwrite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

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
import it.unive.lisa.checks.semantic.SemanticCheck;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeMember;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.call.CFGCall;
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.program.cfg.statement.call.Call.CallType;
import it.unive.lisa.program.cfg.statement.call.NativeCall;
import it.unive.lisa.program.cfg.statement.call.UnresolvedCall;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.ValueExpression;



/**
 * A Go Checker for Read-Write Set Issues of Hyperledger Fabric.
 * 
 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
 */
public class ReadWritePairChecker implements
		SemanticCheck<
				SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>,
				PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>> {
	

	
	private Set<AnalysisReadWriteHFInfo> writers;
	private Set<AnalysisReadWriteHFInfo> readers;
	
	private Set<Pair<AnalysisReadWriteHFInfo, AnalysisReadWriteHFInfo>> readAfterWriteCandidates;
	private Set<Pair<AnalysisReadWriteHFInfo, AnalysisReadWriteHFInfo>> overWriteCandidates;
	
	public Set<Pair<AnalysisReadWriteHFInfo, AnalysisReadWriteHFInfo>> getReadAfterWriteCandidates() {
		return readAfterWriteCandidates;
	}
	
	public Set<Pair<AnalysisReadWriteHFInfo, AnalysisReadWriteHFInfo>> getOverWriteCandidates() {
		return overWriteCandidates;
	}
	
	@Override
	public void beforeExecution(CheckToolWithAnalysisResults<
			SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>,
			PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>> tool) {
		writers = new HashSet<>();
		readers = new HashSet<>();
	}

	@Override
	public void afterExecution(
			CheckToolWithAnalysisResults<
					SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>,
					PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>> tool) {

		readAfterWriteCandidates = computeReadAfterWriteCandidates();
		overWriteCandidates = computeOverWriteCandidates();
	}

	private Set<Pair<AnalysisReadWriteHFInfo, AnalysisReadWriteHFInfo>> computeReadAfterWriteCandidates(){
		
		Set<Pair<AnalysisReadWriteHFInfo, AnalysisReadWriteHFInfo>> res = new HashSet<>();
		
		for(AnalysisReadWriteHFInfo w : writers) {
			Set<Tarsis> wkeyValues = w.getKeyValues().get(0);
			for(AnalysisReadWriteHFInfo r : readers) {
				boolean found = false;
				switch(r.getInfo().getKeyType()) {
					case SINGLE:
						Set<Tarsis> rkeyValues = r.getKeyValues().get(0);
						for(Tarsis wkvState : wkeyValues) {
							if(found)
								break;
							for(Tarsis rkvState : rkeyValues) {
								if(wkvState.isTop() || rkvState.isTop()
									|| extractValueStringFromTarsisStates(rkvState)
									.equals(extractValueStringFromTarsisStates(wkvState))) { //TODO: find a more elegant way to check key values using Tarsis
									res.add(Pair.of(w,r));
									found = true;
									break;
								}
							}
						}
						break;
					case RANGE:
						Set<Tarsis> startKeyValue = r.getKeyValues().get(0);
						Set<Tarsis> endKeyValue = r.getKeyValues().get(1);
						for(Tarsis wkvState : wkeyValues) {
							if(found)
								break;
							for(Tarsis rstartKeyValueState : startKeyValue) {
								for(Tarsis rendKeyValueState : endKeyValue) {
									if(wkvState.isTop() || rendKeyValueState.isTop()
										|| ( rstartKeyValueState.isTop() ? 
												"".compareTo(extractValueStringFromTarsisStates(wkvState)) <= 0 && extractValueStringFromTarsisStates(wkvState).compareTo(extractValueStringFromTarsisStates(rendKeyValueState)) < 0
										: extractValueStringFromTarsisStates(rstartKeyValueState).compareTo(extractValueStringFromTarsisStates(wkvState)) <= 0 && extractValueStringFromTarsisStates(wkvState).compareTo(extractValueStringFromTarsisStates(rendKeyValueState)) < 0)
										) { //TODO: find a more elegant way to check key values using Tarsis
										res.add(Pair.of(w,r));
										found = true;
										break;
									}
								}
							}
						}
						break;
					case COMPOSITE:
						Set<Tarsis> rcompositeKeyValues = r.getKeyValues().get(0);
						for(Tarsis wkvState : wkeyValues) {
							if(found)
								break;
							for(Tarsis rkvState : rcompositeKeyValues) {
								if(wkvState.isTop() || rkvState.isTop()
									|| rkvState.toString().contains(wkvState.toString())) { //TODO: find a more elegant way to check key values using Tarsis
									res.add(Pair.of(w,r));
									found = true;
									break;
								}
							}
						}
						break;
					default:
						throw new IllegalArgumentException("The following key type is not handled: " + r.getInfo().getKeyType());
				}
			}
		}
		
		return res;
	}

	private Set<Pair<AnalysisReadWriteHFInfo, AnalysisReadWriteHFInfo>> computeOverWriteCandidates() {
		
		Set<Pair<AnalysisReadWriteHFInfo, AnalysisReadWriteHFInfo>> res = new HashSet<>();
		
		for(AnalysisReadWriteHFInfo w1 : writers)
			for(AnalysisReadWriteHFInfo w2 : writers) {
				if(!w1.equals(w2)) {
					Set<Tarsis> w1keyValues = w1.getKeyValues().get(0);
					Set<Tarsis> w2keyValues = w2.getKeyValues().get(0);
					boolean found = false;
					for(Tarsis w1kvState : w1keyValues) {
						if(found)
							break;
						for(Tarsis w2kvState : w2keyValues) {
							if(w1kvState.isTop() || w2kvState.isTop()
								|| extractValueStringFromTarsisStates(w1kvState).equals(extractValueStringFromTarsisStates(w2kvState))) { //TODO: find a more elegant way to check key values using Tarsis
								res.add(Pair.of(w1,w2));
								found = true;
								break;
							}
						}
					}
				}
			}
		
		return res;
	}
	
	@Override
	public void visitGlobal(
			CheckToolWithAnalysisResults<
					SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>,
					PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>> tool,
			Unit unit, Global global, boolean instance) {
	}

	@Override
	public boolean visit(CheckToolWithAnalysisResults<
			SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>,
			PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>> tool, CFG graph) {
		return true;
	}

	@Override
	public boolean visit(
			CheckToolWithAnalysisResults<
					SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>,
					PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>> tool,
			CFG graph, Statement node) {
		if (!(node instanceof UnresolvedCall))
			return true;

		UnresolvedCall call = (UnresolvedCall) node;
		if(ReadWriteHFUtils.isReadOrWriteCall(call)) {
			try {
				ReadWriteInfo info = ReadWriteHFUtils.getReadWriteInfo(call);
				int[] keyParams = info.getKeyParameters();
				
				for (AnalyzedCFG<
						SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>,
						PointBasedHeap, ValueEnvironment<Tarsis>,
						TypeEnvironment<InferredTypes>> result : tool.getResultOf(call.getCFG())) {
					Call resolved = (Call) tool.getResolvedVersion(call, result);
					ArrayList<Set<Tarsis>> keyValues = new ArrayList<>();
					
					if (resolved instanceof NativeCall) {
						NativeCall nativeCfg = (NativeCall) resolved;
						Collection<CodeMember> nativeCfgs = nativeCfg.getTargets();
						for (CodeMember n : nativeCfgs) {
							Parameter[] parameters = n.getDescriptor().getFormals();
							keyValues = extractKeyValues(call, keyParams, parameters.length, node, result);
						}
					} else if (resolved instanceof CFGCall) {
						CFGCall cfg = (CFGCall) resolved;
						
						for (CodeMember n : cfg.getTargets()) {
							Parameter[] parameters = n.getDescriptor().getFormals();
							keyValues = extractKeyValues(call, keyParams, parameters.length, node, result);
						}
					} else {
						keyValues = extractKeyValues(call, keyParams, call.getParameters().length, node, result);
					}
					
					AnalysisReadWriteHFInfo infoForAnalysis = new AnalysisReadWriteHFInfo(call, info, keyValues);
					
					if(ReadWriteHFUtils.isReadCall(call))
						readers.add(infoForAnalysis);
					else if(ReadWriteHFUtils.isWriteCall(call))
						writers.add(infoForAnalysis);
				}
				
				
			} catch (SemanticException e) {
				System.err.println("Cannot check " + node);
				e.printStackTrace(System.err);
			}
		}
				
		return true;
	}

	private String extractValueStringFromTarsisStates(Tarsis state) {
		if(state.getAutomaton().emptyString().equals(state.getAutomaton()))
			return "";
		else
			return state.getAutomaton().toString();
	}
	

	private ArrayList<Set<Tarsis>> extractKeyValues(UnresolvedCall call, int[] keyParams, int parametersLength, Statement node, AnalyzedCFG<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>, PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>> result) throws SemanticException {
		
		ArrayList<Set<Tarsis>> valStringDomain = new ArrayList<>(keyParams.length);
		
		for(int i=0; i < keyParams.length; i++) {
			int par = call.getCallType().equals(CallType.STATIC) ? keyParams[i] : keyParams[i]+1;
			valStringDomain.add(new HashSet<>());
			if(par < parametersLength) {
				
				AnalysisState<
				SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>,
						TypeEnvironment<InferredTypes>>,
				PointBasedHeap, ValueEnvironment<Tarsis>,
				TypeEnvironment<InferredTypes>> state = result
						.getAnalysisStateAfter(call.getParameters()[par]);
				for (SymbolicExpression stack : state.rewrite(state.getComputedExpressions(), node)) {
					valStringDomain.get(i).add(state.getState().getValueState().eval((ValueExpression) stack, node));
				}
			}
		}
		
		return valStringDomain;
		
	}

	@Override
	public boolean visit(
			CheckToolWithAnalysisResults<
					SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>,
					PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>> tool,
			CFG graph, Edge edge) {
		return true;
	}

	@Override
	public boolean visitUnit(
			CheckToolWithAnalysisResults<
					SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>,
					PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>> tool,
			Unit unit) {
		return true;
	}
}
