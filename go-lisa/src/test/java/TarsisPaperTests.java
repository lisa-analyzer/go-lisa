
import it.unive.golisa.analysis.scam.SmashedSum;
import it.unive.golisa.cfg.expression.literal.GoString;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.AnalyzedCFG;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.MonolithicHeap;
import it.unive.lisa.analysis.lattices.Satisfiability;
import it.unive.lisa.analysis.nonrelational.value.BaseNonRelationalValueDomain;
import it.unive.lisa.analysis.nonrelational.value.TypeEnvironment;
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
import it.unive.lisa.checks.semantic.CheckToolWithAnalysisResults;
import it.unive.lisa.checks.semantic.SemanticCheck;
import it.unive.lisa.interprocedural.ReturnTopPolicy;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;
import it.unive.lisa.interprocedural.context.ContextBasedAnalysis;
import it.unive.lisa.interprocedural.context.FullStackToken;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.program.cfg.statement.call.UnresolvedCall;
import it.unive.lisa.symbolic.SymbolicExpression;
import java.io.IOException;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@Ignore("This test should only be manually executed for the benchmark as it takes few hours")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TarsisPaperTests extends GoAnalysisTestExecutor {

	private static boolean isAssert(Statement st) {
		return st instanceof UnresolvedCall && ((UnresolvedCall) st).getTargetName().equals("assert");
	}

	private static class AssertionCheck<A extends AbstractState<A>> implements SemanticCheck<A> {

		@Override
		public void beforeExecution(CheckToolWithAnalysisResults<A> tool) {
		}

		@Override
		public void afterExecution(CheckToolWithAnalysisResults<A> tool) {
		}

		@Override
		public boolean visitUnit(CheckToolWithAnalysisResults<A> tool, Unit unit) {
			return true;
		}

		@Override
		public void visitGlobal(CheckToolWithAnalysisResults<A> tool, Unit unit, Global global,
				boolean instance) {
		}

		@Override
		public boolean visit(CheckToolWithAnalysisResults<A> tool, CFG graph) {
			return true;
		}

		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean visit(CheckToolWithAnalysisResults<A> tool, CFG graph, Statement node) {
			if (isAssert(node)) {
				for (AnalyzedCFG<A> res : tool.getResultOf(graph)) {
					AnalysisState<A> post = res.getAnalysisStateAfter(node.getEvaluationPredecessor());
					try {
						SimpleAbstractState state = post.getState().getDomainInstance(SimpleAbstractState.class);

						if (((UnresolvedCall) node).getParameters()[0].toString()
								.startsWith("main::containsChar")) {
							Expression[] args = ((UnresolvedCall) ((UnresolvedCall) node).getParameters()[0])
									.getParameters();
							VariableRef variable = (VariableRef) args[0];
							GoString ch = (GoString) args[1];
							AnalysisState<A> target = res.getAnalysisStateAfter(variable);
							for (SymbolicExpression expr : target.getComputedExpressions()) {
								ContainsCharProvider lattice = (ContainsCharProvider) ((SmashedSum) target.getState()
										.getDomainInstance(ValueEnvironment.class).getState(expr)).getStringValue();
								Satisfiability sat = lattice.containsChar(ch.getValue().charAt(0));
								if (sat == Satisfiability.UNKNOWN)
									tool.warnOn(node, "This assertion might fail");
								else if (sat == Satisfiability.NOT_SATISFIED)
									tool.warnOn(node, "This assertion always fails");
							}
						} else
							for (SymbolicExpression expr : post.getComputedExpressions()) {
								Satisfiability sat = state.satisfies(expr, node, state);
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
		public boolean visit(CheckToolWithAnalysisResults<A> tool, CFG graph, Edge edge) {
			return true;
		}

	}

	private static <S extends BaseNonRelationalValueDomain<S>> CronConfiguration baseConf(S stringDomain)
			throws AnalysisSetupException {
		return baseConf(stringDomain, false);
	}

	private static <S extends BaseNonRelationalValueDomain<S>> CronConfiguration baseConf(S stringDomain, boolean dump)
			throws AnalysisSetupException {
		CronConfiguration conf = new CronConfiguration();
		conf.jsonOutput = true;
		conf.abstractState = new TracePartitioning<>(new SimpleAbstractState<>(
				new MonolithicHeap(),
				new ValueEnvironment<>(new SmashedSum<>(new Interval(), stringDomain)),
				new TypeEnvironment<>(new InferredTypes())));
		conf.semanticChecks.add(new AssertionCheck<>());
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(FullStackToken.getSingleton());
		conf.compareWithOptimization = false;
		conf.optimize = true;
		conf.hotspots = st -> isAssert(st)
				|| (st instanceof Expression && isAssert(((Expression) st).getRootStatement()));
		if (dump) {
			conf.serializeResults = true;
			conf.analysisGraphs = it.unive.lisa.conf.LiSAConfiguration.GraphType.HTML_WITH_SUBNODES;
		}
		return conf;
	}

	private void perform(String dir, String subDir, String program, CronConfiguration conf) {
		conf.testDir = dir;
		conf.testSubDir = subDir;
		conf.programFile = program;
		perform(conf);
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
	@Ignore("This takes ~10 minutes to execute, so we keep it disabled by default")
	public void toStringFaTest() throws IOException, AnalysisSetupException {
		perform("tarsis/tostring", "fa", "tostring.go", baseConf(new FSA()));
	}

	@Test
	public void toStringTarsisTest() throws IOException, AnalysisSetupException {
		perform("tarsis/tostring", "tarsis", "tostring.go", baseConf(new Tarsis()));
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
		perform("tarsis/substring", "fa", "subs.go", baseConf(new FSA()));
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
		perform("tarsis/loop", "fa", "loop.go", baseConf(new FSA()));
	}

	@Test
	public void loopTarsisTest() throws IOException, AnalysisSetupException {
		perform("tarsis/loop", "tarsis", "loop.go", baseConf(new Tarsis()));
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
		perform("tarsis/count", "fa", "count.go", baseConf(new FSA()));
	}

	@Test
	public void cmTarsisTest() throws IOException, AnalysisSetupException {
		perform("tarsis/count", "tarsis", "count.go", baseConf(new Tarsis()));
	}
}
