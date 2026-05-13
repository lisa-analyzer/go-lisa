package it.unive.golisa.checker.hf;


import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.unive.golisa.cfg.utils.CFGUtils;
import it.unive.golisa.cfg.utils.CFGUtils.Search;
import it.unive.golisa.checker.hf.readwrite.ReadWriteHFUtils;
import it.unive.golisa.program.cfg.VariableScopingCFG;
import it.unive.lisa.analysis.SimpleAbstractDomain;
import it.unive.lisa.analysis.nonrelational.heap.HeapEnvironment;
import it.unive.lisa.analysis.nonrelational.heap.HeapValue;
import it.unive.lisa.analysis.nonrelational.type.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.type.TypeValue;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.checks.semantic.SemanticCheck;
import it.unive.lisa.checks.semantic.SemanticTool;
import it.unive.lisa.lattices.SimpleAbstractState;
import it.unive.lisa.lattices.string.tarsis.RegexAutomaton;
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
 * @param <H> the lattice that represents a property of the memory of the program
 * @param <T> the lattice that represents a set of types corresponding to the runtime types of an expression
 * 
 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
 */
public class CrossChannelInvocationsWriteOpsChecker<H extends HeapValue<H>, T extends TypeValue<T>> implements
SemanticCheck<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>> {


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
	public boolean visitUnit(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>> tool,
			Unit unit) {
		return true;
	}


	@Override
	public void visitGlobal(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>> tool,
			Unit unit, Global global, boolean instance) {
	}


	@Override
	public boolean visit(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>> tool,
			CFG graph, Statement node) {

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
	
	private void interproceduralAnalysis(SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>> tool, CFG graph, Set<CodeMember> seen) {
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

	/**
	 * Yields the callees.
	 * @param tool the semantic tool
	 * @param cm the code member
	 * @return the callees
	 */
	public Collection<CodeMember> getCalleesTransitively(SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>> tool, CodeMember cm) {

		VisitOnceFIFOWorkingSet<CodeMember> instance = new VisitOnceFIFOWorkingSet<>();
		VisitOnceWorkingSet<CodeMember> ws = instance.mk();
		tool.getCallees(cm).stream().forEach(ws::push);
		while (!ws.isEmpty())
			tool.getCallees(ws.pop()).stream().forEach(ws::push);
		return ws.getSeen();
	}
	
}
