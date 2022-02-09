
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.analysis.taint.annotation.AnnotationSet;
import it.unive.golisa.analysis.taint.annotation.CodeAnnotation;
import it.unive.golisa.analysis.taint.annotation.MethodAnnotation;
import it.unive.golisa.analysis.taint.annotation.MethodParameterAnnotation;
import it.unive.golisa.checker.TaintChecker;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.LiSAFactory;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.heap.MonolithicHeap;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.interprocedural.ContextBasedAnalysis;
import it.unive.lisa.interprocedural.ReturnTopPolicy;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;

public class TaintAnalysisTest extends GoChaincodeTestExecutor {
	
	private final AnnotationSet annSet = new SimpleTaintAnnotationSet();
	
	@Test
	public void taintTest001() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(
						LiSAFactory.getDefaultFor(AbstractState.class, new MonolithicHeap(), new TaintDomain(),
								new InferredTypes()))
				.addSemanticCheck(new TaintChecker())

				.setJsonOutput(true)
				.setDumpAnalysis(true)
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("taint/t1", "t1.go", conf, annSet);
	}

	@Test
	public void taintTest002() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(
						LiSAFactory.getDefaultFor(AbstractState.class, new MonolithicHeap(), new TaintDomain(),
								new InferredTypes()))
				.addSemanticCheck(new TaintChecker())

				.setJsonOutput(true)
				.setDumpAnalysis(true)
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("taint/t2", "t2.go", conf, annSet);
	}

	@Test
	public void taintTest003() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(
						LiSAFactory.getDefaultFor(AbstractState.class, new MonolithicHeap(), new TaintDomain(),
								new InferredTypes()))
				.addSemanticCheck(new TaintChecker())

				.setJsonOutput(true)
				.setDumpAnalysis(true)
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("taint/t3", "t3.go", conf, annSet);
	}

	@Test
	public void taintTest004() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(
						LiSAFactory.getDefaultFor(AbstractState.class, new MonolithicHeap(), new TaintDomain(),
								new InferredTypes()))
				.addSemanticCheck(new TaintChecker())

				.setJsonOutput(true)
				.setDumpAnalysis(true)
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("taint/t4", "t4.go", conf, annSet);
	}

	@Test
	public void taintTest005() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(
						LiSAFactory.getDefaultFor(AbstractState.class, new MonolithicHeap(), new TaintDomain(),
								new InferredTypes()))
				.addSemanticCheck(new TaintChecker())

				.setJsonOutput(true)
				.setDumpAnalysis(true)
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("taint/t5", "t5.go", conf, annSet);
	}

	@Test
	public void taintTest006() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(
						LiSAFactory.getDefaultFor(AbstractState.class, new MonolithicHeap(), new TaintDomain(),
								new InferredTypes()))
				.addSemanticCheck(new TaintChecker())

				.setJsonOutput(true)
				.setDumpAnalysis(true)
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("taint/t6", "t6.go", conf, annSet);
	}

	@Test
	public void taintTest007() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(
						LiSAFactory.getDefaultFor(AbstractState.class, new MonolithicHeap(), new TaintDomain(),
								new InferredTypes()))
				.addSemanticCheck(new TaintChecker())

				.setJsonOutput(true)
				.setDumpAnalysis(true)
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("taint/t7", "t7.go", conf, annSet);
	}

	@Test
	public void taintTest008() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(
						LiSAFactory.getDefaultFor(AbstractState.class, new MonolithicHeap(), new TaintDomain(),
								new InferredTypes()))
				.addSemanticCheck(new TaintChecker())

				.setJsonOutput(true)
				.setDumpAnalysis(true)
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("taint/t8", "t8.go", conf, annSet);
	}

	@Test
	public void taintTest009() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration()
				.setAbstractState(
						LiSAFactory.getDefaultFor(AbstractState.class, new MonolithicHeap(), new TaintDomain(),
								new InferredTypes()))
				.addSemanticCheck(new TaintChecker())

				.setJsonOutput(true)
				.setDumpAnalysis(true)
				.setOpenCallPolicy(ReturnTopPolicy.INSTANCE)
				.setCallGraph(new RTACallGraph())
				.setInterproceduralAnalysis(new ContextBasedAnalysis<>());
		perform("taint/t9", "t9.go", conf, annSet);
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
