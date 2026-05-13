package it.unive.golisa.checker;

import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.SimpleAbstractDomain;
import it.unive.lisa.analysis.informationFlow.BaseTaint;
import it.unive.lisa.analysis.nonrelational.heap.HeapEnvironment;
import it.unive.lisa.analysis.nonrelational.heap.HeapValue;
import it.unive.lisa.analysis.nonrelational.type.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.type.TypeValue;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.checks.semantic.SemanticCheck;
import it.unive.lisa.checks.semantic.SemanticTool;
import it.unive.lisa.lattices.SimpleAbstractState;
import it.unive.lisa.lattices.informationFlow.TaintLattice;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.annotations.Annotation;
import it.unive.lisa.program.annotations.matcher.AnnotationMatcher;
import it.unive.lisa.program.annotations.matcher.BasicAnnotationMatcher;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeMember;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.call.CFGCall;
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.program.cfg.statement.call.NativeCall;
import it.unive.lisa.program.cfg.statement.call.UnresolvedCall;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.type.Type;
import it.unive.lisa.util.StringUtilities;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A Go taint checker.
 * 
 * @param <H> the lattice that represents a property of the memory of the
 *                program
 * @param <T> the lattice that represents a set of types corresponding to the
 *                runtime types of an expression
 * @param <V> the taint analysis lattice
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class TaintChecker<H extends HeapValue<H>, V extends TaintLattice<V>, T extends TypeValue<T>> implements
		SemanticCheck<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<V>, TypeEnvironment<T>>,
				SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<V>, TypeEnvironment<T>>> {

	/**
	 * The message to add in the warnings.
	 */
	protected final String message;

	/**
	 * Builds the instance of the checker.
	 * 
	 * @param message the message for the warnings
	 */
	public TaintChecker(String message) {
		this.message = message;
	}

	/**
	 * Builds the instance of the checker.
	 */
	public TaintChecker() {
		this.message = "";
	}

	/**
	 * Sink annotation.
	 */
	public static final Annotation SINK_ANNOTATION = new Annotation("lisa.taint.Sink");

	/**
	 * Sink matcher.
	 */
	public static final AnnotationMatcher SINK_MATCHER = new BasicAnnotationMatcher(SINK_ANNOTATION);

	@Override
	public void beforeExecution(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<V>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<V>, TypeEnvironment<T>>> tool) {
	}

	@Override
	public void afterExecution(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<V>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<V>, TypeEnvironment<T>>> tool) {
	}

	@Override
	public boolean visitUnit(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<V>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<V>, TypeEnvironment<T>>> tool,
			Unit unit) {
		return true;
	}

	@Override
	public void visitGlobal(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<V>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<V>, TypeEnvironment<T>>> tool,
			Unit unit, Global global, boolean instance) {
	}

	@Override
	public boolean visit(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<V>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<V>, TypeEnvironment<T>>> tool,
			CFG graph) {
		return true;
	}

	@Override
	public boolean visit(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<V>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<V>, TypeEnvironment<T>>> tool,
			CFG graph, Statement node) {
		if (!(node instanceof UnresolvedCall))
			return true;

		UnresolvedCall call = (UnresolvedCall) node;
		try {
			for (var result : tool.getResultOf(graph)) {
				Call resolved = tool.getResolvedVersion(call, result);
				if (resolved == null)
					System.err.println("Error");

				if (resolved instanceof NativeCall) {
					NativeCall nativeCfg = (NativeCall) resolved;
					Collection<CodeMember> nativeCfgs = nativeCfg.getTargets();
					for (CodeMember n : nativeCfgs) {
						Parameter[] parameters = n.getDescriptor().getFormals();
						boolean[] resultsParam = new boolean[parameters.length];
						for (int i = 0; i < parameters.length; i++)
							if (parameters[i].getAnnotations().contains(SINK_MATCHER)) {
								var postState = result.getAnalysisStateAfter(call.getParameters()[i]);

								Set<SymbolicExpression> reachableIds = new HashSet<>();
								Iterator<SymbolicExpression> comExprIterator = postState.getExecutionExpressions()
										.iterator();
								if (comExprIterator.hasNext()) {

									SymbolicExpression expr = comExprIterator.next();
									try {
										reachableIds
												.addAll(tool.getAnalysis().reachableFrom(postState, expr,
														(Statement) call).elements);

										for (SymbolicExpression s : reachableIds) {
											Set<Type> types = tool.getAnalysis().getRuntimeTypesOf(postState, s,
													(Statement) call);

											if (types.stream().allMatch(t -> t.isInMemoryType() || t.isPointerType()))
												continue;
											// extraction of the abstract value
											ValueEnvironment<V> valueState = postState.getExecutionState().valueState;
											BaseTaint<V> taintAnalysisValueDomain = (BaseTaint<
													V>) tool.getAnalysis().domain.valueDomain;
											SemanticOracle oracle = tool.getAnalysis().domain
													.makeOracle(postState.getExecutionState());
											TaintLattice<V> abstractValue = (TaintLattice<V>) taintAnalysisValueDomain
													.eval(valueState, (ValueExpression) s,
															(ProgramPoint) call, oracle);

											// check the abstractValue of the
											// parameter
											if (abstractValue.isPossiblyTainted() || abstractValue.isAlwaysTainted())
												resultsParam[i] = true;
										}
									} catch (SemanticException e) {
										e.printStackTrace();
									}

								}
							}
						buildWarning(tool, call, parameters, resultsParam);
					}
				} else if (resolved instanceof CFGCall) {
					CFGCall cfg = (CFGCall) resolved;
					for (CodeMember n : cfg.getTargets()) {
						Parameter[] parameters = n.getDescriptor().getFormals();
						boolean[] resultsParam = new boolean[parameters.length];
						for (int i = 0; i < parameters.length; i++)
							if (parameters[i].getAnnotations().contains(SINK_MATCHER)) {
								var postState = result.getAnalysisStateAfter(call.getParameters()[i]);
								Set<SymbolicExpression> reachableIds = new HashSet<>();
								Iterator<SymbolicExpression> comExprIterator = postState.getExecutionExpressions()
										.iterator();
								if (comExprIterator.hasNext()) {

									SymbolicExpression boolExpr = comExprIterator.next();
									try {
										reachableIds
												.addAll(tool.getAnalysis().reachableFrom(postState, boolExpr,
														(Statement) call).elements);

										for (SymbolicExpression s : reachableIds) {
											Set<Type> types = tool.getAnalysis().getRuntimeTypesOf(postState, s,
													(Statement) call);

											if (types.stream().allMatch(t -> t.isInMemoryType() || t.isPointerType()))
												continue;
											// extraction of the abstract value
											ValueEnvironment<V> valueState = postState.getExecutionState().valueState;
											BaseTaint<V> taintAnalysisValueDomain = (BaseTaint<
													V>) tool.getAnalysis().domain.valueDomain;
											SemanticOracle oracle = tool.getAnalysis().domain
													.makeOracle(postState.getExecutionState());
											TaintLattice<V> abstractValue = (TaintLattice<V>) taintAnalysisValueDomain
													.eval(valueState, (ValueExpression) s,
															(ProgramPoint) call, oracle);

											// check the abstractValue of the
											// parameter
											if (abstractValue.isPossiblyTainted() || abstractValue.isAlwaysTainted())
												resultsParam[i] = true;
										}
									} catch (SemanticException e) {
										e.printStackTrace();
									}

								}
							}

						buildWarning(tool, call, parameters, resultsParam);
					}
				}
			}
		} catch (SemanticException e) {
			System.err.println("Cannot check " + node);
			e.printStackTrace(System.err);
		}

		return true;
	}

	@Override
	public boolean visit(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<V>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<V>, TypeEnvironment<T>>> tool,
			CFG graph, Edge edge) {
		return true;
	}

	/**
	 * Builds the warning of taint checker.
	 * 
	 * @param tool       the semantic tool
	 * @param call       the target call of the warning
	 * @param parameters the call parameters
	 * @param results    the results of taint analysis for each parameters
	 */
	protected void buildWarning(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<V>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<V>, TypeEnvironment<T>>> tool,
			UnresolvedCall call, Parameter[] parameters, boolean[] results) {

		int matches = 0;
		String res = "";
		for (int i = 0; i < results.length; i++) {
			if (results[i]) {
				if (matches > 0)
					res += ", ";

				res += parameters != null ? parameters[i].getName() + "(" + StringUtilities.ordinal(i + 1) + ")"
						: StringUtilities.ordinal(i + 1) + " param";
				matches++;
			}

		}
		if (matches > 0)
			tool.warnOn(call, "[" + message + "] " + "The sink " + call.getFullTargetName()
					+ " has the following tainted parameter" + (matches > 1 ? "s" : "") + ": " + res);

	}

}
