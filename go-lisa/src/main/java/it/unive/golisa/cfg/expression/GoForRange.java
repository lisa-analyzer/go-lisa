package it.unive.golisa.cfg.expression;

import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.controlFlow.Loop;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.util.datastructures.graph.code.NodeList;
import java.util.Collection;

/**
 * The Go for-range loop.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoForRange extends Loop {

	private Statement idxKey, idxValue;

	/**
	 * Builds the for-range loop.
	 *
	 * @param cfgMatrix     the cfg behind this loop
	 * @param idxKey        the index key
	 * @param idxValue      the index value
	 * @param condition     the ranged condition
	 * @param firstFollower the first follower
	 * @param body          the body
	 */
	public GoForRange(NodeList<CFG, Statement, Edge> cfgMatrix, Statement idxKey, Statement idxValue,
			Statement condition, Statement firstFollower,
			Collection<Statement> body) {
		super(cfgMatrix, condition, firstFollower, body);
		this.idxKey = idxKey;
		this.idxValue = idxValue;
	}

	@Override
	public boolean contains(Statement st) {
		return super.contains(st) || idxKey.equals(st) || idxValue.equals(st);
	}

}