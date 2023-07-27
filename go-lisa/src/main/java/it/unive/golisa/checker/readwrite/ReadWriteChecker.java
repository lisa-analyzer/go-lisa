package it.unive.golisa.checker.readwrite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import it.unive.golisa.checker.readwrite.ReadWriteHFUtils.KeyType;
import it.unive.golisa.checker.readwrite.ReadWriteHFUtils.TypeInstruction;
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



/**
 * A Go Checker for Read-Write Set Issues of Hyperledger Fabric.
 * 
 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
 */
public class ReadWriteChecker implements
		SemanticCheck<
				SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>,
				PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>> {
	

	@Override
	public void beforeExecution(CheckToolWithAnalysisResults<
			SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>,
			PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>> tool) {
	}

	@Override
	public void afterExecution(
			CheckToolWithAnalysisResults<
					SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>,
					PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>> tool) {

	}

	@Override
	public void visitGlobal(
			CheckToolWithAnalysisResults<
					SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>,
					PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>> tool,
			Unit unit, Global global, boolean instance) {
	}

	@Override
	public boolean visit(CheckToolWithAnalysisResults<
			SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>,
			PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>> tool, CFG graph) {
		return true;
	}

	@Override
	public boolean visit(
			CheckToolWithAnalysisResults<
					SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>,
					PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>> tool,
			CFG graph, Statement node) {
		if (!(node instanceof UnresolvedCall))
			return true;

		UnresolvedCall call = (UnresolvedCall) node;
		if(ReadWriteHFUtils.isReadOrWriteCall(call)) {
			try {
				Pair<TypeInstruction, Triple<String, KeyType, int[]>> info = ReadWriteHFUtils.getReadWriteInfo(call);
				int[] keyParams = info.getRight().getRight();
				
				Collection<AnalyzedCFG<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>, PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> ss = tool.getResultOf(call.getCFG());
				for (AnalyzedCFG<
						SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>,
						PointBasedHeap, ValueEnvironment<Tarsis>,
						TypeEnvironment<InferredTypes>> result : tool.getResultOf(call.getCFG())) {
					Call resolved = (Call) tool.getResolvedVersion(call, result);
					ArrayList<Set<Tarsis>> keyValues = new ArrayList<>();
					
					if (resolved instanceof NativeCall) {
						NativeCall nativeCfg = (NativeCall) resolved;
						Collection<CodeMember> nativeCfgs = nativeCfg.getTargets();
						for (CodeMember n : nativeCfgs) {
							Parameter[] parameters = n.getDescriptor().getFormals();
							keyValues = extractKeyValues(call, keyParams, parameters, node, result);
						}
					} else if (resolved instanceof CFGCall) {
						CFGCall cfg = (CFGCall) resolved;
						
						for (CodeMember n : cfg.getTargets()) {
							Parameter[] parameters = n.getDescriptor().getFormals();
							keyValues = extractKeyValues(call, keyParams, parameters, node, result);
						}
					}
					
					FactoryReadWriteHF.buildReadWriteHF(info, keyValues);
					
				}
			} catch (SemanticException e) {
				System.err.println("Cannot check " + node);
				e.printStackTrace(System.err);
			}
		}
		
		return true;
	}



	private ArrayList<Set<Tarsis>> extractKeyValues(UnresolvedCall call, int[] keyParams, Parameter[] parameters, Statement node, AnalyzedCFG<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>, PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>> result) throws SemanticException {
		
		ArrayList<Set<Tarsis>> valStringDomain = new ArrayList<>(keyParams.length);
		
		for(int i=0; i < keyParams.length; i++) {
			int par = keyParams[i];
			valStringDomain.add(new HashSet<>());
			if(par < parameters.length) {
				
				AnalysisState<
				SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>,
						TypeEnvironment<InferredTypes>>,
				PointBasedHeap, ValueEnvironment<Tarsis>,
				TypeEnvironment<InferredTypes>> state = result
						.getAnalysisStateAfter(call.getParameters()[par]);
				for (SymbolicExpression stack : state.rewrite(state.getComputedExpressions(), node)) {
					valStringDomain.get(i).add(state.getState().getValueState().eval((ValueExpression) stack, node));
				}
			}
		}
		
		return valStringDomain;
		
	}

	@Override
	public boolean visit(
			CheckToolWithAnalysisResults<
					SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>,
					PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>> tool,
			CFG graph, Edge edge) {
		return true;
	}

	@Override
	public boolean visitUnit(
			CheckToolWithAnalysisResults<
					SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>,
					PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>> tool,
			Unit unit) {
		return true;
	}

}
