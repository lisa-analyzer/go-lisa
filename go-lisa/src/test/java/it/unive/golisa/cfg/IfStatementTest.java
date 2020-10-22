package it.unive.golisa.cfg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collection;

import org.junit.Test;

import it.unive.golisa.cfg.calls.binary.*;
import it.unive.golisa.cfg.custom.GoAssignment;
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
	String path = "src/test/resources/go-tutorial/if/";

	@Test
	public void simpleIfElseStmt() throws IOException {
		String file = path + "if001.go";
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

	@Test
	public void ifStmtWithoutElse() throws IOException {
		String file = path + "if002.go";
		Collection<CFG> cfgs = new GoToCFG(file).toLiSACFG();

		// Check number of generated cfgs
		assertEquals(cfgs.size(), 1);

		CFG expectedCfg = new CFG(new CFGDescriptor(file, 5, 38, "main", new String[0]));

		GoVariableDeclaration xAsg = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "x"), new GoInteger(expectedCfg, 100));
		expectedCfg.addNode(xAsg);

		GoEquals guard = new GoEquals(expectedCfg, new Variable(expectedCfg, "x"),  new GoInteger(expectedCfg, 100)); 
		expectedCfg.addNode(guard);

		GoVariableDeclaration yAsg = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "y"), new GoInteger(expectedCfg, 5));
		expectedCfg.addNode(yAsg);

		NoOp noop = new NoOp(expectedCfg);
		expectedCfg.addNode(noop);

		expectedCfg.addEdge(new SequentialEdge(xAsg, guard));
		expectedCfg.addEdge(new SequentialEdge(yAsg, noop));
		expectedCfg.addEdge(new TrueEdge(guard, yAsg));
		expectedCfg.addEdge(new FalseEdge(guard, noop));

		CFG cfg = cfgs.iterator().next();	
		assertTrue(expectedCfg.isEqualTo(cfg));
	}

	@Test
	public void ifElseTest() throws IOException {
		String file = path + "if003.go";
		Collection<CFG> cfgs = new GoToCFG(file).toLiSACFG();

		// Check number of generated cfgs
		assertEquals(cfgs.size(), 1);

		CFG expectedCfg = new CFG(new CFGDescriptor(file, 5, 38, "main", new String[0]));

		GoEquals guard = new GoEquals(expectedCfg, new Variable(expectedCfg, "x"),  new GoInteger(expectedCfg, 100)); 
		expectedCfg.addNode(guard);

		GoVariableDeclaration xAsg = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "x"), new GoInteger(expectedCfg, 1));
		expectedCfg.addNode(xAsg);
		GoVariableDeclaration yAsg = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "y"), new GoInteger(expectedCfg, 2));
		expectedCfg.addNode(yAsg);

		GoVariableDeclaration wAsg = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "w"), new GoInteger(expectedCfg, 3));
		expectedCfg.addNode(wAsg);
		GoVariableDeclaration zAsg = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "z"), new GoInteger(expectedCfg, 4));
		expectedCfg.addNode(zAsg);

		NoOp noop = new NoOp(expectedCfg);
		expectedCfg.addNode(noop);

		expectedCfg.addEdge(new TrueEdge(guard, xAsg));
		expectedCfg.addEdge(new FalseEdge(guard, wAsg));
		expectedCfg.addEdge(new SequentialEdge(xAsg, yAsg));
		expectedCfg.addEdge(new SequentialEdge(wAsg, zAsg));

		expectedCfg.addEdge(new SequentialEdge(yAsg, noop));
		expectedCfg.addEdge(new SequentialEdge(zAsg, noop));

		CFG cfg = cfgs.iterator().next();	
		assertTrue(expectedCfg.isEqualTo(cfg));
	}

	@Test
	public void nestedIfTest() throws IOException {
		String file = path + "if004.go";
		Collection<CFG> cfgs = new GoToCFG(file).toLiSACFG();

		// Check number of generated cfgs
		assertEquals(cfgs.size(), 1);

		CFG expectedCfg = new CFG(new CFGDescriptor(file, 5, 38, "main", new String[0]));

		GoEquals firstGuard = new GoEquals(expectedCfg, new Variable(expectedCfg, "x"),  new GoInteger(expectedCfg, 100)); 
		expectedCfg.addNode(firstGuard);
		GoEquals secondGuard = new GoEquals(expectedCfg, new Variable(expectedCfg, "x"),  new GoInteger(expectedCfg, 99)); 
		expectedCfg.addNode(secondGuard);

		GoVariableDeclaration xAsg = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "x"), new GoInteger(expectedCfg, 1));
		expectedCfg.addNode(xAsg);
		GoVariableDeclaration yAsg = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "y"), new GoInteger(expectedCfg, 2));
		expectedCfg.addNode(yAsg);

		GoVariableDeclaration zAsg = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "z"), new GoInteger(expectedCfg, 3));
		expectedCfg.addNode(zAsg);
		GoVariableDeclaration wAsg = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "w"), new GoInteger(expectedCfg, 4));
		expectedCfg.addNode(wAsg);

		NoOp noop1 = new NoOp(expectedCfg);
		expectedCfg.addNode(noop1);

		NoOp noop2 = new NoOp(expectedCfg);
		expectedCfg.addNode(noop2);

		expectedCfg.addEdge(new TrueEdge(firstGuard, xAsg));
		expectedCfg.addEdge(new FalseEdge(firstGuard, secondGuard));
		expectedCfg.addEdge(new SequentialEdge(xAsg, yAsg));
		expectedCfg.addEdge(new SequentialEdge(yAsg, noop2));
		expectedCfg.addEdge(new TrueEdge(secondGuard, zAsg));
		expectedCfg.addEdge(new FalseEdge(secondGuard, wAsg));

		expectedCfg.addEdge(new SequentialEdge(zAsg, noop1));
		expectedCfg.addEdge(new SequentialEdge(wAsg, noop1));
		expectedCfg.addEdge(new SequentialEdge(noop1, noop2));

		CFG cfg = cfgs.iterator().next();	
		assertTrue(expectedCfg.isEqualTo(cfg));
	}

	@Test
	public void nestedIfTestWithAssignments() throws IOException {
		String file = path + "if005.go";
		Collection<CFG> cfgs = new GoToCFG(file).toLiSACFG();

		// Check number of generated cfgs
		assertEquals(cfgs.size(), 1);

		CFG expectedCfg = new CFG(new CFGDescriptor(file, 5, 38, "main", new String[0]));

		GoVariableDeclaration xDecl = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "x"), new GoInteger(expectedCfg, 1));
		expectedCfg.addNode(xDecl);
		GoVariableDeclaration yDecl = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "y"), new GoInteger(expectedCfg, 2));
		expectedCfg.addNode(yDecl);
		GoVariableDeclaration wDecl = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "w"), new GoInteger(expectedCfg, 3));
		expectedCfg.addNode(wDecl);
		GoVariableDeclaration zDecl = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "z"), new GoInteger(expectedCfg, 4));
		expectedCfg.addNode(zDecl);

		GoEquals firstGuard = new GoEquals(expectedCfg, new Variable(expectedCfg, "x"),  new GoInteger(expectedCfg, 100)); 
		expectedCfg.addNode(firstGuard);
		GoEquals secondGuard = new GoEquals(expectedCfg, new Variable(expectedCfg, "x"),  new GoInteger(expectedCfg, 99)); 
		expectedCfg.addNode(secondGuard);

		GoAssignment xAsg = new GoAssignment(expectedCfg, new Variable(expectedCfg, "x"), new GoInteger(expectedCfg, 1));
		expectedCfg.addNode(xAsg);
		GoAssignment yAsg = new GoAssignment(expectedCfg, new Variable(expectedCfg, "y"), new GoInteger(expectedCfg, 2));
		expectedCfg.addNode(yAsg);

		GoAssignment zAsg = new GoAssignment(expectedCfg, new Variable(expectedCfg, "z"), new GoInteger(expectedCfg, 3));
		expectedCfg.addNode(zAsg);
		GoAssignment wAsg = new GoAssignment(expectedCfg, new Variable(expectedCfg, "w"), new GoInteger(expectedCfg, 4));
		expectedCfg.addNode(wAsg);

		NoOp noop1 = new NoOp(expectedCfg);
		expectedCfg.addNode(noop1);

		NoOp noop2 = new NoOp(expectedCfg);
		expectedCfg.addNode(noop2);

		expectedCfg.addEdge(new SequentialEdge(xDecl, yDecl));
		expectedCfg.addEdge(new SequentialEdge(yDecl, wDecl));
		expectedCfg.addEdge(new SequentialEdge(wDecl, zDecl));
		expectedCfg.addEdge(new SequentialEdge(zDecl, firstGuard));

		expectedCfg.addEdge(new TrueEdge(firstGuard, xAsg));
		expectedCfg.addEdge(new FalseEdge(firstGuard, secondGuard));
		expectedCfg.addEdge(new SequentialEdge(xAsg, yAsg));
		expectedCfg.addEdge(new SequentialEdge(yAsg, noop2));
		expectedCfg.addEdge(new TrueEdge(secondGuard, zAsg));
		expectedCfg.addEdge(new FalseEdge(secondGuard, wAsg));

		expectedCfg.addEdge(new SequentialEdge(zAsg, noop1));
		expectedCfg.addEdge(new SequentialEdge(wAsg, noop1));
		expectedCfg.addEdge(new SequentialEdge(noop1, noop2));

		CFG cfg = cfgs.iterator().next();	
		assertTrue(expectedCfg.isEqualTo(cfg));
	}

	@Test
	public void nestedIfTestWithoutElse() throws IOException {
		String file = path + "if006.go";
		Collection<CFG> cfgs = new GoToCFG(file).toLiSACFG();

		// Check number of generated cfgs
		assertEquals(cfgs.size(), 1);

		CFG expectedCfg = new CFG(new CFGDescriptor(file, 5, 38, "main", new String[0]));

		GoVariableDeclaration xDecl = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "x"), new GoInteger(expectedCfg, 1));
		expectedCfg.addNode(xDecl);
		GoVariableDeclaration yDecl = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "y"), new GoInteger(expectedCfg, 2));
		expectedCfg.addNode(yDecl);
		GoVariableDeclaration zDecl = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "z"), new GoInteger(expectedCfg, 3));
		expectedCfg.addNode(zDecl);

		GoEquals firstGuard = new GoEquals(expectedCfg, new Variable(expectedCfg, "x"),  new GoInteger(expectedCfg, 100)); 
		expectedCfg.addNode(firstGuard);
		GoEquals secondGuard = new GoEquals(expectedCfg, new Variable(expectedCfg, "x"),  new GoInteger(expectedCfg, 99)); 
		expectedCfg.addNode(secondGuard);

		GoAssignment xAsg = new GoAssignment(expectedCfg, new Variable(expectedCfg, "x"), new GoInteger(expectedCfg, 1));
		expectedCfg.addNode(xAsg);
		GoAssignment yAsg = new GoAssignment(expectedCfg, new Variable(expectedCfg, "y"), new GoInteger(expectedCfg, 2));
		expectedCfg.addNode(yAsg);
		GoAssignment zAsg = new GoAssignment(expectedCfg, new Variable(expectedCfg, "z"), new GoInteger(expectedCfg, 3));
		expectedCfg.addNode(zAsg);

		NoOp noop1 = new NoOp(expectedCfg);
		expectedCfg.addNode(noop1);

		NoOp noop2 = new NoOp(expectedCfg);
		expectedCfg.addNode(noop2);

		expectedCfg.addEdge(new SequentialEdge(xDecl, yDecl));
		expectedCfg.addEdge(new SequentialEdge(yDecl, zDecl));
		expectedCfg.addEdge(new SequentialEdge(zDecl, firstGuard));

		expectedCfg.addEdge(new TrueEdge(firstGuard, xAsg));
		expectedCfg.addEdge(new SequentialEdge(xAsg, yAsg));
		expectedCfg.addEdge(new SequentialEdge(yAsg, noop2));

		expectedCfg.addEdge(new FalseEdge(firstGuard, secondGuard));
		expectedCfg.addEdge(new TrueEdge(secondGuard, zAsg));
		expectedCfg.addEdge(new SequentialEdge(zAsg, noop1));
		expectedCfg.addEdge(new FalseEdge(secondGuard, noop1));
		expectedCfg.addEdge(new SequentialEdge(noop1, noop2));

		CFG cfg = cfgs.iterator().next();	
		assertTrue(expectedCfg.isEqualTo(cfg));
	}
}
