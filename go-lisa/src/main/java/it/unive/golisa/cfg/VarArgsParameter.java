package it.unive.golisa.cfg;

import it.unive.lisa.program.annotations.Annotations;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.Type;

public class VarArgsParameter extends Parameter {

	public VarArgsParameter(CodeLocation location, String name, Expression defaultValue) {
		super(location, name, defaultValue);
	}

	public VarArgsParameter(CodeLocation location, String name, Type staticType, Expression defaultValue,
			Annotations annotations) {
		super(location, name, staticType, defaultValue, annotations);
	}

	public VarArgsParameter(CodeLocation location, String name, Type staticType) {
		super(location, name, staticType);
	}

	public VarArgsParameter(CodeLocation location, String name) {
		super(location, name);
	}
}
