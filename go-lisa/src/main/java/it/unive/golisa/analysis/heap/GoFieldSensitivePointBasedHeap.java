package it.unive.golisa.analysis.heap;

import it.unive.golisa.cfg.type.composite.GoArrayType;
import it.unive.golisa.cfg.type.composite.GoPointerType;
import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.cfg.type.composite.GoTypesTuple;
import it.unive.golisa.cfg.type.numeric.signed.GoInt32Type;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.heap.pointbased.AllocationSite;
import it.unive.lisa.analysis.lattices.ExpressionSet;
import it.unive.lisa.analysis.nonrelational.heap.HeapEnvironment;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.AccessChild;
import it.unive.lisa.symbolic.heap.HeapAllocation;
import it.unive.lisa.symbolic.value.HeapLocation;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.MemoryPointer;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.symbolic.value.Variable;
import it.unive.lisa.type.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;

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

	@Override
	public GoPointBasedHeap assign(Identifier id, SymbolicExpression expression, ProgramPoint pp)
			throws SemanticException {

		GoPointBasedHeap sss = smallStepSemantics(expression, pp);
		ExpressionSet<ValueExpression> rewrittenExp = sss.rewrite(expression, pp);

		GoPointBasedHeap result = bottom();
		for (ValueExpression exp : rewrittenExp)
			if (exp instanceof MemoryPointer) {
				MemoryPointer pid = (MemoryPointer) exp;
				HeapLocation star_y = pid.getReferencedLocation();
				if (id instanceof MemoryPointer) {
					// we have x = y, where both are pointers
					// we perform *x = *y so that x and y
					// become aliases
					Identifier star_x = ((MemoryPointer) id).getReferencedLocation();
					HeapEnvironment<GoAllocationSites> heap = sss.heapEnv.assign(star_x, star_y, pp);
					result = result.lub(new GoFieldSensitivePointBasedHeap(heap));
				} else {
					if (star_y instanceof StackAllocationSite && sss.heapEnv.getKeys().contains(expression)) {
						// in other case, where star_y is a stack allocation
						// site, we should
						// copy
						Set<Pair<HeapLocation, HeapLocation>> newCopies = new HashSet<>();

						if (star_y.getStaticType() instanceof GoStructType) {
							GoStructType struct = (GoStructType) star_y.getStaticType();
							Collection<Global> fields = struct.getUnit().getInstanceGlobals(isBottom());

							for (Global f : fields) {
								StackAllocationSite copySite = new StackAllocationSite(f.getStaticType(),
										id.getCodeLocation().toString(), getVariable(f), star_y.isWeak(),
										id.getCodeLocation());
								StackAllocationSite copySiteRight = new StackAllocationSite(f.getStaticType(),
										star_y.getCodeLocation().toString(), getVariable(f), star_y.isWeak(),
										star_y.getCodeLocation());
								newCopies.add(Pair.of(copySite, copySiteRight));
							}
						} else if (star_y.getStaticType() instanceof GoArrayType) {
							GoArrayType array = (GoArrayType) star_y.getStaticType();
							Type contentType = array.getContenType();

							for (int i = 0; i < array.getLength(); i++) {
								Variable field = new Variable(contentType, i + "",
										exp.getCodeLocation());
								StackAllocationSite copySite = new StackAllocationSite(contentType,
										id.getCodeLocation().toString(), field, star_y.isWeak(),
										id.getCodeLocation());
								StackAllocationSite copySiteRight = new StackAllocationSite(contentType,
										star_y.getCodeLocation().toString(), field, star_y.isWeak(),
										star_y.getCodeLocation());
								newCopies.add(Pair.of(copySite, copySiteRight));
							}

							Variable field = new Variable(GoInt32Type.INSTANCE, "len",
									exp.getCodeLocation());
							StackAllocationSite copySite = new StackAllocationSite(GoInt32Type.INSTANCE,
									id.getCodeLocation().toString(), field, star_y.isWeak(),
									id.getCodeLocation());
							StackAllocationSite copySiteRight = new StackAllocationSite(GoInt32Type.INSTANCE,
									star_y.getCodeLocation().toString(), field, star_y.isWeak(),
									star_y.getCodeLocation());
							newCopies.add(Pair.of(copySite, copySiteRight));

							field = new Variable(GoInt32Type.INSTANCE, "cap",
									exp.getCodeLocation());
							copySite = new StackAllocationSite(GoInt32Type.INSTANCE,
									id.getCodeLocation().toString(), field, star_y.isWeak(),
									id.getCodeLocation());
							copySiteRight = new StackAllocationSite(GoInt32Type.INSTANCE,
									star_y.getCodeLocation().toString(), field, star_y.isWeak(),
									star_y.getCodeLocation());
							newCopies.add(Pair.of(copySite, copySiteRight));
						} else if (star_y.getStaticType() instanceof GoTypesTuple) {
							GoTypesTuple array = (GoTypesTuple) star_y.getStaticType();

							for (int i = 0; i < array.size(); i++) {
								Variable field = new Variable(array.get(i).getStaticType(), i + "",
										exp.getCodeLocation());
								StackAllocationSite copySite = new StackAllocationSite(array.get(i).getStaticType(),
										id.getCodeLocation().toString(), field, star_y.isWeak(),
										id.getCodeLocation());
								StackAllocationSite copySiteRight = new StackAllocationSite(
										array.get(i).getStaticType(),
										star_y.getCodeLocation().toString(), field, star_y.isWeak(),
										star_y.getCodeLocation());
								newCopies.add(Pair.of(copySite, copySiteRight));
							}
						}

						StackAllocationSite copySite = new StackAllocationSite(star_y.getStaticType(),
								id.getCodeLocation().toString(), star_y.isWeak(), id.getCodeLocation());
						HeapEnvironment<GoAllocationSites> heap = sss.heapEnv.assign(id, copySite, pp);

						result = result.lub(new GoFieldSensitivePointBasedHeap(heap));
						result.decouples.addAll(newCopies);

					}
//					else if (exp instanceof StackAllocationSite){
//						result = result.lub(sss);
//					} 
					else {
						// plain assignment just if star_y is a heap
						// allocation site
						HeapEnvironment<GoAllocationSites> heap = sss.heapEnv.assign(id, star_y, pp);
						result = result.lub(new GoFieldSensitivePointBasedHeap(heap));
					}
				}
			}

			else if (exp instanceof HeapAllocationSite) {
				HeapEnvironment<GoAllocationSites> heap = sss.heapEnv.assign(id, exp, pp);
				result = result.lub(new GoFieldSensitivePointBasedHeap(heap));
			} else
				result = result.lub(sss);

		return result;
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
				} else {
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
						expression.getCodeLocation());
				;
			}

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

	private SymbolicExpression getVariable(Global global) {
		return new Variable(global.getStaticType(), global.getName(),
				global.getLocation());
	}
}
