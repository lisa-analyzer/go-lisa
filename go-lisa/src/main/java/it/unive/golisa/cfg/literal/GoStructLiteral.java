package it.unive.golisa.cfg.literal;

import java.util.Map;

import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.statement.Literal;

public class GoStructLiteral extends Literal {
	
	public GoStructLiteral(CFG cfg, Map<Expression, Expression> value, GoStructType staticType) {
		super(cfg, value, staticType);
	}
}
