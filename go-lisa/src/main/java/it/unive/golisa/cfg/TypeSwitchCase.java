package it.unive.golisa.cfg;

import java.util.Collection;

import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Statement;

/**
 * A type switch-case control flow structure.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class TypeSwitchCase extends SwitchCase {

	private final Expression initialization;

	/**
	 * Builds the switch-case control flow structure.
	 * 
	 * @param initialization the initialization expression
	 * @param condition      the switch-case condition
	 * @param body           the body associated with this switch-case
	 */
	public TypeSwitchCase(Expression initialization, Expression condition, Collection<Statement> body) {
		super(condition, body);
		this.initialization = initialization;
	}

	/**
	 * Yields the initialization expression.
	 * 
	 * @return the initialization expression
	 */
	public Expression getInitialization() {
		return initialization;
	}

	@Override
	public String toString() {
		return "case[" + initialization + "; " + getCondition() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((initialization == null) ? 0 : initialization.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		TypeSwitchCase other = (TypeSwitchCase) obj;
		if (initialization == null) {
			if (other.initialization != null)
				return false;
		} else if (!initialization.equals(other.initialization))
			return false;
		return true;
	}
}
