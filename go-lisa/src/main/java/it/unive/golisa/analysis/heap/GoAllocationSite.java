package it.unive.golisa.analysis.heap;

import it.unive.lisa.analysis.heap.pointbased.AllocationSite;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.type.Type;

/**
 * A heap identifier that track also the source location where it has been
 * allocated and a field (optional). This class is used in
 * {@link GoPointBasedHeap} and {@link GoFieldSensitivePointBasedHeap}.
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoAllocationSite extends AllocationSite {

	/**
	 * Builds an allocation site from its source code location and its field and
	 * specifying if it is weak.
	 * 
	 * @param staticType   the static type of this allocation site
	 * @param locationName the source code location string representation where
	 *                         this allocation site has been allocated
	 * @param field        the field of this allocation site
	 * @param isWeak       boolean value specifying if this allocation site is
	 *                         weak
	 * @param location     the code location of the statement that has generated
	 *                         this expression
	 */
	public GoAllocationSite(Type staticType, String locationName, SymbolicExpression field, boolean isWeak,
			CodeLocation location) {
		super(staticType, locationName, field, isWeak, location);
	}
}
