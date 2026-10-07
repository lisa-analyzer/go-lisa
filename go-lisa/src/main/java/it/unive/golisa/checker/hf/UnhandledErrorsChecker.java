package it.unive.golisa.checker.hf;

import it.unive.golisa.cfg.statement.assignment.GoAssignment;
import it.unive.golisa.cfg.statement.assignment.GoMultiAssignment;
import it.unive.golisa.cfg.statement.assignment.GoShortVariableDeclaration;
import it.unive.golisa.cfg.statement.assignment.GoVariableDeclaration;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoPointerType;
import it.unive.golisa.cfg.type.composite.GoTupleType;
import it.unive.golisa.cfg.utils.CFGUtils;
import it.unive.golisa.checker.hf.readwrite.ReadWriteHFUtils;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.ReportingTool;
import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.SimpleAbstractDomain;
import it.unive.lisa.analysis.nonrelational.heap.HeapEnvironment;
import it.unive.lisa.analysis.nonrelational.heap.HeapValue;
import it.unive.lisa.analysis.nonrelational.type.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.type.TypeValue;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.checks.semantic.SemanticCheck;
import it.unive.lisa.checks.semantic.SemanticTool;
import it.unive.lisa.lattices.SimpleAbstractState;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeMember;
import it.unive.lisa.program.cfg.controlFlow.ControlFlowStructure;
import it.unive.lisa.program.cfg.controlFlow.IfThenElse;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Return;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.type.Type;
import it.unive.lisa.util.datastructures.graph.code.CodeGraph;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Unhandled errors Checker in Hyperledger Fabric.
 *
 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
 */
public class UnhandledErrorsChecker<H extends HeapValue<H>, L extends Lattice<L>, T extends TypeValue<T>> implements
		SemanticCheck<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<L>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<L>, TypeEnvironment<T>>> {

	private Map<Call, Boolean> assignmentMap;

	@Override
	public void beforeExecution(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<L>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<L>, TypeEnvironment<T>>> tool) {

		assignmentMap = new HashMap<>();
	}

	@Override
	public void afterExecution(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<L>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<L>, TypeEnvironment<T>>> tool) {
		for (Call call : assignmentMap.keySet()) {
			if (!assignmentMap.get(call).booleanValue()) {
				tool.warnOn(call,
						"Unhandled error of a blockchain "
								+ (ReadWriteHFUtils.isReadCall((Call) call) ? "read"
										: (ReadWriteHFUtils.isWriteCall((Call) call) ? "write" : "event emission"))
								+ " operation. The error seems not assigned in any variable");
			}
		}
	}
	
	@Override
	public void visitGlobal(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<L>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<L>, TypeEnvironment<T>>> tool,
			Unit unit, Global global, boolean instance) {
	}

	@Override
	public boolean visit(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<L>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<L>, TypeEnvironment<T>>> tool,
			CFG graph) {
		return true;
	}

	@Override
	public boolean visit(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<L>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<L>, TypeEnvironment<T>>> tool,
			CFG graph, Statement node) {

		if (node instanceof Call) {
			if (// ReadWriteHFUtils.isReadOrWriteCall((Call) node) ||
			isEvent((Call) node)) {
				if (!assignmentMap.containsKey((Call) node))
					assignmentMap.put((Call) node, Boolean.FALSE);
			}
		}

		if (node instanceof GoMultiAssignment) {
			GoMultiAssignment multiAssign = (GoMultiAssignment) node;
			Expression expr = multiAssign.getExpressionToAssign();
			List<Call> calls = CFGUtils.extractCallsFromStatement(expr);
			for (Call c : calls) {
					/*
					 * if (ReadWriteHFUtils.isReadOrWriteCall(c)) {
					 * assignmentMap.put(c, Boolean.TRUE); if (multiAssign.getIds().length
					 * == 2) if (multiAssign.getIds()[1] instanceof VariableRef) {
					 * checkVariableRef((VariableRef) multiAssign.getIds()[1], c, tool,
					 * graph, node); } }
					 */
			}
		}

		if (node instanceof GoAssignment) {
			GoAssignment assign = (GoAssignment) node;
			Expression right = assign.getRight();
			List<Call> calls = CFGUtils.extractCallsFromStatement(right);
			for (Call c : calls){
				if (// ReadWriteHFUtils.isWriteCall(c) ||
				isEvent(c)) {
					assignmentMap.put(c, Boolean.TRUE);
					Expression left = assign.getLeft();
					if (left instanceof VariableRef) {
						checkVariableRef((VariableRef) left, c, tool, graph, node);
					}
				}

			}
		}

		if (node instanceof GoShortVariableDeclaration) {
			GoShortVariableDeclaration declr = (GoShortVariableDeclaration) node;
			Expression right = declr.getRight();
			List<Call> calls = CFGUtils.extractCallsFromStatement(right);
			for (Call c : calls){
				if (// ReadWriteHFUtils.isWriteCall(c) ||
				isEvent(c)) {
					assignmentMap.put(c, Boolean.TRUE);
					Expression left = declr.getLeft();
					if (left instanceof VariableRef) {
						checkVariableRef((VariableRef) left, c, tool, graph, node);
					}
				}

			}
		}

		if (node instanceof GoVariableDeclaration) {
			GoVariableDeclaration declr = (GoVariableDeclaration) node;
			Expression right = declr.getRight();
			List<Call> calls = CFGUtils.extractCallsFromStatement(right);
			for (Call c : calls){
				if (// ReadWriteHFUtils.isWriteCall(c) ||
				isEvent(c)) {
					assignmentMap.put(c, Boolean.TRUE);
					Expression left = declr.getLeft();
					if (left instanceof VariableRef) {
						checkVariableRef((VariableRef) left, c, tool, graph, node);
					}
				}

			}
		}

		if (node instanceof Return) {
			Return ret = (Return) node;
			// Contract API contracts don't define an explicit Invoke method or call
			// shim.Success/shim.Error.
			// Method dispatch is handled via reflection: the framework inspects the
			// contract class's public methods and invokes the one matching the
			// requested
			// function name, passing in the deserialized arguments and
			// automatically
			// wrapping the return value (or thrown exception) into the chaincode
			// response.
			boolean isContractAPI = node.getCFG().getProgram().getUnits().stream()
					.anyMatch(u -> u.getName().contains("contractapi"));

			// for legacy code (Hyperledger Fabric v0.6)
			boolean isInvokeReturnErrors = invokeReturnError(node);
			if (isContractAPI || isInvokeReturnErrors) {

				Expression right = ret.getSubExpression();
				List<Call> calls = CFGUtils.extractCallsFromStatement(right);
				for (Call c : calls){
					if (// ReadWriteHFUtils.isWriteCall(c) ||
					isEvent(c)) {
						if ((isContractAPI && isEntryPointSmartContract(tool, node))
								|| (isInvokeReturnErrors && isRetByInvoke(tool, node))) {
							// It is handled in the sense that the transaction response return a failure in case of returned error
							assignmentMap.put(c, Boolean.TRUE);
						}
					}
				}

			}
		}

		return true;
	}

	@Override
	public boolean visit(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<L>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<L>, TypeEnvironment<T>>> tool,
			CFG graph, Edge edge) {
		return true;
	}

	private boolean isRetByInvoke(SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<L>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<L>, TypeEnvironment<T>>> tool, Statement node) {
		if (node.getCFG().getDescriptor().getName().equals("Invoke"))
			return true;

		// check if function is called in a return of Invoke
		if (tool.getCallGraph().getNodes().stream().anyMatch(n -> n.getCodeMember().equals(node.getCFG())))
			for (CodeMember caller : tool.getCallers(node.getCFG())) {
				if (caller.getDescriptor().getName().equals("Invoke")) {
					for (CFG cfg : node.getProgram().getAllCFGs())
						if (cfg.getDescriptor().equals(caller.getDescriptor())) {
							for (Statement stmt : cfg.getNodes()) {
								if (stmt instanceof Return) {
									List<Call> calls = CFGUtils.extractCallsFromStatement(stmt);
									if (calls.stream().anyMatch(
											c -> c.getTargetName().equals(node.getCFG().getDescriptor().getName())))
										return true;
								}
							}
						}
				}
			}

		return false;
	}

	private boolean isEntryPointSmartContract(SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<L>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<L>, TypeEnvironment<T>>> tool, Statement node) {
		if (tool.getCallGraph().getNodes().stream().anyMatch(n -> n.getCodeMember().equals(node.getCFG()))) {
			if (!tool.getCallers(node.getCFG()).isEmpty())
				return false;
		}
		// the function is not called
		return true;
	}

	private boolean invokeReturnError(Statement node) {
		for (CFG cfg : node.getCFG().getProgram().getAllCFGs()) {
			if (cfg.getDescriptor().getName().equals("Invoke")) {
				Type retType = cfg.getDescriptor().getReturnType();
				if (retType.isErrorType() || retType instanceof GoErrorType)
					return true;
				else if (retType instanceof GoTupleType) {
					GoTupleType tupleType = (GoTupleType) retType;
					if (tupleType.getLast().getStaticType().isErrorType()
							|| tupleType.getLast().getStaticType() instanceof GoErrorType)
						return true;
				} else if (retType instanceof GoPointerType) {
					GoPointerType pointerType = (GoPointerType) retType;
					if (pointerType.getInnerType() instanceof GoTupleType) {
						GoTupleType tupleType = (GoTupleType) pointerType.getInnerType();
						if (tupleType.getLast().getStaticType().isErrorType()
								|| tupleType.getLast().getStaticType() instanceof GoErrorType)
							return true;
					}
				}
				return false;
			}
		}
		return false;
	}

	private boolean isEvent(Call call) {
		return call.getTargetName().equals("SetEvent")
				&& (call.getParameters().length == 2 || call.getParameters().length == 3);
	}

	private void checkVariableRef(VariableRef ref, Call call, ReportingTool tool, CFG graph, Statement node) {
		if (GoLangUtils.isBlankIdentifier(ref.getVariable()))
			tool.warnOn(node,
					"Unhandled error of a blockchain "
							+ (ReadWriteHFUtils.isReadCall((Call) call) ? "read"
									: (ReadWriteHFUtils.isWriteCall((Call) call) ? "write" : "event emission"))
							+ " operation. It is discarded during the assignment.");
		else {
			boolean found = false;
			for (ControlFlowStructure cfs : graph.getDescriptor().getControlFlowStructures()) {
				if (cfs instanceof IfThenElse) {
					CodeGraph<CFG, Statement, Edge> path = CFGUtils.getPath(graph, node, cfs.getCondition());
					if (path != null && !path.getNodes().isEmpty() && !existVariableOverwriteInPath(path, node, ref)
							&& isVariableRefUsedInCondition(ref, cfs.getCondition())) {
						found = true;
						break;
					}
				}

			}

			if (!found)
				tool.warnOn(node,
						"Unhandled error of a blockchain "
								+ (ReadWriteHFUtils.isReadCall((Call) call) ? "read"
										: (ReadWriteHFUtils.isWriteCall((Call) call) ? "write" : "event emission"))
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

}
