package benchmarks.tarsis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.function.FailableFunction;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import it.unive.lisa.analysis.SemanticDomain.Satisfiability;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.string.fsa.FSA;
import it.unive.lisa.analysis.string.tarsis.RegexAutomaton;
import it.unive.lisa.analysis.string.tarsis.Tarsis;
import it.unive.lisa.logging.TimeFormat;
import it.unive.lisa.util.datastructures.automaton.State;
import it.unive.lisa.util.datastructures.automaton.Transition;
import it.unive.lisa.util.datastructures.regex.Atom;
import it.unive.lisa.util.datastructures.regex.RegularExpression;
import it.unive.lisa.util.datastructures.regex.TopAtom;
import it.unive.lisa.util.numeric.IntInterval;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TarsisJournalEvaluation {

	private static final String TARSIS_KEY = "Tarsis";

	private static final String FSA_KEY = "FSA";

	private static final Random GEN = new Random(-37736743);

	private static final Logger LOG = LogManager.getLogger(TarsisJournalEvaluation.class);

	private static final ExecutorService EXECUTORS = Executors.newSingleThreadExecutor();

	private static final long TIMEOUT = 1;

	private static final TimeUnit TIMEOUT_UNIT = TimeUnit.MINUTES;

	private static final int WARMUP = 1;

	private static final int ROUNDS = 50;

	private static final int STATE_TRANSITIONS = 2;

	private static final List<Triple<Tarsis, Tarsis, Tarsis>> TARSIS_TRIPLES = new ArrayList<>(ROUNDS + WARMUP);

	private static final List<Triple<FSA, FSA, FSA>> FSA_TRIPLES = new ArrayList<>(ROUNDS + WARMUP);

	private static final List<Pair<Integer, Integer>> SUBSTRING_INDEXES = new ArrayList<>(ROUNDS + WARMUP);

	private static final SortedSet<State> STATES = new TreeSet<>();

	private static final Map<Integer, State> MAPPING = new HashMap<>();

	@BeforeClass
	public static void initialize() {
		State q0 = new State(0, true, false);
		STATES.add(q0);
		MAPPING.put(0, q0);

		State q1 = new State(1, false, false);
		STATES.add(q1);
		MAPPING.put(1, q1);

		State q2 = new State(2, false, false);
		STATES.add(q2);
		MAPPING.put(2, q2);

		State q3 = new State(3, false, true);
		STATES.add(q3);
		MAPPING.put(3, q3);

		State q4 = new State(4, false, true);
		STATES.add(q4);
		MAPPING.put(4, q4);

		State q5 = new State(5, false, false);
		STATES.add(q5);
		MAPPING.put(5, q5);

		LOG.info("Generating automata...");
		for (int k = 0; k < WARMUP + ROUNDS; k++) {
			LOG.trace("Generating automata for round " + (k + 1));
			Tarsis l = generateSingleTarsis();
			Tarsis m = generateSingleTarsis();
			Tarsis r = generateSingleTarsis();
			TARSIS_TRIPLES.add(Triple.of(l, m, r));

			LOG.trace("Converting round " + (k + 1) + " automata to fsa");
			FSA lfsa = l.toFSA();
			FSA mfsa = m.toFSA();
			FSA rfsa = r.toFSA();
			FSA_TRIPLES.add(Triple.of(lfsa, mfsa, rfsa));

			SUBSTRING_INDEXES.add(Pair.of(GEN.nextInt(20), GEN.nextInt(20)));
		}
	}

	@AfterClass
	public static void shutdown() {
		EXECUTORS.shutdown();
	}

	private static RegularExpression randomString() {
		if (GEN.nextDouble() >= 0.9)
			return TopAtom.INSTANCE;

		String ALPHA_NUMERIC_STRING = "0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
		int init = GEN.nextInt(ALPHA_NUMERIC_STRING.length());
		int end = GEN.nextInt(ALPHA_NUMERIC_STRING.length());

		if (init > end) {
			int tmp = init;
			init = end;
			end = tmp;
		}

		return new Atom(ALPHA_NUMERIC_STRING.substring(init, end));
	}

	private static Tarsis generateSingleTarsis() {
		RegexAutomaton a = null;

		do {
			SortedSet<Transition<RegularExpression>> delta = new TreeSet<>();

			for (State s : STATES)
				for (int i = 0; i < STATE_TRANSITIONS; i++)
					delta.add(new Transition<>(s, MAPPING.get(GEN.nextInt(STATES.size())), randomString()));

			a = new RegexAutomaton(STATES, delta).minimize();
		} while (a.acceptsEmptyLanguage());

		return new Tarsis(a);
	}

	@Test
	public void bench02lub() throws SemanticException {
		LOG.info("Benchmarking lub");
		Map<Integer, RunResult<Tarsis>> tarsis = benchmark(TARSIS_TRIPLES, TARSIS_KEY,
				triple -> triple.getLeft().lub(triple.getMiddle()));
		Map<Integer, RunResult<FSA>> fsa = benchmark(FSA_TRIPLES, FSA_KEY,
				triple -> triple.getLeft().lub(triple.getMiddle()));
		compare(tarsis, fsa);
	}

	@Test
	public void bench01leq() throws SemanticException {
		LOG.info("Benchmarking leq");
		Map<Integer, RunResult<Boolean>> tarsis = benchmark(TARSIS_TRIPLES, TARSIS_KEY,
				triple -> triple.getLeft().lessOrEqual(triple.getMiddle()));
		Map<Integer, RunResult<Boolean>> fsa = benchmark(FSA_TRIPLES, FSA_KEY,
				triple -> triple.getLeft().lessOrEqual(triple.getMiddle()));
		compareBools(tarsis, fsa);
	}

	@Test
	public void bench03widening() throws SemanticException {
		LOG.info("Benchmarking widening");
		Map<Integer, RunResult<Tarsis>> tarsis = benchmark(TARSIS_TRIPLES, TARSIS_KEY,
				triple -> triple.getLeft().widening(triple.getLeft()));
		Map<Integer, RunResult<FSA>> fsa = benchmark(FSA_TRIPLES, FSA_KEY,
				triple -> triple.getLeft().widening(triple.getLeft()));
		compare(tarsis, fsa);
	}

	@Test
	public void bench04concat() throws SemanticException {
		LOG.info("Benchmarking concat");
		Map<Integer, RunResult<Tarsis>> tarsis = benchmark(TARSIS_TRIPLES, TARSIS_KEY,
				triple -> triple.getLeft().concat(triple.getMiddle()));
		Map<Integer, RunResult<FSA>> fsa = benchmark(FSA_TRIPLES, FSA_KEY,
				triple -> triple.getLeft().concat(triple.getMiddle()));
		compare(tarsis, fsa);
	}

	@Test
	public void bench05contains() throws SemanticException {
		LOG.info("Benchmarking contains");
		Map<Integer, RunResult<Satisfiability>> tarsis = benchmark(TARSIS_TRIPLES, TARSIS_KEY,
				triple -> triple.getLeft().contains(triple.getMiddle()));
		Map<Integer, RunResult<Satisfiability>> fsa = benchmark(FSA_TRIPLES, FSA_KEY,
				triple -> triple.getLeft().contains(triple.getMiddle()));
		compareSats(tarsis, fsa);
	}

	@Test
	public void bench07indexOf() throws SemanticException {
		LOG.info("Benchmarking indexOf");
		Map<Integer, RunResult<IntInterval>> tarsis = benchmark(TARSIS_TRIPLES, TARSIS_KEY,
				triple -> triple.getLeft().indexOf(triple.getMiddle()));
		Map<Integer, RunResult<IntInterval>> fsa = benchmark(FSA_TRIPLES, FSA_KEY,
				triple -> triple.getLeft().indexOf(triple.getMiddle()));
		compareIntervals(tarsis, fsa);
	}

	@Test
	public void bench06length() throws SemanticException {
		LOG.info("Benchmarking length");
		Map<Integer, RunResult<IntInterval>> tarsis = benchmark(TARSIS_TRIPLES, TARSIS_KEY,
				triple -> triple.getLeft().length());
		Map<Integer, RunResult<IntInterval>> fsa = benchmark(FSA_TRIPLES, FSA_KEY, triple -> triple.getLeft().length());
		compareIntervals(tarsis, fsa);
	}

	@Test
	public void bench09replace() throws SemanticException {
		LOG.info("Benchmarking replace");
		Map<Integer, RunResult<Tarsis>> tarsis = benchmark(TARSIS_TRIPLES, TARSIS_KEY,
				triple -> triple.getLeft().replace(triple.getMiddle(), triple.getRight()));
		Map<Integer, RunResult<FSA>> fsa = benchmark(FSA_TRIPLES, FSA_KEY,
				triple -> triple.getLeft().replace(triple.getMiddle(), triple.getRight()));
		compare(tarsis, fsa);
	}

	@Test
	public void bench08substring() throws SemanticException {
		LOG.info("Benchmarking substring");
		AtomicInteger idx = new AtomicInteger();
		Map<Integer, RunResult<Tarsis>> tarsis = benchmark(TARSIS_TRIPLES, TARSIS_KEY,
				triple -> triple.getLeft().substring(SUBSTRING_INDEXES.get(idx.get()).getLeft(),
						SUBSTRING_INDEXES.get(idx.getAndIncrement()).getRight()));
		idx.set(0);
		Map<Integer,
				RunResult<FSA>> fsa = benchmark(FSA_TRIPLES, FSA_KEY,
						triple -> triple.getLeft().substring(SUBSTRING_INDEXES.get(idx.get()).getLeft(),
								SUBSTRING_INDEXES.get(idx.getAndIncrement()).getRight()));
		compare(tarsis, fsa);
	}

	private static <I, O> Map<Integer, RunResult<O>> benchmark(Iterable<I> inputs, String key,
			FailableFunction<I, O, Exception> action) {
		Iterator<I> iterator = inputs.iterator();
		for (int i = 0; i < WARMUP; i++) {
			LOG.trace("Warmup iteration " + (i + 1));
			benchmarkSingle(iterator.next(), action);
		}

		Map<Integer, RunResult<O>> results = new HashMap<>();
		for (int i = 0; i < ROUNDS; i++) {
			LOG.trace("Round " + (i + 1));
			results.put(i, benchmarkSingle(iterator.next(), action));
		}

		int timeouts = 0, failures = 0, successes = 0;
		long max = -1L, min = Long.MAX_VALUE;
		double average = 0;
		for (RunResult<O> entry : results.values()) {
			if (entry.timeout)
				timeouts++;
			else if (entry.e != null)
				failures++;
			else {
				min = Math.min(min, entry.elapsed);
				max = Math.max(max, entry.elapsed);
				average = ((average * successes) + entry.elapsed) / (successes + 1);
				successes++;
			}
		}

		if (successes == 0)
			// cleaner displaying
			max = min = 0;

		LOG.info("  Results for " + key + ": " + successes + " successes, " + failures + " failures, " + timeouts
				+ " timeouts");
		LOG.info("    Execution time for successes: average " + TimeFormat.SECONDS_AND_MILLIS.format((long) average)
				+ ", minimum " + TimeFormat.SECONDS_AND_MILLIS.format(min) + ", maximum "
				+ TimeFormat.SECONDS_AND_MILLIS.format(max));

		return results;
	}

	private static <I, O> RunResult<O> benchmarkSingle(I input, FailableFunction<I, O, Exception> action) {
		RunResult<O> res;
		final Future<RunResult<O>> future = EXECUTORS.submit(new SingleRun<>(action, input));

		try {
			res = future.get(TIMEOUT, TIMEOUT_UNIT);
		} catch (InterruptedException | ExecutionException e) {
			res = new RunResult<>(e);
		} catch (TimeoutException te) {
			res = new RunResult<>();
		}

		return res;
	}

	private static class SingleRun<I, O> implements Callable<RunResult<O>> {
		private final FailableFunction<I, O, Exception> action;
		private final I input;

		private SingleRun(FailableFunction<I, O, Exception> action, I input) {
			this.action = action;
			this.input = input;
		}

		@Override
		public RunResult<O> call() {
			long start = System.nanoTime();
			O res;
			try {
				res = action.apply(input);
			} catch (Exception e) {
				return new RunResult<>(e);
			}
			long end = System.nanoTime();
			return new RunResult<O>(end - start, res);
		}
	}

	private static class RunResult<T> {
		private final long elapsed;
		private final boolean timeout;
		private final Exception e;
		private final T result;

		private RunResult() {
			this(-1, true, null, null);
		}

		private RunResult(long elapsed, T result) {
			this(elapsed, false, null, result);
		}

		private RunResult(Exception e) {
			this(-1, false, e, null);
		}

		private RunResult(long elapsed, boolean timeout, Exception e, T result) {
			this.elapsed = elapsed;
			this.timeout = timeout;
			this.e = e;
			this.result = result;
		}
	}

	private static void compareIntervals(Map<Integer, RunResult<IntInterval>> tarsisRes,
			Map<Integer, RunResult<IntInterval>> fsaRes) {
		int available = 0, equal = 0, tarsisBetter = 0, fsaBetter = 0, incomparable = 0;
		for (Entry<Integer, RunResult<IntInterval>> entry : tarsisRes.entrySet()) {
			RunResult<IntInterval> tarsis = entry.getValue();
			RunResult<IntInterval> fsa = fsaRes.get(entry.getKey());
			if (tarsis.result != null && fsa.result != null) {
				available++;
				boolean tleq = fsa.result.includes(tarsis.result);
				boolean fleq = tarsis.result.includes(fsa.result);
				if (tleq && fleq)
					equal++;
				else if (tleq)
					tarsisBetter++;
				else if (fleq)
					fsaBetter++;
				else
					incomparable++;
			}
		}

		LOG.info("  Comparison results: " + available + " results to be compared, "
				+ equal + " same precision, "
				+ tarsisBetter + " tarsis better, "
				+ fsaBetter + " fsa better, "
				+ incomparable + " incomparable");
	}

	private static void compareSats(Map<Integer, RunResult<Satisfiability>> tarsisRes,
			Map<Integer, RunResult<Satisfiability>> fsaRes) throws SemanticException {
		int available = 0, equal = 0, tarsisBetter = 0, fsaBetter = 0, incomparable = 0;
		for (Entry<Integer, RunResult<Satisfiability>> entry : tarsisRes.entrySet()) {
			RunResult<Satisfiability> tarsis = entry.getValue();
			RunResult<Satisfiability> fsa = fsaRes.get(entry.getKey());
			if (tarsis.result != null && fsa.result != null) {
				available++;
				boolean tleq = tarsis.result.lessOrEqual(fsa.result);
				boolean fleq = fsa.result.lessOrEqual(tarsis.result);
				if (tleq && fleq)
					equal++;
				else if (tleq)
					tarsisBetter++;
				else if (fleq)
					fsaBetter++;
				else
					incomparable++;
			}
		}

		LOG.info("  Comparison results: " + available + " results to be compared, "
				+ equal + " same precision, "
				+ tarsisBetter + " tarsis better, "
				+ fsaBetter + " fsa better, "
				+ incomparable + " incomparable");
	}

	private static void compareBools(Map<Integer, RunResult<Boolean>> tarsisRes,
			Map<Integer, RunResult<Boolean>> fsaRes) {
		int available = 0, equal = 0, incomparable = 0;
		for (Entry<Integer, RunResult<Boolean>> entry : tarsisRes.entrySet()) {
			RunResult<Boolean> tarsis = entry.getValue();
			RunResult<Boolean> fsa = fsaRes.get(entry.getKey());
			if (tarsis.result != null && fsa.result != null) {
				available++;
				if (tarsis.result == fsa.result)
					equal++;
				else
					incomparable++;
			}
		}

		LOG.info("  Comparison results: " + available + " results to be compared, "
				+ equal + " same result, "
				+ incomparable + " different result");
	}

	private static void compare(Map<Integer, RunResult<Tarsis>> tarsisRes, Map<Integer, RunResult<FSA>> fsaRes)
			throws SemanticException {
		int available = 0, equal = 0, tarsisBetter = 0, fsaBetter = 0, incomparable = 0;
		for (Entry<Integer, RunResult<Tarsis>> entry : tarsisRes.entrySet()) {
			RunResult<Tarsis> tarsis = entry.getValue();
			RunResult<FSA> fsa = fsaRes.get(entry.getKey());
			if (tarsis.result != null && fsa.result != null) {
				available++;
				FSA tarsisConverted = tarsis.result.toFSA();
				boolean tleq = tarsisConverted.lessOrEqual(fsa.result);
				boolean fleq = fsa.result.lessOrEqual(tarsisConverted);
				if (tleq && fleq)
					equal++;
				else if (tleq)
					tarsisBetter++;
				else if (fleq)
					fsaBetter++;
				else
					incomparable++;
			}
		}

		LOG.info("  Comparison results: " + available + " results to be compared, "
				+ equal + " same precision, "
				+ tarsisBetter + " tarsis better, "
				+ fsaBetter + " fsa better, "
				+ incomparable + " incomparable");
	}
}
