package it.unive.golisa.program.cfg.controlflow.switches;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.controlFlow.ControlFlowStructure;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.util.datastructures.graph.code.NodeList;

/**
 * A switch control flow structure.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class TypeSwitch extends ControlFlowStructure {

	private final TypeSwitchCase[] cases;

	private final DefaultSwitchCase defaultCase;

	/**
	 * Builds the switch control flow structure.
	 * 
	 * @param cfgMatrix     the matrix behind this control flow structure
	 * @param condition     the switch condition
	 * @param firstFollower the first follower of this control flow structure
	 * @param cases         the cases associated with this control flow
	 *                          structure
	 * @param defaultCase   the default case
	 */
	public TypeSwitch(NodeList<CFG, Statement, Edge> cfgMatrix, Statement condition, Statement firstFollower,
			TypeSwitchCase[] cases, DefaultSwitchCase defaultCase) {
		super(cfgMatrix, condition, firstFollower);
		this.cases = cases;
		this.defaultCase = defaultCase;
	}

	@Override
	protected Collection<Statement> bodyStatements() {
		Collection<Statement> body = new HashSet<>();
		for (SwitchCase case_ : cases)
			body.addAll(case_.getBody());
		if (defaultCase != null)
			body.addAll(defaultCase.getBody());
		return body;
	}

	@Override
	public boolean contains(Statement st) {
		return bodyStatements().contains(st);
	}

	@Override
	public String toString() {
		return "switch[" + getCondition() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((cases == null) ? 0 : cases.hashCode());
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
		TypeSwitch other = (TypeSwitch) obj;
		if (cases == null) {
			if (other.cases != null)
				return false;
		} else if (!cases.equals(other.cases))
			return false;
		return true;
	}

	@Override
	public Collection<Statement> getTargetedStatements() {
		Collection<Statement> targeted = new HashSet<>();
		for (SwitchCase case_ : cases)
			targeted.addAll(cfgMatrix.followersOf(case_.getCondition()));
		targeted.add(getFirstFollower());
		return targeted;
	}

	@Override
	public void simplify(Set<Statement> targets) {
		for (SwitchCase case_ : cases)
			case_.simplify(targets);
		if (defaultCase != null)
			defaultCase.simplify(targets);
	}

	@Override
	public void addWith(Statement toAdd, Statement reference) {
		for (SwitchCase switchCase : cases)
			if (switchCase.getBody().contains(reference))
				switchCase.getBody().add(reference);

		if (defaultCase.getBody().contains(reference))
			defaultCase.getBody().add(reference);
	}

	@Override
	public void replace(Statement original, Statement replacement) {
		for (SwitchCase switchCase : cases)
			if (switchCase.getBody().contains(original)) {
				switchCase.getBody().remove(original);
				switchCase.getBody().add(replacement);
			}

		if (defaultCase.getBody().contains(original)) {
			defaultCase.getBody().remove(original);
			defaultCase.getBody().add(replacement);
		}
	}
}
