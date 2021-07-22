package it.unive.golisa.analysis;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
import it.unive.lisa.type.Untyped;

public class BreakConsensusChaincodeChecker implements SyntacticCheck {

	@Override
	public void beforeExecution(CheckTool tool) {
	}

	@Override
	public void afterExecution(CheckTool tool) {
	}

	@Override
	public boolean visit(CheckTool tool, CFG graph, Statement node) {

		checkIssuesRelatedToGoLangAPI(tool, graph, node);
		
		checkIssuesRelatedToHyperLeadgerFabric(tool, graph, node);
		
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
		
		if(signatureName.contains(call.getTargetName()))
			System.out.println();
		
		if( signatureName != null && signatureName.equals(call.getTargetName()) 
					&& call.getParameters().length > 1 
					&& call.getParameters()[0] instanceof VariableRef) {
				
				VariableRef var = (VariableRef) call.getParameters()[0];
				if(goLangApiSignature.getPackage().contains(var.getName()))
					return true;			
		}
		
		return false;
	}

	private void checkIssuesRelatedToHyperLeadgerFabric(CheckTool tool, CFG graph, Statement node) {

		/*
		 * Checks taken from paper read by Imran
		 * A Survey on Hyperledger Fabric Chaincodes Vulnerabilities and Formal Analysis Techniques
		 */
		if (node instanceof UnresolvedCall) {
			if(matchPhantomRead((UnresolvedCall) node))
				tool.warnOn(node, "Phantom read detected!");
			if(matchGlobalVariable(node))
				tool.warnOn(node, "Global variable detected!");
			if(matchFieldDeclaration(node))
				tool.warnOn(node, "Unsafe field declaration detected!");
			if(matchReadWriteSet(node))
				tool.warnOn(node, "Read-write set detected!");
		}
		
	}
	
	
	
	
	/*
	 * Read-write set. During simulation of a transaction at an endorser, a read-write set is prepared for the transaction.
	 * The read-set contains a list of unique keys and their committed version numbers that the transaction reads
	 * during the simulation. The write-set contains a list of unique keys and their new values that the transaction
	 * writes. Notice that, if the transaction writes a value multiple times for a key, only the last written value is retained.
	 * Also, if a transaction reads a value for a key, the value in the committed state is returned even if the transaction
	 * has updated the value for the key before issuing the read. Therefore, in Hyperledger Fabric, read-your-writes
	 * semantics may create inconsistency among the peers. The following code snippet contain such vulnerability
	 */
	private boolean matchReadWriteSet(Statement node) {
		// TODO 
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
		//TODO: currenty "range" instruction is not supported
		return false;
	}

	/*
	 * Field declarations. Developers has to define two methods namely: Init() and Invoke(), to implement chaincode interface in Go. The Init() method is used to initialize the chaincode applications. All chaincodes need to
	 * have an an Init() function. The Invoke() method is used to invoke chaincode functions. When these methods
	 * are implemented as methods of a structure, developer can also define fields of the structure. The field can be
	 * accessed and used as global state in these methods. This may create inconsistency because every peer does not
	 * execute every transaction and therefore, state does not keep identical value among peers.
	 */
	private boolean matchFieldDeclaration(Statement node) {
		// TODO 
		return false;
	}

	/*
	 * Concurrency. Goroutines and channels introduce concurrency into Go chaincodes. The concurrency may
	 * lead to non-deterministic behavior and race condition problems.The following code snippet suffers from such
	 * vulnerability.
	 */
	private boolean matchConcurrencyStatement(Statement node) {
		// TODO : : currenty go routine and go channel are not supported
		return false;
	}

	/*
	 * 	Global variables. Like general project development using Go, chaincodes developers also consider global variables. As global variables can be changed inherently, the usage of global variables might cause non-determinism
	 * in chaincode
	 */
	private boolean matchGlobalVariable(Statement node) {
		// TODO 
		return false;
	}

	/*
	 * Phantom Read. GetHistoryForKey is a fabric-shim API which returns a history of key values across time. For
	 * each historic key update, the historic value, associated transaction id, and timestamp are returned. This means
	 * that data retrieved using this method should not be used to write any new data or update data on the ledger. This
	 * could lead to unexpected behaviors which may affect the execution of transactions and cause unintended results.
	 */
	private boolean matchPhantomRead(UnresolvedCall call) {
		
		List<String> OTHER_SIGNATURES = List.of("GetHistoryForKey");
		
		if( OTHER_SIGNATURES.contains(call.getTargetName()) 
				&& call.getParameters().length > 1 
				&& call.getParameters()[0] instanceof VariableRef) {
			
			VariableRef var = (VariableRef) call.getParameters()[0];
			if(var.getStaticType().isUntyped()) {
				Untyped type = (Untyped) var.getStaticType();
				//TODO: with type inference we should check if it is typed as shim.ChaincodeStubInterface
				return true;			
			}
		}
		
		return false;
	}

	/*
	 * System timestamp. In Go chaincode, timestamp-based libraries leads to inconsistent computation between
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
	 * In Hyperledger Fabric, transactions are simulated by the endorsers. Therefore, use of random number generation
	 * functions in the chaincodes may lead to inconsistency among the peers
	 */
	private boolean matchRandomStatement(UnresolvedCall call) {
		
		List<String> RANDOM_SIGNATURES = List.of("Intn", "Float64");
		
		if(RANDOM_SIGNATURES.contains(call.getTargetName()) 
				&& call.getParameters().length > 1 
				&& call.getParameters()[0] instanceof VariableRef) {
			
			VariableRef var = (VariableRef) call.getParameters()[0];
			if(var.getName().equals("rand"))
				return true;			
		}

		return false;
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