package it.unive.golisa.cfg.expression.unary;


import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.numeric.Negation;


/**
 * The Go unary minus expression (e.g., -x).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * @author <a href="mailto:luca.negrini@unive.it">Luca Negrini</a>
 */
public class GoMinus extends Negation {

	/**
	 * Builds the minus expression.
	 * 
	 * @param cfg      the {@link CFG} where this expression lies
	 * @param location the location where this expression is defined
	 * @param exp      the expression of minus
	 */
	public GoMinus(CFG cfg, CodeLocation location, Expression expression) {
		super(cfg, location, expression);
	}

}
