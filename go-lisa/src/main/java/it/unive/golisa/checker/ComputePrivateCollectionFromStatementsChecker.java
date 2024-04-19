package it.unive.golisa.checker;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.unive.golisa.analysis.heap.GoAbstractState;
import it.unive.golisa.analysis.heap.GoPointBasedHeap;
import it.unive.golisa.analysis.tarsis.Tarsis;
import it.unive.golisa.analysis.utilities.PrivacySignatures;
import it.unive.golisa.cfg.utils.CFGUtils;
import it.unive.lisa.analysis.CFGWithAnalysisResults;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.nonrelational.value.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.checks.semantic.CheckToolWithAnalysisResults;
import it.unive.lisa.checks.semantic.SemanticCheck;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.call.Call;



/**
 * A Go Checker to compute private collection string from statements in Hyperledger Fabric
 * 
 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
 */
public class ComputePrivateCollectionFromStatementsChecker implements
	SemanticCheck<GoAbstractState<ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>,
	GoPointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>> {
	
	private Map<Call, Set<Tarsis>> writers;
	private Map<Call, Set<Tarsis>>  readers;
	
	
	public Map<Call, Set<Tarsis>> getWritePrivateStatesInstructions() {
		return writers;
	}
	
	public Map<Call, Set<Tarsis>> getReadPrivateStatesInstructions() {
		return readers;
	}

	@Override
	public void beforeExecution(
			CheckToolWithAnalysisResults<GoAbstractState<ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>, GoPointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>> tool) {
		writers = new HashMap<>();
		readers = new HashMap<>();
		
	}

	@Override
	public void afterExecution(
			CheckToolWithAnalysisResults<GoAbstractState<ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>, GoPointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>> tool) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean visitUnit(
			CheckToolWithAnalysisResults<GoAbstractState<ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>, GoPointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>> tool,
			Unit unit) {
		return true;
	}

	@Override
	public void visitGlobal(
			CheckToolWithAnalysisResults<GoAbstractState<ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>, GoPointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>> tool,
			Unit unit, Global global, boolean instance) {
		
	}

	@Override
	public boolean visit(
			CheckToolWithAnalysisResults<GoAbstractState<ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>, GoPointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>> tool,
			CFG graph) {
		return true;
	}

	@Override
	public boolean visit(
			CheckToolWithAnalysisResults<GoAbstractState<ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>, GoPointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>> tool,
			CFG graph, Statement node) {
		List<Call> calls = CFGUtils.extractCallsFromStatement(node);
		if(calls.isEmpty())
			return true;
		
		for(Call call : calls) {
			if(PrivacySignatures.isReadPrivateState(call) || PrivacySignatures.isWritePrivateState(call)) {
				try {
					Set<Tarsis> collectionValues = new HashSet<>();
					
					for (CFGWithAnalysisResults<GoAbstractState<ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>, 
							GoPointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>> result : tool.getResultOf(call.getCFG())) {
						Tarsis valueState = extractCollectionValues(call, node, result);
						if(valueState != null)
							collectionValues.add(valueState);
					}
					
					if(PrivacySignatures.isReadPrivateState(call)) {
						readers.computeIfAbsent(call, s -> new HashSet<>());
						readers.get(call).addAll(collectionValues);
					} else {
						writers.computeIfAbsent(call, s -> new HashSet<>());
						writers.get(call).addAll(collectionValues);
					}
					
				} catch (SemanticException e) {
					System.err.println("Cannot check " + node);
					e.printStackTrace(System.err);
				}
			}
		}		
		return true;
	}
	
	private Tarsis extractCollectionValues(Call call, Statement node,
			CFGWithAnalysisResults<GoAbstractState<ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>, 
			GoPointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>> result) throws SemanticException {

		int par = 1; // collection string parameter
			
		if(par < call.getParameters().length) {
			return result.getAnalysisStateAfter(call.getParameters()[par]).getState().getValueState().getValueOnStack();
		}
		
		return null;
	}

	@Override
	public boolean visit(
			CheckToolWithAnalysisResults<GoAbstractState<ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>, GoPointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>> tool,
			CFG graph, Edge edge) {
		return true;
	}
}