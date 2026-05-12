package it.unive.golisa.analysis;

import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.value.ValueLattice;
import it.unive.lisa.lattices.ExpressionInverseSet;
import it.unive.lisa.lattices.FunctionalLattice;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.OutOfScopeIdentifier;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * The equality domain, tracking definite information about which variables are
 * equals to another one.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class EqualityLattice extends FunctionalLattice<EqualityLattice, Identifier, ExpressionInverseSet>
		implements ValueLattice<EqualityLattice> {

	
	/**
	 * Builds the domain.
	 */
	public EqualityLattice() {
		this(new ExpressionInverseSet(), null);
	}
	
	/**
	 * Builds the domain.
	 * 
	 * @param lattice  the underlying lattice
	 * @param function the function to clone
	 */
	
	public EqualityLattice(ExpressionInverseSet lattice,
			Map<Identifier, ExpressionInverseSet> function) {
		super(lattice, function);
	}
	
	public EqualityLattice(ExpressionInverseSet lattice) {
		super(lattice);
	}

	@Override
	public EqualityLattice top() {
		return new EqualityLattice(lattice.top(), null);
	}

	@Override
	public EqualityLattice bottom() {
		return new EqualityLattice(lattice.bottom(), null);
	}
	
	@Override
	public ExpressionInverseSet stateOfUnknown(Identifier key) {
		return lattice.bottom();
	}

	@Override
	public EqualityLattice mk(ExpressionInverseSet lattice, Map<Identifier, ExpressionInverseSet> function) {
		return new EqualityLattice(lattice, function);
	}
	

	@Override
	public boolean isTop() {
		return lattice.isTop() && function == null;
	}

	@Override
	public boolean isBottom() {
		return lattice.isBottom() && function == null;
	}

	@Override
	public EqualityLattice store(Identifier target, Identifier source) throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean knowsIdentifier(Identifier id) {
		return getKeys().contains(id);
	}

	@Override
	public EqualityLattice forgetIdentifier(Identifier id, ProgramPoint pp) throws SemanticException {
		if (isTop() || isBottom())
			return this;

		EqualityLattice result = new EqualityLattice(lattice, new HashMap<>(function));
		if (result.function.containsKey(id))
			result.function.remove(id);

		return result;
	}

	@Override
	public EqualityLattice forgetIdentifiersIf(Predicate<Identifier> test, ProgramPoint pp) throws SemanticException {
		if (isTop() || isBottom())
			return this;

		EqualityLattice result = new EqualityLattice(lattice, new HashMap<>(function));
		Set<Identifier> keys = result.function.keySet().stream().filter(test::test).collect(Collectors.toSet());
		keys.forEach(result.function::remove);

		return result;
	}

	@Override
	public EqualityLattice forgetIdentifiers(Iterable<Identifier> ids, ProgramPoint pp) throws SemanticException {
		if (isTop() || isBottom())
			return this;

		EqualityLattice result = new EqualityLattice(lattice, new HashMap<>(function));
		for(Identifier id : ids)
			if (result.function.containsKey(id))
				result.function.remove(id);

		return result;
	}

	@Override
	public boolean lessOrEqual(EqualityLattice other) throws SemanticException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public EqualityLattice lub(EqualityLattice other) throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EqualityLattice pushScope(ScopeToken token, ProgramPoint pp) throws SemanticException {
		return liftIdentifiers(id -> new OutOfScopeIdentifier(id, token, id.getCodeLocation()));
	}

	@Override
	public EqualityLattice popScope(ScopeToken token, ProgramPoint pp) throws SemanticException {
		AtomicReference<SemanticException> holder = new AtomicReference<>();

		EqualityLattice result = liftIdentifiers(id -> {
			if (id instanceof OutOfScopeIdentifier)
				try {
					return (Identifier) id.popScope(token, pp);
				} catch (SemanticException e) {
					holder.set(e);
				}
			return null;
		});

		if (holder.get() != null)
			throw new SemanticException("Popping the scope '" + token + "' raised an error", holder.get());

		return result;
	}

	@Override
	public StructuredRepresentation representation() {
		if (isTop())
			return Lattice.topRepresentation();

		if (isBottom())
			return Lattice.bottomRepresentation();

		StringBuilder builder = new StringBuilder();
		for (Entry<Identifier, ExpressionInverseSet> entry : function.entrySet())
			builder.append(entry.getKey()).append(" == ").append(entry.getValue().toString()).append("\n");

		return new StringRepresentation(builder.toString().trim());
	}

	private EqualityLattice liftIdentifiers(Function<Identifier, Identifier> lifter) throws SemanticException {
		if (isBottom() || isTop())
			return this;

		Map<Identifier, ExpressionInverseSet> function = mkNewFunction(null, false);
		for (Identifier id : getKeys()) {
			Identifier lifted = lifter.apply(id);
			if (lifted != null)
				function.put(lifted, getState(id));
		}

		return new EqualityLattice(lattice, function);
	}


}
