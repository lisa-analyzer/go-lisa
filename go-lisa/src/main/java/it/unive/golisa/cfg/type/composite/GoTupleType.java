package it.unive.golisa.cfg.type.composite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import it.unive.golisa.cfg.expression.literal.GoTupleExpression;
import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.InMemoryType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;
import it.unive.lisa.type.Untyped;

/**
 * A tuple of types (e.g., (int, Vertex, float32)).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
@SuppressWarnings("serial")
public class GoTupleType extends ArrayList<Parameter> implements GoType, InMemoryType {

	private static final Set<GoTupleType> tupleTypes = new HashSet<>();

	/**
	 * Yields a unique instance (either an existing one or a fresh one) of
	 * {@link GoTupleType} representing a tuple type.
	 * 
	 * @param pars the parameters of the tuple type to lookup
	 * 
	 * @return the unique instance of {@link GoTupleType} representing the tuple
	 *             type given as argument
	 */
	public static GoTupleType lookup(Parameter... pars) {
		GoTupleType type = new GoTupleType(pars);
		if (!tupleTypes.contains(type))
			tupleTypes.add(type);
		return tupleTypes.stream().filter(x -> x.equals(type)).findFirst().get();
	}

	/**
	 * Checks whether a tuple type {@code raw} has been already built.
	 * 
	 * @param raw the tuple type
	 * 
	 * @return whether a tuple type {@code name} has been already built.
	 */
	public static boolean hasTupleType(GoTupleType raw) {
		return tupleTypes.contains(raw);
	}

	/**
	 * Builds a tuple type.
	 * 
	 * @param pars the parameters
	 */
	private GoTupleType(Parameter... pars) {
		super();
		for (int i = 0; i < pars.length; i++)
			this.add(pars[i]);
	}

	/**
	 * Yields the type at the {@code i}-position of the tuple.
	 * 
	 * @param i the position
	 * 
	 * @return the type at the {@code i}-position of the tuple
	 */
	public Type getTypeAt(int i) {
		return this.get(i).getStaticType();
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		if (other instanceof GoTupleType) {
			GoTupleType that = (GoTupleType) other;

			if (that.size() != size())
				return false;

			for (int i = 0; i < size(); i++)
				if (!get(i).getStaticType().canBeAssignedTo(that.get(i).getStaticType()))
					return false;
			return true;
		}

		return other.isUntyped();
	}

	@Override
	public Type commonSupertype(Type other) {
		if (other instanceof GoTupleType) {
			GoTupleType that = (GoTupleType) other;
			return this.canBeAssignedTo(that) ? this : that.canBeAssignedTo(this) ? that : Untyped.INSTANCE;
		}

		return Untyped.INSTANCE;
	}

	@Override
	public Expression defaultValue(CFG cfg, SourceCodeLocation location) {
		Expression[] exps = new Expression[size()];

		for (int i = 0; i < size(); i++)
			exps[i] = ((GoType) get(i).getStaticType()).defaultValue(cfg, location);

		return new GoTupleExpression(cfg, location, exps);
	}

	/**
	 * Yields all the tuple types.
	 * 
	 * @return all the tuple types
	 */
	public static Set<Type> all() {
		Set<Type> instances = new HashSet<>();
		for (GoTupleType in : tupleTypes)
			instances.add(in);
		return instances;
	}

	@Override
	public Set<Type> allInstances(TypeSystem type) {
		return Collections.singleton(this);
	}

	@Override
	public String toString() {
		return super.toString();
	}

	/**
	 * Builds a tuple type.
	 * 
	 * @param location location of this tuple type
	 * @param types    types of the tuple
	 * 
	 * @return a tuple type
	 */
	public static GoTupleType getTupleTypeOf(CodeLocation location, Type... types) {
		Parameter[] pars = new Parameter[types.length];
		for (int i = 0; i < types.length; i++)
			pars[i] = new Parameter(location, "_", types[i]);

		return lookup(pars);
	}

	/**
	 * From "A tour of Go": Go's return values may be named. If so, they are
	 * treated as variables defined at the top of the function. These names
	 * should be used to document the meaning of the return values. A return
	 * statement without arguments returns the named return values. This is
	 * known as a "naked" return.
	 * 
	 * @return if this tuple type has named parameters
	 */
	public boolean isNamedValues() {
		return !get(0).getName().equals("_");
	}

	/**
	 * Clears all the tuple types.
	 */
	public static void clearAll() {
		tupleTypes.clear();
	}
}
