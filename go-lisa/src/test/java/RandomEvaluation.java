import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Test;

import it.unive.lisa.analysis.SemanticDomain.Satisfiability;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.string.fsa.FSA;
import it.unive.lisa.analysis.string.tarsis.RegexAutomaton;
import it.unive.lisa.analysis.string.tarsis.Tarsis;
import it.unive.lisa.util.datastructures.automaton.CyclicAutomatonException;
import it.unive.lisa.util.datastructures.automaton.State;
import it.unive.lisa.util.datastructures.automaton.Transition;
import it.unive.lisa.util.datastructures.regex.Atom;
import it.unive.lisa.util.datastructures.regex.RegularExpression;
import it.unive.lisa.util.datastructures.regex.TopAtom;

public class RandomEvaluation {

	private static final Random random = new Random();


	private final SortedSet<State> states = new TreeSet<>();
	private final Map<Integer, State> mapping = new HashMap<>();

	private final int numberOfTransitionsForEachState = 2;
	private final int numberOfGeneratedAutomata = 100;

	private enum StringOps {
		OP_LEN,
		OP_LUB,
		OP_LEQ,
		OP_CONTAINS,
		OP_SUBSTRING,
		OP_CONCAT,
		OP_REPLACE,
		OP_INDEXOF,
		OP_WID
	}
	
	@Before
	public void initialize() {
		State q0 = new State(0, true, false);
		states.add(q0);
		mapping.put(0, q0);

		State q1 = new State(1, false, false);
		states.add(q1);
		mapping.put(1, q1);

		State q2 = new State(2, false, false);
		states.add(q2);
		mapping.put(2, q2);

		State q3 = new State(3, false, true);
		states.add(q3);
		mapping.put(3, q3);

		State q4 = new State(4, false, true);
		states.add(q4);
		mapping.put(4, q4);

		State q5 = new State(5, false, false);
		states.add(q5);
		mapping.put(5, q5);
	}

	@Test
	public void lubEvaluation() throws SemanticException, CyclicAutomatonException {
		int cumulativeTarsisLub = 0;
		int cumulativeFsaLub = 0;
		int timedOutTarsis = 0;
		int timedOutFSA = 0;


		for (int k = 0; k < numberOfGeneratedAutomata; k++) {
			Tarsis a = generateAutomaton(states, mapping, numberOfTransitionsForEachState);
			Tarsis b = generateAutomaton(states, mapping, numberOfTransitionsForEachState);

			FSA afsa = a.toFSA();
			FSA bfsa = b.toFSA();

			System.err.print(k + " ");

			// testing lub
			long startTime = System.currentTimeMillis();
			long estimatedTimeTarsis = 0;

			if (!new AutomatonOperation(StringOps.OP_LUB, a, b).runTest()) 
				timedOutTarsis++;
			else
				estimatedTimeTarsis = System.currentTimeMillis() - startTime;

			startTime = System.currentTimeMillis();
			long estimatedTimeFSA = 0;
			if (!new AutomatonOperation(StringOps.OP_LUB, afsa, bfsa).runTest()) 
				timedOutFSA++;
			else	
				estimatedTimeFSA = System.currentTimeMillis() - startTime;

			cumulativeFsaLub += estimatedTimeFSA;
			cumulativeTarsisLub += estimatedTimeTarsis;			
		}

		System.err.println();
		System.err.println("Lub: tarsis(timed-out = " + timedOutTarsis + ": )" + cumulativeTarsisLub + " fsa: (timed-out = " + timedOutFSA + ": )"+ cumulativeFsaLub);
	}

	@Test
	public void leqEvaluation() throws SemanticException, CyclicAutomatonException {
		int cumulativeTarsisLeq = 0;
		int cumulativeFsaLeq = 0;
		int timedOutTarsis = 0;
		int timedOutFSA = 0;

		for (int k = 0; k < numberOfGeneratedAutomata; k++) {
			Tarsis a = generateAutomaton(states, mapping, numberOfTransitionsForEachState);
			Tarsis b = generateAutomaton(states, mapping, numberOfTransitionsForEachState);

			FSA afsa = a.toFSA();
			FSA bfsa = b.toFSA();

			System.err.print(k + " ");

			// testing leq
			long startTime = System.currentTimeMillis();
			long estimatedTimeTarsis = 0;

			if (!new AutomatonOperation(StringOps.OP_LEQ, a, b).runTest()) 
				timedOutTarsis++;
			else
				estimatedTimeTarsis = System.currentTimeMillis() - startTime;

			startTime = System.currentTimeMillis();
			long estimatedTimeFSA = 0;
			if (!new AutomatonOperation(StringOps.OP_LEQ, afsa, bfsa).runTest()) 
				timedOutFSA++;
			else	
				estimatedTimeFSA = System.currentTimeMillis() - startTime;

			cumulativeFsaLeq += estimatedTimeFSA;
			cumulativeTarsisLeq += estimatedTimeTarsis;			
		}

		System.err.println();
		System.err.println("Leq: tarsis(timed-out = " + timedOutTarsis + ": )" + cumulativeTarsisLeq + " fsa: (timed-out = " + timedOutFSA + ": )"+ cumulativeFsaLeq);
	}

	@Test
	public void concatEvaluation() throws SemanticException, CyclicAutomatonException {
		int cumulativeTarsisConcat = 0;
		int cumulativeFsaConcat = 0;
		int timedOutTarsis = 0;
		int timedOutFSA = 0;

		for (int k = 0; k < numberOfGeneratedAutomata; k++) {
			Tarsis a = generateAutomaton(states, mapping, numberOfTransitionsForEachState);
			Tarsis b = generateAutomaton(states, mapping, numberOfTransitionsForEachState);

			FSA afsa = a.toFSA();
			FSA bfsa = b.toFSA();

			System.err.print(k + " ");

			// testing concat
			long startTime = System.currentTimeMillis();
			long estimatedTimeTarsis = 0;

			if (!new AutomatonOperation(StringOps.OP_CONCAT, a, b).runTest()) 
				timedOutTarsis++;
			else
				estimatedTimeTarsis = System.currentTimeMillis() - startTime;

			startTime = System.currentTimeMillis();
			long estimatedTimeFSA = 0;
			if (!new AutomatonOperation(StringOps.OP_CONCAT, afsa, bfsa).runTest()) 
				timedOutFSA++;
			else	
				estimatedTimeFSA = System.currentTimeMillis() - startTime;

			cumulativeFsaConcat += estimatedTimeFSA;
			cumulativeTarsisConcat += estimatedTimeTarsis;			
		}

		System.err.println();
		System.err.println("Concat: tarsis(timed-out = " + timedOutTarsis + ": )" + cumulativeTarsisConcat + " fsa: (timed-out = " + timedOutFSA + ": )"+ cumulativeFsaConcat);
	}

	@Test
	public void substringEvaluation() throws SemanticException, CyclicAutomatonException {
		long cumulativeFsaSubstring = 0;
		long cumulativeTarsisSubstring = 0;
		int timedOutTarsis = 0;
		int timedOutFSA = 0;

		for (int k = 0; k < numberOfGeneratedAutomata; k++) {
			Tarsis a = generateAutomaton(states, mapping, numberOfTransitionsForEachState);

			FSA afsa = a.toFSA();

			System.err.print(k + " ");

			// testing substring
			int init = (int) (Math.random() * 20);
			int end = (int) (Math.random() * 20);

			if (init > end) {
				int tmp = init;
				init = end;
				end = tmp;
			}
			long estimatedTimeTarsis = 0;
			long startTime = System.currentTimeMillis();
			if (!new AutomatonOperation(StringOps.OP_SUBSTRING, a, (long) init, (long) end).runTest()) 
				timedOutTarsis++;
			else
				estimatedTimeTarsis = System.currentTimeMillis() - startTime;

			startTime = System.currentTimeMillis();
			long estimatedTimeFSA = 0;

			if (!new AutomatonOperation(StringOps.OP_SUBSTRING, afsa, (long) init, (long) end).runTest()) 
				timedOutFSA++;
			else	
				estimatedTimeFSA = System.currentTimeMillis() - startTime;

			cumulativeFsaSubstring += estimatedTimeFSA;
			cumulativeTarsisSubstring += estimatedTimeTarsis;
		}

		System.err.println();
		System.err.println("Substring: tarsis(timed-out = " + timedOutTarsis + ": )" + cumulativeTarsisSubstring + " fsa: (timed-out = " + timedOutFSA + ": )"+ cumulativeFsaSubstring);
	}


	@Test
	public void lengthEvaluation() throws SemanticException, CyclicAutomatonException {

		int cumulativeTarsisLength = 0;
		int cumulativeFsaLength = 0;
		int timedOutTarsis = 0;
		int timedOutFSA = 0;

		for (int k = 0; k < numberOfGeneratedAutomata; k++) {
			Tarsis a = generateAutomaton(states, mapping, numberOfTransitionsForEachState);

			FSA afsa = a.toFSA();

			System.err.print(k + " ");

			// testing length
			long startTime = System.currentTimeMillis();
			long estimatedTimeTarsis = 0;

			if (!new AutomatonOperation(StringOps.OP_LEN, a).runTest()) 
				timedOutTarsis++;
			else
				estimatedTimeTarsis = System.currentTimeMillis() - startTime;

			startTime = System.currentTimeMillis();
			long estimatedTimeFSA = 0;

			if (!new AutomatonOperation(StringOps.OP_LEN, afsa).runTest()) 
				timedOutFSA++;
			else	
				estimatedTimeFSA = System.currentTimeMillis() - startTime;

			cumulativeFsaLength += estimatedTimeFSA;
			cumulativeTarsisLength += estimatedTimeTarsis;			
		}

		System.err.println();
		System.err.println("Length: tarsis(timed-out = " + timedOutTarsis + ": )" + cumulativeTarsisLength + " fsa: (timed-out = " + timedOutFSA + ": )"+ cumulativeFsaLength);
	}

	@Test
	public void containsEvaluation() throws SemanticException, CyclicAutomatonException {
		int cumulativeTarsisContains = 0;
		int cumulativeFsaContains = 0;
		int timedOutTarsis = 0;
		int timedOutFSA = 0;

		for (int k = 0; k < numberOfGeneratedAutomata; k++) {
			Tarsis a = generateAutomaton(states, mapping, numberOfTransitionsForEachState);
			Tarsis b = generateAutomaton(states, mapping, numberOfTransitionsForEachState);

			FSA afsa = a.toFSA();
			FSA bfsa = b.toFSA();

			System.err.print(k + " ");

			// testing contains
			long startTime = System.currentTimeMillis();
			long estimatedTimeTarsis = 0;

			if (!new AutomatonOperation(StringOps.OP_CONTAINS, a, b).runTest()) 
				timedOutTarsis++;
			else
				estimatedTimeTarsis = System.currentTimeMillis() - startTime;

			startTime = System.currentTimeMillis();
			long estimatedTimeFSA = 0;
			if (!new AutomatonOperation(StringOps.OP_CONTAINS, afsa, bfsa).runTest()) 
				timedOutFSA++;
			else	
				estimatedTimeFSA = System.currentTimeMillis() - startTime;

			cumulativeFsaContains += estimatedTimeFSA;
			cumulativeTarsisContains += estimatedTimeTarsis;			
		}

		System.err.println();
		System.err.println("Contains: tarsis(timed-out = " + timedOutTarsis + ": )" + cumulativeTarsisContains + " fsa: (timed-out = " + timedOutFSA + ": )"+ cumulativeFsaContains);
	}

	@Test
	public void indexOfEvaluation() throws SemanticException, CyclicAutomatonException {
		int cumulativeTarsisContains = 0;
		int cumulativeFsaContains = 0;
		int timedOutTarsis = 0;
		int timedOutFSA = 0;

		for (int k = 0; k < numberOfGeneratedAutomata; k++) {
			Tarsis a = generateAutomaton(states, mapping, numberOfTransitionsForEachState);
			Tarsis b = generateAutomaton(states, mapping, numberOfTransitionsForEachState);

			FSA afsa = a.toFSA();
			FSA bfsa = b.toFSA();

			if (a.contains(b) == Satisfiability.NOT_SATISFIED) {
				k--;
				continue;
			}

			System.err.print(k + " ");

			// testing indexof
			long startTime = System.currentTimeMillis();
			long estimatedTimeTarsis = 0;

			if (!new AutomatonOperation(StringOps.OP_INDEXOF, a, b).runTest()) 
				timedOutTarsis++;
			else
				estimatedTimeTarsis = System.currentTimeMillis() - startTime;

			startTime = System.currentTimeMillis();
			long estimatedTimeFSA = 0;
			if (!new AutomatonOperation(StringOps.OP_INDEXOF, afsa, bfsa).runTest()) 
				timedOutFSA++;
			else	
				estimatedTimeFSA = System.currentTimeMillis() - startTime;

			cumulativeFsaContains += estimatedTimeFSA;
			cumulativeTarsisContains += estimatedTimeTarsis;			
		}
		System.err.println();
		System.err.println("IndexOf: tarsis(timed-out = " + timedOutTarsis + ": )" + cumulativeTarsisContains + " fsa: (timed-out = " + timedOutFSA + ": )"+ cumulativeFsaContains);
	}

	@Test
	public void replaceEvaluation() throws SemanticException, CyclicAutomatonException {
		int cumulativeTarsisReplace = 0;
		int cumulativeFsaReplace = 0;
		int timedOutTarsis = 0;
		int timedOutFSA = 0;

		for (int k = 0; k < numberOfGeneratedAutomata; k++) {
			Tarsis a = generateAutomaton(states, mapping, numberOfTransitionsForEachState);
			Tarsis b = generateAutomaton(states, mapping, numberOfTransitionsForEachState);
			Tarsis c = generateAutomaton(states, mapping, numberOfTransitionsForEachState);

			FSA afsa = a.toFSA();
			FSA bfsa = b.toFSA();
			FSA cfsa = c.toFSA();

			System.err.print(k + " ");

			// testing contains
			long startTime = System.currentTimeMillis();
			long estimatedTimeTarsis = 0;

			if (!new AutomatonOperation(StringOps.OP_REPLACE, a, b, c).runTest()) 
				timedOutTarsis++;
			else
				estimatedTimeTarsis = System.currentTimeMillis() - startTime;

			startTime = System.currentTimeMillis();
			long estimatedTimeFSA = 0;
			if (!new AutomatonOperation(StringOps.OP_REPLACE, afsa, bfsa, cfsa).runTest()) 
				timedOutFSA++;
			else	
				estimatedTimeFSA = System.currentTimeMillis() - startTime;

			cumulativeFsaReplace += estimatedTimeFSA;
			cumulativeTarsisReplace += estimatedTimeTarsis;			
		}

		System.err.println();
		System.err.println("Replace: tarsis(timed-out = " + timedOutTarsis + ": )" + cumulativeTarsisReplace + " fsa: (timed-out = " + timedOutFSA + ": )"+ cumulativeFsaReplace);
	}

	@Test
	public void wideningEvaluation() throws SemanticException, CyclicAutomatonException {

		int cumulativeTarsisLength = 0;
		int cumulativeFsaLength = 0;
		int timedOutTarsis = 0;
		int timedOutFSA = 0;

		for (int k = 0; k < numberOfGeneratedAutomata; k++) {
			Tarsis a = generateAutomaton(states, mapping, numberOfTransitionsForEachState);

			FSA afsa = a.toFSA();

			int wid = a.length().getLow().getNumber().toBigInteger().intValue();

			System.err.print(k + " ");

			// testing length
			long startTime = System.currentTimeMillis();
			long estimatedTimeTarsis = 0;

			if (!new AutomatonOperation(StringOps.OP_WID, a, wid).runTest()) 
				timedOutTarsis++;
			else
				estimatedTimeTarsis = System.currentTimeMillis() - startTime;

			startTime = System.currentTimeMillis();
			long estimatedTimeFSA = 0;

			if (!new AutomatonOperation(StringOps.OP_WID, afsa, wid).runTest()) 
				timedOutFSA++;
			else	
				estimatedTimeFSA = System.currentTimeMillis() - startTime;

			cumulativeFsaLength += estimatedTimeFSA;
			cumulativeTarsisLength += estimatedTimeTarsis;			
		}

		System.err.println();
		System.err.println("Length: tarsis(timed-out = " + timedOutTarsis + ": )" + cumulativeTarsisLength + " fsa: (timed-out = " + timedOutFSA + ": )"+ cumulativeFsaLength);
	}

	public static RegularExpression randomString() {
		if (random.nextDouble() >= 0.8)
			return TopAtom.INSTANCE;

		String ALPHA_NUMERIC_STRING = "0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
		int init = random.nextInt(ALPHA_NUMERIC_STRING.length());
		int end = random.nextInt(ALPHA_NUMERIC_STRING.length());

		if (init > end) {
			int tmp = init;
			init = end;
			end = tmp;
		}

		return new Atom(ALPHA_NUMERIC_STRING.substring(init, end));
	}

	public static Tarsis generateAutomaton(SortedSet<State> states, Map<Integer, State> mapping,
			int numberOfTransitionsForEachState) {
		RegexAutomaton a = null;

		do {
			SortedSet<Transition<RegularExpression>> delta = new TreeSet<>();

			for (State s : states)
				for (int i = 0; i < numberOfTransitionsForEachState; i++)
					delta.add(new Transition<>(s, mapping.get(random.nextInt(states.size())), randomString()));

			a = new RegexAutomaton(states, delta).minimize();			
		} while (a.acceptsEmptyLanguage());

		return new Tarsis(a);
	}

	private class AutomatonOperation extends Thread {

		private final Object[] params;
		private final StringOps op;

		public AutomatonOperation(StringOps op, Object... automata) {
			this.params = automata;
			this.op = op;
		}

		@Override
		public void run() { 
	
			switch(op) {
			case OP_CONCAT:
				if (params[0] instanceof FSA)
					((FSA) params[0]).concat(((FSA) params[1]));
				else
					((Tarsis) params[0]).concat(((Tarsis) params[1]));
				break;
			case OP_CONTAINS:
				if (params[0] instanceof FSA)
					((FSA) params[0]).contains(((FSA) params[1]));
				else
					((Tarsis) params[0]).contains(((Tarsis) params[1]));
				break;
			case OP_INDEXOF:
				if (params[0] instanceof FSA)
					try {
						((FSA) params[0]).indexOf(((FSA) params[1]));
					} catch (CyclicAutomatonException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				else
					try {
						((Tarsis) params[0]).indexOf(((Tarsis) params[1]));
					} catch (CyclicAutomatonException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				break;
			case OP_LEN:
				if (params[0] instanceof FSA)
					((FSA) params[0]).length();
				else
					((Tarsis) params[0]).length();
				break;
			case OP_LEQ:
				if (params[0] instanceof FSA)
					try {
						((FSA) params[0]).lessOrEqual(((FSA) params[1]));
					} catch (SemanticException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				else
					try {
						((Tarsis) params[0]).lessOrEqual(((Tarsis) params[1]));
					} catch (SemanticException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				break;
			case OP_LUB:
				if (params[0] instanceof FSA)
					try {
						((FSA) params[0]).lub(((FSA) params[1]));
					} catch (SemanticException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				else
					try {
						((Tarsis) params[0]).lub(((Tarsis) params[1]));
					} catch (SemanticException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				break;
			case OP_REPLACE:
				if (params[0] instanceof FSA)
					((FSA) params[0]).replace(((FSA) params[1]), ((FSA) params[2]));
				else
					((Tarsis) params[0]).replace(((Tarsis) params[1]), ((Tarsis) params[2]));
				break;
			case OP_SUBSTRING:
				if (params[0] instanceof FSA)
					try {
						((FSA) params[0]).substring((long) params[1],  (long) params[2]);
					} catch (CyclicAutomatonException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				else
					((Tarsis) params[0]).substring((long) params[1],  (long) params[2]);
			case OP_WID:
				if (params[0] instanceof FSA)
					try {
						((FSA) params[0]).widening(((FSA) params[0]));
					} catch (SemanticException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				else
					try {
						((Tarsis) params[0]).widening(((Tarsis) params[0]));
					} catch (SemanticException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				break;
			default:
				throw new RuntimeException("Unknown operation");
			}
		}

		public boolean runTest() {
			final ExecutorService executor = Executors.newSingleThreadExecutor();
			final Future future = executor.submit(this);
			executor.shutdown(); // This does not cancel the already-scheduled task.

			try { 
				future.get(3, TimeUnit.MINUTES); 
			}
			catch (InterruptedException ie) { 
				System.err.println("Interrupted");
				return false;
			}
			catch (ExecutionException ee) { 

				ee.printStackTrace();
				return false;
			}
			catch (TimeoutException te) { 
				if (!executor.isTerminated())
					executor.shutdownNow(); // If you want to stop the code that hasn't finished.
				return false;
			}

			return true;
		}
	}	
}
