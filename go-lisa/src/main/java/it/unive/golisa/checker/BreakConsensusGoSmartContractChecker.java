package it.unive.golisa.checker;

import java.util.List;
import java.util.Map;
import java.util.Set;

import it.unive.golisa.cfg.expression.binary.GoChannelSend;
import it.unive.golisa.cfg.expression.unary.GoChannelReceive;
import it.unive.golisa.cfg.statement.GoRoutine;
import it.unive.golisa.golang.api.signature.FuncGoLangApiSignature;
import it.unive.golisa.golang.api.signature.GoLangApiSignature;
import it.unive.golisa.golang.api.signature.MethodGoLangApiSignature;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.checks.syntactic.CheckTool;
import it.unive.lisa.checks.syntactic.SyntacticCheck;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.program.cfg.statement.call.UnresolvedCall;
import it.unive.lisa.program.cfg.statement.global.AccessInstanceGlobal;

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
		
		if(matchConcurrencyStatement(node))
			tool.warnOn(node, "Concurrecy behavior detected!");
		
		if(node instanceof AccessInstanceGlobal) {
			System.out.print("");//TODO:
		}
		
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
		
		if(node instanceof AccessInstanceGlobal) {
			System.out.print("");//TODO:
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
		Map<String, Set<FuncGoLangApiSignature>> mapf = GoLangUtils.getGoLangApiFunctionSignatures();
		Map<String, Set<MethodGoLangApiSignature>> mapm = GoLangUtils.getGoLangApiMethodSignatures();
		
		if(mapf.containsKey(packageName) && mapf.get(packageName).stream().anyMatch(s ->  matchSignature(s, call))
				|| mapm.containsKey(packageName) && mapm.get(packageName).stream().anyMatch(s ->  matchSignature(s, call)))
			return true;
		if(checkSubPackages)
			for(String k : GoLangUtils.getGoLangApiPackageSignatures())
				if((k.startsWith(packageName+"/") || k.endsWith("/"+packageName)) && 
						(mapf.get(k).stream().anyMatch(s -> matchSignature(s, call))
								|| mapm.get(k).stream().anyMatch(s -> matchSignature(s, call))))
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
		
		List<String> SYSTEM_TIME_SIGNATURES = List.of("Now", "Since", "Until");
		
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

		return  matchAnyPackageSignatures("rand", call, true);
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
		
		if(checkExternalLibraries(tool, unit))
			tool.warnOn(unit, "Possible external library detected!");
		return true;
	}

	private boolean checkExternalLibraries(CheckTool tool, CompilationUnit unit) {

		if(unit.getLocation() instanceof SourceCodeLocation) {
			SourceCodeLocation scl = (SourceCodeLocation) unit.getLocation();
			if( scl.getSourceFile() != null && !scl.getSourceFile().equals(GoLangUtils.GO_UNKNOWN_SOURCE)
					|| !unit.getName().contains(".") || isWhiteListRepo(unit))
				return false;
		}
		
		return true;
		
	}

	private boolean isWhiteListRepo(CompilationUnit unit) {
		List<String> whitelist = List.of("github.com/golang/", "golang.org/", "github.com/google/go", "google.golang.org/", "github.com/cosmos/", "github.com/hyperledger/");
		return whitelist.parallelStream().anyMatch(p -> unit.getName().startsWith(p));
	}

	@Override
	public void visitGlobal(CheckTool tool, Unit unit, Global global, boolean instance) {
	}
}