package it.unive.golisa.cfg.expression.literal;

import it.unive.golisa.cfg.type.GoStringType;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Literal;

public class GoRune extends Literal {

	public GoRune(CFG cfg, String value) {
		// TODO: create rune type
		this(cfg, null, -1, -1, value);
	}
	
	public GoRune(CFG cfg, String sourceFile, int line, int col, String value) {
		super(cfg, new SourceCodeLocation(sourceFile, line, col), value, GoStringType.INSTANCE);
	}
	
	@Override
	public String toString() {
		return "'" + getValue() + "'";
	}
}
