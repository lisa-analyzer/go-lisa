
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.checker.TaintChecker;
import it.unive.golisa.loader.annotation.AnnotationSet;
import it.unive.golisa.loader.annotation.CodeAnnotation;
import it.unive.golisa.loader.annotation.MethodAnnotation;
import it.unive.golisa.loader.annotation.MethodParameterAnnotation;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.nonrelational.value.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.interprocedural.ReturnTopPolicy;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;
import it.unive.lisa.interprocedural.context.ContextBasedAnalysis;

public class TaintAnalysisTest extends GoChaincodeTestExecutor {

	private final AnnotationSet annSet = new SimpleTaintAnnotationSet();

	@Test
	public void taintTest001() throws AnalysisSetupException {
		CronConfiguration conf = new CronConfiguration();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.semanticChecks.add(new TaintChecker());
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>();
		conf.compareWithOptimization = false;
		conf.testDir = "taint/t1";
		conf.programFile = "t1.go";
		conf.annSet = annSet;
		perform(conf);
	}

	@Test
	public void taintTest002() throws AnalysisSetupException {
		CronConfiguration conf = new CronConfiguration();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.semanticChecks.add(new TaintChecker());
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>();
		conf.compareWithOptimization = false;
		conf.testDir = "taint/t2";
		conf.programFile = "t2.go";
		conf.annSet = annSet;
		perform(conf);
	}

	@Test
	public void taintTest003() throws AnalysisSetupException {
		CronConfiguration conf = new CronConfiguration();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.semanticChecks.add(new TaintChecker());
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>();
		conf.compareWithOptimization = false;
		conf.testDir = "taint/t3";
		conf.programFile = "t3.go";
		conf.annSet = annSet;
		perform(conf);
	}

	@Test
	public void taintTest004() throws AnalysisSetupException {
		CronConfiguration conf = new CronConfiguration();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.semanticChecks.add(new TaintChecker());
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>();
		conf.compareWithOptimization = false;
		conf.testDir = "taint/t4";
		conf.programFile = "t4.go";
		conf.annSet = annSet;
		perform(conf);
	}

	@Test
	public void taintTest005() throws AnalysisSetupException {
		CronConfiguration conf = new CronConfiguration();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.semanticChecks.add(new TaintChecker());
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>();
		conf.compareWithOptimization = false;
		conf.testDir = "taint/t5";
		conf.programFile = "t5.go";
		conf.annSet = annSet;
		perform(conf);
	}

	@Test
	public void taintTest006() throws AnalysisSetupException {
		CronConfiguration conf = new CronConfiguration();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.semanticChecks.add(new TaintChecker());
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>();
		conf.compareWithOptimization = false;
		conf.testDir = "taint/t6";
		conf.programFile = "t6.go";
		conf.annSet = annSet;
		perform(conf);
	}

	@Test
	public void taintTest007() throws AnalysisSetupException {
		CronConfiguration conf = new CronConfiguration();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.semanticChecks.add(new TaintChecker());
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>();
		conf.compareWithOptimization = false;
		conf.testDir = "taint/t7";
		conf.programFile = "t7.go";
		conf.annSet = annSet;
		perform(conf);
	}

	@Test
	public void taintTest008() throws AnalysisSetupException {
		CronConfiguration conf = new CronConfiguration();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.semanticChecks.add(new TaintChecker());
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>();
		conf.compareWithOptimization = false;
		conf.testDir = "taint/t8";
		conf.programFile = "t8.go";
		conf.annSet = annSet;
		perform(conf);
	}

	@Test
	public void taintTest009() throws AnalysisSetupException {
		CronConfiguration conf = new CronConfiguration();
		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(),
				new ValueEnvironment<>(new TaintDomain()),
				new TypeEnvironment<>(new InferredTypes()));
		conf.semanticChecks.add(new TaintChecker());
		conf.jsonOutput = true;
		conf.openCallPolicy = ReturnTopPolicy.INSTANCE;
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>();
		conf.compareWithOptimization = false;
		conf.testDir = "taint/t9";
		conf.programFile = "t9.go";
		conf.annSet = annSet;
		perform(conf);
	}

	private class SimpleTaintAnnotationSet extends AnnotationSet {

		public SimpleTaintAnnotationSet() {

		}

		@Override
		public Set<? extends CodeAnnotation> getAnnotationsForCodeMembers() {
			Set<CodeAnnotation> set = new HashSet<>();
			set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "main", "source"));
			set.add(new MethodAnnotation(TaintChecker.SINK_ANNOTATION, "main", "sink"));
			set.add(new MethodParameterAnnotation(TaintChecker.SINK_ANNOTATION, "main", "sink", 0));

			return set;
		}

		@Override
		public Set<? extends CodeAnnotation> getAnnotationsForConstructors() {
			return new HashSet<>();
		}

		@Override
		public Set<? extends CodeAnnotation> getAnnotationsForGlobals() {
			return new HashSet<>();
		}

	}
}
