package it.unive.golisa.checker;

import java.util.HashSet;
import java.util.Set;

import it.unive.lisa.analysis.SimpleAbstractDomain;
import it.unive.lisa.analysis.nonrelational.heap.HeapEnvironment;
import it.unive.lisa.analysis.nonrelational.heap.HeapValue;
import it.unive.lisa.analysis.nonrelational.type.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.type.TypeValue;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.checks.semantic.SemanticTool;
import it.unive.lisa.lattices.SimpleAbstractState;
import it.unive.lisa.lattices.informationFlow.TaintLattice;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.program.cfg.statement.call.UnresolvedCall;

/**
 * A Go taint checker for untrusted cross-contract invocation issues.
 * 
 * @param <H> the lattice that represents a property of the memory of the program
 * @param <T> the lattice that represents a set of types corresponding to the runtime types of an expression
 * @param <V> the taint analysis lattice
 * 
 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
 */
public class UntrustedCrossContractInvocationsChecker<H extends HeapValue<H>, V extends TaintLattice<V>, T extends TypeValue<T>>
		extends TaintChecker<H, V, T> {
	
	/**
	 * The untrusted cross-contract invocations.
	 */
	private final Set<Call> uccis;

	/**
	 * Builds an instance of the checker.
	 */
	public UntrustedCrossContractInvocationsChecker() {
		super("Possible untrusted cross-contract invocation.");
		uccis = new HashSet<>();
	}

	@Override
	protected void buildWarning(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<V>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<V>, TypeEnvironment<T>>> tool,
			UnresolvedCall call, Parameter[] parameters, boolean[] results) {

		boolean found = false;
		for (boolean b : results) {
		    if (b) {
		        found = true;
		        break;
		    }
		}
		
		if(found)
			uccis.add(call);
		
		super.buildWarning(tool, call, parameters, results);
	}

	/**
	 * Yields the untrusted cross contract invocations.
	 * @return the untrusted cross contract invocations
	 */
	public Set<Call> getUCCIs() {
		return uccis;
	}

}
