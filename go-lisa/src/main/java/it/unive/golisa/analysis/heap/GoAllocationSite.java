package it.unive.golisa.analysis.heap;

import it.unive.lisa.analysis.heap.pointbased.AllocationSite;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.type.Type;

public abstract class GoAllocationSite extends AllocationSite {

	public GoAllocationSite(Type staticType, String locationName, boolean isWeak, CodeLocation location) {
		super(staticType, locationName, isWeak, location);
	}
//
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = prime + ((getName() == null) ? 0 : getName().hashCode());
//		result = prime * result + (isWeak() ? 1231 : 1237);
//		return result;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (obj instanceof GoAllocationSite) {
//			GoAllocationSite other = (GoAllocationSite) obj;
//			if (getName() == null) {
//				if (other.getName() != null)
//					return false;
//			} else if (!other.getName().equals(other.getName()))
//				return false;
//			if (isWeak() != other.isWeak())
//				return false;
//			return true;
//		}
//		return false;
//	}

	public GoAllocationSite(Type staticType, String locationName, SymbolicExpression field, boolean isWeak,
			CodeLocation location) {
		super(staticType, locationName, field, isWeak, location);
	}
}
