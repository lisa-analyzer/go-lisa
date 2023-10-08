package it.unive.golisa.checker.readwrite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import it.unive.golisa.cfg.CFGUtils;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.AnalyzedCFG;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.nonrelational.value.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.string.tarsis.RegexAutomaton;
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
				if(!matchCollection(r,w))
					continue;
				
				boolean found = false;
				switch(r.getInfo().getKeyType()) {
					case SINGLE:
						Set<Tarsis> rkeyValues = r.getKeyValues().get(0);
						for(Tarsis wkvState : wkeyValues) {
							if(found)
								break;
							for(Tarsis rkvState : rkeyValues) {
								if(wkvState.isTop() || rkvState.isTop()
									|| possibleEqualsMatch(rkvState,wkvState)) {
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
										|| possibleRangeMatch(wkvState, rstartKeyValueState, rendKeyValueState)) {
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
									|| possibleCompositeMatch(rkvState, wkvState)) {
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

	private boolean possibleCompositeMatch(Tarsis rkvState, Tarsis wkvState) {
		String prefixValue = extractValueStringFromTarsisStates(rkvState);
		String value = extractValueStringFromTarsisStates(wkvState);
		if(prefixValue != null && value != null)
			return value.contains(prefixValue);
		return true;
	}

	private boolean possibleRangeMatch(Tarsis wkvState, Tarsis rstartKeyValueState, Tarsis rendKeyValueState) {
		if(! rendKeyValueState.getAutomaton().isEqualTo(RegexAutomaton.emptyStr())) {
			String value = extractValueStringFromTarsisStates(wkvState);
			if(value != null) {
				boolean l = true;
				boolean u = true;
				
				if(!rstartKeyValueState.isTop()) {
					String lBoundValue = extractValueStringFromTarsisStates(rstartKeyValueState);
					
					if(lBoundValue != null)
						l = lBoundValue.compareTo(value) <= 0; 
				}
				
				String uBoundValue = extractValueStringFromTarsisStates(rendKeyValueState);
				if(uBoundValue != null)
					u = value.compareTo(uBoundValue) < 0;
				
				return l && u;
			}
		}
		return true; 
	}

	private boolean possibleEqualsMatch(Tarsis state1, Tarsis state2) {
		return state1.getAutomaton().isEqualTo(state2.getAutomaton()) 
				|| state1.getAutomaton().isContained(state2.getAutomaton()) 
					|| state2.getAutomaton().isContained(state1.getAutomaton());
	}

	private boolean matchCollection(AnalysisReadWriteHFInfo st1, AnalysisReadWriteHFInfo st2) {

		ReadWriteInfo st1Info = st1.getInfo();
		ReadWriteInfo st2Info = st2.getInfo();
		
		if((st1Info.hasCollection() && !st2Info.hasCollection())
			|| (!st1Info.hasCollection() && st2Info.hasCollection()))
			return false;
			
		if(st1.getInfo().hasCollection() && st2.getInfo().hasCollection()) {
			Set<Tarsis> c1Values = st1.getCollectionValues();
			Set<Tarsis> c2Values = st2.getCollectionValues();
			for(Tarsis c1ValueState : c1Values) {
				for(Tarsis c2ValuesState : c2Values) {
					if(c1ValueState.isTop() || c2ValuesState.isTop()
						|| possibleEqualsMatch(c2ValuesState, c1ValueState)) {
						return true;
					}
				}
			}
			
			return false;
		}
		
		return true;
	}

	private Set<Pair<AnalysisReadWriteHFInfo, AnalysisReadWriteHFInfo>> computeOverWriteCandidates() {
		
		Set<Pair<AnalysisReadWriteHFInfo, AnalysisReadWriteHFInfo>> res = new HashSet<>();
		
		for(AnalysisReadWriteHFInfo w1 : writers)
			for(AnalysisReadWriteHFInfo w2 : writers) {
				if(!matchCollection(w1,w2))
					continue;
				
				if(!w1.equals(w2)) {
					Set<Tarsis> w1keyValues = w1.getKeyValues().get(0);
					Set<Tarsis> w2keyValues = w2.getKeyValues().get(0);
					boolean found = false;
					for(Tarsis w1kvState : w1keyValues) {
						if(found)
							break;
						for(Tarsis w2kvState : w2keyValues) {
							if(w1kvState.isTop() || w2kvState.isTop()
								|| possibleEqualsMatch(w1kvState, w2kvState)) {
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
		
		List<Call> calls = CFGUtils.extractCallsFromStatement(node);
		if(calls.isEmpty())
			return true;
		
		for(Call call : calls) {
			if(ReadWriteHFUtils.isReadOrWriteCall(call)) {
				try {
					ReadWriteInfo info = ReadWriteHFUtils.getReadWriteInfo(call);
					int[] keyParams = info.getKeyParameters();
					
					for (AnalyzedCFG<
							SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>,
							PointBasedHeap, ValueEnvironment<Tarsis>,
							TypeEnvironment<InferredTypes>> result : tool.getResultOf(call.getCFG())) {
						Call resolved = call instanceof UnresolvedCall ? (Call) tool.getResolvedVersion((UnresolvedCall) call, result) : call;
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
						
						AnalysisReadWriteHFInfo infoForAnalysis;
						if(!info.hasCollection())
							infoForAnalysis = new AnalysisReadWriteHFInfo(call, info, keyValues);
						else {
							Set<Tarsis> collectionValues = extractCollectionValues(call, info.getCollectionParam().intValue(), node, result);
							infoForAnalysis = new AnalysisReadWriteHFInfo(call, info, keyValues, collectionValues);
						}
						
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
		}		
		return true;
	}


	private Set<Tarsis> extractCollectionValues(Call call, int collectionParam, Statement node,
			AnalyzedCFG<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>, PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>> result) throws SemanticException {

	
		int par = call.getCallType().equals(CallType.STATIC) ? collectionParam : collectionParam+1;
		Set<Tarsis> res = new HashSet<>();
			
		if(par < call.getParameters().length) {
				
			AnalysisState<
			SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>,
					TypeEnvironment<InferredTypes>>,
			PointBasedHeap, ValueEnvironment<Tarsis>,
			TypeEnvironment<InferredTypes>> state = result
					.getAnalysisStateAfter(call.getParameters()[par]);
			for (SymbolicExpression stack : state.rewrite(state.getComputedExpressions(), node))
				res.add(state.getState().getValueState().eval((ValueExpression) stack, node));
		
		}
		
		return res;
	}

	private String extractValueStringFromTarsisStates(Tarsis state) {
		if(state.getAutomaton().emptyString().equals(state.getAutomaton()))
			return "";
		else if(!state.getAutomaton().acceptsTopEventually())
			return state.getAutomaton().toString();
		
		return null;
	}
	

	private ArrayList<Set<Tarsis>> extractKeyValues(Call call, int[] keyParams, int parametersLength, Statement node, AnalyzedCFG<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>, PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>> result) throws SemanticException {
		
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
