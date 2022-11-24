package it.unive.golisa.analysis.heap;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.commons.lang3.tuple.Pair;

import it.unive.golisa.cfg.type.composite.GoPointerType;
import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.heap.BaseHeapDomain;
import it.unive.lisa.analysis.heap.pointbased.AllocationSite;
import it.unive.lisa.analysis.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.lattices.ExpressionSet;
import it.unive.lisa.analysis.nonrelational.heap.HeapEnvironment;
import it.unive.lisa.analysis.representation.DomainRepresentation;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.AccessChild;
import it.unive.lisa.symbolic.heap.HeapAllocation;
import it.unive.lisa.symbolic.heap.HeapDereference;
import it.unive.lisa.symbolic.heap.HeapExpression;
import it.unive.lisa.symbolic.heap.HeapReference;
import it.unive.lisa.symbolic.value.HeapLocation;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.MemoryPointer;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.symbolic.value.Variable;
import it.unive.lisa.type.Type;

/**
 * A field-insensitive point-based heap implementation for Go that abstracts
 * heap locations depending on their allocation sites, namely the position of
 * the code where heap locations are generated. All heap locations that are
 * generated at the same allocation sites are abstracted into a single unique
 * heap identifier. The implementation follows X. Rival and K. Yi, "Introduction
 * to Static Analysis An Abstract Interpretation Perspective", Section 8.3.4
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 * 
 * @see <a href=
 *          "https://mitpress.mit.edu/books/introduction-static-analysis">https://mitpress.mit.edu/books/introduction-static-analysis</a>
 */
public class GoPointBasedHeap extends BaseHeapDomain<GoPointBasedHeap> {

	/**
	 * An heap environment tracking which allocation sites are associated to
	 * each identifier.
	 */
	protected final HeapEnvironment<GoAllocationSites> heapEnv;

	/**
	 * A set of pair tracking which heap location values must be copied by the
	 * value domain.
	 */
	protected final Set<Pair<HeapLocation, HeapLocation>> decouples;

	/**
	 * Builds a new instance of field-insensitive point-based heap.
	 */
	public GoPointBasedHeap() {
		this(new HeapEnvironment<>(new GoAllocationSites()));
	}

	/**
	 * Builds a new instance of field-insensitive point-based heap from its heap
	 * environment.
	 * 
	 * @param heapEnv the heap environment that this instance tracks
	 */
	protected GoPointBasedHeap(HeapEnvironment<GoAllocationSites> heapEnv) {
		this(heapEnv, new HashSet<>());
	}

	/**
	 * Builds a new instance of field-insensitive point-based heap from its heap
	 * environment.
	 * 
	 * @param heapEnv the heap environment that this instance tracks
	 * @param copies  the set of pairs of heap location to decouple
	 */
	public GoPointBasedHeap(HeapEnvironment<GoAllocationSites> heapEnv, Set<Pair<HeapLocation, HeapLocation>> copies) {
		this.heapEnv = heapEnv;
		this.decouples = copies;
	}

	/**
	 * Builds a point-based heap from a reference one.
	 * 
	 * @param original reference point-based heap
	 * 
	 * @return a point-based heap build from the original one
	 */
	protected GoPointBasedHeap from(GoPointBasedHeap original) {
		return original;
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
					result = result.lub(from(new GoPointBasedHeap(heap)));
				} else {
					if (star_y instanceof StackAllocationSite && sss.heapEnv.getKeys().contains(expression)) {
						// in other case, where star_y is a stack alloacation
						// site, we should
						// copy
						StackAllocationSite cloneSite = new StackAllocationSite(star_y.getStaticType(),
								id.getCodeLocation().toString(), star_y.isWeak(), id.getCodeLocation());
						StackAllocationSite toClone = new StackAllocationSite(star_y.getStaticType(),
								star_y.getCodeLocation().toString(), star_y.isWeak(), star_y.getCodeLocation());
						HeapEnvironment<GoAllocationSites> heap = sss.heapEnv.assign(id, cloneSite, pp);
						result = result.lub(from(new GoPointBasedHeap(heap)));
						result.decouples.add(Pair.of(cloneSite, toClone));

					} else {
						// plain assignment just if star_y is a real heap
						// allocation site
						HeapEnvironment<GoAllocationSites> heap = sss.heapEnv.assign(id, star_y, pp);
						result = result.lub(from(new GoPointBasedHeap(heap)));
					}
				}
			} else if (exp instanceof AllocationSite) {
				HeapEnvironment<GoAllocationSites> heap = sss.heapEnv.assign(id, exp, pp);
				result = result.lub(from(new GoPointBasedHeap(heap)));
			} else
				result = result.lub(sss);

		return result;
	}

	@Override
	public GoPointBasedHeap assume(SymbolicExpression expression, ProgramPoint pp) throws SemanticException {
		// we just rewrite the expression if needed
		return smallStepSemantics(expression, pp);
	}

	@Override
	public GoPointBasedHeap forgetIdentifier(Identifier id) throws SemanticException {
		return from(new GoPointBasedHeap(heapEnv.forgetIdentifier(id)));
	}

	@Override
	public GoPointBasedHeap forgetIdentifiersIf(Predicate<Identifier> test) throws SemanticException {
		return from(new GoPointBasedHeap(heapEnv.forgetIdentifiersIf(test)));
	}

	@Override
	public Satisfiability satisfies(SymbolicExpression expression, ProgramPoint pp) throws SemanticException {
		// we leave the decision to the value domain
		return Satisfiability.UNKNOWN;
	}

	@Override
	public DomainRepresentation representation() {
		if (isTop())
			return Lattice.topRepresentation();

		if (isBottom())
			return Lattice.bottomRepresentation();

		return heapEnv.representation();
	}

	@Override
	public GoPointBasedHeap top() {
		return from(new GoPointBasedHeap(heapEnv.top()));
	}

	@Override
	public boolean isTop() {
		return heapEnv.isTop();
	}

	@Override
	public GoPointBasedHeap bottom() {
		return from(new GoPointBasedHeap(heapEnv.bottom()));
	}

	@Override
	public boolean isBottom() {
		return heapEnv.isBottom();
	}

	@Override
	public List<HeapReplacement> getSubstitution() {
		return Collections.emptyList();
	}

	@Override
	public GoPointBasedHeap mk(GoPointBasedHeap reference) {
		return from(new GoPointBasedHeap(reference.heapEnv));
	}

	@Override
	public GoPointBasedHeap lubAux(GoPointBasedHeap other) throws SemanticException {
		Set<Pair<HeapLocation, HeapLocation>> lubCopies = new HashSet<>();

		for (Pair<HeapLocation, HeapLocation> p : this.decouples)
			lubCopies.add(p);
		for (Pair<HeapLocation, HeapLocation> p : other.decouples)
			lubCopies.add(p);

		return from(new GoPointBasedHeap(heapEnv.lub(other.heapEnv), lubCopies));
	}

	@Override
	public GoPointBasedHeap wideningAux(GoPointBasedHeap other) throws SemanticException {
		return lubAux(other);
	}

	@Override
	public boolean lessOrEqualAux(GoPointBasedHeap other) throws SemanticException {
		return heapEnv.lessOrEqual(other.heapEnv);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((heapEnv == null) ? 0 : heapEnv.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GoPointBasedHeap other = (GoPointBasedHeap) obj;
		if (heapEnv == null) {
			if (other.heapEnv != null)
				return false;
		} else if (!heapEnv.equals(other.heapEnv))
			return false;
		return true;
	}

	@Override
	public GoPointBasedHeap semanticsOf(HeapExpression expression, ProgramPoint pp) throws SemanticException {
		return this;
	}

	@Override
	public ExpressionSet<ValueExpression> rewrite(SymbolicExpression expression, ProgramPoint pp)
			throws SemanticException {
		return expression.accept(new Rewriter());
	}

	@Override
	public GoPointBasedHeap popScope(ScopeToken scope) throws SemanticException {
		return from(new GoPointBasedHeap(heapEnv.popScope(scope)));
	}

	@Override
	public GoPointBasedHeap pushScope(ScopeToken scope) throws SemanticException {
		return from(new GoPointBasedHeap(heapEnv.pushScope(scope)));
	}

	/**
	 * A {@link it.unive.lisa.analysis.heap.BaseHeapDomain.Rewriter} for the
	 * {@link PointBasedHeap} domain.
	 * 
	 * @author <a href="mailto:luca.negrini@unive.it">Luca Negrini</a>
	 */
	protected class Rewriter extends BaseHeapDomain.Rewriter {

		/*
		 * note that all the cases where we are adding a plain expression to the
		 * result set in these methods is because it could have been already
		 * rewritten by other rewrite methods to an allocation site
		 */

		@Override
		public ExpressionSet<ValueExpression> visit(AccessChild expression, ExpressionSet<ValueExpression> receiver,
				ExpressionSet<ValueExpression> child, Object... params) throws SemanticException {
			Set<ValueExpression> result = new HashSet<>();

			for (ValueExpression rec : receiver)
				if (rec instanceof MemoryPointer) {
					MemoryPointer pid = (MemoryPointer) rec;
					GoAllocationSite site = (GoAllocationSite) pid.getReferencedLocation();
					GoAllocationSite e;
					if (site instanceof StackAllocationSite) {
						e = new StackAllocationSite(
								expression.getStaticType(),
								site.getLocationName(),
								true,
								expression.getCodeLocation());
					} else
						e = new HeapAllocationSite(
								expression.getStaticType(),
								site.getLocationName(),
								true,
								expression.getCodeLocation());

					if (expression.hasRuntimeTypes())
						e.setRuntimeTypes(expression.getRuntimeTypes(null));
					result.add(e);
				} else if (rec instanceof GoAllocationSite)
					result.add(rec);

			return new ExpressionSet<>(result);
		}

		@Override
		public ExpressionSet<ValueExpression> visit(HeapAllocation expression, Object... params)
				throws SemanticException {
			GoAllocationSite id;
			if (expression.getStaticType() instanceof GoPointerType) {
				GoPointerType pointer = (GoPointerType) expression.getStaticType();
				id = new HeapAllocationSite(
						pointer.getInnerType(),
						expression.getCodeLocation().getCodeLocation(),
						true,
						expression.getCodeLocation());
			} else {
				id = new StackAllocationSite(
						expression.getStaticType(),
						expression.getCodeLocation().getCodeLocation(),
						true,
						expression.getCodeLocation());
			}

			if (expression.hasRuntimeTypes())
				id.setRuntimeTypes(expression.getRuntimeTypes(null));
			return new ExpressionSet<>(id);
		}

		@Override
		public ExpressionSet<ValueExpression> visit(HeapReference expression, ExpressionSet<ValueExpression> arg,
				Object... params)
				throws SemanticException {
			Set<ValueExpression> result = new HashSet<>();

			for (ValueExpression loc : arg)
				if (loc instanceof GoAllocationSite) {
					MemoryPointer e = new MemoryPointer(loc.getStaticType(),
							(GoAllocationSite) loc,
							loc.getCodeLocation());
					if (expression.hasRuntimeTypes())
//						e.setRuntimeTypes(loc.getRuntimeTypes(null));
						e.setRuntimeTypes(expression.getRuntimeTypes(null));
					result.add(e);
				} else
					result.add(loc);
			return new ExpressionSet<>(result);
		}

		@Override
		public ExpressionSet<ValueExpression> visit(HeapDereference expression, ExpressionSet<ValueExpression> arg,
				Object... params)
				throws SemanticException {
			Set<ValueExpression> result = new HashSet<>();

			for (ValueExpression ref : arg)
				if (ref instanceof MemoryPointer)
					result.add(((MemoryPointer) ref).getReferencedLocation());
				else if (ref instanceof Identifier) {
					// this could be aliasing!
					Identifier id = (Identifier) ref;
					if (heapEnv.getKeys().contains(id))
						result.addAll(resolveIdentifier(id));
					else if (id instanceof Variable) {
						// this is a variable from the program that we know
						// nothing about
						CodeLocation loc = expression.getCodeLocation();
						if (id.hasRuntimeTypes()) {
							for (Type t : id.getRuntimeTypes(null))
								if (t.isPointerType())
									result.add(new HeapAllocationSite(t, "unknown@" + id.getName(), true, loc));
								else if (t.isInMemoryType())
									result.add(new StackAllocationSite(t, "unknown@" + id.getName(), true, loc));
						} else if (id.getStaticType().isPointerType())
							result.add(
									new HeapAllocationSite(id.getStaticType(), "unknown@" + id.getName(), true, loc));
						else if (id.getStaticType().isInMemoryType())
							result.add(
									new StackAllocationSite(id.getStaticType(), "unknown@" + id.getName(), true, loc));
					}
				} else
					result.add(ref);

			return new ExpressionSet<>(result);
		}

		@Override
		public final ExpressionSet<ValueExpression> visit(Identifier expression, Object... params)
				throws SemanticException {
			if (!(expression instanceof MemoryPointer) && heapEnv.getKeys().contains(expression))
				return new ExpressionSet<>(resolveIdentifier(expression));

			return new ExpressionSet<>(expression);
		}

		private Set<ValueExpression> resolveIdentifier(Identifier v) {
			Set<ValueExpression> result = new HashSet<>();
			for (GoAllocationSite site : heapEnv.getState(v)) {
				MemoryPointer e = new MemoryPointer(site.getStaticType(),
						site,
						site.getCodeLocation());
				if (v.hasRuntimeTypes())
					e.setRuntimeTypes(v.getRuntimeTypes(null));
				result.add(e);
			}

			return result;
		}

//		@Override
//		public ExpressionSet<ValueExpression> visit(PushAny expression, Object... params)
//				throws SemanticException {
//			Set<ValueExpression> result = new HashSet<>();
//			CodeLocation loc = expression.getCodeLocation();
//			if (expression.hasRuntimeTypes()) {
//				for (Type t : expression.getRuntimeTypes(null))
//					if (t.isPointerType())
//						result.add(new HeapAllocationSite(t, "unknown@" + loc.getCodeLocation(), true, loc));
//					else if (t.isInMemoryType())
//						result.add(new StackAllocationSite(t, "unknown@" + loc.getCodeLocation(), true, loc));
//			} else if (expression.getStaticType().isPointerType())
//				result.add(
//						new HeapAllocationSite(expression.getStaticType(), "unknown@" + loc.getCodeLocation(), true,
//								loc));
//			else if (expression.getStaticType().isInMemoryType())
//				result.add(
//						new StackAllocationSite(expression.getStaticType(), "unknown@" + loc.getCodeLocation(), true,
//								loc));
//			if (!result.isEmpty())
//				return new ExpressionSet<>(result);
//			return new ExpressionSet<>(expression);
//		}
	}

	/**
	 * Yields the set of pair of heap locations to decouple.
	 * 
	 * @return the set of pair of heap locations to decouple
	 */
	public Set<Pair<HeapLocation, HeapLocation>> getDecouples() {
		return decouples;
	}
}
