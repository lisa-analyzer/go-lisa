package it.unive.golisa.checker.hf;

import it.unive.golisa.cfg.statement.assignment.GoAssignment;
import it.unive.golisa.cfg.statement.assignment.GoMultiAssignment;
import it.unive.golisa.cfg.statement.assignment.GoShortVariableDeclaration;
import it.unive.golisa.cfg.statement.assignment.GoVariableDeclaration;
import it.unive.golisa.cfg.utils.CFGUtils;
import it.unive.golisa.checker.hf.readwrite.ReadWriteHFUtils;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.ReportingTool;
import it.unive.lisa.checks.syntactic.SyntacticCheck;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.controlFlow.ControlFlowStructure;
import it.unive.lisa.program.cfg.controlFlow.IfThenElse;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.util.datastructures.graph.code.CodeGraph;
import java.util.HashMap;
import java.util.Map;

/**
 * Unhandled errors Checker in Hyperledger Fabric.
 *
 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
 */
public class UnhandledErrorsChecker implements SyntacticCheck {

	private Map<Call, Boolean> assignmentMap;

	@Override
	public void beforeExecution(ReportingTool tool) {

		assignmentMap = new HashMap<>();
	}

	@Override
	public void afterExecution(ReportingTool tool) {

		for (Call call : assignmentMap.keySet()) {
			if (!assignmentMap.get(call).booleanValue()) {
				tool.warnOn(call, "Unhandled error of a blockchain "
						+ (ReadWriteHFUtils.isReadCall((Call) call) ? "read" : (ReadWriteHFUtils.isWriteCall((Call) call) ? "write" : "event emission"))
						+ " operation. The error seems not assigned in any variable");
			}

		}
	}

	@Override
	public boolean visit(ReportingTool tool, CFG graph, Statement node) {

		if (node instanceof Call) {
			if (ReadWriteHFUtils.isReadOrWriteCall((Call) node)  || isEvent((Call) node)) {
				if (!assignmentMap.containsKey((Call) node))
					assignmentMap.put((Call) node, Boolean.FALSE);
			}
		}

		if (node instanceof GoMultiAssignment) {
			GoMultiAssignment multiAssign = (GoMultiAssignment) node;
			Expression expr = multiAssign.getExpressionToAssign();
			if (expr instanceof Call) {
				if (ReadWriteHFUtils.isReadOrWriteCall((Call) expr)) {
					assignmentMap.put((Call) expr, Boolean.TRUE);
					if (multiAssign.getIds().length == 2)
						if (multiAssign.getIds()[1] instanceof VariableRef) {
							checkVariableRef((VariableRef) multiAssign.getIds()[1], (Call) expr, tool, graph, node);
						}
				}
			}
		}

		if (node instanceof GoAssignment) {
			GoAssignment assign = (GoAssignment) node;
			Expression right = assign.getRight();
			if (right instanceof Call) {
				if (ReadWriteHFUtils.isWriteCall((Call) right) || isEvent((Call) node)) {
					assignmentMap.put((Call) right, Boolean.TRUE);
					Expression left = assign.getLeft();
					if (left instanceof VariableRef) {
						checkVariableRef((VariableRef) left, (Call) right, tool, graph, node);
					}
				}

			}
		}

		if (node instanceof GoShortVariableDeclaration) {
			GoShortVariableDeclaration declr = (GoShortVariableDeclaration) node;
			Expression right = declr.getRight();
			if (right instanceof Call) {
				if (ReadWriteHFUtils.isWriteCall((Call) right) || isEvent((Call) node)) {
					assignmentMap.put((Call) right, Boolean.TRUE);
					Expression left = declr.getLeft();
					if (left instanceof VariableRef) {
						checkVariableRef((VariableRef) left, (Call) right, tool, graph, node);
					}
				}

			}
		}

		if (node instanceof GoVariableDeclaration) {
			GoVariableDeclaration declr = (GoVariableDeclaration) node;
			Expression right = declr.getRight();
			if (right instanceof Call) {
				if (ReadWriteHFUtils.isWriteCall((Call) right) || isEvent((Call) node)) {
					assignmentMap.put((Call) right, Boolean.TRUE);
					Expression left = declr.getLeft();
					if (left instanceof VariableRef) {
						checkVariableRef((VariableRef) left, (Call) right, tool, graph, node);
					}
				}

			}
		}

		return true;
	}

	private boolean isEvent(Call call) {
		return call.getTargetName().equals("SetEvent") && call.getParameters().length == 3;
	}

	private void checkVariableRef(VariableRef ref, Call call, ReportingTool tool, CFG graph, Statement node) {
		if (GoLangUtils.isBlankIdentifier(ref.getVariable()))
			tool.warnOn(node,
					"Unhandled error of a blockchain "
							+ (ReadWriteHFUtils.isReadCall((Call) call) ? "read" : (ReadWriteHFUtils.isWriteCall((Call) call) ? "write" : "event emission"))
							+ " operation. It is discarded during the assignment.");
		else {
			boolean found = false;
			for (ControlFlowStructure cfs : graph.getDescriptor().getControlFlowStructures()) {
				if (cfs instanceof IfThenElse) {
					CodeGraph<CFG, Statement,
							Edge> path = CFGUtils.getPath(graph, node, cfs.getCondition());
					if (path != null
							&& !existVariableOverwriteInPath(path, node, ref)
							&& isVariableRefUsedInCondition(ref, cfs.getCondition())) {
						found = true;
						break;
					}
				}

			}

			if (!found)
				tool.warnOn(node, "Unhandled error of a blockchain "
						+ (ReadWriteHFUtils.isReadCall((Call) call) ? "read" : (ReadWriteHFUtils.isWriteCall((Call) call) ? "write" : "event emission"))
						+ " operation. It seems not checked in any condition statements in the method");
		}
	}

	private boolean existVariableOverwriteInPath(CodeGraph<CFG, Statement, Edge> path, Statement node,
			VariableRef ref) {
		for (Statement n : path.getNodeList()) {
			if (n instanceof GoMultiAssignment) {
				if (!n.equals(node)) {
					for (Expression id : ((GoMultiAssignment) n).getIds()) {
						if (id instanceof VariableRef
								&& ((VariableRef) id).getVariable().getName().equals(ref.getVariable().getName()))
							return true;
					}
				}
			} else if (n instanceof GoAssignment) {
				Expression target = ((GoAssignment) n).getLeft();
				if (target instanceof VariableRef
						&& ((VariableRef) target).getVariable().getName().equals(ref.getVariable().getName()))
					return true;
			}
		}
		return false;
	}

	private boolean isVariableRefUsedInCondition(VariableRef ref, Statement condition) {
		return CFGUtils.matchNodeOrSubExpressions(condition, e -> {
			if (e instanceof VariableRef) {
				return ((VariableRef) e).getVariable().getName().equals(ref.getVariable().getName());
			}
			return false;
		});
	}

	@Override
	public boolean visit(ReportingTool tool, CFG g) {
		return true;
	}

	@Override
	public boolean visit(ReportingTool tool, CFG graph, Edge edge) {
		return true;
	}

	@Override
	public boolean visitUnit(ReportingTool tool, Unit unit) {
		return true;
	}

	@Override
	public void visitGlobal(ReportingTool tool, Unit unit, Global global, boolean instance) {

	}
}
