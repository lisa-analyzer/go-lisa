package it.unive.golisa.checker;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import it.unive.golisa.cfg.VariableScopingCFG;
import it.unive.golisa.cfg.statement.GoRoutine;
import it.unive.golisa.cfg.statement.assignment.GoAssignment;
import it.unive.golisa.cfg.statement.assignment.GoMultiAssignment;
import it.unive.golisa.cfg.statement.assignment.GoMultiShortVariableDeclaration;
import it.unive.golisa.cfg.statement.assignment.GoVariableDeclaration;
import it.unive.golisa.cfg.statement.block.IdInfo;
import it.unive.lisa.checks.syntactic.CheckTool;
import it.unive.lisa.checks.syntactic.SyntacticCheck;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.annotations.Annotation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.VariableTableEntry;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.program.cfg.statement.call.CFGCall;

/**
 * Routune source checker.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoRoutineSourcesChecker implements SyntacticCheck {

	@Override
	public void beforeExecution(CheckTool tool) {
	}

	@Override
	public void afterExecution(CheckTool tool) {
	}

	@Override
	public boolean visit(CheckTool tool, CFG graph, Statement node) {

		if (node instanceof GoRoutine) {
			GoRoutine routine = (GoRoutine) node;
			CFGCall cfg = (CFGCall) routine.getExpression();
			Map<String, Set<IdInfo>> visibleIds = ((VariableScopingCFG) graph).getVisibleIds(routine);
			checkGoRoutine((VariableScopingCFG) graph, visibleIds, cfg);
		}

		return true;
	}

	private void checkGoRoutine(VariableScopingCFG graph, Map<String, Set<IdInfo>> visibleIds, CFGCall cfgCall) {
		for (CFG cfg : cfgCall.getTargetedCFGs()) {
			VariableScopingCFG vsCFG = (VariableScopingCFG) cfg;
			for (Statement node : vsCFG.getNodes()) {
				if (node instanceof GoAssignment) {
					GoAssignment assign = (GoAssignment) node;
					Expression expr = assign.getLeft();
					if (expr instanceof VariableRef)
						checkMatchReference((VariableRef) expr, visibleIds, graph);
				} else if (node instanceof GoMultiAssignment) {
					GoMultiAssignment multiAssign = (GoMultiAssignment) node;
					for (Expression id : multiAssign.getIds())
						if (id instanceof VariableRef)
							checkMatchReference((VariableRef) id, visibleIds, graph);
				}
				if (node instanceof GoVariableDeclaration) {
					GoVariableDeclaration assign = (GoVariableDeclaration) node;
					Expression expr = assign.getLeft();
					if (expr instanceof VariableRef)
						checkMatchReference((VariableRef) expr, visibleIds, graph);
				} else if (node instanceof GoMultiShortVariableDeclaration) {
					GoMultiShortVariableDeclaration multiAssign = (GoMultiShortVariableDeclaration) node;
					for (Expression id : multiAssign.getIds())
						if (id instanceof VariableRef)
							checkMatchReference((VariableRef) id, visibleIds, graph);
				}
			}
		}

	}

	private void checkMatchReference(VariableRef ref, Map<String, Set<IdInfo>> visibleIds, VariableScopingCFG graph) {

		for (Entry<String, Set<IdInfo>> e : visibleIds.entrySet()) {
			if (e.getKey().equals(ref.getName())) {
				for (IdInfo info : e.getValue())
					for (VariableTableEntry table : graph.getDescriptor().getVariables())
						if (table.getName().equals(info.getRef().getName())
								|| table.getLocation().equals(info.getRef().getLocation()))
							table.addAnnotation(new Annotation("lisa.taint.Tainted"));
			}
		}
	}

	@Override
	public boolean visit(CheckTool tool, CFG g) {
		return true;
	}

	@Override
	public boolean visit(CheckTool tool, CFG graph, Edge edge) {
		return true;
	}

	@Override
	public boolean visitUnit(CheckTool tool, Unit unit) {
		return true;
	}

	@Override
	public void visitGlobal(CheckTool tool, Unit unit, Global global, boolean instance) {
		global.addAnnotation(new Annotation("lisa.taint.Tainted"));
	}
}