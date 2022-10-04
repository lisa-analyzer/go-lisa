package it.unive.golisa.cfg.expression.literal;

import it.unive.golisa.cfg.type.GoStringType;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.literal.Literal;

/**
 * A Go rune literal (e.g., 'a').
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoRune extends Literal<String> {

	/**
	 * Builds a Go rune literal value.
	 * 
	 * @param cfg      the {@link CFG} where this expression lies
	 * @param location the location where this expression is defined
	 * @param value    the value
	 */
	public GoRune(CFG cfg, SourceCodeLocation location, String value) {
		super(cfg, location, value, GoStringType.INSTANCE);
	}

	@Override
	public String toString() {
		return "'" + getValue() + "'";
	}
}
