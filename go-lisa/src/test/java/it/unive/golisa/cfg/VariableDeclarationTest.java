package it.unive.golisa.cfg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collection;
import org.junit.Test;

import it.unive.golisa.cfg.expression.literal.GoInteger;
import it.unive.golisa.cfg.expression.literal.GoString;
import it.unive.golisa.cfg.statement.GoVariableDeclaration;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.CFGDescriptor;
import it.unive.lisa.cfg.edge.SequentialEdge;
import it.unive.lisa.cfg.Parameter;
import it.unive.lisa.cfg.statement.Statement;
import it.unive.lisa.cfg.statement.Variable;

public class VariableDeclarationTest {

	String path = "src/test/resources/go-tutorial/decl/";

	@Test
	public void singleVariableDeclaration() throws IOException {

		String file = path + "go001.go";
		Collection<CFG> cfgs = new GoFrontEnd(file).toLiSACFG();

		// Check number of generated cfgs
		assertEquals(cfgs.size(), 1);

		Parameter[] args = new Parameter[] {new Parameter("x", GoIntType.INSTANCE), new Parameter("y", GoIntType.INSTANCE)};
		CFG expectedCfg = new CFG(new CFGDescriptor(file, 5, 20, "main", GoIntType.INSTANCE, args));

		GoVariableDeclaration xAsg = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "i", GoIntType.INSTANCE), 
				new GoInteger(expectedCfg, 1));
		expectedCfg.addNode(xAsg);

		GoVariableDeclaration yAsg = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "j", GoIntType.INSTANCE), 
				new GoInteger(expectedCfg, 2));
		expectedCfg.addNode(yAsg);

		expectedCfg.addEdge(new SequentialEdge(xAsg, yAsg));

		CFG cfg = cfgs.iterator().next();
		assertTrue(expectedCfg.isEqualTo(cfg)); 
	}

	@Test
	public void multipleVariableDeclarations() throws IOException {

		String file = path + "go002.go";
		Collection<CFG> cfgs = new GoFrontEnd(file).toLiSACFG();

		// Check number of generated cfgs
		assertEquals(cfgs.size(), 1);

		CFG expectedCfg = new CFG(new CFGDescriptor(file, 5, 10, "main", new Parameter[0]));

		GoVariableDeclaration xAsg = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "i", GoIntType.INSTANCE), 
				new GoInteger(expectedCfg, 1));
		expectedCfg.addNode(xAsg);

		GoVariableDeclaration yAsg = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "j", GoIntType.INSTANCE), 
				new GoInteger(expectedCfg, 2));
		expectedCfg.addNode(yAsg);

		GoVariableDeclaration kAsg = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "k", GoIntType.INSTANCE), 
				new GoInteger(expectedCfg, 3));
		expectedCfg.addNode(kAsg);

		expectedCfg.addEdge(new SequentialEdge(xAsg, yAsg));
		expectedCfg.addEdge(new SequentialEdge(yAsg, kAsg));

		CFG cfg = cfgs.iterator().next();		
		assertTrue(expectedCfg.isEqualTo(cfg));
	}

	@Test
	public void multipleVariableDeclarations2() throws IOException {

		String file = path + "go003.go";
		Collection<CFG> cfgs = new GoFrontEnd(file).toLiSACFG();

		// Check number of generated cfgs
		assertEquals(cfgs.size(), 1);

		CFG expectedCfg = new CFG(new CFGDescriptor(file, 5, 10, "main", new Parameter[0]));

		GoVariableDeclaration iAsg = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "i", GoIntType.INSTANCE), 
				new GoInteger(expectedCfg, 1));
		expectedCfg.addNode(iAsg);

		GoVariableDeclaration jAsg = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "j", GoIntType.INSTANCE), 
				new GoInteger(expectedCfg, 2));
		expectedCfg.addNode(jAsg);

		GoVariableDeclaration kAsg = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "k", GoStringType.INSTANCE), 
				new GoString(expectedCfg, "abc"));
		expectedCfg.addNode(kAsg);

		GoVariableDeclaration lAsg = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "l", GoStringType.INSTANCE), 
				new GoString(expectedCfg, "def"));
		expectedCfg.addNode(lAsg);

		expectedCfg.addEdge(new SequentialEdge(iAsg, jAsg));
		expectedCfg.addEdge(new SequentialEdge(jAsg, kAsg));
		expectedCfg.addEdge(new SequentialEdge(kAsg, lAsg));

		CFG cfg = cfgs.iterator().next();		
		assertTrue(expectedCfg.isEqualTo(cfg));
	}
}
