package it.unive.golisa.interprocedural;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unive.lisa.AnalysisExecutionException;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.CFGWithAnalysisResults;
import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.lattices.ExpressionSet;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.CFGResults;
import it.unive.lisa.interprocedural.CallGraphBasedAnalysis;
import it.unive.lisa.interprocedural.ContextSensitivityToken;
import it.unive.lisa.interprocedural.FixpointResults;
import it.unive.lisa.interprocedural.NoEntryPointException;
import it.unive.lisa.interprocedural.SingleScopeToken;
import it.unive.lisa.logging.IterationLogger;
import it.unive.lisa.logging.TimerLogger;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.call.CFGCall;
import it.unive.lisa.program.cfg.statement.call.OpenCall;
import it.unive.lisa.program.language.parameterassignment.ParameterAssigningStrategy;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.util.collections.workset.FIFOWorkingSet;
import it.unive.lisa.util.collections.workset.VisitOnceWorkingSet;
import it.unive.lisa.util.collections.workset.WorkingSet;
import it.unive.lisa.util.datastructures.graph.algorithms.FixpointException;

public class GoContextBasedAnalysis<A extends AbstractState<A, H, V, T>,
		H extends HeapDomain<H>,
		V extends ValueDomain<V>,
		T extends TypeDomain<T>> extends CallGraphBasedAnalysis<A, H, V, T> {

	private static final Logger LOG = LogManager.getLogger(GoContextBasedAnalysis.class);

	private FixpointResults<A, H, V, T> results;

	private FixpointEntries<A, H, V, T> entries;

	private ContextSensitivityToken token;

	private final Collection<CFG> fixpointTriggers;

	private Class<? extends WorkingSet<Statement>> fixpointWorkingSet;

	private int wideningThreshold;

	public GoContextBasedAnalysis() {
		this(SingleScopeToken.getSingleton());
	}

	public GoContextBasedAnalysis(ContextSensitivityToken token) {
		this.token = token.empty();
		fixpointTriggers = new HashSet<>();
	}

	@Override
	public void fixpoint(
			AnalysisState<A, H, V, T> entryState,
			Class<? extends WorkingSet<Statement>> fixpointWorkingSet,
			int wideningThreshold)
			throws FixpointException {
		this.results = null;
		this.entries = null;
		this.fixpointWorkingSet = fixpointWorkingSet;
		this.wideningThreshold = wideningThreshold;

		if (app.getEntryPoints().isEmpty())
			throw new NoEntryPointException();

		TimerLogger.execAction(LOG, "Computing fixpoint over the whole program",
				() -> this.fixpointAux(entryState, fixpointWorkingSet, wideningThreshold));
	}

	private static String ordinal(int i) {
		int n = i % 100;
		if (n == 11 || n == 12 || n == 13 || n % 10 == 0 || n % 10 > 3)
			return i + "th";

		if (n % 10 == 1)
			return i + "st";

		if (n % 10 == 2)
			return i + "nd";

		return i + "rd";
	}

	private void fixpointAux(AnalysisState<A, H, V, T> entryState,
			Class<? extends WorkingSet<Statement>> fixpointWorkingSet,
			int wideningThreshold) throws AnalysisExecutionException {
		int iter = 0;
		do {
			LOG.info("Performing {} fixpoint iteration", ordinal(iter + 1));
			fixpointTriggers.clear();
			for (CFG cfg : IterationLogger.iterate(LOG, app.getEntryPoints(), "Processing entrypoints", "entries"))
				try {
					if (results == null)
						this.results = new FixpointResults<>(
								new CFGResults<>(new CFGWithAnalysisResults<>(cfg, entryState)));
					if (entries == null)
						this.entries = new FixpointEntries<>(new CFGEntries<>(entryState));

					AnalysisState<A, H, V, T> entryStateCFG = prepareEntryStateOfEntryPoint(entryState, cfg);
					ContextSensitivityToken tk = token.empty();
					entries.putEntry(cfg, tk, entryState);
					results.putResult(cfg, tk,
							cfg.fixpoint(entryStateCFG, this, WorkingSet.of(fixpointWorkingSet), wideningThreshold));
				} catch (SemanticException | AnalysisSetupException e) {
					throw new AnalysisExecutionException("Error while creating the entrystate for " + cfg, e);
				} catch (FixpointException e) {
					throw new AnalysisExecutionException("Error while computing fixpoint for entrypoint " + cfg, e);
				}

			// starting from the callers of the cfgs that needed a lub,
			// find out the complete set of cfgs that might need to be
			// processed again
			VisitOnceWorkingSet<CFG> ws = VisitOnceWorkingSet.mk(FIFOWorkingSet.mk());
			fixpointTriggers.forEach(cfg -> getCallersRecursively(cfg, fixpointTriggers).forEach(ws::push));
			while (!ws.isEmpty())
				getCallersRecursively(ws.pop(), fixpointTriggers).forEach(ws::push);

			ws.getSeen().forEach(results::forget);
			ws.getSeen().forEach(entries::forget);

			iter++;
		} while (!fixpointTriggers.isEmpty());
	}
	
	private Stream<CFG> getCallersRecursively(CFG cfg, Collection<CFG> excluded) {
		return callgraph.getCallers(cfg).stream()
				.filter(CFG.class::isInstance)
				.map(CFG.class::cast)
				.filter(caller -> cfg != caller)
				.filter(Predicate.not(excluded::contains));
	}

	@Override
	public Collection<CFGWithAnalysisResults<A, H, V, T>> getAnalysisResultsOf(CFG cfg) {
		if (results.contains(cfg))
			return results.getState(cfg).getAll();
		else
			return Collections.emptySet();
	}

	private Pair<AnalysisState<A, H, V, T>, AnalysisState<A, H, V, T>> getEntryAndExit(CFG cfg)
			throws SemanticException {
		if (!entries.contains(cfg))
			return null;
		CFGEntries<A, H, V, T> cfgentry = entries.getState(cfg);
		if (!cfgentry.contains(token))
			return null;
		AnalysisState<A, H, V, T> analysisentry = cfgentry.getState(token);
		if (!results.contains(cfg))
			return Pair.of(analysisentry, null);
		CFGResults<A, H, V, T> cfgresult = results.getState(cfg);
		if (!cfgresult.contains(token))
			return Pair.of(analysisentry, null);
		CFGWithAnalysisResults<A, H, V, T> analysisresult = cfgresult.getState(token);
		return Pair.of(analysisentry, analysisresult.getExitState());
	}

	@Override
	public AnalysisState<A, H, V, T> getAbstractResultOf(
			CFGCall call,
			AnalysisState<A, H, V, T> entryState,
			ExpressionSet<SymbolicExpression>[] parameters,
			StatementStore<A, H, V, T> expressions)
			throws SemanticException {
		ScopeToken scope = new ScopeToken(call);
		ContextSensitivityToken fresh = token.pushToken(scope);
		boolean hasPushed = fresh != token;
		token = fresh;
		AnalysisState<A, H, V, T> result = entryState.bottom();

		for (CFG cfg : call.getTargetedCFGs()) {
			Pair<AnalysisState<A, H, V, T>, AnalysisState<A, H, V, T>> states = getEntryAndExit(cfg);

			// prepare the state for the call: hide the visible variables
			AnalysisState<A, H, V, T> callState = entryState.pushScope(scope);

			Parameter[] formals = cfg.getDescriptor().getFormals();
			@SuppressWarnings("unchecked")
			ExpressionSet<SymbolicExpression>[] actuals = new ExpressionSet[parameters.length];

			for (int i = 0; i < parameters.length; i++)
				actuals[i] = parameters[i].pushScope(scope);

			ParameterAssigningStrategy strategy = call.getProgram().getFeatures().getAssigningStrategy();
			Pair<AnalysisState<A, H, V, T>,
					ExpressionSet<SymbolicExpression>[]> prepared = strategy.prepare(call, callState,
							this, expressions, formals, actuals);

			AnalysisState<A, H, V, T> exitState;
			AnalysisState<A, H, V, T> prep = prepared.getLeft();
			if (states != null && prep.lessOrEqual(states.getLeft())) {
				// no need to compute the fixpoint: we already have an
				// approximation
				if (states.getRight() != null)
					exitState = states.getRight();
				else
					// the state refers to a call in the current call chain,
					// that has not been fully evaluated yet. We treat this
					// as an open call
					exitState = policy.apply(
							new OpenCall(
									call.getCFG(), 
									call.getLocation(), 
									call.getCallType(), 
									call.getQualifier(),
									call.getTargetName(), 
									call.getOrder(), 
									call.getStaticType(), 
									call.getParameters()),
							prep, prepared.getRight());
			} else {
				// store the current entry, lubbing the entry state
				if (states != null)
					prep = prep.lub(states.getLeft());
				entries.putEntry(cfg, token, prep);

				// compute the result
				CFGWithAnalysisResults<A, H, V, T> fixpointResult = null;
				try {
					fixpointResult = computeFixpoint(cfg, token, prep);
				} catch (FixpointException | AnalysisSetupException e) {
					throw new SemanticException("Exception during the interprocedural analysis", e);
				}

				exitState = fixpointResult.getExitState();
			}

			// store the return value of the call inside the meta variable
			AnalysisState<A, H, V, T> tmp = callState.bottom();
			Identifier meta = (Identifier) call.getMetaVariable().pushScope(scope);
			for (SymbolicExpression ret : exitState.getComputedExpressions())
				tmp = tmp.lub(exitState.assign(meta, ret, call));

			// save the resulting state
			result = result.lub(tmp.popScope(scope));
		}

		if (hasPushed)
			token = token.popToken();

		callgraph.registerCall(call);

		return result;
	}

	private CFGWithAnalysisResults<A, H, V, T> computeFixpoint(CFG cfg, ContextSensitivityToken localToken,
			AnalysisState<A, H, V, T> computedEntryState)
			throws FixpointException, SemanticException, AnalysisSetupException {
		CFGWithAnalysisResults<A, H, V, T> fixpointResult = cfg.fixpoint(computedEntryState, this,
				WorkingSet.of(fixpointWorkingSet), wideningThreshold);
		fixpointResult.setId(localToken.toString());
		Pair<Boolean, CFGWithAnalysisResults<A, H, V, T>> res = results.putResult(cfg, localToken, fixpointResult);
		if (Boolean.TRUE.equals(res.getLeft()))
			fixpointTriggers.add(cfg);
		return res.getRight();
	}
}
