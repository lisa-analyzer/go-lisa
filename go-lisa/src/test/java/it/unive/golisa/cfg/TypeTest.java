package it.unive.golisa.cfg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collection;

import org.junit.Test;

import it.unive.golisa.cfg.custom.GoVariableDeclaration;
import it.unive.golisa.cfg.literal.GoBoolean;
import it.unive.golisa.cfg.literal.GoInteger;
import it.unive.golisa.cfg.type.GoBoolType;
import it.unive.golisa.cfg.type.numeric.signed.GoInt16Type;
import it.unive.golisa.cfg.type.numeric.signed.GoInt32Type;
import it.unive.golisa.cfg.type.numeric.signed.GoInt64Type;
import it.unive.golisa.cfg.type.numeric.signed.GoInt8Type;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt16Type;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt32Type;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt64Type;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt8Type;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUIntType;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.CFGDescriptor;
import it.unive.lisa.cfg.edge.SequentialEdge;
import it.unive.lisa.cfg.statement.Parameter;
import it.unive.lisa.cfg.statement.Variable;

public class TypeTest {

	String path = "src/test/resources/go-tutorial/type/";

	@Test
	public void singleVariableDeclaration() throws IOException {

		String file = path + "type001.go";
		Collection<CFG> cfgs = new GoToCFG(file).toLiSACFG();

		// Check number of generated cfgs
		assertEquals(cfgs.size(), 1);

		Parameter[] args = new Parameter[] {};
		CFG expectedCfg = new CFG(new CFGDescriptor(file, 5, 10, "main", args));

		GoVariableDeclaration x1Asg = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "x1", GoIntType.INSTANCE), 
				new GoInteger(expectedCfg, 1));
		expectedCfg.addNode(x1Asg);
		
		GoVariableDeclaration x2Asg = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "x2", GoInt8Type.INSTANCE), 
				new GoInteger(expectedCfg, 2));
		expectedCfg.addNode(x2Asg);
		
		GoVariableDeclaration x3Asg = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "x3", GoInt16Type.INSTANCE), 
				new GoInteger(expectedCfg, 3));
		expectedCfg.addNode(x3Asg);
		
		GoVariableDeclaration x4Asg = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "x4", GoInt32Type.INSTANCE), 
				new GoInteger(expectedCfg, 4));
		expectedCfg.addNode(x4Asg);
		
		GoVariableDeclaration x5Asg = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "x5", GoInt64Type.INSTANCE), 
				new GoInteger(expectedCfg, 5));
		expectedCfg.addNode(x5Asg);
		
		GoVariableDeclaration x6Asg = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "x6", GoBoolType.INSTANCE), 
				new GoBoolean(expectedCfg, true));
		expectedCfg.addNode(x6Asg);
		
		GoVariableDeclaration u1Asg = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "u1", GoUIntType.INSTANCE), 
				new GoInteger(expectedCfg, 1));
		expectedCfg.addNode(u1Asg);
		
		GoVariableDeclaration u2Asg = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "u2", GoUInt8Type.INSTANCE), 
				new GoInteger(expectedCfg, 2));
		expectedCfg.addNode(u2Asg);
		
		GoVariableDeclaration u3Asg = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "u3", GoUInt16Type.INSTANCE), 
				new GoInteger(expectedCfg, 3));
		expectedCfg.addNode(u3Asg);
		
		GoVariableDeclaration u4Asg = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "u4", GoUInt32Type.INSTANCE), 
				new GoInteger(expectedCfg, 4));
		expectedCfg.addNode(u4Asg);
		
		GoVariableDeclaration u5Asg = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "u5", GoUInt64Type.INSTANCE), 
				new GoInteger(expectedCfg, 5));
		expectedCfg.addNode(u5Asg);
		
		expectedCfg.addEdge(new SequentialEdge(x1Asg, x2Asg));
		expectedCfg.addEdge(new SequentialEdge(x2Asg, x3Asg));
		expectedCfg.addEdge(new SequentialEdge(x3Asg, x4Asg));
		expectedCfg.addEdge(new SequentialEdge(x4Asg, x5Asg));
		expectedCfg.addEdge(new SequentialEdge(x5Asg, x6Asg));
		expectedCfg.addEdge(new SequentialEdge(x6Asg, u1Asg));
		expectedCfg.addEdge(new SequentialEdge(u1Asg, u2Asg));
		expectedCfg.addEdge(new SequentialEdge(u2Asg, u3Asg));
		expectedCfg.addEdge(new SequentialEdge(u3Asg, u4Asg));
		expectedCfg.addEdge(new SequentialEdge(u4Asg, u5Asg));

		CFG cfg = cfgs.iterator().next();	
		assertTrue(expectedCfg.isEqualTo(cfg));
	}
}
