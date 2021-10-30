package it.unive.golisa.cfg.statement.assignment;

import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import org.apache.commons.lang3.StringUtils;

public class GoMultiShortVariableDeclaration extends GoMultiAssignment {

	public GoMultiShortVariableDeclaration(CFG cfg, String filePath, int line, int col, Expression[] ids,
			Expression e) {
		super(cfg, filePath, line, col, ids, e);
	}

	@Override
	public String toString() {
		return StringUtils.join(ids, ", ") + " := " + e.toString();
	}
}
