package it.unive.golisa.cfg;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import org.junit.Test;

import it.unive.golisa.cfg.literals.GoInteger;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.CFGDescriptor;
import it.unive.lisa.cfg.SequentialEdge;
import it.unive.lisa.cfg.statement.Assignment;
import it.unive.lisa.cfg.statement.Variable;

public class VariableDeclarationTest {

	String path = "src/test/resources/go-tutorial/";
	
	@Test
	public void singleVariableDeclaration() {
		
		String file = path + "go001.go";
		Collection<CFG> cfgs = new GoToCFG(file).toLiSACFG();
		
		// Check number of generated cfgs
		assertEquals(cfgs.size(), 1);
		
		CFG cfg = cfgs.iterator().next();
		CFG expectedCfg = new CFG(new CFGDescriptor(file, 5, 38, "main", new String[0]));

		Assignment xAsg = new Assignment(expectedCfg, new Variable(cfg, "i"), new GoInteger(cfg, 1));
		expectedCfg.addNode(xAsg);

		Assignment yAsg = new Assignment(expectedCfg, new Variable(cfg, "j"), new GoInteger(cfg, 2));
		expectedCfg.addNode(yAsg);

		Assignment zAsg = new Assignment(expectedCfg, new Variable(cfg, "k"), new GoInteger(cfg, 3));
		expectedCfg.addNode(zAsg);

		expectedCfg.addEdge(new SequentialEdge(xAsg, yAsg));
		expectedCfg.addEdge(new SequentialEdge(yAsg, zAsg));
		
		assertEquals(expectedCfg, cfg);
	}
}
