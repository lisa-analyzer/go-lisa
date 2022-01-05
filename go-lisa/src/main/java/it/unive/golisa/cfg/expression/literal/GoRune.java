package it.unive.golisa.cfg.expression.literal;

import it.unive.golisa.cfg.type.GoStringType;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.literal.Literal;

public class GoRune extends Literal<String> {

	public GoRune(CFG cfg, SourceCodeLocation location, String value) {
		super(cfg, location, value, GoStringType.INSTANCE);
	}

	@Override
	public String toString() {
		return "'" + getValue() + "'";
	}
}
