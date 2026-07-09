package it.unive.golisa.checker.hf.events;

import it.unive.golisa.cfg.expression.literal.GoNil;
import it.unive.golisa.cfg.expression.literal.GoTupleExpression;
import it.unive.golisa.cfg.statement.GoDefer;
import it.unive.golisa.cfg.statement.GoReturn;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoPointerType;
import it.unive.golisa.cfg.type.composite.GoTupleType;
import it.unive.golisa.cfg.utils.CFGUtils;
import it.unive.golisa.cfg.utils.CFGUtils.Search;
import it.unive.golisa.program.cfg.VariableScopingCFG;
import it.unive.lisa.analysis.SimpleAbstractDomain;
import it.unive.lisa.analysis.nonrelational.heap.HeapEnvironment;
import it.unive.lisa.analysis.nonrelational.heap.HeapValue;
import it.unive.lisa.analysis.nonrelational.type.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.type.TypeValue;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.checks.semantic.SemanticCheck;
import it.unive.lisa.checks.semantic.SemanticTool;
import it.unive.lisa.lattices.SimpleAbstractState;
import it.unive.lisa.lattices.string.tarsis.RegexAutomaton;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeMember;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Return;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.program.cfg.statement.call.UnresolvedCall;
import it.unive.lisa.type.Type;
import it.unive.lisa.util.collections.workset.VisitOnceFIFOWorkingSet;
import it.unive.lisa.util.collections.workset.VisitOnceWorkingSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A Go Checker for detect multiple event emits in Hyperledger Fabric.
 * 
 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
 * 
 * @param <H> the lattice that represents a property of the memory of the
 *                program
 * @param <T> the lattice that represents a set of types corresponding to the
 *                runtime types of an expression
 */
public class EventEmitWithNoSuccessChecker<H extends HeapValue<H>, T extends TypeValue<T>> implements
		SemanticCheck<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>,
				SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>> {

	private static final Logger LOG = LogManager.getLogger(EventEmitWithNoSuccessChecker.class);

	@Override
	public void beforeExecution(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>,
							TypeEnvironment<T>>> tool) {
	}

	@Override
	public void afterExecution(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>,
							TypeEnvironment<T>>> tool) {

	}

	@Override
	public void visitGlobal(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>,
							TypeEnvironment<T>>> tool,
			Unit unit, Global global, boolean instance) {
	}

	@Override
	public boolean visit(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>,
							TypeEnvironment<T>>> tool,
			CFG graph) {
		return true;
	}

	@Override
	public boolean visit(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>,
							TypeEnvironment<T>>> tool,
			CFG graph, Statement node) {

		List<Call> calls = CFGUtils.extractCallsFromStatement(node);
		if (calls.isEmpty())
			return true;

		checkIssue(tool, graph, node);

		return true;
	}

	private void checkIssue(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>,
							TypeEnvironment<T>>> tool,
			CFG graph, Statement node) {

		List<Call> calls = CFGUtils.extractCallsFromStatement(node);

		boolean found = false;
		for (Call c : calls)
			if (c.getTargetName().equals("SetEvent")
					&& (c.getParameters().length == 2 || c.getParameters().length == 3))
				found = true;

		if (!found)
			return;

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

		if (!interproceduralCheck(tool, graph, node, node, new HashSet<CodeMember>(), new HashSet<CodeMember>(),
				isContractAPI, isInvokeReturnErrors)) {
			tool.warnOn(node, "Detected event emittion without a success response.");
		}

	}

	private boolean interproceduralCheck(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>,
							TypeEnvironment<T>>> tool,
			CFG graph, Statement root, Statement start, Set<CodeMember> seenCallees, Set<CodeMember> seenCallers,
			boolean isContractAPI, boolean isInvokeReturnErrors) {

		Statement startNode = CFGUtils.extractTargetNodeFromGraph(graph, start);
		startNode = startNode == null ? start : startNode;

		boolean isStartDeferred = startNode instanceof GoDefer;

		Set<Statement> successResponseNodes = new HashSet<>();
		for (Statement node : graph.getNodeList()) {
			List<Call> calls = CFGUtils.extractCallsFromStatement(node);
			for (Call c : calls)
				if (c.getTargetName().equals("Success"))
					successResponseNodes.add(node);
			if (node instanceof Return) {
				if (isContractAPI || isInvokeReturnErrors) {
					Expression[] subs = ((Return) node).getSubExpressions();
					Expression s = subs[0];
					if (s instanceof GoTupleExpression) {
						GoTupleExpression tuple = (GoTupleExpression) s;
						if (tuple.getSubExpressions()[1] instanceof GoNil)
							successResponseNodes.add(node);
					} else if (s instanceof GoNil)
						successResponseNodes.add(node);
				}
			}
		}

		for (Statement endNode : successResponseNodes) {
			boolean isEndDeferred = endNode instanceof GoDefer;

			if (isMatching(tool, graph, startNode, isStartDeferred, endNode, isEndDeferred, isContractAPI,
					isInvokeReturnErrors))
				return true;
		}

		if (checkCallees(tool, graph, root, startNode, seenCallees, isStartDeferred, isContractAPI,
				isInvokeReturnErrors))
			return true;

		if (checkCallers(tool, graph, root, seenCallees, seenCallers, isContractAPI, isInvokeReturnErrors))
			return true;

		return false;
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

	private boolean checkCallees(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>,
							TypeEnvironment<T>>> tool,
			CFG graph, Statement root, Statement start, Set<CodeMember> seen, boolean isStartDeferred,
			boolean isContractAPI, boolean isInvokeReturnErrors) {

		if (seen.contains(graph))
			return false;
		seen.add(graph);

		Collection<CodeMember> codemembers = getCalleesTransitively(tool, graph);
		for (CodeMember cm : codemembers) {
			if (cm instanceof VariableScopingCFG) {
				VariableScopingCFG interCFG = (VariableScopingCFG) cm;

				for (Statement n : graph.getNodes()) {
					if (n.equals(start))
						continue;
					List<Call> calls = CFGUtils.extractCallsFromStatement(n);
					if (!calls.isEmpty()) {

						boolean isEndDeferred = n instanceof GoDefer;
						if (isMatching(tool, graph, start, isStartDeferred, n, isEndDeferred, isContractAPI,
								isInvokeReturnErrors)) {
							for (Call c : calls)
								if (c instanceof UnresolvedCall) {
									if (tool.getCallSites(cm).contains(c)) {
										for (Statement e : interCFG.getEntrypoints())
											if (checkCalleesRecursive(tool, interCFG, e, seen))
												return true;
									}
								}
						}
					}
				}
			}
		}

		return false;
	}

	private boolean checkCalleesRecursive(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>,
							TypeEnvironment<T>>> tool,
			CFG graph, Statement start, Set<CodeMember> seen) {

		if (seen.contains(graph))
			return false;
		seen.add(graph);

		Set<Statement> successResponseNodes = new HashSet<>();
		for (Statement node : graph.getNodeList()) {
			List<Call> calls = CFGUtils.extractCallsFromStatement(node);
			for (Call c : calls)
				if (c.getTargetName().equals("Success"))
					successResponseNodes.add(node);
		}

		if (successResponseNodes.size() > 0)
			return true;

		Collection<CodeMember> codemembers = getCalleesTransitively(tool, graph);
		for (CodeMember cm : codemembers) {
			if (cm instanceof VariableScopingCFG) {
				VariableScopingCFG interCFG = (VariableScopingCFG) cm;
				for (Statement endNode : successResponseNodes) {
					if (CFGUtils.extractTargetNodeFromGraph(graph, endNode) != null)
						for (Statement n : graph.getNodes()) {
							List<Call> calls = CFGUtils.extractCallsFromStatement(n);
							if (!calls.isEmpty()) {
								for (Call c : calls)
									if (c instanceof UnresolvedCall) {
										if (tool.getCallSites(cm).contains(c)) {
											for (Statement e : interCFG.getEntrypoints())
												if (checkCalleesRecursive(tool, interCFG, e, seen))
													return true;
										}
									}
							}
						}
				}

			}
		}
		return false;
	}

	private boolean checkCallers(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>,
							TypeEnvironment<T>>> tool,
			CFG graph, Statement root, Set<CodeMember> seenCallees, Set<CodeMember> seenCallers, boolean isContractAPI,
			boolean isInvokeReturnErrors) {

		if (tool.getCallGraph().getNodes().stream().anyMatch(n -> n.getCodeMember().equals(graph))) {

			Collection<CodeMember> callers = tool.getCallers(graph);

			for (CodeMember cm : callers) {
				if (seenCallers.contains(cm))
					return false;
				seenCallers.add(cm);

				for (Call c : tool.getCallSites(graph)) {
					if (cm instanceof VariableScopingCFG) {
						VariableScopingCFG callerCFG = (VariableScopingCFG) cm;
						Statement sTarget = CFGUtils.extractTargetNodeFromGraph(callerCFG, c);
						if (sTarget != null)
							if (interproceduralCheck(tool, callerCFG, root, sTarget, seenCallees, seenCallers,
									isContractAPI, isInvokeReturnErrors)) {
								return true;
							}
					}
				}
			}
		}

		return false;
	}

	private boolean isMatching(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>,
							TypeEnvironment<T>>> tool,
			CFG graph, Statement startNode, boolean isStartDeferred, Statement endNode,
			boolean isEndDeferred, boolean isContractAPI, boolean isInvokeReturnErrors) {

		boolean foundPath = false;
		if (!isStartDeferred && !isEndDeferred)
			if (CFGUtils.existPath(graph, startNode, endNode, Search.BFS))
				foundPath = true;

		if (isEndDeferred && isStartDeferred)
			if (CFGUtils.existPath(graph, endNode, startNode, Search.BFS))
				foundPath = true;

		if (!isStartDeferred && isEndDeferred)
			if (CFGUtils.existPath(graph, startNode, endNode, Search.BFS))
				foundPath = true;

		if (isEndDeferred && !isStartDeferred)
			if (CFGUtils.existPath(graph, endNode, startNode, Search.BFS))
				foundPath = true;

		if (foundPath) {
			if (!isContractAPI && !isInvokeReturnErrors)
				return true;
			if (isContractAPI && isEntryPointSmartContract(tool, endNode))
				return true;
			if (isInvokeReturnErrors && isRetByInvoke(tool, endNode))
				return true;
		}

		return false;
	}

	private boolean isRetByInvoke(SemanticTool<
			SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>,
			SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>> tool,
			Statement endNode) {
		if (endNode.getCFG().getDescriptor().getName().equals("Invoke"))
			return true;

		// check if function is called in a return of Invoke
		if (tool.getCallGraph().getNodes().stream().anyMatch(n -> n.getCodeMember().equals(endNode.getCFG())))
			for (CodeMember caller : tool.getCallers(endNode.getCFG())) {
				if (caller.getDescriptor().getName().equals("Invoke")) {
					for (CFG cfg : endNode.getProgram().getAllCFGs())
						if (cfg.getDescriptor().equals(caller.getDescriptor())) {
							for (Statement stmt : cfg.getNodes()) {
								if (stmt instanceof Return) {
									List<Call> calls = CFGUtils.extractCallsFromStatement(stmt);
									if (calls.stream().anyMatch(
											c -> c.getTargetName().equals(endNode.getCFG().getDescriptor().getName())))
										return true;
								}
							}
						}
				}
			}

		return false;
	}

	private boolean isEntryPointSmartContract(SemanticTool<
			SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>,
			SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>> tool,
			Statement endNode) {
		if (tool.getCallGraph().getNodes().stream().anyMatch(n -> n.getCodeMember().equals(endNode.getCFG()))) {
			if (!tool.getCallers(endNode.getCFG()).isEmpty())
				return false;
		}
		// the function is not called
		return true;
	}

	@Override
	public boolean visit(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>,
							TypeEnvironment<T>>> tool,
			CFG graph, Edge edge) {
		return true;
	}

	@Override
	public boolean visitUnit(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>,
							TypeEnvironment<T>>> tool,
			Unit unit) {
		return true;
	}

	/**
	 * Compute the callees.
	 * 
	 * @param tool the semantic tool
	 * @param cm   the code memeber
	 * 
	 * @return the computed callees
	 */
	public Collection<CodeMember> getCalleesTransitively(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>,
							TypeEnvironment<T>>> tool,
			CodeMember cm) {
		VisitOnceFIFOWorkingSet<CodeMember> instance = new VisitOnceFIFOWorkingSet<>();
		VisitOnceWorkingSet<CodeMember> ws = instance.mk();
		if (tool.getCallGraph().getNodes().stream().anyMatch(n -> n.getCodeMember().equals(cm))) {
			tool.getCallees(cm).stream().forEach(ws::push);
			while (!ws.isEmpty())
				tool.getCallees(ws.pop()).stream().forEach(ws::push);
		}
		return ws.getSeen();

	}

}