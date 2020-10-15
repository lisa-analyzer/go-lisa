package it.unive.golisa.cfg;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import org.junit.Test;
import it.unive.lisa.cfg.CFG;

public class VariableDeclarationTest {

	String path = "src/test/resources/go-tutorial/";
	
	@Test
	public void singleVariableDeclaration() {
		Collection<CFG> cfgs = new GoToCFG(path + "go001.go").toLiSACFG();
		
		// Check number of generated cfgs
		assertEquals(cfgs.size(), 1);
		
		CFG cfg = cfgs.iterator().next();
		
		// Check nodes and edges number
		assertEquals(cfg.getNodesCount(), 3);
		assertEquals(cfg.getEdgesCount(), 2);
	}
}
