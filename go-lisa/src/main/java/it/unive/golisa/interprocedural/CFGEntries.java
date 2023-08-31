package it.unive.golisa.interprocedural;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.lattices.FunctionalLattice;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.ContextSensitivityToken;

public class CFGEntries<A extends AbstractState<A, H, V, T>,
		H extends HeapDomain<H>,
		V extends ValueDomain<V>,
		T extends TypeDomain<T>>
		extends FunctionalLattice<CFGEntries<A, H, V, T>, ContextSensitivityToken, AnalysisState<A, H, V, T>> {

	public CFGEntries(AnalysisState<A, H, V, T> lattice) {
		super(lattice);
	}

	private CFGEntries(AnalysisState<A, H, V, T> lattice,
			Map<ContextSensitivityToken, AnalysisState<A, H, V, T>> function) {
		super(lattice, function);
	}

	public void putEntry(ContextSensitivityToken token,
			AnalysisState<A, H, V, T> result)
			throws SemanticException {
		if (function == null)
			function = mkNewFunction(null, false);
		function.put(token, result);
	}

	public boolean contains(ContextSensitivityToken token) {
		return function != null && function.containsKey(token);
	}

	public Collection<AnalysisState<A, H, V, T>> getAll() {
		return function == null ? Collections.emptySet() : function.values();
	}

	@Override
	public CFGEntries<A, H, V, T> top() {
		return new CFGEntries<>(lattice.top());
	}

	@Override
	public CFGEntries<A, H, V, T> bottom() {
		return new CFGEntries<>(lattice.bottom());
	}

	@Override
	public CFGEntries<A, H, V, T> mk(AnalysisState<A, H, V, T> lattice,
			Map<ContextSensitivityToken, AnalysisState<A, H, V, T>> function) {
		return new CFGEntries<>(lattice, function);
	}
}
