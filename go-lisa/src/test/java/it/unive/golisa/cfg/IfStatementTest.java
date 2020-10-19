package it.unive.golisa.cfg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collection;

import org.junit.Test;

import it.unive.golisa.cfg.calls.GoEquals;
import it.unive.golisa.cfg.custom.GoVariableDeclaration;
import it.unive.golisa.cfg.literals.GoInteger;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.CFGDescriptor;
import it.unive.lisa.cfg.edge.FalseEdge;
import it.unive.lisa.cfg.edge.SequentialEdge;
import it.unive.lisa.cfg.edge.TrueEdge;
import it.unive.lisa.cfg.statement.NoOp;
import it.unive.lisa.cfg.statement.Variable;

public class IfStatementTest {
String path = "src/test/resources/go-tutorial/";
	
	@Test
	public void singleVariableDeclaration() throws IOException {
		
		String file = path + "go002.go";
		Collection<CFG> cfgs = new GoToCFG(file).toLiSACFG();
		
		// Check number of generated cfgs
		assertEquals(cfgs.size(), 1);
		
		CFG expectedCfg = new CFG(new CFGDescriptor(file, 5, 38, "main", new String[0]));
		
		GoVariableDeclaration xAsg = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "i"), new GoInteger(expectedCfg, 1));
		expectedCfg.addNode(xAsg);

		GoEquals guard = new GoEquals(expectedCfg, new Variable(expectedCfg, "i"),  new GoInteger(expectedCfg, 2)); 
		expectedCfg.addNode(guard);

		GoVariableDeclaration aAsg = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "a"), new GoInteger(expectedCfg, 1));
		expectedCfg.addNode(aAsg);

		GoVariableDeclaration bAsg = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "b"), new GoInteger(expectedCfg, 2));
		expectedCfg.addNode(bAsg);
		
		NoOp noop = new NoOp(expectedCfg);
		expectedCfg.addNode(noop);
		
		expectedCfg.addEdge(new SequentialEdge(xAsg, guard));
		expectedCfg.addEdge(new TrueEdge(guard, aAsg));
		expectedCfg.addEdge(new FalseEdge(guard, bAsg));
		expectedCfg.addEdge(new SequentialEdge(aAsg, noop));
		expectedCfg.addEdge(new SequentialEdge(bAsg, noop));

		CFG cfg = cfgs.iterator().next();		
		assertTrue(expectedCfg.isEqualTo(cfg));
	}
}
