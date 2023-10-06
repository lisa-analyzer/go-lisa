
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import it.unive.golisa.checker.readwrite.ReadWritePairChecker;
import it.unive.golisa.checker.readwrite.ReadWritePathChecker;
import it.unive.golisa.interprocedural.RelaxedOpenCallPolicy;
import it.unive.golisa.loader.annotation.AnnotationSet;
import it.unive.golisa.loader.annotation.sets.HyperledgerFabricNonDeterminismAnnotationSet;
import it.unive.lisa.AnalysisException;
import it.unive.lisa.LiSA;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.nonrelational.value.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.string.tarsis.Tarsis;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.conf.LiSAConfiguration;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;
import it.unive.lisa.interprocedural.context.ContextBasedAnalysis;
import it.unive.lisa.interprocedural.context.FullStackToken;
import it.unive.lisa.program.Program;

public class ReadWriteSetHFTest extends GoChaincodeTestExecutor {

	private final AnnotationSet annSet = new HyperledgerFabricNonDeterminismAnnotationSet();

	private ReadWritePairChecker readWritePairChecker = null;

	@Override
	protected void run(LiSAConfiguration configuration, Program program) {

		// phase 1
		LiSA lisa = new LiSA(configuration);
		try {
			lisa.run(program);
		} catch (AnalysisException e) {
			e.printStackTrace(System.err);
			fail("Analysis terminated with errors");
		}

		// phase 2

		CronConfiguration conf2 = new CronConfiguration();
		
		conf2.jsonOutput = configuration.jsonOutput;
		conf2.openCallPolicy = configuration.openCallPolicy;
		conf2.interproceduralAnalysis = configuration.interproceduralAnalysis;
		conf2.callGraph = configuration.callGraph;

		conf2.abstractState = new SimpleAbstractState<>(new PointBasedHeap(), new ValueEnvironment<>(new Tarsis()),
				new TypeEnvironment<>(new InferredTypes()));
		conf2.semanticChecks.add(new ReadWritePathChecker(readWritePairChecker.getReadAfterWriteCandidates(),
				readWritePairChecker.getOverWriteCandidates(), false));

		if(configuration instanceof CronConfiguration) {
			CronConfiguration cronConf = (CronConfiguration) configuration;
			conf2.compareWithOptimization = cronConf.compareWithOptimization;
			conf2.testDir = cronConf.testDir;
			conf2.testSubDir = cronConf.testSubDir;
			conf2.programFile = cronConf.programFile;
		}
		
		conf2.annSet = annSet;

		LiSA lisa2 = new LiSA(conf2);

		try {
			lisa2.run(program);
		} catch (Exception e) {
			// an error occurred during the analysis
			e.printStackTrace();
			return;
		}
	}
	
	
	@Test
	public void testSingle1() throws AnalysisException, IOException {

		CronConfiguration conf = new CronConfiguration();

		conf.jsonOutput = true;
		conf.openCallPolicy = RelaxedOpenCallPolicy.INSTANCE;
		conf.interproceduralAnalysis = new ContextBasedAnalysis<>(FullStackToken.getSingleton());
		conf.callGraph = new RTACallGraph();

		conf.abstractState = new SimpleAbstractState<>(new PointBasedHeap(), new ValueEnvironment<>(new Tarsis()),
				new TypeEnvironment<>(new InferredTypes()));
		readWritePairChecker = new ReadWritePairChecker();
		conf.semanticChecks.add(readWritePairChecker);

		conf.compareWithOptimization = false;
		conf.testDir = "readwritehf/single";
		conf.testSubDir = "single1";
		conf.programFile = "single1.go";
		conf.annSet = annSet;
		perform(conf);
	}
	
}
