package benchmarks.tarsis;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

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
import it.unive.lisa.analysis.string.tarsis.Tarsis;
import it.unive.lisa.logging.TimeFormat;
import it.unive.lisa.util.numeric.IntInterval;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TarsisJournalEvaluation {

	private static final String TARSIS_KEY = "Tarsis";

	private static final String FSA_KEY = "FSA";

	static final Random GEN = new Random(-37736743);

	private static final Logger LOG = LogManager.getLogger(TarsisJournalEvaluation.class);

	private static final ExecutorService EXECUTORS = Executors.newSingleThreadExecutor();

	private static final long TIMEOUT = 1;

	private static final TimeUnit TIMEOUT_UNIT = TimeUnit.MINUTES;

	private static final int WARMUP = 1;

	private static final int ROUNDS_PER_CLASS = 5;

	private static final int ROUNDS = 100;

	private static final List<Triple<Tarsis, Tarsis, Tarsis>> TARSIS_TRIPLES = new ArrayList<>(ROUNDS + WARMUP);

	private static final List<Triple<FSA, FSA, FSA>> FSA_TRIPLES = new ArrayList<>(ROUNDS + WARMUP);

	private static final List<Pair<Integer, Integer>> SUBSTRING_INDEXES = new ArrayList<>(ROUNDS + WARMUP);

	@BeforeClass
	public static void initialize() {
		LOG.info("Generating automata for warmups");
		for (int k = 0; k < WARMUP; k++)
			generateSingle(AutomataGenerator::random);
		LOG.info("Generating automata for top");
		generateSingle(AutomataGenerator::top);
		LOG.info("Class 1: Generating automata for atoms");
		for (int k = 0; k < ROUNDS_PER_CLASS; k++)
			generateSingle(AutomataGenerator::atom);
		LOG.info("Class 2: Generating automata for concatenated constants");
		for (int k = 0; k < ROUNDS_PER_CLASS; k++)
			generateSingle(AutomataGenerator::concatConstants);
		LOG.info("Class 3: Generating automata for concatenated constants and tops");
		for (int k = 0; k < ROUNDS_PER_CLASS; k++)
			generateSingle(AutomataGenerator::concatConstantsAndTop);
		LOG.info("Class 4: Generating automata for single paths");
		for (int k = 0; k < ROUNDS_PER_CLASS; k++)
			generateSingle(AutomataGenerator::singlePath);
		LOG.info("Class 5: Generating automata for single paths with top");
		for (int k = 0; k < ROUNDS_PER_CLASS; k++)
			generateSingle(AutomataGenerator::singlePathWithTop);
		LOG.info("Class 6: Generating automata for joined constants");
		for (int k = 0; k < ROUNDS_PER_CLASS; k++)
			generateSingle(AutomataGenerator::joinConstants);
		LOG.info("Class 7: Generating automata for joined constants with tops");
		for (int k = 0; k < ROUNDS_PER_CLASS; k++)
			generateSingle(AutomataGenerator::joinConstantsAndTop);
		LOG.info("Class 8: Generating automata for loops with constants");
		for (int k = 0; k < ROUNDS_PER_CLASS; k++)
			generateSingle(AutomataGenerator::loopingConstants);
		LOG.info("Class 9: Generating automata for loops with constants and tops");
		for (int k = 0; k < ROUNDS_PER_CLASS; k++)
			generateSingle(AutomataGenerator::loopingConstantsAndTop);
		LOG.info("Generating random automata");
		// 1 is to account for the top automaton
		for (int k = 0; k < ROUNDS - (ROUNDS_PER_CLASS * 9) - 1; k++) 
			generateSingle(AutomataGenerator::random);

		assertEquals(ROUNDS + WARMUP, TARSIS_TRIPLES.size());
		assertEquals(ROUNDS + WARMUP, FSA_TRIPLES.size());
		assertEquals(ROUNDS + WARMUP, SUBSTRING_INDEXES.size());
	}

	private static void generateSingle(Supplier<Tarsis> gen) {
		Tarsis l = gen.get();
		Tarsis m = gen.get();
		Tarsis r = gen.get();
		TARSIS_TRIPLES.add(Triple.of(l, m, r));

		FSA lfsa = l.toFSA();
		FSA mfsa = m.toFSA();
		FSA rfsa = r.toFSA();
		FSA_TRIPLES.add(Triple.of(lfsa, mfsa, rfsa));

		int i = GEN.nextInt(20);
		int j = i + GEN.nextInt(20 - i);
		SUBSTRING_INDEXES.add(Pair.of(i, j));
	}

	@AfterClass
	public static void shutdown() {
		EXECUTORS.shutdown();
	}

	@Test
	public void bench02lub() throws SemanticException {
		LOG.info("Benchmarking lub");
		Map<Integer, RunResult<Tarsis>> tarsis = benchmark(TARSIS_TRIPLES, TARSIS_KEY,
				triple -> triple.getLeft().lub(triple.getMiddle()));
		Map<Integer, RunResult<FSA>> fsa = benchmark(FSA_TRIPLES, FSA_KEY,
				triple -> triple.getLeft().lub(triple.getMiddle()));
		speedup(tarsis, fsa);
//		compare(tarsis, fsa);
	}
	
	@Test
	public void bench03glb() throws SemanticException {
		LOG.info("Benchmarking glb");
		Map<Integer, RunResult<Tarsis>> tarsis = benchmark(TARSIS_TRIPLES, TARSIS_KEY,
				triple -> triple.getLeft().glb(triple.getMiddle()));
		Map<Integer, RunResult<FSA>> fsa = benchmark(FSA_TRIPLES, FSA_KEY,
				triple -> triple.getLeft().glb(triple.getMiddle()));
		speedup(tarsis, fsa);
//		compare(tarsis, fsa);
	}

	@Test
	public void bench01leq() throws SemanticException {
		LOG.info("Benchmarking leq");
		Map<Integer, RunResult<Boolean>> tarsis = benchmark(TARSIS_TRIPLES, TARSIS_KEY,
				triple -> triple.getLeft().lessOrEqual(triple.getMiddle()));
		Map<Integer, RunResult<Boolean>> fsa = benchmark(FSA_TRIPLES, FSA_KEY,
				triple -> triple.getLeft().lessOrEqual(triple.getMiddle()));
		speedup(tarsis, fsa);
//		compareBools(tarsis, fsa);
	}

	@Test
	public void bench04widening() throws SemanticException {
		LOG.info("Benchmarking widening");
		Map<Integer, RunResult<Tarsis>> tarsis = benchmark(TARSIS_TRIPLES, TARSIS_KEY,
				triple -> triple.getLeft().widening(triple.getRight()));
		Map<Integer, RunResult<FSA>> fsa = benchmark(FSA_TRIPLES, FSA_KEY,
				triple -> triple.getLeft().widening(triple.getRight()));
		speedup(tarsis, fsa);
//		compare(tarsis, fsa);
	}

	@Test
	public void bench05concat() throws SemanticException {
		LOG.info("Benchmarking concat");
		Map<Integer, RunResult<Tarsis>> tarsis = benchmark(TARSIS_TRIPLES, TARSIS_KEY,
				triple -> triple.getLeft().concat(triple.getMiddle()));
		Map<Integer, RunResult<FSA>> fsa = benchmark(FSA_TRIPLES, FSA_KEY,
				triple -> triple.getLeft().concat(triple.getMiddle()));
		speedup(tarsis, fsa);
//		compare(tarsis, fsa);
	}

	@Test
	public void bench06contains() throws SemanticException {
		LOG.info("Benchmarking contains");
		Map<Integer, RunResult<Satisfiability>> tarsis = benchmark(TARSIS_TRIPLES, TARSIS_KEY,
				triple -> triple.getLeft().contains(triple.getMiddle()));
		Map<Integer, RunResult<Satisfiability>> fsa = benchmark(FSA_TRIPLES, FSA_KEY,
				triple -> triple.getLeft().contains(triple.getMiddle()));
		speedup(tarsis, fsa);
//		compareSats(tarsis, fsa);
	}

	@Test
	public void bench08indexOf() throws SemanticException {
		LOG.info("Benchmarking indexOf");
		Map<Integer, RunResult<IntInterval>> tarsis = benchmark(TARSIS_TRIPLES, TARSIS_KEY,
				triple -> triple.getLeft().indexOf(triple.getMiddle()));
		Map<Integer, RunResult<IntInterval>> fsa = benchmark(FSA_TRIPLES, FSA_KEY,
				triple -> triple.getLeft().indexOf(triple.getMiddle()));
		speedup(tarsis, fsa);
//		compareIntervals(tarsis, fsa);
	}

	@Test
	public void bench07length() throws SemanticException {
		LOG.info("Benchmarking length");
		Map<Integer, RunResult<IntInterval>> tarsis = benchmark(TARSIS_TRIPLES, TARSIS_KEY,
				triple -> triple.getLeft().length());
		Map<Integer, RunResult<IntInterval>> fsa = benchmark(FSA_TRIPLES, FSA_KEY, triple -> triple.getLeft().length());
		speedup(tarsis, fsa);
//		compareIntervals(tarsis, fsa);
	}

	@Test
	public void bench10replace() throws SemanticException {
		LOG.info("Benchmarking replace");
		Map<Integer, RunResult<Tarsis>> tarsis = benchmark(TARSIS_TRIPLES, TARSIS_KEY,
				triple -> triple.getLeft().replace(triple.getMiddle(), triple.getRight()));
		Map<Integer, RunResult<FSA>> fsa = benchmark(FSA_TRIPLES, FSA_KEY,
				triple -> triple.getLeft().replace(triple.getMiddle(), triple.getRight()));
		speedup(tarsis, fsa);
//		compare(tarsis, fsa);
	}

	@Test
	public void bench09substring() throws SemanticException {
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
		speedup(tarsis, fsa);
//		compare(tarsis, fsa);
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
		long max = -1L, min = Long.MAX_VALUE, total = 0;
		double average = 0;
		for (Entry<Integer, RunResult<O>> entry : results.entrySet()) {
			if (entry.getValue().timeout)
				timeouts++;
			else if (entry.getValue().e != null) {
				failures++;
				LOG.warn("Iteration " + entry.getKey() + " failed with exception", entry.getValue().e);
			} else {
				long elapsed = entry.getValue().elapsed;
				total += elapsed;
				min = Math.min(min, elapsed);
				max = Math.max(max, elapsed);
				average = ((average * successes) + elapsed) / (successes + 1);
				successes++;
			}
		}

		if (successes == 0)
			// cleaner displaying
			max = min = 0;

		LOG.info("  Results for " + key + ": " + successes + " successes, " + failures + " failures, " + timeouts
				+ " timeouts");
		LOG.info("    Execution time for successes: total: " + TimeFormat.UP_TO_MINUTES.format(total)
				+ ", average: " + TimeFormat.UP_TO_MINUTES.format((long) average)
				+ ", minimum: " + TimeFormat.UP_TO_MINUTES.format(min)
				+ ", maximum: " + TimeFormat.UP_TO_MINUTES.format(max));

		return results;
	}

	private static <O1, O2> void speedup(Map<Integer, RunResult<O1>> tarsis, Map<Integer, RunResult<O2>> fsa) {
		long totalTarsis = 0, totalFSA = 0;
		for (Entry<Integer, RunResult<O1>> entry : tarsis.entrySet()) {
			RunResult<O1> t = entry.getValue();
			if (!t.timeout && t.e == null && t.result != null) {
				RunResult<O2> f = fsa.get(entry.getKey());
				if (!f.timeout && f.e == null && f.result != null) {
					totalTarsis += t.elapsed;
					totalFSA += f.elapsed;
				}
			}
		}

		LOG.info("  Average speedup: " + (((totalFSA - totalTarsis) / totalFSA) * 100) + "%");
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
