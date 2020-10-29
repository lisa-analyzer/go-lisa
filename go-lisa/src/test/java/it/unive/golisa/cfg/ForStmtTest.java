package it.unive.golisa.cfg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collection;

import org.junit.Test;

import it.unive.golisa.cfg.call.binary.GoLess;
import it.unive.golisa.cfg.call.binary.GoSum;
import it.unive.golisa.cfg.custom.GoAssignment;
import it.unive.golisa.cfg.custom.GoConstantDeclaration;
import it.unive.golisa.cfg.custom.GoVariableDeclaration;
import it.unive.golisa.cfg.literal.GoInteger;
import it.unive.golisa.cfg.literal.GoString;
import it.unive.golisa.cfg.type.GoIntType;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.CFGDescriptor;
import it.unive.lisa.cfg.edge.FalseEdge;
import it.unive.lisa.cfg.edge.SequentialEdge;
import it.unive.lisa.cfg.edge.TrueEdge;
import it.unive.lisa.cfg.statement.NoOp;
import it.unive.lisa.cfg.statement.Parameter;
import it.unive.lisa.cfg.statement.Variable;
import it.unive.lisa.cfg.type.Untyped;

public class ForStmtTest {
	String path = "src/test/resources/go-tutorial/simple-for/";
	
	@Test
	public void simpleForLoop() throws IOException {
		
		String file = path + "for001.go";
		Collection<CFG> cfgs = new GoToCFG(file).toLiSACFG();
		
		// Check number of generated cfgs
		assertEquals(cfgs.size(), 1);
		
		CFG expectedCfg = new CFG(new CFGDescriptor(file, 5, 10, "main", new Parameter[0]));
		
		GoConstantDeclaration constantA = new GoConstantDeclaration(expectedCfg, new Variable(expectedCfg, "A", GoIntType.INSTANCE), new GoInteger(expectedCfg, 1));
		expectedCfg.addNode(constantA);
		
		GoConstantDeclaration constantB = new GoConstantDeclaration(expectedCfg, new Variable(expectedCfg, "B", GoIntType.INSTANCE), new GoInteger(expectedCfg, 2));
		expectedCfg.addNode(constantB);
		
		GoVariableDeclaration sum = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "sum", GoIntType.INSTANCE), 
				new GoInteger(expectedCfg, 0));
		expectedCfg.addNode(sum);

		GoVariableDeclaration init = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "i", GoIntType.INSTANCE), 
				new GoInteger(expectedCfg, 0));
		expectedCfg.addNode(init);
		
		GoLess cond = new GoLess(expectedCfg, new Variable(expectedCfg, "i"), new GoInteger(expectedCfg, 10));
		expectedCfg.addNode(cond);
		
		GoAssignment post = new GoAssignment(expectedCfg, new Variable(expectedCfg, "i"), 
				new GoSum(expectedCfg, new Variable(expectedCfg, "i"), new GoInteger(expectedCfg, 1)));
		expectedCfg.addNode(post);
		
		GoAssignment body = new GoAssignment(expectedCfg, new Variable(expectedCfg, "sum"), 
				new GoSum(expectedCfg, new Variable(expectedCfg, "sum"), new Variable(expectedCfg, "i")));
		expectedCfg.addNode(body);

		GoVariableDeclaration res = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "res", GoStringType.INSTANCE), 
				new GoString(expectedCfg, "Hello"));
		expectedCfg.addNode(res);
		
		NoOp exitFor = new NoOp(expectedCfg);
		expectedCfg.addNode(exitFor);

		expectedCfg.addEdge(new SequentialEdge(constantA, constantB));
		expectedCfg.addEdge(new SequentialEdge(constantB, sum));
		expectedCfg.addEdge(new SequentialEdge(sum, init));
		expectedCfg.addEdge(new SequentialEdge(init, cond));
		expectedCfg.addEdge(new TrueEdge(cond, body));
		expectedCfg.addEdge(new FalseEdge(cond, exitFor));
		expectedCfg.addEdge(new SequentialEdge(body, post));
		expectedCfg.addEdge(new SequentialEdge(post, cond));
		expectedCfg.addEdge(new SequentialEdge(exitFor, res));
			
		CFG cfg = cfgs.iterator().next();	
		assertTrue(expectedCfg.isEqualTo(cfg));
	}
	
	@Test
	public void simpleForLoopWithoutInitialization() throws IOException {
		
		String file = path + "for002.go";
		Collection<CFG> cfgs = new GoToCFG(file).toLiSACFG();
		
		// Check number of generated cfgs
		assertEquals(cfgs.size(), 1);
		
		CFG expectedCfg = new CFG(new CFGDescriptor(file, 5, 10, "main", new Parameter[0]));
		
		GoConstantDeclaration constantBig = new GoConstantDeclaration(expectedCfg, new Variable(expectedCfg, "Big"), new GoInteger(expectedCfg, 100));
		expectedCfg.addNode(constantBig);
		
		GoConstantDeclaration constantSmall = new GoConstantDeclaration(expectedCfg, new Variable(expectedCfg, "Small"), new GoInteger(expectedCfg, 0));
		expectedCfg.addNode(constantSmall);
		
		GoVariableDeclaration sum = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "sum", GoIntType.INSTANCE),
				new GoInteger(expectedCfg, 0));
		expectedCfg.addNode(sum);
		
		GoLess cond = new GoLess(expectedCfg, new Variable(expectedCfg, "i"), new GoInteger(expectedCfg, 10));
		expectedCfg.addNode(cond);
		
		GoAssignment post = new GoAssignment(expectedCfg, new Variable(expectedCfg, "i"), 
				new GoSum(expectedCfg, new Variable(expectedCfg, "i"), new GoInteger(expectedCfg, 1)));
		expectedCfg.addNode(post);
		
		GoAssignment body = new GoAssignment(expectedCfg, new Variable(expectedCfg, "sum"), 
				new GoSum(expectedCfg, new Variable(expectedCfg, "sum"), new Variable(expectedCfg, "i")));
		expectedCfg.addNode(body);

		NoOp exitFor = new NoOp(expectedCfg);
		expectedCfg.addNode(exitFor);

		expectedCfg.addEdge(new SequentialEdge(constantBig, constantSmall));
		expectedCfg.addEdge(new SequentialEdge(constantSmall, sum));
		expectedCfg.addEdge(new SequentialEdge(sum, cond));
		expectedCfg.addEdge(new TrueEdge(cond, body));
		expectedCfg.addEdge(new FalseEdge(cond, exitFor));
		expectedCfg.addEdge(new SequentialEdge(body, post));
		expectedCfg.addEdge(new SequentialEdge(post, cond));
			
		CFG cfg = cfgs.iterator().next();	
		assertTrue(expectedCfg.isEqualTo(cfg));
	}
	
	@Test
	public void simpleForLoopWithoutPostStmt() throws IOException {
		
		String file = path + "for003.go";
		Collection<CFG> cfgs = new GoToCFG(file).toLiSACFG();
		
		// Check number of generated cfgs
		assertEquals(cfgs.size(), 1);
		
		CFG expectedCfg = new CFG(new CFGDescriptor(file, 5, 10, "main", new Parameter[0]));
		
		GoVariableDeclaration init = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "i", GoIntType.INSTANCE), 
				new GoInteger(expectedCfg, 0));
		expectedCfg.addNode(init);
		
		GoVariableDeclaration sum = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "sum", GoIntType.INSTANCE), 
				new GoInteger(expectedCfg, 0));
		expectedCfg.addNode(sum);
		
		GoLess cond = new GoLess(expectedCfg, new Variable(expectedCfg, "i"), new GoInteger(expectedCfg, 10));
		expectedCfg.addNode(cond);
				
		GoAssignment body = new GoAssignment(expectedCfg, new Variable(expectedCfg, "sum"), 
				new GoSum(expectedCfg, new Variable(expectedCfg, "sum"), new Variable(expectedCfg, "i")));
		expectedCfg.addNode(body);

		NoOp exitFor = new NoOp(expectedCfg);
		expectedCfg.addNode(exitFor);

		expectedCfg.addEdge(new SequentialEdge(sum, init));
		expectedCfg.addEdge(new SequentialEdge(init, cond));
		expectedCfg.addEdge(new TrueEdge(cond, body));
		expectedCfg.addEdge(new FalseEdge(cond, exitFor));
		expectedCfg.addEdge(new SequentialEdge(body, cond));
			
		CFG cfg = cfgs.iterator().next();	
		assertTrue(expectedCfg.isEqualTo(cfg));
	}
}
