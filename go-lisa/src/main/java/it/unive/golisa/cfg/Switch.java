package it.unive.golisa.cfg;

import java.util.Collection;

import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.controlFlow.ControlFlowStructure;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.NoOp;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.util.datastructures.graph.AdjacencyMatrix;

public class Switch extends ControlFlowStructure {

	private final Collection<Statement> cases;

	public Switch(AdjacencyMatrix<Statement, Edge, CFG> cfgMatrix, Statement condition, Statement firstFollower,
			Collection<Statement> cases) {
		super(cfgMatrix, condition, firstFollower);
		this.cases = cases;
	}

	@Override
	protected Collection<Statement> bodyStatements() {
		return cases;
	}

	@Override
	public boolean contains(Statement st) {
		return cases.contains(st);
	}

	@Override
	public void simplify() {
		cases.removeIf(NoOp.class::isInstance);
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
		Switch other = (Switch) obj;
		if (cases == null) {
			if (other.cases != null)
				return false;
		} else if (!cases.equals(other.cases))
			return false;
		return true;
	}
}