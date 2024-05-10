package it.unive.golisa.checker;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.unive.golisa.analysis.utilities.PrivacySignatures;
import it.unive.golisa.cfg.utils.CFGUtils;
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
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.type.Type;



/**
 * A Go Checker to compute private collection string from statements in Hyperledger Fabric
 * 
 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
 */
public class ComputePrivateCollectionFromStatementsChecker implements
SemanticCheck<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> {
	
	private Map<Call, Tarsis> writers = new HashMap<>();
	private Map<Call, Tarsis>  readers  = new HashMap<>();
	
	
	public Map<Call, Tarsis> getWritePrivateStatesInstructions() {
		return writers;
	}
	
	public Map<Call, Tarsis> getReadPrivateStatesInstructions() {
		return readers;
	}

	@Override
	public boolean visitUnit(CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> tool, Unit unit) {
		return true;
	}

	@Override
	public void visitGlobal(CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> tool, Unit unit, Global global, boolean instance) {
		
	}

	@Override
	public boolean visit(CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> tool, CFG graph) {
		return true;
	}

	@Override
	public boolean visit(CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> tool, CFG graph, Statement node) {
		List<Call> calls = CFGUtils.extractCallsFromStatement(node);
		if(calls.isEmpty())
			return true;
		
		for(Call call : calls) {
			if(PrivacySignatures.isReadPrivateState(call) || PrivacySignatures.isWritePrivateState(call)) {
				try {
					Tarsis collectionValue = null;

					for (AnalyzedCFG<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> result : tool.getResultOf(call.getCFG())) {
						Tarsis valueState = extractCollectionValues(call, node, result);
						if(valueState != null)
							collectionValue = valueState;
					}
					
					if(PrivacySignatures.isReadPrivateState(call)) {
						readers.put(call,collectionValue);
					} else {
						writers.put(call,collectionValue);
					}
					
				} catch (SemanticException e) {
					System.err.println("Cannot check " + node);
					e.printStackTrace(System.err);
				}
			}
		}		
		return true;
	}
	
	private Tarsis extractCollectionValues(Call call, Statement node, AnalyzedCFG<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> result) throws SemanticException {

		int par = 1; // collection string parameter
			
		Tarsis res = null;
		if(par < call.getParameters().length) {
			
			AnalysisState<
			SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>,
					TypeEnvironment<InferredTypes>>> state = result
							.getAnalysisStateAfter(call.getParameters()[par]);

			Set<SymbolicExpression> reachableIds = new HashSet<>();
			for (SymbolicExpression e : state.getComputedExpressions())
				reachableIds
						.addAll(state.getState().reachableFrom(e, node, state.getState()).elements);
			
			for (SymbolicExpression s : reachableIds) {
				Set<Type> types = state.getState().getRuntimeTypesOf(s, node, state.getState());
			
				if (types.stream().allMatch(t -> t.isInMemoryType() || t.isPointerType()))
					continue;
			
				ValueEnvironment<Tarsis> valueState = state.getState().getValueState();
				res = res == null ? valueState.eval((ValueExpression) s, node, state.getState()) : res.lub(valueState.eval((ValueExpression) s, node, state.getState()));
			}
		}

		return res;
	}

	@Override
	public boolean visit(CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> tool,
			CFG graph, Edge edge) {
		return true;
	}
	
	
}