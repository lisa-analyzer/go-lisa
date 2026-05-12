package it.unive.golisa.checker.hf.readwrite;

import it.unive.golisa.cfg.utils.CFGUtils;
import it.unive.lisa.analysis.Analysis;
import it.unive.lisa.analysis.AnalyzedCFG;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.SimpleAbstractDomain;
import it.unive.lisa.analysis.nonrelational.heap.HeapEnvironment;
import it.unive.lisa.analysis.nonrelational.heap.HeapValue;
import it.unive.lisa.analysis.nonrelational.type.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.type.TypeValue;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.string.tarsis.Tarsis;
import it.unive.lisa.checks.semantic.SemanticCheck;
import it.unive.lisa.checks.semantic.SemanticTool;
import it.unive.lisa.lattices.SimpleAbstractState;
import it.unive.lisa.lattices.string.tarsis.RegexAutomaton;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeMember;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.call.CFGCall;
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.program.cfg.statement.call.Call.CallType;
import it.unive.lisa.program.cfg.statement.call.NativeCall;
import it.unive.lisa.program.cfg.statement.call.UnresolvedCall;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.ValueExpression;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;

/**
 * A Go checker for read-write set issues of Hyperledger Fabric. Note that
 * read-write set issues analysis is split in two checkers/phases. This is the
 * first phase.
 * 
 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
 */
public class ReadWritePairChecker<H extends HeapValue<H>, T extends TypeValue<T>> implements
SemanticCheck<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>>  {

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
	public void beforeExecution(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>> tool) {
		writers = new HashSet<>();
		readers = new HashSet<>();
	}

	@Override
	public void afterExecution(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>> tool) {
		readAfterWriteCandidates = computeReadAfterWriteCandidates();
		overWriteCandidates = computeOverWriteCandidates();
	}

	@Override
	public boolean visitUnit(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>> tool,
			Unit unit) {
		return true;
	}

	@Override
	public void visitGlobal(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>> tool,
			Unit unit, Global global, boolean instance) {
	}

	

	@Override
	public boolean visit(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>> tool,
			CFG graph, Statement node) {

		List<Call> calls = CFGUtils.extractCallsFromStatement(node);
		if (calls.isEmpty())
			return true;

		for (Call call : calls) {
			if (ReadWriteHFUtils.isReadOrWriteCall(call)) {
				try {
					ReadWriteInfo info = ReadWriteHFUtils.getReadWriteInfo(call);
					int[] keyParams = info.getKeyParameters();

					for (var result : tool.getResultOf(call.getCFG())) {
						Call resolved = call instanceof UnresolvedCall
								? (Call) tool.getResolvedVersion((UnresolvedCall) call, result)
								: call;
						ArrayList<Set<RegexAutomaton>> keyValues = new ArrayList<>();

						if (resolved instanceof NativeCall) {
							NativeCall nativeCfg = (NativeCall) resolved;
							Collection<CodeMember> nativeCfgs = nativeCfg.getTargets();
							for (CodeMember n : nativeCfgs) {
								Parameter[] parameters = n.getDescriptor().getFormals();
								keyValues = extractKeyValues(tool.getAnalysis(), call, keyParams, parameters.length, node, result);
							}
						} else if (resolved instanceof CFGCall) {
							CFGCall cfg = (CFGCall) resolved;

							for (CodeMember n : cfg.getTargets()) {
								Parameter[] parameters = n.getDescriptor().getFormals();
								keyValues = extractKeyValues(tool.getAnalysis(), call, keyParams, parameters.length, node, result);
							}
						} else {
							keyValues = extractKeyValues(tool.getAnalysis(), call, keyParams, call.getParameters().length, node, result);
						}

						AnalysisReadWriteHFInfo infoForAnalysis;
						if (!info.hasCollection())
							infoForAnalysis = new AnalysisReadWriteHFInfo(call, info, keyValues);
						else {
							Set<RegexAutomaton> collectionValues = extractCollectionValues(tool.getAnalysis(), call,
									info.getCollectionParam().intValue(), node, result);
							infoForAnalysis = new AnalysisReadWriteHFInfo(call, info, keyValues, collectionValues);
						}

						if (ReadWriteHFUtils.isReadCall(call))
							readers.add(infoForAnalysis);
						else if (ReadWriteHFUtils.isWriteCall(call))
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

	@Override
	public boolean visit(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>> tool,
			CFG graph, Edge edge) {
		return true;
	}

	private Set<Pair<AnalysisReadWriteHFInfo, AnalysisReadWriteHFInfo>> computeReadAfterWriteCandidates() {

		Set<Pair<AnalysisReadWriteHFInfo, AnalysisReadWriteHFInfo>> res = new HashSet<>();

		for (AnalysisReadWriteHFInfo w : writers) {
			Set<RegexAutomaton> wkeyValues = w.getKeyValues().get(0);
			for (AnalysisReadWriteHFInfo r : readers) {
				if (!matchCollection(r, w))
					continue;

				boolean found = false;
				switch (r.getInfo().getKeyType()) {
				case SINGLE:
					Set<RegexAutomaton> rkeyValues = r.getKeyValues().get(0);
					for (RegexAutomaton wkvState : wkeyValues) {
						if (found)
							break;
						for (RegexAutomaton rkvState : rkeyValues) {
							if (wkvState.isTop() || rkvState.isTop()
									|| possibleEqualsMatch(rkvState, wkvState)) {
								res.add(Pair.of(w, r));
								found = true;
								break;
							}
						}
					}
					break;
				case RANGE:
					Set<RegexAutomaton> startKeyValue = r.getKeyValues().get(0);
					Set<RegexAutomaton> endKeyValue = r.getKeyValues().get(1);
					for (RegexAutomaton wkvState : wkeyValues) {
						if (found)
							break;
						for (RegexAutomaton rstartKeyValueState : startKeyValue) {
							for (RegexAutomaton rendKeyValueState : endKeyValue) {
								if (wkvState.isTop() || rendKeyValueState.isTop()
										|| possibleRangeMatch(wkvState, rstartKeyValueState, rendKeyValueState)) {
									res.add(Pair.of(w, r));
									found = true;
									break;
								}
							}
						}
					}
					break;
				case COMPOSITE:
					Set<RegexAutomaton> rcompositeKeyValues = r.getKeyValues().get(0);
					for (RegexAutomaton wkvState : wkeyValues) {
						if (found)
							break;
						for (RegexAutomaton rkvState : rcompositeKeyValues) {
							if (wkvState.isTop() || rkvState.isTop()
									|| possibleCompositeMatch(rkvState, wkvState)) {
								res.add(Pair.of(w, r));
								found = true;
								break;
							}
						}
					}
					break;
				default:
					throw new IllegalArgumentException(
							"The following key type is not handled: " + r.getInfo().getKeyType());
				}
			}
		}

		return res;
	}

	private boolean possibleCompositeMatch(RegexAutomaton rkvState, RegexAutomaton wkvState) {
		String prefixValue = extractValueStringFromRegexAutomatonStates(rkvState);
		String value = extractValueStringFromRegexAutomatonStates(wkvState);
		if (prefixValue != null && value != null)
			return value.contains(prefixValue);
		return true;
	}

	private boolean possibleRangeMatch(RegexAutomaton wkvState, RegexAutomaton rstartKeyValueState, RegexAutomaton rendKeyValueState) {
		if (!rendKeyValueState.isEqualTo(RegexAutomaton.emptyStr())) {
			String value = extractValueStringFromRegexAutomatonStates(wkvState);
			if (value != null) {
				boolean l = true;
				boolean u = true;

				if (!rstartKeyValueState.isTop()) {
					String lBoundValue = extractValueStringFromRegexAutomatonStates(rstartKeyValueState);

					if (lBoundValue != null)
						l = lBoundValue.compareTo(value) <= 0;
				}

				String uBoundValue = extractValueStringFromRegexAutomatonStates(rendKeyValueState);
				if (uBoundValue != null)
					u = value.compareTo(uBoundValue) < 0;

				return l && u;
			}
		}
		return true;
	}

	private boolean possibleEqualsMatch(RegexAutomaton state1, RegexAutomaton state2) {
		return state1.isEqualTo(state2)
				|| state1.isContained(state2)
				|| state2.isContained(state1);
	}

	private boolean matchCollection(AnalysisReadWriteHFInfo st1, AnalysisReadWriteHFInfo st2) {

		ReadWriteInfo st1Info = st1.getInfo();
		ReadWriteInfo st2Info = st2.getInfo();

		if ((st1Info.hasCollection() && !st2Info.hasCollection())
				|| (!st1Info.hasCollection() && st2Info.hasCollection()))
			return false;

		if (st1.getInfo().hasCollection() && st2.getInfo().hasCollection()) {
			Set<RegexAutomaton> c1Values = st1.getCollectionValues();
			Set<RegexAutomaton> c2Values = st2.getCollectionValues();
			for (RegexAutomaton c1ValueState : c1Values) {
				for (RegexAutomaton c2ValuesState : c2Values) {
					if (c1ValueState.isTop() || c2ValuesState.isTop()
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

		for (AnalysisReadWriteHFInfo w1 : writers)
			for (AnalysisReadWriteHFInfo w2 : writers) {
				if (!matchCollection(w1, w2))
					continue;

				if (!w1.equals(w2)) {
					Set<RegexAutomaton> w1keyValues = w1.getKeyValues().get(0);
					Set<RegexAutomaton> w2keyValues = w2.getKeyValues().get(0);
					boolean found = false;
					for (RegexAutomaton w1kvState : w1keyValues) {
						if (found)
							break;
						for (RegexAutomaton w2kvState : w2keyValues) {
							if (w1kvState.isTop() || w2kvState.isTop()
									|| possibleEqualsMatch(w1kvState, w2kvState)) {
								res.add(Pair.of(w1, w2));
								found = true;
								break;
							}
						}
					}
				}
			}

		return res;
	}



	private Set<RegexAutomaton> extractCollectionValues(Analysis<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>> analysis, Call call, int collectionParam, Statement node,
			AnalyzedCFG<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>> result)
			throws SemanticException {

		int par = call.getCallType().equals(CallType.STATIC) ? collectionParam : collectionParam + 1;
		Set<RegexAutomaton> res = new HashSet<>();

		if (par < call.getParameters().length) {
			var state = result.getAnalysisStateAfter(call.getParameters()[par]);
			for (SymbolicExpression stack : analysis.rewrite(state, state.getExecutionExpressions(), node)) {
				var valueState = state.getExecutionState().valueState;
				Tarsis analysisValueDomain = (Tarsis) analysis.domain.valueDomain;
				SemanticOracle oracle = analysis.domain.makeOracle(state.getExecutionState());
				var value = analysisValueDomain.eval(valueState, (ValueExpression) stack, (ProgramPoint) node, oracle);
				res.add(value);
			}
		}

		return res;
	}

	private String extractValueStringFromRegexAutomatonStates(RegexAutomaton state) {
		if (state.emptyString().equals(state))
			return "";
		else if (!state.acceptsTopEventually())
			return state.toString();

		return null;
	}

	private ArrayList<Set<RegexAutomaton>> extractKeyValues(Analysis<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>> analysis,
			Call call, int[] keyParams, int parametersLength, Statement node, AnalyzedCFG<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>> result)
			throws SemanticException {

		ArrayList<Set<RegexAutomaton>> valStringDomain = new ArrayList<>(keyParams.length);

		for (int i = 0; i < keyParams.length; i++) {
			int par = call.getCallType().equals(CallType.STATIC) ? keyParams[i] : keyParams[i] + 1;
			valStringDomain.add(new HashSet<>());
			if (par < parametersLength) {

				var state = result.getAnalysisStateAfter(call.getParameters()[par]);
				for (SymbolicExpression stack : analysis.rewrite(state, state.getExecutionExpressions(), node)) {
					var valueState = state.getExecutionState().valueState;
					Tarsis analysisValueDomain = (Tarsis) analysis.domain.valueDomain;
					SemanticOracle oracle = analysis.domain.makeOracle(state.getExecutionState());
					var value = analysisValueDomain.eval(valueState, (ValueExpression) stack, (ProgramPoint) node, oracle);
					valStringDomain.get(i).add(value);
				}
			}
		}

		return valStringDomain;

	}
}