package it.unive.golisa.cfg.statement;

import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Assignment;
import it.unive.lisa.cfg.statement.Expression;

/**
 * A Go assignment (e.g., x = 5).
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoAssignment extends Assignment {

	/**
	 * Builds a Go assignment, assigning {@code expression} to {@code target} 
	 * without make explicit the location (i.e. no source
	 * file/line/column is available).
	 * 
	 * @param cfg        the cfg that this statement belongs to
	 * @param target     the target of the assignment
	 * @param expression the expression to assign to {@code target}
	 */
	public GoAssignment(CFG cfg, Expression target, Expression expression) {
		super(cfg, target, expression);
	}
	
	/**
	 * Builds a Go assignment, assigning {@code expression} to {@code target},
	 * happening at the given location in the program.
	 * 
	 * @param cfg        the cfg that this assignment belongs to
	 * @param sourceFile the source file where this statement happens. If unknown,
	 *                   use {@code null}
	 * @param line       the line number where this assignment happens in the source
	 *                   file. If unknown, use {@code -1}
	 * @param col        the column where this statement happens in the source file.
	 *                   If unknown, use {@code -1}
	 * @param target     the target of the assignment
	 * @param expression the expression to assign to {@code target}
	 */
	public GoAssignment(CFG cfg, String sourceFile, int line, int col, Expression target, Expression expression) {
		super(cfg, sourceFile, line, col, target, expression);
	}
}
