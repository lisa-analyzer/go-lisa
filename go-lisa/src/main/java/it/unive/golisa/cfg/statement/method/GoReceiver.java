package it.unive.golisa.cfg.statement.method;

import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.Parameter;

public class GoReceiver extends Parameter {

	public GoReceiver(SourceCodeLocation location, String name, GoType type) {
		super(location, name, type);
	}
}
