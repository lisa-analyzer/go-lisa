
import org.junit.Test;

import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.interprocedural.ModularWorstCaseAnalysis;
import it.unive.lisa.interprocedural.callgraph.RTACallGraph;

public class CFGTest extends GoAnalysisTestExecutor {

	private static CronConfiguration mkConf() throws AnalysisSetupException {
		CronConfiguration conf = new CronConfiguration();
		conf.callGraph = new RTACallGraph();
		conf.interproceduralAnalysis = new ModularWorstCaseAnalysis<>();
		conf.serializeInputs = true;
		return conf;
	}

	@Test
	public void testDeclaration() throws AnalysisSetupException {
		CronConfiguration conf = mkConf();
		conf.testDir = "cfg/decl";
		conf.programFile = "go-decl.go";
		perform(conf);
	}

	@Test
	public void testIf() throws AnalysisSetupException {
		CronConfiguration conf = mkConf();
		conf.testDir = "cfg/if";
		conf.programFile = "go-if.go";
		perform(conf);
	}

	@Test
	public void testFor() throws AnalysisSetupException {
		CronConfiguration conf = mkConf();
		conf.testDir = "cfg/for";
		conf.programFile = "go-for.go";
		perform(conf);
	}

	@Test
	public void testTypes() throws AnalysisSetupException {
		CronConfiguration conf = mkConf();
		conf.testDir = "cfg/types";
		conf.programFile = "go-types.go";
		perform(conf);
	}

	@Test
	public void testTour() throws AnalysisSetupException {
		CronConfiguration conf = mkConf();
		conf.testDir = "cfg/tour";
		conf.programFile = "go-tour.go";
		perform(conf);
	}

	@Test
	public void testExprSwitch() throws AnalysisSetupException {
		CronConfiguration conf = mkConf();
		conf.testDir = "cfg/switch/expr";
		conf.programFile = "go-switch.go";
		perform(conf);
	}

	@Test
	public void testTypeSwitch() throws AnalysisSetupException {
		CronConfiguration conf = mkConf();
		conf.testDir = "cfg/switch/type";
		conf.programFile = "go-switch.go";
		perform(conf);
	}

	@Test
	public void testReturn() throws AnalysisSetupException {
		CronConfiguration conf = mkConf();
		conf.testDir = "cfg/return";
		conf.programFile = "go-return.go";
		perform(conf);
	}

	@Test
	public void testChannel() throws AnalysisSetupException {
		CronConfiguration conf = mkConf();
		conf.testDir = "cfg/channel";
		conf.programFile = "go-channel.go";
		perform(conf);
	}

	@Test
	public void testRoutine() throws AnalysisSetupException {
		CronConfiguration conf = mkConf();
		conf.testDir = "cfg/routine";
		conf.programFile = "go-routine.go";
		perform(conf);
	}

	@Test
	public void testGoTo() throws AnalysisSetupException {
		CronConfiguration conf = mkConf();
		conf.testDir = "cfg/goto";
		conf.programFile = "goto.go";
		perform(conf);
	}
}
