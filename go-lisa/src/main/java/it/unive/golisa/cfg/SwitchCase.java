package it.unive.golisa.cfg;

import java.util.Collection;
import java.util.HashSet;

import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.controlFlow.ControlFlowStructure;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.NoOp;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.util.datastructures.graph.code.NodeList;

/**
 * A switch-case control flow structure.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class SwitchCase extends ControlFlowStructure {

	private final Collection<Statement> body;

	/**
	 * Builds the switch-case control flow structure.
	 * 
	 * @param cfgMatrix     the matrix behind this control flow structure
	 * @param condition     the switch-case condition
	 * @param firstFollower the first follower of this switch-case
	 * @param body          the body associated with this switch-case
	 */
	public SwitchCase(NodeList<CFG, Statement, Edge> cfgMatrix, Statement condition, Statement firstFollower,
			Collection<Statement> body) {
		super(cfgMatrix, condition, firstFollower);
		this.body = body;
	}

	public Collection<Statement> getBody() {
		return body;
	}
	
	@Override
	protected Collection<Statement> bodyStatements() {
		return body;
	}

	@Override
	public boolean contains(Statement st) {
		return body.contains(st);
	}

	@Override
	public void simplify() {
		body.removeIf(NoOp.class::isInstance);
	}

	@Override
	public String toString() {
		return "case[" + getCondition() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((body == null) ? 0 : body.hashCode());
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
		SwitchCase other = (SwitchCase) obj;
		if (body == null) {
			if (other.body != null)
				return false;
		} else if (!body.equals(other.body))
			return false;
		return true;
	}

	@Override
	public Collection<Statement> getTargetedStatements() {
		return body;
	}
}