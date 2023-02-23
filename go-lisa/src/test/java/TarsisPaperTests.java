import static it.unive.lisa.LiSAFactory.getDefaultFor;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import it.unive.golisa.analysis.scam.SmashedSum;
import it.unive.golisa.cfg.expression.literal.GoString;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.CFGWithAnalysisResults;
import it.unive.lisa.analysis.SemanticDomain.Satisfiability;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.nonrelational.value.BaseNonRelationalValueDomain;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.numeric.Interval;
import it.unive.lisa.analysis.string.CharInclusion;
import it.unive.lisa.analysis.string.ContainsCharProvider;
import it.unive.lisa.analysis.string.Prefix;
import it.unive.lisa.analysis.string.Suffix;
import it.unive.lisa.analysis.string.bricks.Bricks;
import it.unive.lisa.analysis.string.fsa.FSA;
import it.unive.lisa.analysis.string.tarsis.Tarsis;
import it.unive.lisa.analysis.traces.TracePartitioning;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.checks.semantic.CheckToolWithAnalysisResults;
import it.unive.lisa.checks.semantic.SemanticCheck;
import it.unive.lisa.interprocedural.ContextBasedAnalysis;
import it.unive.lisa.interprocedural.RecursionFreeToken;
import it.unive.lisa.interprocedural.ReturnTopPolicy;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.program.cfg.statement.call.UnresolvedCall;
import it.unive.lisa.symbolic.SymbolicExpression;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TarsisPaperTests extends GoAnalysisTestExecutor {

	private static class AssertionCheck<A extends AbstractState<A, H, V, T>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>,
			T extends TypeDomain<T>> implements SemanticCheck<A, H, V, T> {

		@Override
		public void beforeExecution(CheckToolWithAnalysisResults<A, H, V, T> tool) {
		}

		@Override
		public void afterExecution(CheckToolWithAnalysisResults<A, H, V, T> tool) {
		}

		@Override
		public boolean visitUnit(CheckToolWithAnalysisResults<A, H, V, T> tool, Unit unit) {
			return true;
		}

		@Override
		public void visitGlobal(CheckToolWithAnalysisResults<A, H, V, T> tool, Unit unit, Global global,
				boolean instance) {
		}

		@Override
		public boolean visit(CheckToolWithAnalysisResults<A, H, V, T> tool, CFG graph) {
			return true;
		}

		boolean first = true;

		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean visit(CheckToolWithAnalysisResults<A, H, V, T> tool, CFG graph, Statement node) {
			if (node instanceof UnresolvedCall)
				if (((UnresolvedCall) node).getTargetName().equals("assert")) {
					for (CFGWithAnalysisResults<A, H, V, T> res : tool.getResultOf(graph)) {
						AnalysisState<A, H, V, T> post = res.getAnalysisStateAfter(node.getEvaluationPredecessor());
						try {
							SimpleAbstractState state = post.getDomainInstance(SimpleAbstractState.class);

							if (first) {
								System.err.println(state.getValueState().representation().toString());
								first = false;
							}

							if (((UnresolvedCall) node).getParameters()[0].toString()
									.startsWith("main::containsChar")) {
								Expression[] args = ((UnresolvedCall) ((UnresolvedCall) node).getParameters()[0])
										.getParameters();
								VariableRef variable = (VariableRef) args[0];
								GoString ch = (GoString) args[1];
								AnalysisState<A, H, V, T> target = res.getAnalysisStateAfter(variable);
								for (SymbolicExpression expr : target.getComputedExpressions()) {
									ContainsCharProvider lattice = (ContainsCharProvider) ((SmashedSum) target
											.getDomainInstance(ValueEnvironment.class).getState(expr)).getStringValue();
									Satisfiability sat = lattice.containsChar(ch.getValue().charAt(0));
									if (sat == Satisfiability.UNKNOWN)
										tool.warnOn(node, "This assertion might fail");
									else if (sat == Satisfiability.NOT_SATISFIED)
										tool.warnOn(node, "This assertion always fails");
								}
							} else
								for (SymbolicExpression expr : post.getComputedExpressions()) {
									Satisfiability sat = state.satisfies(expr, node);
									if (sat == Satisfiability.UNKNOWN)
										tool.warnOn(node, "This assertion might fail");
									else if (sat == Satisfiability.NOT_SATISFIED)
										tool.warnOn(node, "This assertion always fails");
								}
						} catch (SemanticException e) {
							throw new RuntimeException(e);
						}
					}
				}
			return true;
		}

		@Override
		public boolean visit(CheckToolWithAnalysisResults<A, H, V, T> tool, CFG graph, Edge edge) {
			return true;
		}

	}

	private static <S extends BaseNonRelationalValueDomain<S>> LiSAConfiguration baseConf(S stringDomain)
			throws AnalysisSetupException {
		return baseConf(stringDomain, false);
	}

	private static <S extends BaseNonRelationalValueDomain<S>> LiSAConfiguration baseConf(S stringDomain, boolean dump)
			throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.jsonOutput = true;
		conf.abstractState = new TracePartitioning<>(getDefaultFor(AbstractState.class, getDefaultFor(HeapDomain.class),
				new SmashedSum<>(new Interval(), stringDomain),
				new InferredTypes()));
		conf.serializeResults = true;
		conf.semanticChecks.add(new AssertionCheck<>());
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(RecursionFreeToken.getSingleton());
		if (dump)
			conf.analysisGraphs = it.unive.lisa.LiSAConfiguration.GraphType.HTML_WITH_SUBNODES;
		return conf;
	}

	@Test
	public void toStringPrefixTest() throws IOException, AnalysisSetupException {
		perform("tarsis/tostring", "prefix", "tostring.go", baseConf(new Prefix()));
	}

	@Test
	public void toStringSuffixTest() throws IOException, AnalysisSetupException {
		perform("tarsis/tostring", "suffix", "tostring.go", baseConf(new Suffix()));
	}

	@Test
	public void toStringCiTest() throws IOException, AnalysisSetupException {
		perform("tarsis/tostring", "ci", "tostring.go", baseConf(new CharInclusion()));
	}

	@Test
	public void toStringBricksTest() throws IOException, AnalysisSetupException {
		perform("tarsis/tostring", "bricks", "tostring.go", baseConf(new Bricks()));
	}

	@Test
	public void toStringFaTest() throws IOException, AnalysisSetupException {
		fail("INCORRECT APPROXIMATION OF RES THAT LEADS TO FAILURES");
		LiSAConfiguration conf = baseConf(new FSA());
		conf.serializeResults = false; // too expensive
		perform("tarsis/tostring", "fa", "tostring.go", conf);
	}

	@Test
	public void toStringTarsisTest() throws IOException, AnalysisSetupException {
		LiSAConfiguration conf = baseConf(new Tarsis());
		conf.serializeResults = false; // too expensive
		perform("tarsis/tostring", "tarsis", "tostring.go", conf);
	}

	@Test
	public void substringPrefixTest() throws IOException, AnalysisSetupException {
		perform("tarsis/substring", "prefix", "subs.go", baseConf(new Prefix()));
	}

	@Test
	public void substringSuffixTest() throws IOException, AnalysisSetupException {
		perform("tarsis/substring", "suffix", "subs.go", baseConf(new Suffix()));
	}

	@Test
	public void substringCiTest() throws IOException, AnalysisSetupException {
		perform("tarsis/substring", "ci", "subs.go", baseConf(new CharInclusion()));
	}

	@Test
	public void substringBricksTest() throws IOException, AnalysisSetupException {
		perform("tarsis/substring", "bricks", "subs.go", baseConf(new Bricks()));
	}

	@Test
	public void substringFaTest() throws IOException, AnalysisSetupException {
		fail("FAILS AND TAKES TOO MUCH TO DEBUG");
		LiSAConfiguration conf = baseConf(new FSA());
		conf.serializeResults = false; // too expensive
		perform("tarsis/substring", "fa", "subs.go", conf);
	}

	@Test
	public void substringTarsisTest() throws IOException, AnalysisSetupException {
		perform("tarsis/substring", "tarsis", "subs.go", baseConf(new Tarsis()));
	}

	@Test
	public void loopPrefixTest() throws IOException, AnalysisSetupException {
		perform("tarsis/loop", "prefix", "loop.go", baseConf(new Prefix()));
	}

	@Test
	public void loopSuffixTest() throws IOException, AnalysisSetupException {
		perform("tarsis/loop", "suffix", "loop.go", baseConf(new Suffix()));
	}

	@Test
	public void loopCiTest() throws IOException, AnalysisSetupException {
		perform("tarsis/loop", "ci", "loop.go", baseConf(new CharInclusion()));
	}

	@Test
	public void loopBricksTest() throws IOException, AnalysisSetupException {
		perform("tarsis/loop", "bricks", "loop.go", baseConf(new Bricks()));
	}

	@Test
	public void loopFaTest() throws IOException, AnalysisSetupException {
		fail("FAILS AND TAKES TOO MUCH TO DEBUG");
		LiSAConfiguration conf = baseConf(new FSA());
		conf.serializeResults = false;
		perform("tarsis/loop", "fa", "loop.go", conf);
	}

	@Test
	public void loopTarsisTest() throws IOException, AnalysisSetupException {
		LiSAConfiguration conf = baseConf(new Tarsis());
		conf.serializeResults = false; // too expensive
		perform("tarsis/loop", "tarsis", "loop.go", conf);
	}

	@Test
	public void cmPrefixTest() throws IOException, AnalysisSetupException {
		perform("tarsis/count", "prefix", "count.go", baseConf(new Prefix()));
	}

	@Test
	public void cmSuffixTest() throws IOException, AnalysisSetupException {
		perform("tarsis/count", "suffix", "count.go", baseConf(new Suffix()));
	}

	@Test
	public void cmCiTest() throws IOException, AnalysisSetupException {
		perform("tarsis/count", "ci", "count.go", baseConf(new CharInclusion()));
	}

	@Test
	public void cmBricksTest() throws IOException, AnalysisSetupException {
		perform("tarsis/count", "bricks", "count.go", baseConf(new Bricks()));
	}

	@Test
	public void cmFaTest() throws IOException, AnalysisSetupException {
		fail("WARNINGS ARE WRONG");
		LiSAConfiguration conf = baseConf(new FSA());
		conf.serializeResults = false;
		perform("tarsis/count", "fa", "count.go", conf);
	}

	@Test
	public void cmTarsisTest() throws IOException, AnalysisSetupException {
		perform("tarsis/count", "tarsis", "count.go", baseConf(new Tarsis()));
	}
}
