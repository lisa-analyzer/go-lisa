package it.unive.golisa.checker;

import java.util.List;
import java.util.Map;
import java.util.Set;

import it.unive.golisa.cfg.expression.unary.GoRange;
import it.unive.golisa.cfg.statement.GoRoutine;
import it.unive.golisa.golang.api.signature.FuncGoLangApiSignature;
import it.unive.golisa.golang.api.signature.GoLangApiSignature;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.golisa.golang.api.signature.MethodGoLangApiSignature;
import it.unive.lisa.checks.syntactic.CheckTool;
import it.unive.lisa.checks.syntactic.SyntacticCheck;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.UnresolvedCall;
import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.golisa.cfg.expression.unary.GoChannelReceive;
import it.unive.golisa.cfg.expression.unary.GoLength;
import it.unive.golisa.cfg.expression.binary.GoChannelSend;
import it.unive.golisa.cfg.expression.binary.GoLess;
import it.unive.lisa.type.Untyped;
import it.unive.lisa.type.Type;

public class BreakConsensusGoSmartContractChecker implements SyntacticCheck {

	@Override
	public void beforeExecution(CheckTool tool) {
	}

	@Override
	public void afterExecution(CheckTool tool) {
	}

	@Override
	public boolean visit(CheckTool tool, CFG graph, Statement node) {

		checkIssuesRelatedToGoLangAPI(tool, graph, node);
		
		return true;
	}
	
	private void checkIssuesRelatedToGoLangAPI(CheckTool tool, CFG graph, Statement node) {
		
		if(node instanceof UnresolvedCall) {
			if(matchRandomStatement((UnresolvedCall) node))
				tool.warnOn(node, "Random method detected!");
			if(matchSystemTimestampStatement((UnresolvedCall) node))
				tool.warnOn(node, "System time detected!");
		}
		
		if(matchMapIterationStatement(node))
			tool.warnOn(node, "MapIteration detected!");
		if(matchConcurrencyStatement(node))
			tool.warnOn(node, "Concurrecy behavior detected!");
		
		checkIssuesRelatedToExternalEnviroments(tool, graph, node);
		
		
	}

	
	private void checkIssuesRelatedToExternalEnviroments(CheckTool tool, CFG graph, Statement node) {
		
		if (node instanceof UnresolvedCall) {
			
			if(matchFileSystemApi((UnresolvedCall) node))
				tool.warnOn(node, "Use of file system API detected!");
			if(matchDataBaseApi((UnresolvedCall) node))
				tool.warnOn(node, "Use of database API detected!");
			if(matchNetworkApi((UnresolvedCall) node))
				tool.warnOn(node, "Use of network API detected!");
			if(matchOsApi((UnresolvedCall) node))
				tool.warnOn(node, "Use of OS API detected!");
		}
		
	}

	
	private boolean matchOsApi(UnresolvedCall call) {

		return matchAnyPackageSignatures("os", call, true)
				|| matchAnyPackageSignatures("internal", call, true)
				|| matchAnyPackageSignatures("syscall", call, true)
				|| matchAnyPackageSignatures("internal", call, true);
	}

	private boolean matchNetworkApi(UnresolvedCall call) {

		return matchAnyPackageSignatures("net", call, true);
	}

	private boolean matchDataBaseApi(UnresolvedCall call) {
		
		return matchAnyPackageSignatures("database", call, true);
	}

	private boolean matchFileSystemApi(UnresolvedCall call) {
		
		return matchAnyPackageSignatures("io", call, true)
				|| matchAnyPackageSignatures("embed", call, true)
					|| matchAnyPackageSignatures("archive", call, true)
						|| matchAnyPackageSignatures("compress", call, true);
	}
	
	private boolean matchAnyPackageSignatures(String packageName, UnresolvedCall call, boolean checkSubPackages){
		Map<String, Set<GoLangApiSignature>> mapPackagesGoLangApi = GoLangUtils.getGoLangApiSignatures();
		
		if(mapPackagesGoLangApi.containsKey(packageName) && mapPackagesGoLangApi.get(packageName).stream().anyMatch(s -> (s instanceof MethodGoLangApiSignature || s instanceof FuncGoLangApiSignature)  && matchSignature(s, call)))
			return true;
		if(checkSubPackages)
			for(String k : mapPackagesGoLangApi.keySet())
				if(k.startsWith(packageName+"/") && mapPackagesGoLangApi.get(k).stream().anyMatch(s -> (s instanceof MethodGoLangApiSignature || s instanceof FuncGoLangApiSignature)  && matchSignature(s, call)))
					return true;
		return false;
	}

	private boolean matchSignature(GoLangApiSignature goLangApiSignature,  UnresolvedCall call) {
		
		String signatureName = null;
		if(goLangApiSignature instanceof FuncGoLangApiSignature)
			signatureName = ((FuncGoLangApiSignature) goLangApiSignature).getName(); 
		else if(goLangApiSignature instanceof MethodGoLangApiSignature)
			signatureName = ((MethodGoLangApiSignature) goLangApiSignature).getName(); 
		
		if( signatureName != null && signatureName.equals(call.getTargetName()) 
					&& call.getParameters().length > 1 
					&& call.getParameters()[0] instanceof VariableRef) {
				
				VariableRef var = (VariableRef) call.getParameters()[0];
				if(goLangApiSignature.getPackage().contains(var.getName()))
					return true;			
		}
		
		return false;
	}
	
	
	
	
	/*
	 * Maps. Go supports a built-in map type that implements a hash table. The range keyword allows us to iterate
	 * over maps. However, the order in which the map entries are ranged is not deterministic. Therefore, iterating
	 * the Go map may result in non-deterministic behaviors. Maps are also not safe for concurrent uses because it is
	 * known what happens when you simultaneously read and write to them. The following code snippet suffer from
	 * such vulnerability
	 */
	private boolean matchMapIterationStatement(Statement node) {
		if(node instanceof GoRange) {
			return true;
			//TODO: type checking/inference needed
/*			GoRange range = (GoRange) node;
			for(Expression e1 : range.getParameters()) {
				if(e1 instanceof GoLess) {
					GoLess less = (GoLess) e1;
					for(Expression e2 : less.getParameters()) {
						if(e2 instanceof GoLength) {
							GoLength length = (GoLength) e2;
							Type type = null;
							for(Expression e3 : length.getParameters()) {
								type = e3.getStaticType();
							}
						}
					}
				}
			}
*/
		}
		return false;
	}


	/*
	 * Concurrency. Goroutines and channels introduce concurrency into Go chaincodes. The concurrency may
	 * lead to non-deterministic behavior and race condition problems.The following code snippet suffers from such
	 * vulnerability.
	 */
	private boolean matchConcurrencyStatement(Statement node) {
		return node instanceof GoRoutine || node instanceof GoChannelReceive || node instanceof GoChannelSend;
	}



	/*
	 * System timestamp. In Go, timestamp-based libraries leads to inconsistent computation between
	 * peers, leading to a lack of consensus. For example, the time library in Go allows peers to get the current timestamp
	 * at a given time. It is unlikely that all peers execute a transaction at the same time and receives a similar timestamp.
	 */
	private boolean matchSystemTimestampStatement(UnresolvedCall call) {
		
		List<String> SYSTEM_TIME_SIGNATURES = List.of("Now");
		
		if( SYSTEM_TIME_SIGNATURES.contains(call.getTargetName()) 
				&& call.getParameters().length > 1 
				&& call.getParameters()[0] instanceof VariableRef) {
			
			VariableRef var = (VariableRef) call.getParameters()[0];
			if(var.getName().equals("time"))
				return true;			
		}

		return false;
	}

	/*
	 * Random number generation. Go has built-in support for random number generation in the standard library.
	 */
	private boolean matchRandomStatement(UnresolvedCall call) {

		return  matchAnyPackageSignatures("math/rand", call, true);
	}

	@Override
	public boolean visit(CheckTool tool, CFG g) {
		return true;
	}

	@Override
	public boolean visit(CheckTool tool, CFG graph, Edge edge) {
		return true;
	}

	@Override
	public boolean visitCompilationUnit(CheckTool tool, CompilationUnit unit) {
		return true;
	}

	@Override
	public void visitGlobal(CheckTool tool, Unit unit, Global global, boolean instance) {
	}
}