package it.unive.golisa.cfg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collection;
import org.junit.Test;

import it.unive.golisa.cfg.custom.GoVariableDeclaration;
import it.unive.golisa.cfg.literals.GoInteger;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.CFGDescriptor;
import it.unive.lisa.cfg.edge.SequentialEdge;
import it.unive.lisa.cfg.statement.Variable;

public class VariableDeclarationTest {

	String path = "src/test/resources/go-tutorial/";
	
	@Test
	public void singleVariableDeclaration() throws IOException {
		
		String file = path + "go001.go";
		Collection<CFG> cfgs = new GoToCFG(file).toLiSACFG();
		
		// Check number of generated cfgs
		assertEquals(cfgs.size(), 1);
		
		CFG expectedCfg = new CFG(new CFGDescriptor(file, 5, 38, "main", new String[0]));
		
		GoVariableDeclaration xAsg = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "i"), new GoInteger(expectedCfg, 1));
		expectedCfg.addNode(xAsg);

		GoVariableDeclaration yAsg = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "j"), new GoInteger(expectedCfg, 2));
		expectedCfg.addNode(yAsg);

		expectedCfg.addEdge(new SequentialEdge(xAsg, yAsg));
			
		CFG cfg = cfgs.iterator().next();		
		assertTrue(expectedCfg.isEqualTo(cfg));
	}
	
	@Test
	public void multipleVariableDeclarations() throws IOException {
		
		String file = path + "go003.go";
		Collection<CFG> cfgs = new GoToCFG(file).toLiSACFG();
		
		// Check number of generated cfgs
		assertEquals(cfgs.size(), 1);
		
		CFG expectedCfg = new CFG(new CFGDescriptor(file, 5, 38, "main", new String[0]));
		
		GoVariableDeclaration xAsg = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "i"), new GoInteger(expectedCfg, 1));
		expectedCfg.addNode(xAsg);

		GoVariableDeclaration yAsg = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "j"), new GoInteger(expectedCfg, 2));
		expectedCfg.addNode(yAsg);

		GoVariableDeclaration kAsg = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "k"), new GoInteger(expectedCfg, 3));
		expectedCfg.addNode(kAsg);

		expectedCfg.addEdge(new SequentialEdge(xAsg, yAsg));
		expectedCfg.addEdge(new SequentialEdge(yAsg, kAsg));
			
		CFG cfg = cfgs.iterator().next();		
		assertTrue(expectedCfg.isEqualTo(cfg));
	}
}
