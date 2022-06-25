package it.unive.golisa.analysis.heap;

import java.util.HashSet;
import java.util.Set;

import it.unive.golisa.cfg.type.composite.GoPointerType;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.heap.pointbased.AllocationSite;
import it.unive.lisa.analysis.lattices.ExpressionSet;
import it.unive.lisa.analysis.nonrelational.heap.HeapEnvironment;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.AccessChild;
import it.unive.lisa.symbolic.heap.HeapAllocation;
import it.unive.lisa.symbolic.value.MemoryPointer;
import it.unive.lisa.symbolic.value.ValueExpression;

public class GoFieldSensitivePointBasedHeap extends GoPointBasedHeap {

	/**
	 * Builds a new instance of field-sensitive point-based heap.
	 */
	public GoFieldSensitivePointBasedHeap() {
		super();
	}

	private GoFieldSensitivePointBasedHeap(HeapEnvironment<GoAllocationSites> allocationSites) {
		super(allocationSites);
	}

	@Override
	protected GoFieldSensitivePointBasedHeap from(GoPointBasedHeap original) {
		return new GoFieldSensitivePointBasedHeap(original.heapEnv);
	}

	@Override
	public ExpressionSet<ValueExpression> rewrite(SymbolicExpression expression, ProgramPoint pp)
			throws SemanticException {
		return expression.accept(new Rewriter());
	}

	private class Rewriter extends GoPointBasedHeap.Rewriter {

		@Override
		public ExpressionSet<ValueExpression> visit(AccessChild expression, ExpressionSet<ValueExpression> receiver,
				ExpressionSet<ValueExpression> child, Object... params) throws SemanticException {
			Set<ValueExpression> result = new HashSet<>();

			for (ValueExpression rec : receiver)
				if (rec instanceof MemoryPointer) {
					GoAllocationSite site = (GoAllocationSite) ((MemoryPointer) rec).getReferencedLocation();
					populate(expression, child, result, site);
				} else if (rec instanceof AllocationSite) {
					AllocationSite site = (AllocationSite) rec;
					populate(expression, child, result, site);
				}

			return new ExpressionSet<>(result);
		}

		protected void populate(AccessChild expression, ExpressionSet<ValueExpression> child,
				Set<ValueExpression> result, AllocationSite site) {
			for (SymbolicExpression target : child) {
				GoAllocationSite e;
				if (site instanceof StackAllocationSite) {
					e = new StackAllocationSite(
							expression.getStaticType(),
							site.getLocationName(),
							target,
							site.isWeak(),
							site.getCodeLocation());
				} else  {
					e = new HeapAllocationSite(
							expression.getStaticType(),
							site.getLocationName(),
							target,
							site.isWeak(),
							site.getCodeLocation());
				}
				
				
				if (expression.hasRuntimeTypes())
					e.setRuntimeTypes(expression.getRuntimeTypes());
				result.add(e);
			}
		}

		@Override
		public ExpressionSet<ValueExpression> visit(HeapAllocation expression, Object... params)
				throws SemanticException {
			String pp = expression.getCodeLocation().getCodeLocation();

			boolean weak;
			if (alreadyAllocated(pp) != null)
				weak = true;
			else
				weak = false;
			GoAllocationSite id;
			
			if (expression.getStaticType() instanceof GoPointerType) {
				GoPointerType pointer = (GoPointerType) expression.getStaticType();
				id = new HeapAllocationSite(
						pointer.getInnerTypes().stream().findAny().get(),
						pp,
						weak,
						expression.getCodeLocation());
			} else {
				id = new StackAllocationSite(
						expression.getStaticType(),
						pp,
						weak,
						expression.getCodeLocation());;
			}
			
//			 = new AllocationSite(expression.getStaticType(), pp, weak, expression.getCodeLocation());
			if (expression.hasRuntimeTypes())
				id.setRuntimeTypes(expression.getRuntimeTypes());
			return new ExpressionSet<>(id);
		}

		private GoAllocationSite alreadyAllocated(String id) {
			for (GoAllocationSites set : heapEnv.getValues())
				for (GoAllocationSite site : set)
					if (site.getLocationName().equals(id))
						return site;

			return null;
		}
	}
}
