package it.unive.golisa.cfg.expression.literal;

import it.unive.golisa.cfg.type.GoStringType;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.literal.Literal;

/**
 * A Go string value. The static type of a Go string value is
 * {@link GoStringType}.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoString extends Literal<String> {

	/**
	 * Builds a Go string value. The location where this Go string value appears
	 * is unknown (i.e. no source file/line/column is available). The static
	 * type of a Go string value is {@link GoStringType}.
	 * 
	 * @param cfg   the cfg that this Go string belongs to
	 * @param value the string value
	 */
	public GoString(CFG cfg, SourceCodeLocation location, String value) {
		super(cfg, location, value, GoStringType.INSTANCE);
	}

	@Override
	public String toString() {
		return "\"" + getValue() + "\"";
	}
}
