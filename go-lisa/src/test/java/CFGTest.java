import org.junit.Test;

import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSAConfiguration;

public class CFGTest extends GoAnalysisTestExecutor {

	
	@Test
	public void testDeclaration() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpCFGs(true);
		perform("cfg/decl", "go-decl.go", conf);
	}
	
	@Test
	public void testIf() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpCFGs(true);
		perform("cfg/if", "go-if.go", conf);
	}
	
	@Test
	public void testFor() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpCFGs(true);
		perform("cfg/for", "go-for.go", conf);
	}
	
	@Test
	public void testTypes() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpCFGs(true);
		perform("cfg/types", "go-types.go", conf);
	}
		
	@Test
	public void testTour() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpCFGs(true);
		perform("cfg/tour", "go-tour.go", conf);
	}
	
	@Test
	public void testSwitch() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpCFGs(true);
		perform("cfg/switch", "go-switch.go", conf);
	}
	
	@Test
	public void testReturn() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpCFGs(true);
		perform("cfg/return", "go-return.go", conf);
	}
	
	@Test
	public void testRoutine() throws AnalysisSetupException {
		LiSAConfiguration conf = new LiSAConfiguration().setDumpCFGs(true);
		perform("cfg/routine", "go-routine.go", conf);
	}
}
