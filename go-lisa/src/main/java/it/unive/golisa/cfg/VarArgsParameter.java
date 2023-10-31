package it.unive.golisa.cfg;

import it.unive.lisa.program.annotations.Annotations;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.Type;

/**
 * The variadic argument parameter.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class VarArgsParameter extends Parameter {

	/**
	 * Builds the parameter.
	 * 
	 * @param location     the location of this parameter
	 * @param name         the name of this parameter
	 * @param defaultValue the default value of this parameter
	 */
	public VarArgsParameter(CodeLocation location, String name, Expression defaultValue) {
		super(location, name, defaultValue);
	}

	/**
	 * Builds the parameter.
	 * 
	 * @param location     the location of this parameter
	 * @param name         the name of this parameter
	 * @param staticType   the static type of this parameter
	 * @param defaultValue the default value of this parameter
	 * @param annotations  this annotations of this parameter
	 */
	public VarArgsParameter(CodeLocation location, String name, Type staticType, Expression defaultValue,
			Annotations annotations) {
		super(location, name, staticType, defaultValue, annotations);
	}

	/**
	 * Builds the parameter.
	 * 
	 * @param location   the location of this parameter
	 * @param name       the name of this parameter
	 * @param staticType the static type of this parameter
	 */
	public VarArgsParameter(CodeLocation location, String name, Type staticType) {
		super(location, name, staticType);
	}

	/**
	 * Builds the parameter.
	 * 
	 * @param location the location of this parameter
	 * @param name     the name of this parameter
	 */
	public VarArgsParameter(CodeLocation location, String name) {
		super(location, name);
	}
}
