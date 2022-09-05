package it.unive.golisa.cfg.expression;

import java.util.Collection;

import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.controlFlow.Loop;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.util.datastructures.graph.code.NodeList;

public class GoForRange extends Loop{

	Statement idxKey, idxValue;

	public GoForRange(NodeList<CFG, Statement, Edge> cfgMatrix, Statement idxKey, Statement idxValue, Statement condition, Statement firstFollower,
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