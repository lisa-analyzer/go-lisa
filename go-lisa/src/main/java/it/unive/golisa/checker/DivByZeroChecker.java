package it.unive.golisa.checker;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import it.unive.golisa.cfg.expression.binary.GoDiv;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.AnalyzedCFG;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.nonrelational.value.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.numeric.Interval;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.checks.semantic.CheckToolWithAnalysisResults;
import it.unive.lisa.checks.semantic.SemanticCheck;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.type.Type;
import it.unive.lisa.util.numeric.IntInterval;


/**
 * Checker for the detection of division by zero.
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 * 
 */
public class DivByZeroChecker implements
		SemanticCheck<
				SimpleAbstractState<PointBasedHeap, ValueEnvironment<Interval>, TypeEnvironment<InferredTypes>>> {


	@Override
	public boolean visit(
			CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Interval>, TypeEnvironment<InferredTypes>>> tool,
			CFG graph, Statement node) {
		
		try {
			if( node instanceof GoDiv)
					checkGoDiv(tool, graph, (GoDiv) node);
		
		} catch (SemanticException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		
		return true;
	}
	
	private void checkGoDiv(CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Interval>, TypeEnvironment<InferredTypes>>> tool, CFG graph, GoDiv div) throws SemanticException {
		
		for (AnalyzedCFG<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Interval>,
				TypeEnvironment<InferredTypes>>> result : tool.getResultOf(graph)) {
			AnalysisState<
			SimpleAbstractState<PointBasedHeap, ValueEnvironment<Interval>,
					TypeEnvironment<InferredTypes>>> state = result.getAnalysisStateAfter(div.getRight());
			
			Set<SymbolicExpression> reachableIds = new HashSet<>();
			Iterator<SymbolicExpression> comExprIterator = state.getComputedExpressions().iterator();
			if(comExprIterator.hasNext()) {
				SymbolicExpression divisor = comExprIterator.next();
					reachableIds
							.addAll(state.getState().reachableFrom(divisor, div, state.getState()).elements);
	
				for (SymbolicExpression s : reachableIds) {
					Set<Type> types = state.getState().getRuntimeTypesOf(s, div, state.getState());
	
					if (types.stream().allMatch(t -> t.isInMemoryType() || t.isPointerType()))
						continue;
	
					ValueEnvironment<Interval> valueState = state.getState().getValueState();
					
					Interval intervalAbstractValue = valueState.eval((ValueExpression) s, div, state.getState());
					if(!intervalAbstractValue.isBottom()) {
						IntInterval interval = intervalAbstractValue.interval;
						
						if(interval.includes(IntInterval.ZERO))
							tool.warnOn(div, "Detected possible division by zero");					
					}
				}
			}
		}
	}
}
