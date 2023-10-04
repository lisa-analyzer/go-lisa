package it.unive.golisa.analysis.entrypoints;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import it.unive.golisa.cfg.CFGUtils;
import it.unive.golisa.checker.readwrite.ReadWriteHFUtils;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.program.cfg.statement.call.UnresolvedCall;

/**
 * The class is a factory for the creation of entrypoint sets.
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class EntryPointsFactory {

	/**
	 * Yields the set of entrypoints for the target framework.
	 * 
	 * @param framework the target framework
	 * @param analysis 
	 * 
	 * @return the set of entrypoints for the target framework, otherwise, a set
	 *             contained a main method signature.
	 */
	public static EntryPointSet getEntryPoints(Program program, String framework, String analysis) {

		EntryPointSet frameworkEntryPoints = null;
		if (framework != null) {
			if (framework.equalsIgnoreCase("HYPERLEDGER-FABRIC")) {
				frameworkEntryPoints = new HyperledgerFabricEntryPointSet();
			} else if (framework.equalsIgnoreCase("TENDERMINT-CORE")) {
				frameworkEntryPoints = new TendermintCoreEntryPointSet();	
			} else if (framework.equalsIgnoreCase("COSMOS-SDK")) {
				frameworkEntryPoints = new CosmosSDKEntryPointSet();
			}
		} 
		
		EntryPointSet analysisEntryPoints = null;
		
		if(analysis != null) {
			if(analysis.equalsIgnoreCase("READ-WRITE")) {
				analysisEntryPoints = computeEntryPointsForReadWriteAnalysis(program);
			}
		}
		
		return frameworkEntryPoints == null && analysisEntryPoints == null ? new EntryPointSet() {

			@Override
			protected void build(Set<String> entryPoints) {
				entryPoints.add("main");
			}
		} :  EntryPointSet.mergeEntryPointSets(frameworkEntryPoints, analysisEntryPoints);
	}

	private static EntryPointSet computeEntryPointsForReadWriteAnalysis(Program program) {
		
		Set<String> collectedEntryPoints = new HashSet<>();
		
		for(CFG cfg : program.getAllCFGs()) {
			Function<Statement, Boolean > func = n -> {
				List<Call> calls = CFGUtils.extractCallsFromStatement(n);
				for(Call c : calls)
					if(ReadWriteHFUtils.isReadOrWriteCall(c))
						return true;
				return false;
			};
			
			if(CFGUtils.anyMatchInCFGNodes(cfg, func))
				collectedEntryPoints.add(cfg.getDescriptor().getName());
				
		}
		
		return new EntryPointSet() {

			@Override
			protected void build(Set<String> entryPoints) {
				entryPoints.addAll(collectedEntryPoints);
			}
		};
	}
}
