package it.unive.golisa.checker.hf;

import it.unive.golisa.cfg.statement.assignment.GoMultiAssignment;
import it.unive.golisa.cfg.utils.CFGUtils;
import it.unive.golisa.cfg.utils.CFGUtils.Search;
import it.unive.golisa.checker.hf.readwrite.ReadWriteHFUtils;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.checks.syntactic.CheckTool;
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

/**
 * Unhandled errors Checker in Hyperledger Fabric.
 *
 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
 */
public class UnhandledErrorsChecker implements SyntacticCheck {

	@Override
	public void beforeExecution(CheckTool tool) {
	}

	@Override
	public void afterExecution(CheckTool tool) {
	}

	@Override
	public boolean visit(CheckTool tool, CFG graph, Statement node) {

		if (node instanceof GoMultiAssignment) {
			GoMultiAssignment multiAssign = (GoMultiAssignment) node;
			Expression expr = multiAssign.getExpressionToAssign();
			if (expr instanceof Call) {
				if (ReadWriteHFUtils.isReadOrWriteCall((Call) expr)) {
					if (multiAssign.getIds().length == 2)
						if (multiAssign.getIds()[1] instanceof VariableRef) {
							VariableRef ref = (VariableRef) multiAssign.getIds()[1];
							if (GoLangUtils.isBlankIdentifier(ref.getVariable()))
								tool.warnOn(node,
										"Unhandled error of a blockchain "
												+ (ReadWriteHFUtils.isReadCall((Call) expr) ? "read" : "write")
												+ " operation. It is discarded during the assignment.");
							else {
								// TODO: check if the err variable is used in a
								// conditional statement
								boolean found = false;
								for (ControlFlowStructure cfs : graph.getControlFlowStructures()) {
									if (cfs instanceof IfThenElse) {
										if (CFGUtils.existPath(graph, node, cfs.getCondition(), Search.BFS)
										// TODO: add condition to avoid
										// overwrite of err
										) {
											if (isVariableRefUsedInCondition(multiAssign.getIds()[1],
													cfs.getCondition())) {
												found = true;
												break;
											}
										}

									}
								}

								if (!found)
									tool.warnOn(node,
											"Unhandled error of a blockchain "
													+ (ReadWriteHFUtils.isReadCall((Call) expr) ? "read" : "write")
													+ " operation. It seems not checked.");
							}
						}

				}
			}
		}
		return true;
	}

	private boolean isVariableRefUsedInCondition(Expression expression, Statement condition) {
		// TODO
		return false;
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

	}
}
