import org.junit.Test;

import it.unive.lisa.AnalysisSetupException;

public class CFGTest extends AnalysisTest {

	@Test
	public void testDeclaration() throws AnalysisSetupException {
		perform("cfg/decl", "go-decl.go", false, false, true, null);
	}
	
	@Test
	public void testIf() throws AnalysisSetupException {
		perform("cfg/if", "go-if.go", false, false, true, null);
	}
	
	@Test
	public void testFor() throws AnalysisSetupException {
		perform("cfg/for", "go-for.go", false, false, true, null);
	}
	
	@Test
	public void testTypes() throws AnalysisSetupException {
		perform("cfg/types", "go-types.go", false, false, true, null);
	}
		
	@Test
	public void testTour() throws AnalysisSetupException {
		perform("cfg/tour", "go-tour.go", false, false, true, null);
	}
	
	@Test
	public void testSwitch() throws AnalysisSetupException {
		perform("cfg/switch", "go-switch.go", false, false, true, null);
	}
	
	@Test
	public void testReturn() throws AnalysisSetupException {
		perform("cfg/return", "go-return.go", false, false, true, null);
	}
}
