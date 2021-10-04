package it.unive.golisa.cfg.expression.literal;

import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.literal.Literal;
import it.unive.lisa.type.Type;

public class GoFunctionLiteral extends Literal<CFG> {

	public GoFunctionLiteral(CFG cfg, CodeLocation location, CFG value, Type staticType) {
		super(cfg, location, value, staticType);
	}

}
