import org.junit.Test;

import it.unive.lisa.AnalysisSetupException;

public class TypingTest extends AnalysisTest {
	
	@Test
	public void testTypingDeclaration() throws AnalysisSetupException {
		perform("typing", "typing-decl.go", true, true, false, null);
	}
}
