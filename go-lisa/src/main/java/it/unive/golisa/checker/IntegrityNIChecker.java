package it.unive.golisa.checker;

import it.unive.golisa.analysis.ni.IntegrityNIDomain;
import it.unive.lisa.analysis.AnalyzedCFG;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.SimpleAbstractDomain;
import it.unive.lisa.analysis.nonrelational.heap.HeapEnvironment;
import it.unive.lisa.analysis.nonrelational.heap.HeapValue;
import it.unive.lisa.analysis.nonrelational.type.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.type.TypeValue;
import it.unive.lisa.checks.semantic.SemanticCheck;
import it.unive.lisa.checks.semantic.SemanticTool;
import it.unive.lisa.lattices.SimpleAbstractState;
import it.unive.lisa.lattices.informationFlow.NonInterferenceEnvironment;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.annotations.Annotation;
import it.unive.lisa.program.annotations.matcher.AnnotationMatcher;
import it.unive.lisa.program.annotations.matcher.BasicAnnotationMatcher;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.NativeCFG;
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
 * A non-interference integrity checker.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * 
 * @param <H> the lattice that represents a property of the memory of the
 *                program
 * @param <T> the lattice that represents a set of types corresponding to the
 *                runtime types of an expression
 */
public class IntegrityNIChecker<H extends HeapValue<H>, T extends TypeValue<T>> implements
		SemanticCheck<SimpleAbstractState<HeapEnvironment<H>, NonInterferenceEnvironment, TypeEnvironment<T>>,
				SimpleAbstractDomain<HeapEnvironment<H>, NonInterferenceEnvironment, TypeEnvironment<T>>> {

	/**
	 * The sink annotation.
	 */
	public static final Annotation SINK_ANNOTATION = new Annotation("lisa.intni.Sink");

	/**
	 * The sink matcher.
	 */
	public static final AnnotationMatcher SINK_MATCHER = new BasicAnnotationMatcher(SINK_ANNOTATION);

	@Override
	public void beforeExecution(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, NonInterferenceEnvironment, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, NonInterferenceEnvironment, TypeEnvironment<T>>> tool) {
	}

	@Override
	public void afterExecution(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, NonInterferenceEnvironment, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, NonInterferenceEnvironment, TypeEnvironment<T>>> tool) {
	}

	@Override
	public boolean visitUnit(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, NonInterferenceEnvironment, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, NonInterferenceEnvironment, TypeEnvironment<T>>> tool,
			Unit unit) {
		return true;
	}

	@Override
	public void visitGlobal(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, NonInterferenceEnvironment, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, NonInterferenceEnvironment, TypeEnvironment<T>>> tool,
			Unit unit, Global global, boolean instance) {
	}

	@Override
	public boolean visit(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, NonInterferenceEnvironment, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, NonInterferenceEnvironment, TypeEnvironment<T>>> tool,
			CFG graph, Statement node) {
		if (!(node instanceof UnresolvedCall))
			return true;

		UnresolvedCall call = (UnresolvedCall) node;
		try {
			for (var result : tool.getResultOf(call.getCFG())) {
				Call resolved = (Call) tool.getResolvedVersion(call, result);

				if (resolved instanceof NativeCall) {
					NativeCall nativeCfg = (NativeCall) resolved;
					Collection<NativeCFG> nativeCfgs = nativeCfg.getTargetedConstructs();
					for (NativeCFG n : nativeCfgs)
						process(tool, call, resolved, n.getDescriptor(), result);
				} else if (resolved instanceof CFGCall) {
					CFGCall cfg = (CFGCall) resolved;
					for (CFG n : cfg.getTargetedCFGs())
						process(tool, call, resolved, n.getDescriptor(), result);
				}
			}
		} catch (SemanticException e) {
			e.printStackTrace();
		}

		return true;

	}

	private void process(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, NonInterferenceEnvironment, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, NonInterferenceEnvironment, TypeEnvironment<T>>> tool,
			UnresolvedCall call, Call resolved, CodeMemberDescriptor desc,
			AnalyzedCFG<SimpleAbstractState<HeapEnvironment<H>, NonInterferenceEnvironment, TypeEnvironment<T>>> res)
			throws SemanticException {
		if (desc.getAnnotations().contains(SINK_MATCHER)) {
			var postState = res.getAnalysisStateAfter(call);

			Set<SymbolicExpression> reachableIds = new HashSet<>();
			Iterator<SymbolicExpression> comExprIterator = postState.getExecutionExpressions().iterator();
			if (comExprIterator.hasNext()) {

				SymbolicExpression expr = comExprIterator.next();
				try {
					reachableIds.addAll(tool.getAnalysis().reachableFrom(postState, expr, call).elements);

					for (SymbolicExpression s : reachableIds) {
						Set<Type> types = tool.getAnalysis().getRuntimeTypesOf(postState, s, call);

						if (types.stream().allMatch(t -> t.isInMemoryType() || t.isPointerType()))
							continue;
						// extraction of the abstract value
						var valueState = postState.getExecutionState().valueState;

						SemanticOracle oracle = tool.getAnalysis().domain.makeOracle(postState.getExecutionState());

						IntegrityNIDomain analysisValueDomain = (IntegrityNIDomain) tool
								.getAnalysis().domain.valueDomain;

						var abstractValue = analysisValueDomain.eval(valueState, (ValueExpression) s,
								(ProgramPoint) call, oracle);

						if (types.stream().allMatch(t -> t.isInMemoryType() || t.isPointerType()))
							continue;

						if (abstractValue.isLowIntegrity()) {
							tool.warnOn(call, "The execution of this call is guarded by a tainted condition"
									+ " resulting in an implicit flow");
						}

					}
				} catch (SemanticException e) {
					e.printStackTrace();
				}
			}
		}

		Parameter[] parameters = desc.getFormals();
		for (int i = 0; i < parameters.length; i++)
			if (parameters[i].getAnnotations().contains(SINK_MATCHER)) {
				var postState = res.getAnalysisStateAfter(call.getParameters()[i]);

				Set<SymbolicExpression> reachableIds = new HashSet<>();
				Iterator<SymbolicExpression> comExprIterator = postState.getExecutionExpressions().iterator();
				if (comExprIterator.hasNext()) {

					SymbolicExpression expr = comExprIterator.next();
					try {
						reachableIds.addAll(tool.getAnalysis().reachableFrom(postState, expr, call).elements);

						for (SymbolicExpression s : reachableIds) {
							Set<Type> types = tool.getAnalysis().getRuntimeTypesOf(postState, s, call);

							if (types.stream().allMatch(t -> t.isInMemoryType() || t.isPointerType()))
								continue;
							// extraction of the abstract value
							var valueState = postState.getExecutionState().valueState;

							SemanticOracle oracle = tool.getAnalysis().domain.makeOracle(postState.getExecutionState());

							IntegrityNIDomain analysisValueDomain = (IntegrityNIDomain) tool
									.getAnalysis().domain.valueDomain;

							var abstractValue = analysisValueDomain.eval(valueState, (ValueExpression) s,
									(ProgramPoint) call, oracle);

							if (types.stream().allMatch(t -> t.isInMemoryType() || t.isPointerType()))
								continue;

							if (abstractValue.isLowIntegrity()) {
								tool.warnOn(call, "The value passed for the " + StringUtilities.ordinal(i + 1)
										+ " parameter of this call is tainted, and it reaches the sink at parameter '"
										+ parameters[i].getName() + "' of " + resolved.getFullTargetName());
								if (i == call.getParameters().length - 1)
									tool.warnOn(call, "The execution of this call is guarded by a tainted condition"
											+ " resulting in an implicit flow");
							}

						}
					} catch (SemanticException e) {
						e.printStackTrace();
					}
				}
			}
	}

	@Override
	public boolean visit(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, NonInterferenceEnvironment, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, NonInterferenceEnvironment, TypeEnvironment<T>>> tool,
			CFG graph) {
		return true;
	}

	@Override
	public boolean visit(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, NonInterferenceEnvironment, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, NonInterferenceEnvironment, TypeEnvironment<T>>> tool,
			CFG graph, Edge edge) {
		return true;
	}

}
