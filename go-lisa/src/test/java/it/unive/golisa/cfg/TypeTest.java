package it.unive.golisa.cfg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collection;

import org.junit.Test;

import it.unive.golisa.cfg.custom.GoVariableDeclaration;
import it.unive.golisa.cfg.literal.GoBoolean;
import it.unive.golisa.cfg.type.GoBoolType;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.CFGDescriptor;
import it.unive.lisa.cfg.statement.Parameter;
import it.unive.lisa.cfg.statement.Variable;
import it.unive.lisa.cfg.type.Untyped;

public class TypeTest {

	String path = "src/test/resources/go-tutorial/type/";

	@Test
	public void singleVariableDeclaration() throws IOException {

		String file = path + "type001.go";
		Collection<CFG> cfgs = new GoToCFG(file).toLiSACFG();

		// Check number of generated cfgs
		assertEquals(cfgs.size(), 1);

		Parameter[] args = new Parameter[] {};
		CFG expectedCfg = new CFG(new CFGDescriptor(file, 5, 10, "main", Untyped.INSTANCE, args));

		GoVariableDeclaration xAsg = new GoVariableDeclaration(expectedCfg, new Variable(expectedCfg, "x", GoBoolType.INSTANCE), 
				new GoBoolean(expectedCfg, true));
		expectedCfg.addNode(xAsg);


		CFG cfg = cfgs.iterator().next();	
		assertTrue(expectedCfg.isEqualTo(cfg));
	}
}
