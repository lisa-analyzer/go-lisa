package it.unive.golisa.analysis.heap;

import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.type.Type;

public class HeapAllocationSite extends GoAllocationSite {

	public HeapAllocationSite(Type staticType, String locationName, boolean isWeak, CodeLocation location) {
		super(staticType, locationName, isWeak, location);
	}
	
	public HeapAllocationSite(Type staticType, String locationName, SymbolicExpression field, boolean isWeak,
			CodeLocation location) {
		super(staticType, locationName, field, isWeak, location);
	}
}
