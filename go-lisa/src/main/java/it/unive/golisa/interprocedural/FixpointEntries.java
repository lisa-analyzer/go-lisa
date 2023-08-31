package it.unive.golisa.interprocedural;

import java.util.Map;

import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.lattices.FunctionalLattice;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.ContextSensitivityToken;
import it.unive.lisa.program.cfg.CFG;

public class FixpointEntries<A extends AbstractState<A, H, V, T>,
		H extends HeapDomain<H>,
		V extends ValueDomain<V>,
		T extends TypeDomain<T>>
		extends FunctionalLattice<FixpointEntries<A, H, V, T>, CFG, CFGEntries<A, H, V, T>> {

	public FixpointEntries(CFGEntries<A, H, V, T> lattice) {
		super(lattice);
	}

	private FixpointEntries(CFGEntries<A, H, V, T> lattice, Map<CFG, CFGEntries<A, H, V, T>> function) {
		super(lattice, function);
	}

	public void putEntry(CFG cfg, ContextSensitivityToken token, AnalysisState<A, H, V, T> result)
			throws SemanticException {
		if (function == null)
			function = mkNewFunction(null, false);
		CFGEntries<A, H, V, T> res = function.computeIfAbsent(cfg, c -> new CFGEntries<>(result.top()));
		res.putEntry(token, result);
	}

	public boolean contains(CFG cfg) {
		return function != null && function.containsKey(cfg);
	}

	@Override
	public FixpointEntries<A, H, V, T> top() {
		return new FixpointEntries<>(lattice.top());
	}

	@Override
	public FixpointEntries<A, H, V, T> bottom() {
		return new FixpointEntries<>(lattice.bottom());
	}

	public void forget(CFG cfg) {
		if (function == null)
			return;
		function.remove(cfg);
		if (function.isEmpty())
			function = null;
	}

	@Override
	public FixpointEntries<A, H, V, T> mk(CFGEntries<A, H, V, T> lattice,
			Map<CFG, CFGEntries<A, H, V, T>> function) {
		return new FixpointEntries<>(lattice, function);
	}
}
