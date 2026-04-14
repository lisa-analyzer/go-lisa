package it.unive.golisa.checker.hf;


import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.unive.golisa.cfg.VariableScopingCFG;
import it.unive.golisa.cfg.utils.CFGUtils;
import it.unive.golisa.cfg.utils.CFGUtils.Search;
import it.unive.golisa.checker.hf.readwrite.ReadWriteHFUtils;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.nonrelational.value.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.string.tarsis.Tarsis;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.checks.semantic.CheckToolWithAnalysisResults;
import it.unive.lisa.checks.semantic.SemanticCheck;
import it.unive.lisa.outputs.serializableGraph.SerializableGraph;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeMember;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.util.collections.workset.VisitOnceFIFOWorkingSet;
import it.unive.lisa.util.collections.workset.VisitOnceWorkingSet;
import it.unive.lisa.util.file.FileManager;


/**
 * A Go Checker for the detection write operations from different cross-channel invocations in
 * Hyperledger Fabric.
 * 
 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
 */
public class CrossChannelInvocationsWriteOpsChecker implements
		SemanticCheck<
				SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> {


	private final boolean computeGraph;
	private Set<Statement> cchisToCheck;
	
	public CrossChannelInvocationsWriteOpsChecker(Set<Statement> cchisToCheck,boolean computeGraph) {
		this.cchisToCheck = cchisToCheck;
		this.computeGraph = computeGraph;
	}
	

	private void dump(FileManager fileManager, String filename, SerializableGraph graph) throws IOException {
		fileManager.mkDotFile(filename, writer -> graph.toDot().dump(writer));
	}

	@Override
	public void visitGlobal(
			CheckToolWithAnalysisResults<
					SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> tool,
			Unit unit, Global global, boolean instance) {
	}

	@Override
	public boolean visit(CheckToolWithAnalysisResults<
			SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> tool,
			CFG graph) {
		
		if(graph.getDescriptor().getName().equals("Invoke")) {
			Collection<Statement> entryPoints = graph.getEntrypoints();
			
			Set<Statement> writeOps = getWriteOperations(graph);
			if(!writeOps.isEmpty())
				for(Statement e : entryPoints) {
					for(Statement w : writeOps)
						if(CFGUtils.existPath(graph, e, w, Search.DFS))
							tool.warnOn(w, "Detected possible uncommited write operations");
				}
			
			interproceduralAnalysis(tool, graph, new HashSet<CodeMember>());

		}
		
		return true;
	}
	
	private Set<Statement> getWriteOperations(CFG graph) {
		Set<Statement> result = new HashSet<>();
		for(Statement n : graph.getNodes()) {
			List<Call> calls = CFGUtils.extractCallsFromStatement(n);
			for(Call c : calls) {
				if(ReadWriteHFUtils.isWriteCall(c))
					result.add(n);
			}
		}			
			
		return result;
	}
	
	private void interproceduralAnalysis(CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> tool, CFG graph, Set<CodeMember> seen) {
		if(!seen.contains(graph)) {
			seen.add(graph);
			Collection<CodeMember> codemembers = getCalleesTransitively(tool, graph);
			for (CodeMember cm : codemembers) {
				if (cm instanceof VariableScopingCFG) {
					VariableScopingCFG interCFG = (VariableScopingCFG) cm;
					Collection<Statement> entryPoints = interCFG.getEntrypoints();
					Set<Statement> writeOps = getWriteOperations(interCFG);
					if(!writeOps.isEmpty())
						for(Statement e : entryPoints) {
							for(Statement w : writeOps)
								if(CFGUtils.existPath(graph, e, w, Search.DFS)) {
									for(Statement cchi : cchisToCheck)
										tool.warnOn(w, "Detected possible uncommitted write operation due to a cross-channel invocation at " + cchi.getLocation());
								}
						}
					interproceduralAnalysis(tool, interCFG, seen);
				}
			}
		}
		
	}

	public Collection<CodeMember> getCalleesTransitively(CheckToolWithAnalysisResults<
			SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> tool,
			CodeMember cm) {
		VisitOnceWorkingSet<CodeMember> ws = VisitOnceFIFOWorkingSet.mk();
		tool.getCallees(cm).stream().forEach(ws::push);
		while (!ws.isEmpty())
			tool.getCallees(ws.pop()).stream().forEach(ws::push);
		return ws.getSeen();
	}



	@Override
	public boolean visit(
			CheckToolWithAnalysisResults<
					SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> tool,
			CFG graph, Statement node) {
		return true;
	}

}
