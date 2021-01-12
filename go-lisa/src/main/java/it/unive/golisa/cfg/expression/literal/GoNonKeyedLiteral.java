package it.unive.golisa.cfg.expression.literal;

import java.util.List;

import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Literal;

public class GoNonKeyedLiteral extends Literal {
	
	public GoNonKeyedLiteral(CFG cfg, List<Expression> value, GoType staticType) {
		super(cfg, value, staticType);
	}
}
