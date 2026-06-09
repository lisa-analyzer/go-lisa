package it.unive.golisa.checker.hf.events;

import it.unive.golisa.cfg.utils.CFGUtils;
import it.unive.lisa.analysis.Analysis;
import it.unive.lisa.analysis.AnalyzedCFG;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.SimpleAbstractDomain;
import it.unive.lisa.analysis.nonrelational.heap.HeapEnvironment;
import it.unive.lisa.analysis.nonrelational.heap.HeapValue;
import it.unive.lisa.analysis.nonrelational.type.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.type.TypeValue;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.string.tarsis.Tarsis;
import it.unive.lisa.checks.semantic.SemanticCheck;
import it.unive.lisa.checks.semantic.SemanticTool;
import it.unive.lisa.lattices.SimpleAbstractState;
import it.unive.lisa.lattices.string.tarsis.RegexAutomaton;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeMember;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.call.CFGCall;
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.program.cfg.statement.call.Call.CallType;
import it.unive.lisa.program.cfg.statement.call.NativeCall;
import it.unive.lisa.program.cfg.statement.call.UnresolvedCall;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.ValueExpression;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A Go checker for event issues in Hyperledger Fabric. It detects event emitted
 * with an empty string name, i.e. that leads to an error during the chaincode
 * execution.
 * 
 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
 * 
 * @param <H> the lattice that represents a property of the memory of the
 *                program
 * @param <T> the lattice that represents a set of types corresponding to the
 *                runtime types of an expression
 */
public class EmptyEventNameChecker<H extends HeapValue<H>, T extends TypeValue<T>> implements
		SemanticCheck<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>,
				SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>> {

	@Override
	public boolean visit(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>,
							TypeEnvironment<T>>> tool,
			CFG graph, Statement node) {

		List<Call> calls = CFGUtils.extractCallsFromStatement(node);
		if (calls.isEmpty())
			return true;

		for (Call call : calls) {
			if (call.getTargetName().equals("SetEvent") && call.getParameters().length == 3) {
				try {
					for (var result : tool
							.getResultOf(call.getCFG())) {
						Call resolved = call instanceof UnresolvedCall
								? (Call) tool.getResolvedVersion((UnresolvedCall) call, result)
								: call;
						Set<RegexAutomaton> nameValues = new HashSet<>();

						if (resolved instanceof NativeCall) {
							NativeCall nativeCfg = (NativeCall) resolved;
							Collection<CodeMember> nativeCfgs = nativeCfg.getTargets();
							for (CodeMember n : nativeCfgs) {
								Parameter[] parameters = n.getDescriptor().getFormals();
								nameValues = extractNameValues(tool.getAnalysis(), call, 0, parameters.length, node,
										result);
							}
						} else if (resolved instanceof CFGCall) {
							CFGCall cfg = (CFGCall) resolved;

							for (CodeMember n : cfg.getTargets()) {
								Parameter[] parameters = n.getDescriptor().getFormals();
								nameValues = extractNameValues(tool.getAnalysis(), call, 0, parameters.length, node,
										result);
							}
						} else {
							nameValues = extractNameValues(tool.getAnalysis(), call, 0, call.getParameters().length,
									node, result);
						}

						for (RegexAutomaton val : nameValues) {
							if (val.isEqualTo(val.emptyString())) {
								tool.warnOn(node, "The event name is an empty string");
							} else if (val.isTop() && val.getTransitions().size() <= 1) {
								if (val.getTransitions().size() == 0)
									tool.warnOn(node, "The event may set an empty name string");
								else // added to be sound, but it may be a non
										// empty combined string
									tool.warnOn(node, "The event name may be an empty string");
							}
						}
					}

				} catch (SemanticException e) {
					System.err.println("Cannot check " + node);
					e.printStackTrace(System.err);
				}
			}
		}
		return true;
	}

	private Set<RegexAutomaton> extractNameValues(
			Analysis<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>,
							TypeEnvironment<T>>> analysis,
			Call call, int nameParam, int parametersLength, Statement node,
			AnalyzedCFG<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>,
					TypeEnvironment<T>>> result)
			throws SemanticException {

		Set<RegexAutomaton> valStringDomain = new HashSet<>();

		int par = call.getCallType().equals(CallType.STATIC) ? nameParam : nameParam + 1;
		if (par < parametersLength) {

			var state = result.getAnalysisStateAfter(call.getParameters()[par]);
			for (SymbolicExpression stack : analysis.rewrite(state, state.getExecutionExpressions(), node)) {
				var valueState = state.getExecutionState().valueState;
				Tarsis analysisValueDomain = (Tarsis) analysis.domain.valueDomain;
				SemanticOracle oracle = analysis.domain.makeOracle(state.getExecutionState());
				var value = analysisValueDomain.eval(valueState, (ValueExpression) stack, (ProgramPoint) node,
						oracle);
				valStringDomain.add(value);
			}

		}

		return valStringDomain;

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
}
