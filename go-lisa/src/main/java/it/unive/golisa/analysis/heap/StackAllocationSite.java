package it.unive.golisa.analysis.heap;

import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.type.Type;

public class StackAllocationSite extends GoAllocationSite {

	public StackAllocationSite(Type staticType, String locationName, boolean isWeak, CodeLocation location) {
		this(staticType, locationName, null, isWeak, location);
	}

	public StackAllocationSite(Type staticType, String locationName, SymbolicExpression field, boolean isWeak,
			CodeLocation location) {
		super(staticType, locationName, field, isWeak, location);
	}
}
