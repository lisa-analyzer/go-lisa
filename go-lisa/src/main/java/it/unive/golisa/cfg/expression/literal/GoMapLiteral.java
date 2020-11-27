package it.unive.golisa.cfg.literal;

import java.util.Map;

import it.unive.golisa.cfg.type.composite.GoMapType;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.statement.Literal;

public class GoMapLiteral extends Literal {

	public GoMapLiteral(CFG cfg, Map<Expression, Expression> value, GoMapType staticType) {
		super(cfg, value, staticType);
	}
}
