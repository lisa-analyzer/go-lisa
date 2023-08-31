package it.unive.golisa.checker;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import it.unive.golisa.analysis.ni.IntegrityNIDomain;
import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.cfg.VariableScopingCFG;
import it.unive.golisa.cfg.expression.GoCollectionAccess;
import it.unive.golisa.cfg.statement.GoRoutine;
import it.unive.golisa.cfg.statement.block.IdInfo;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.checks.syntactic.CheckTool;
import it.unive.lisa.checks.syntactic.SyntacticCheck;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.VariableTableEntry;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.program.cfg.statement.call.CFGCall;
import it.unive.lisa.util.datastructures.graph.GraphVisitor;

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
			Expression expr = routine.getExpression();
			if (expr instanceof CFGCall) {
				Map<String, Set<IdInfo>> visibleIds = ((VariableScopingCFG) graph).getVisibleIds(routine);
				checkGoRoutine((VariableScopingCFG) graph, visibleIds, (CFGCall) expr);
			}

			//mark actual parameters as tainted; a side effect might occur
			if (expr instanceof it.unive.lisa.program.cfg.statement.call.Call) {
				it.unive.lisa.program.cfg.statement.call.Call call = (it.unive.lisa.program.cfg.statement.call.Call) expr;
				for( Expression ee : call.getSubExpressions()) { //TODO: maybe needs a recursive check on the sub exprs

					Expression tmp = ee;
					if(ee instanceof GoCollectionAccess) {
						GoCollectionAccess acc = (GoCollectionAccess) ee;
						tmp = acc.getLeft();
					}

					if (tmp instanceof VariableRef) {
						VariableRef ref = (VariableRef) tmp;
						for (Entry<String, Set<IdInfo>> e : ((VariableScopingCFG) graph).getVisibleIds(routine).entrySet())
							if (e.getKey().equals(ref.getName()))
								for (IdInfo info : e.getValue())
									for (VariableTableEntry table : graph.getDescriptor().getVariables())
										if (table.getName().equals(info.getRef().getName())
												|| table.getLocation().equals(info.getRef().getLocation())) {
											table.addAnnotation(TaintDomain.TAINTED_ANNOTATION);
											table.addAnnotation(IntegrityNIDomain.LOW_ANNOTATION);
										}
					}
				}

			}
			/*
			 *
			 *
			 for (VariableTableEntry table : graph.getDescriptor().getVariables())
				if (table.getName().equals(info.getRef().getName())
						|| table.getLocation().equals(info.getRef().getLocation()))
					table.addAnnotation(TaintDomain.TAINTED_ANNOTATION);
			 */
		}

		return true;
	}

	private void checkGoRoutine(VariableScopingCFG graph, Map<String, Set<IdInfo>> visibleIds, CFGCall cfgCall) {
		class GoRoutineVisitor implements GraphVisitor<CFG, Statement, Edge, Void> {

			@Override
			public boolean visit(Void tool, CFG graph) {
				return true;
			}

			@Override
			public boolean visit(Void tool, CFG routine, Statement node) {
				if (node instanceof VariableRef) {
					VariableRef ref = (VariableRef) node;
					for (Entry<String, Set<IdInfo>> e : visibleIds.entrySet())
						if (e.getKey().equals(ref.getName()))
							for (IdInfo info : e.getValue())
								for (VariableTableEntry table : graph.getDescriptor().getVariables())
									if (table.getName().equals(info.getRef().getName())
											|| table.getLocation().equals(info.getRef().getLocation())) {
										table.addAnnotation(TaintDomain.TAINTED_ANNOTATION);
										table.addAnnotation(IntegrityNIDomain.LOW_ANNOTATION);
									}
				}
				return true;
			}

			@Override
			public boolean visit(Void tool, CFG graph, Edge edge) {
				return true;
			}
		}

		for (CFG cfg : cfgCall.getTargetedCFGs())
			cfg.accept(new GoRoutineVisitor(), null);
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
		if(!global.getLocation().getCodeLocation().contains(GoLangUtils.GO_RUNTIME_SOURCE)) {
			global.addAnnotation(TaintDomain.TAINTED_ANNOTATION);
			global.addAnnotation(IntegrityNIDomain.LOW_ANNOTATION);
		}
	}
}
