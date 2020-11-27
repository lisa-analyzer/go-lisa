package it.unive.golisa.cfg.expression.literal;

import java.util.List;

import it.unive.golisa.cfg.type.composite.GoArrayType;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.statement.Literal;

public class GoArrayLiteral extends Literal {
	
	public GoArrayLiteral(CFG cfg, List<Expression> value, GoArrayType staticType) {
		super(cfg, value, staticType);
	}
}
