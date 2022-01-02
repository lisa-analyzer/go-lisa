package it.unive.golisa.cfg.statement.assignment;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.VariableRef;

public class GoMultiShortVariableDeclaration extends GoMultiAssignment {
	
	public GoMultiShortVariableDeclaration(CFG cfg, String filePath, int line, int col, Expression[]  ids, Expression e) {
		super(cfg, filePath, line, col, ids, e, computeSetVarSpec(ids));
	}
	
	private static Set<VariableRef> computeSetVarSpec(Expression[] ids) {
		
	Set<VariableRef> set = new HashSet<VariableRef>();
	 for(Expression id : ids)
		 if(!GoLangUtils.refersToBlankIdentifier(id) && id instanceof VariableRef)
			 set.add((VariableRef) id);
		return set;
	}

	@Override
	public String toString() {
		return StringUtils.join(ids, ", ") + " := " + e.toString();
	}
}
