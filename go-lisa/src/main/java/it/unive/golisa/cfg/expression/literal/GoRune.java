package it.unive.golisa.cfg.expression.literal;

import it.unive.golisa.cfg.type.GoStringType;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Literal;

public class GoRune extends Literal {

	public GoRune(CFG cfg, String value) {
		// TODO: create rune type
		super(cfg, value, GoStringType.INSTANCE);
	}
	
	public GoRune(CFG cfg, String sourceFile, int line, int col, String value) {
		super(cfg, sourceFile, line, col, value, GoStringType.INSTANCE);
	}
	
	@Override
	public String toString() {
		return "'" + getValue() + "'";
	}
}
