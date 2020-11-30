package it.unive.golisa.cfg.expression.literal;

import java.util.Map;

import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.statement.Literal;

public class GoKeyedLiteral extends Literal {
	
	public GoKeyedLiteral(CFG cfg, Map<String, Expression> value, GoType staticType) {
		super(cfg, value, staticType);
	}
}
