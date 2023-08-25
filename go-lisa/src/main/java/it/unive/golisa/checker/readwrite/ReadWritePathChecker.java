package it.unive.golisa.checker.readwrite;

import java.util.Collection;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import it.unive.golisa.cfg.UtilsCFG;
import it.unive.golisa.cfg.UtilsCFG.Search;
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
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.call.UnresolvedCall;
import it.unive.lisa.util.collections.workset.VisitOnceFIFOWorkingSet;
import it.unive.lisa.util.collections.workset.VisitOnceWorkingSet;



/**
 * A Go Checker for Read-Write Set Issues of Hyperledger Fabric.
 * 
 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
 */
public class ReadWritePathChecker implements
		SemanticCheck<
				SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>,
				PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>> {
	
	private final Set<Pair<AnalysisReadWriteHFInfo, AnalysisReadWriteHFInfo>> readAfterWriteCandidates;
	private final Set<Pair<AnalysisReadWriteHFInfo, AnalysisReadWriteHFInfo>> overWriteCandidates;

	public ReadWritePathChecker(Set<Pair<AnalysisReadWriteHFInfo, AnalysisReadWriteHFInfo>> readAfterWriteCandidates, Set<Pair<AnalysisReadWriteHFInfo, AnalysisReadWriteHFInfo>> overWriteCandidates) {
		this.readAfterWriteCandidates = readAfterWriteCandidates;
		this.overWriteCandidates = overWriteCandidates;
	}
	
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

		if(!(node instanceof UnresolvedCall))
			return true;
		
		
		checkReadAfterWriteIssues(tool, graph, node);
		
		checkOverWriteIssue(tool, graph, node);
			
		return true;
	}

	private void checkReadAfterWriteIssues(CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>, PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>> tool, CFG graph, Statement node) {
		for(Pair<AnalysisReadWriteHFInfo, AnalysisReadWriteHFInfo> p : readAfterWriteCandidates) {
			if(p.getLeft().getCall().equals(node)) {
				if(UtilsCFG.existPath(graph, (Statement) p.getLeft().getCall(),(Statement) p.getRight().getCall(), Search.BFS))
					tool.warnOn(node, "Detected a possible read after write issue. Read location: " + p.getRight().getCall().getLocation());
				
				//TODO: interproc and defer
			}

		}
		
	}
	
	private void checkOverWriteIssue(CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>, PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>> tool, CFG graph, Statement node) {
		for(Pair<AnalysisReadWriteHFInfo, AnalysisReadWriteHFInfo> p : overWriteCandidates) {
			if(p.getLeft().getCall().equals(node)) {
				if(UtilsCFG.existPath(graph, (Statement) p.getLeft().getCall(),(Statement) p.getRight().getCall(), Search.BFS))
					tool.warnOn(node, "Detected a possible read after write issue. Over-write location: " + p.getRight().getCall().getLocation());
				
				//TODO: interproc and defer
			}
			
		}
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
	
	public Collection<CodeMember> getCalleesTransitively(CheckToolWithAnalysisResults<
			SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>,
			PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>> tool, CodeMember cm) {
		VisitOnceWorkingSet<CodeMember> ws = VisitOnceFIFOWorkingSet.mk();
		tool.getCallees(cm).stream().forEach(ws::push);
		while (!ws.isEmpty())
			tool.getCallees(ws.pop()).stream().forEach(ws::push);
		return ws.getSeen();
	}

}
