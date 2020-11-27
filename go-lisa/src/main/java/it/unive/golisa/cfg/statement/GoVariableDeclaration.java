package it.unive.golisa.cfg.statement;

import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Assignment;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.statement.Variable;

/**
 * Go variable declaration class (e.g., var x int = 5).
 * TODO: at the moment, we skip the variable type.
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoVariableDeclaration extends Assignment {

	/**
	 * Builds a Go variable declaration with initialization,
	 * assigning {@code expression} to {@code var} 
	 * without make explicit the location (i.e. no source
	 * file/line/column is available).
	 * 
	 * @param cfg        the cfg that this declaration belongs to
	 * @param var     the declared variable
	 * @param expression the expression to assign to {@code var}
	 */
	public GoVariableDeclaration(CFG cfg, Variable var, Expression expression) {
		super(cfg, var, expression);
	}

	/**
	 * Builds a Go variable declaration with initialization,
	 * assigning {@code expression} to {@code target},
	 * happening at the given location in the program.
	 * 
	 * @param cfg        the cfg that this declaration belongs to
	 * @param sourceFile the source file where this declaration happens. If unknown,
	 *                   use {@code null}
	 * @param line       the line number where this declaration happens in the source
	 *                   file. If unknown, use {@code -1}
	 * @param col        the column where this statement happens in the source file.
	 *                   If unknown, use {@code -1}
	 * @param var	     the declared variable
	 * @param expression the expression to assign to {@code var}
	 */
	public GoVariableDeclaration(CFG cfg, String sourceFile, int line, int col, Variable var, Expression expression) {
		super(cfg, sourceFile, line, col, var, expression);
	}
}
