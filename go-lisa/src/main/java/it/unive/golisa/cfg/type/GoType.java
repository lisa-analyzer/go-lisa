package it.unive.golisa.cfg.type;

import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.type.Type;

public interface GoType extends Type {

	public Expression defaultValue(CFG cfg);
}
