
import org.junit.Test;

import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;

public class CFGTest extends GoAnalysisTestExecutor {

	private static LiSAConfiguration mkConf() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration();
		conf.serializeInputs = true;
		return conf;
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
