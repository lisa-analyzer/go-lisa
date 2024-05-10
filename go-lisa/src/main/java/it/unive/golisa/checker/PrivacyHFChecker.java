package it.unive.golisa.checker;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.analysis.taint.TaintDomainForPrivacyHF;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.AnalyzedCFG;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.nonrelational.value.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.string.tarsis.Tarsis;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.checks.semantic.CheckToolWithAnalysisResults;
import it.unive.lisa.checks.semantic.SemanticCheck;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.annotations.Annotation;
import it.unive.lisa.program.annotations.matcher.AnnotationMatcher;
import it.unive.lisa.program.annotations.matcher.BasicAnnotationMatcher;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeMember;
import it.unive.lisa.program.cfg.Parameter;
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

/**
 * A Go taint checker for privacy issues detection in HF.
 * 
 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
 */
public abstract class PrivacyHFChecker implements SemanticCheck<SimpleAbstractState<PointBasedHeap, ValueEnvironment<TaintDomainForPrivacyHF>, TypeEnvironment<InferredTypes>>>  {


	/**
	 * Sink annotation.
	 */
	public static final Annotation SINK_ANNOTATION = new Annotation("lisa.taint.Sink");

	/**
	 * Sink matcher.
	 */
	public static final AnnotationMatcher SINK_MATCHER = new BasicAnnotationMatcher(SINK_ANNOTATION);



	@Override
	public boolean visit(CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<TaintDomainForPrivacyHF>, TypeEnvironment<InferredTypes>>> tool,
			CFG graph, Statement node) {
		if (!(node instanceof UnresolvedCall))
			return true;

		UnresolvedCall call = (UnresolvedCall) node;
		try {
			for (AnalyzedCFG<
					SimpleAbstractState<PointBasedHeap, ValueEnvironment<TaintDomainForPrivacyHF>,
							TypeEnvironment<InferredTypes>>> result : tool.getResultOf(call.getCFG())) {
				Call resolved = (Call) tool.getResolvedVersion(call, result);
		
				if (resolved instanceof NativeCall) {
					NativeCall nativeCfg = (NativeCall) resolved;
					Collection<CodeMember> nativeCfgs = nativeCfg.getTargets();
					for (CodeMember n : nativeCfgs) {
						Parameter[] parameters = n.getDescriptor().getFormals();
						boolean[] resultsParam = new boolean[parameters.length];
						for (int i = 0; i < parameters.length; i++) {
							if (parameters[i].getAnnotations().contains(SINK_MATCHER)) {

								AnalysisState<
								SimpleAbstractState<PointBasedHeap, ValueEnvironment<TaintDomainForPrivacyHF>,
										TypeEnvironment<InferredTypes>>> state = result
												.getAnalysisStateAfter(call.getParameters()[i]);

								Set<SymbolicExpression> reachableIds = new HashSet<>();
								for (SymbolicExpression e : state.getComputedExpressions())
									reachableIds
											.addAll(state.getState().reachableFrom(e, node, state.getState()).elements);
		
								for (SymbolicExpression s : reachableIds) {
									Set<Type> types = state.getState().getRuntimeTypesOf(s, node, state.getState());
		
									if (types.stream().allMatch(t -> t.isInMemoryType() || t.isPointerType()))
										continue;
		
									ValueEnvironment<TaintDomainForPrivacyHF> valueState = state.getState().getValueState();
									if (valueState.eval((ValueExpression) s, node, state.getState())
											.isTainted())
										resultsParam[i] = true;
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
								AnalysisState<
								SimpleAbstractState<PointBasedHeap, ValueEnvironment<TaintDomainForPrivacyHF>,
										TypeEnvironment<InferredTypes>>> state = result
												.getAnalysisStateAfter(call.getParameters()[i]);

								Set<SymbolicExpression> reachableIds = new HashSet<>();
								for (SymbolicExpression e : state.getComputedExpressions())
									reachableIds
											.addAll(state.getState().reachableFrom(e, node, state.getState()).elements);
		
								for (SymbolicExpression s : reachableIds) {
									Set<Type> types = state.getState().getRuntimeTypesOf(s, node, state.getState());
		
									if (types.stream().allMatch(t -> t.isInMemoryType() || t.isPointerType()))
										continue;
		
									ValueEnvironment<TaintDomainForPrivacyHF> valueState = state.getState().getValueState();
									if (valueState.eval((ValueExpression) s, node, state.getState())
											.isTainted())
										resultsParam[i] = true;
								}
							}
						buildWarning(tool, call, parameters, resultsParam);
					}
				} else {
					checkSignature(call, tool);
				}
			}
		} catch (SemanticException e) {
			System.err.println("Cannot check " + node);
			e.printStackTrace(System.err);
		}

		return true;

	}


	protected void buildWarning(CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<TaintDomainForPrivacyHF>, TypeEnvironment<InferredTypes>>> tool,
			UnresolvedCall call, Parameter[] parameters, boolean[] results) {
			
		int matches = 0;
		String res = "";
		for (int i=0; i < results.length; i++) {
			if(results[i]) {
				if(matches > 0)
					res += ", ";
				
				res += parameters != null ? parameters[i].getName() +"("+StringUtilities.ordinal(i + 1)+")" : StringUtilities.ordinal(i + 1) +" param";
				matches++;
			}
			
		}
		if(matches > 0)
			tool.warnOn(call, "The sink "+ call.getFullTargetName() +" has the following tainted parameter"+ (matches > 1 ? "s": "") + ": " + res); 
			
	}
	
	@Override
	public boolean visit(CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<TaintDomainForPrivacyHF>, TypeEnvironment<InferredTypes>>> tool,
			CFG graph, Edge edge) {
		return true;
	}

	@Override
	public boolean visitUnit(CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<TaintDomainForPrivacyHF>, TypeEnvironment<InferredTypes>>> tool,
			Unit unit) {
		return true;
	}
	
	

	
	private final String message;
	
	public PrivacyHFChecker(String message) {
		this.message = message;
	}
	
	public PrivacyHFChecker() {
		this.message = "";
	}



	@Override
	public void beforeExecution(CheckToolWithAnalysisResults<
			SimpleAbstractState<PointBasedHeap, ValueEnvironment<TaintDomainForPrivacyHF>, TypeEnvironment<InferredTypes>>> tool) {
	}

	@Override
	public void afterExecution(
			CheckToolWithAnalysisResults<
					SimpleAbstractState<PointBasedHeap, ValueEnvironment<TaintDomainForPrivacyHF>,
							TypeEnvironment<InferredTypes>>> tool) {
	}

	@Override
	public void visitGlobal(
			CheckToolWithAnalysisResults<
					SimpleAbstractState<PointBasedHeap, ValueEnvironment<TaintDomainForPrivacyHF>,
							TypeEnvironment<InferredTypes>>> tool,
			Unit unit, Global global, boolean instance) {
	}

	@Override
	public boolean visit(CheckToolWithAnalysisResults<
			SimpleAbstractState<PointBasedHeap, ValueEnvironment<TaintDomainForPrivacyHF>, TypeEnvironment<InferredTypes>>> tool,
			CFG graph) {
		return true;
	}

	

	protected abstract void checkSignature(UnresolvedCall call,
			CheckToolWithAnalysisResults<
			SimpleAbstractState<PointBasedHeap, ValueEnvironment<TaintDomainForPrivacyHF>,
					TypeEnvironment<InferredTypes>>> tool);
}