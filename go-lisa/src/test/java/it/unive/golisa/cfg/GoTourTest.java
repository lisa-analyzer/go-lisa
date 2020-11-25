package it.unive.golisa.cfg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collection;

import org.junit.Test;

import it.unive.golisa.cfg.call.binary.GoSum;
import it.unive.golisa.cfg.custom.GoReturn;
import it.unive.golisa.cfg.custom.GoVariableDeclaration;
import it.unive.golisa.cfg.literal.GoInteger;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.CFGDescriptor;
import it.unive.lisa.cfg.Parameter;
import it.unive.lisa.cfg.statement.CFGCall;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.statement.OpenCall;
import it.unive.lisa.cfg.statement.Variable;

public class GoTourTest {
	String path = "src/test/resources/go-tutorial/go-tour/";

	@Test
	public void goTourTest001() throws IOException {
		String file = path + "go001.go";
		Collection<CFG> cfgs = new GoFrontEnd(file).toLiSACFG();

		// Check number of generated cfgs
		assertEquals(cfgs.size(), 1);

		Parameter[] args = new Parameter[]{new Parameter("x", GoIntType.INSTANCE), new Parameter("y", GoIntType.INSTANCE)};
		CFG expectedCfg = new CFG(new CFGDescriptor(file, 8, 23, "add", GoIntType.INSTANCE, args));

		GoReturn ret = new GoReturn(expectedCfg, new GoSum(expectedCfg, new Variable(expectedCfg, "x"), new Variable(expectedCfg, "y")));
		expectedCfg.addNode(ret);

		CFG cfg = cfgs.iterator().next();	
		assertTrue(expectedCfg.isEqualTo(cfg));
	}

	@Test
	public void goTourTest002() throws IOException {
		String file = path + "go002.go";
		Collection<CFG> cfgs = new GoFrontEnd(file).toLiSACFG();

		// Check number of generated cfgs
		assertEquals(cfgs.size(), 1);

		Parameter[] args = new Parameter[]{new Parameter("x", GoIntType.INSTANCE), new Parameter("y", GoIntType.INSTANCE)};
		CFG expectedCfg = new CFG(new CFGDescriptor(file, 8, 19, "add", GoIntType.INSTANCE, args));

		GoReturn ret = new GoReturn(expectedCfg, new GoSum(expectedCfg, new Variable(expectedCfg, "x"), new Variable(expectedCfg, "y")));
		expectedCfg.addNode(ret);

		CFG cfg = cfgs.iterator().next();	
		assertTrue(expectedCfg.isEqualTo(cfg));
	}

	@Test
	public void goTourTest003() throws IOException {
		String file = path + "go003.go";
		Collection<CFG> cfgs = new GoFrontEnd(file).toLiSACFG();

		// Check number of generated cfgs
		assertEquals(cfgs.size(), 2);

		// Checking sum CFG
		Parameter[] args = new Parameter[]{new Parameter("x", GoIntType.INSTANCE), new Parameter("y", GoIntType.INSTANCE)};
		CFG sumExpCFG = new CFG(new CFGDescriptor(file, 5, 19, "sum", GoIntType.INSTANCE, args));

		GoReturn ret = new GoReturn(sumExpCFG, new GoSum(sumExpCFG, new Variable(sumExpCFG, "x"), new Variable(sumExpCFG, "y")));
		sumExpCFG.addNode(ret);

		CFG sumCfg = cfgs.stream().filter(x -> x.getDescriptor().getName().equals("sum")).findFirst().get();
		assertTrue(sumExpCFG.isEqualTo(sumCfg));
		
		// Checking main CFG
		CFG mainExpCFG = new CFG(new CFGDescriptor(file, 9, 10, "main", new Parameter[] {}));

		GoVariableDeclaration aAsg = new GoVariableDeclaration(mainExpCFG, new Variable(mainExpCFG, "a", GoIntType.INSTANCE), 
				new CFGCall(mainExpCFG, file, sumCfg, new Expression[] {
						new GoInteger(mainExpCFG, 1),
						new GoInteger(mainExpCFG, 2),
				}));
		mainExpCFG.addNode(aAsg);

		CFG mainCfg = cfgs.stream().filter(x -> x.getDescriptor().getName().equals("main")).findFirst().get();
		
		assertTrue(mainExpCFG.isEqualTo(mainCfg));
	}
	
	@Test
	public void goTourTest004() throws IOException {
		String file = path + "go004.go";
		Collection<CFG> cfgs = new GoFrontEnd(file).toLiSACFG();

		// Check number of generated cfgs
		assertEquals(cfgs.size(), 2);

		// Checking sum CFG
		Parameter[] args = new Parameter[]{new Parameter("x", GoIntType.INSTANCE), new Parameter("y", GoIntType.INSTANCE)};
		CFG sumExpCFG = new CFG(new CFGDescriptor(file, 5, 19, "sum", GoIntType.INSTANCE, args));

		GoReturn ret = new GoReturn(sumExpCFG, new GoSum(sumExpCFG, new Variable(sumExpCFG, "x"), new Variable(sumExpCFG, "y")));
		sumExpCFG.addNode(ret);

		CFG sumCfg = cfgs.stream().filter(x -> x.getDescriptor().getName().equals("sum")).findFirst().get();
		assertTrue(sumExpCFG.isEqualTo(sumCfg));
		
		// Checking main CFG
		CFG mainExpCFG = new CFG(new CFGDescriptor(file, 9, 10, "main", new Parameter[] {}));

		GoVariableDeclaration aAsg = new GoVariableDeclaration(mainExpCFG, new Variable(mainExpCFG, "a"), 
				new OpenCall(mainExpCFG, "open", new Expression[] {
						new GoInteger(mainExpCFG, 1),
						new GoInteger(mainExpCFG, 2),
				}));
		mainExpCFG.addNode(aAsg);

		CFG mainCfg = cfgs.stream().filter(x -> x.getDescriptor().getName().equals("main")).findFirst().get();
		
		assertTrue(mainExpCFG.isEqualTo(mainCfg));
	}
}
