package it.unive.golisa.cfg.statement;

import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Assignment;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.statement.Variable;

/**
 * A Go constant declaration class (e.g., const x int = 5).
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoConstantDeclaration extends Assignment {
	
	/**
	 * Builds a Go constant declaration, declaring {@code var} 
	 * having constant value {@code expression} 
	 * without make explicit the location (i.e. no source
	 * file/line/column is available).
	 * 
	 * @param cfg        the cfg that this statement belongs to
	 * @param target     the variable
	 * @param expression the expression to assign to {@code var}
	 */
	public GoConstantDeclaration(CFG cfg, Variable target, Expression expression) {
		super(cfg, target, expression);
	}
	
	/**
	 * Builds a Go assignment, assigning {@code expression} to {@code target},
	 * happening at the given location in the program.
	 * 
	 * @param cfg        the cfg that this statement belongs to
	 * @param sourceFile the source file where this statement happens. If unknown,
	 *                   use {@code null}
	 * @param line       the line number where this statement happens in the source
	 *                   file. If unknown, use {@code -1}
	 * @param col        the column where this statement happens in the source file.
	 *                   If unknown, use {@code -1}
	 * @param var	     the variable of the constant declaration
	 * @param expression the expression to assign to {@code var}
	 */
	public GoConstantDeclaration(CFG cfg, String sourceFile, int line, int col, Variable target, Expression expression) {
		super(cfg, sourceFile, line, col, target, expression);
	}
}
