package it.unive.golisa.cfg.expression.literal;

import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.statement.Literal;
import it.unive.lisa.cfg.type.Untyped;

public class GoRawValue extends Literal {
	
	public GoRawValue(CFG cfg, Expression[] exps) {
		super(cfg, exps, Untyped.INSTANCE);
	}
	
	@Override
	public String toString() {
		return "(" + getValue() + ")";
	}
}
