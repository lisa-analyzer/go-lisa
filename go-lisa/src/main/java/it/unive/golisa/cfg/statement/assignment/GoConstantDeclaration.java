package it.unive.golisa.cfg.statement.assignment;


import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Assignment;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.VariableRef;

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
	public GoConstantDeclaration(CFG cfg, VariableRef target, Expression expression) {
		this(cfg, null, -1, -1, target, expression);
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
	public GoConstantDeclaration(CFG cfg, String sourceFile, int line, int col, VariableRef target, Expression expression) {
		super(cfg, new SourceCodeLocation(sourceFile, line, col), target, expression);
	}
}
