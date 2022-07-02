package it.unive.golisa.cfg.statement.assignment;

import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Assignment;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.VariableRef;

/**
 * A Go constant declaration class (e.g., const x int = 5).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoConstantDeclaration extends Assignment {

	/**
	 * Builds a Go assignment, assigning {@code expression} to {@code target}.
	 * 
	 * @param cfg        the cfg that this statement belongs to
	 * @param location   the location where this statement is defined
	 * @param target     the variable of the constant declaration
	 * @param expression the expression to assign to {@code var}
	 */
	public GoConstantDeclaration(CFG cfg, SourceCodeLocation location, VariableRef target, Expression expression) {
		super(cfg, location, target, expression);
	}
}
