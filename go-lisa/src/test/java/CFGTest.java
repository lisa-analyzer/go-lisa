
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.LiSAFactory;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.heap.MonolithicHeap;
import it.unive.lisa.analysis.representation.DomainRepresentation;
import it.unive.lisa.analysis.representation.StringRepresentation;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.caches.Caches;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import it.unive.lisa.util.collections.externalSet.ExternalSet;
import java.util.function.Predicate;
import org.junit.Test;

public class CFGTest extends GoAnalysisTestExecutor {

	private static LiSAConfiguration mkConf() throws AnalysisSetupException {
		return new LiSAConfiguration()
				.setSerializeResults(true)
				.setAbstractState(LiSAFactory.getDefaultFor(AbstractState.class,
						new MonolithicHeap(),
						new NoOpValues(),
						new NoOpTypes()));
	}

	static class NoOpValues implements ValueDomain<NoOpValues> {

		@Override
		public CFGTest.NoOpValues assign(Identifier id, ValueExpression expression, ProgramPoint pp)
				throws SemanticException {
			return this;
		}

		@Override
		public CFGTest.NoOpValues smallStepSemantics(ValueExpression expression, ProgramPoint pp)
				throws SemanticException {
			return this;
		}

		@Override
		public CFGTest.NoOpValues assume(ValueExpression expression, ProgramPoint pp) throws SemanticException {
			return this;
		}

		@Override
		public CFGTest.NoOpValues forgetIdentifier(Identifier id) throws SemanticException {
			return this;
		}

		@Override
		public CFGTest.NoOpValues forgetIdentifiersIf(Predicate<Identifier> test) throws SemanticException {
			return this;
		}

		@Override
		public Satisfiability satisfies(ValueExpression expression, ProgramPoint pp) throws SemanticException {
			return Satisfiability.UNKNOWN;
		}

		@Override
		public CFGTest.NoOpValues pushScope(ScopeToken token) throws SemanticException {
			return this;
		}

		@Override
		public CFGTest.NoOpValues popScope(ScopeToken token) throws SemanticException {
			return this;
		}

		@Override
		public DomainRepresentation representation() {
			return new StringRepresentation("noop");
		}

		@Override
		public CFGTest.NoOpValues lub(CFGTest.NoOpValues other) throws SemanticException {
			return this;
		}

		@Override
		public CFGTest.NoOpValues widening(CFGTest.NoOpValues other) throws SemanticException {
			return this;
		}

		@Override
		public boolean lessOrEqual(CFGTest.NoOpValues other) throws SemanticException {
			return true;
		}

		@Override
		public CFGTest.NoOpValues top() {
			return this;
		}

		@Override
		public CFGTest.NoOpValues bottom() {
			return this;
		}

	}

	static class NoOpTypes implements TypeDomain<NoOpTypes> {

		@Override
		public CFGTest.NoOpTypes assign(Identifier id, ValueExpression expression, ProgramPoint pp)
				throws SemanticException {
			return this;
		}

		@Override
		public CFGTest.NoOpTypes smallStepSemantics(ValueExpression expression, ProgramPoint pp)
				throws SemanticException {
			return this;
		}

		@Override
		public CFGTest.NoOpTypes assume(ValueExpression expression, ProgramPoint pp) throws SemanticException {
			return this;
		}

		@Override
		public CFGTest.NoOpTypes forgetIdentifier(Identifier id) throws SemanticException {
			return this;
		}

		@Override
		public CFGTest.NoOpTypes forgetIdentifiersIf(Predicate<Identifier> test) throws SemanticException {
			return this;
		}

		@Override
		public Satisfiability satisfies(ValueExpression expression, ProgramPoint pp) throws SemanticException {
			return Satisfiability.UNKNOWN;
		}

		@Override
		public CFGTest.NoOpTypes pushScope(ScopeToken token) throws SemanticException {
			return this;
		}

		@Override
		public CFGTest.NoOpTypes popScope(ScopeToken token) throws SemanticException {
			return this;
		}

		@Override
		public DomainRepresentation representation() {
			return new StringRepresentation("noop");
		}

		@Override
		public CFGTest.NoOpTypes lub(CFGTest.NoOpTypes other) throws SemanticException {
			return this;
		}

		@Override
		public CFGTest.NoOpTypes widening(CFGTest.NoOpTypes other) throws SemanticException {
			return this;
		}

		@Override
		public boolean lessOrEqual(CFGTest.NoOpTypes other) throws SemanticException {
			return true;
		}

		@Override
		public CFGTest.NoOpTypes top() {
			return this;
		}

		@Override
		public CFGTest.NoOpTypes bottom() {
			return this;
		}

		@Override
		public ExternalSet<Type> getInferredRuntimeTypes() {
			return Caches.types().mkUniversalSet();
		}

		@Override
		public Type getInferredDynamicType() {
			return Untyped.INSTANCE;
		}
	}

	@Test
	public void testDeclaration() throws AnalysisSetupException {
		LiSAConfiguration conf = mkConf();
		perform("cfg/decl", "go-decl.go", conf);
	}

	@Test
	public void testIf() throws AnalysisSetupException {
		LiSAConfiguration conf = mkConf();
		perform("cfg/if", "go-if.go", conf);
	}

	@Test
	public void testFor() throws AnalysisSetupException {
		LiSAConfiguration conf = mkConf();
		perform("cfg/for", "go-for.go", conf);
	}

	@Test
	public void testTypes() throws AnalysisSetupException {
		LiSAConfiguration conf = mkConf();
		perform("cfg/types", "go-types.go", conf);
	}

	@Test
	public void testTour() throws AnalysisSetupException {
		LiSAConfiguration conf = mkConf();
		perform("cfg/tour", "go-tour.go", conf);
	}

	@Test
	public void testExprSwitch() throws AnalysisSetupException {
		LiSAConfiguration conf = mkConf();
		perform("cfg/switch/expr", "go-switch.go", conf);
	}

	@Test
	public void testTypeSwitch() throws AnalysisSetupException {
		LiSAConfiguration conf = mkConf();
		perform("cfg/switch/type", "go-switch.go", conf);
	}

	@Test
	public void testReturn() throws AnalysisSetupException {
		LiSAConfiguration conf = mkConf();
		perform("cfg/return", "go-return.go", conf);
	}

	@Test
	public void testChannel() throws AnalysisSetupException {
		LiSAConfiguration conf = mkConf();
		perform("cfg/channel", "go-channel.go", conf);
	}

	@Test
	public void testRoutine() throws AnalysisSetupException {
		LiSAConfiguration conf = mkConf();
		perform("cfg/routine", "go-routine.go", conf);
	}

	@Test
	public void testGoTo() throws AnalysisSetupException {
		LiSAConfiguration conf = mkConf();
		perform("cfg/goto", "goto.go", conf);
	}
}
