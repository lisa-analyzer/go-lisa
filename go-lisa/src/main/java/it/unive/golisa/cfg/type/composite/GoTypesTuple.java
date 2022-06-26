package it.unive.golisa.cfg.type.composite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import it.unive.golisa.cfg.expression.literal.GoExpressionsTuple;
import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.InMemoryType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;

public class GoTypesTuple extends ArrayList<Parameter> implements GoType, InMemoryType {

	public static final Set<GoTypesTuple> tupleTypes = new HashSet<>();

	public static GoTypesTuple lookup(GoTypesTuple type) {
		if (!tupleTypes.contains(type))
			tupleTypes.add(type);
		return tupleTypes.stream().filter(x -> x.equals(type)).findFirst().get();
	}

	public static boolean hasTupleType(GoTypesTuple raw) {
		return tupleTypes.contains(raw);
	}

	public GoTypesTuple(Parameter... pars) {
		super();
		for (int i = 0; i < pars.length; i++)
			this.add(pars[i]);
	}

	public Type getTypeAt(int i) {
		return this.get(i).getStaticType();
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		if (other instanceof GoTypesTuple) {
			GoTypesTuple that = (GoTypesTuple) other;

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
		if (other instanceof GoTypesTuple) {
			GoTypesTuple that = (GoTypesTuple) other;
			return this.canBeAssignedTo(that) ? this : that.canBeAssignedTo(this) ? that : Untyped.INSTANCE;
		}

		return Untyped.INSTANCE;
	}

	@Override
	public Expression defaultValue(CFG cfg, SourceCodeLocation location) {
		Expression[] exps = new Expression[size()];

		for (int i = 0; i < size(); i++)
			exps[i] = ((GoType) get(i).getStaticType()).defaultValue(cfg, location);

		return new GoExpressionsTuple(cfg, location, exps);
	}

	public static Collection<Type> all() {
		Collection<Type> instances = new HashSet<>();
		for (GoTypesTuple in : tupleTypes)
			instances.add(in);
		return instances;
	}

	@Override
	public Collection<Type> allInstances() {
		Collection<Type> instances = new HashSet<>();
		for (GoTypesTuple in : tupleTypes)
			instances.add(in);
		return instances;
	}

	@Override
	public String toString() {
		return super.toString();
	}

	public static GoTypesTuple getTupleTypeOf(CodeLocation location, Type... types) {
		Parameter[] pars = new Parameter[types.length];
		for (int i = 0; i < types.length; i++)
			pars[i] = new Parameter(location, "_", types[i]);

		return GoTypesTuple.lookup(new GoTypesTuple(pars));
	}

	/**
	 * From "A tour of Go": Go's return values may be named. If so, they are
	 * treated as variables defined at the top of the function. These names
	 * should be used to document the meaning of the return values. A return
	 * statement without arguments returns the named return values. This is
	 * known as a "naked" return.
	 */
	public boolean isNamedValues() {
		return !get(0).getName().equals("_");
	}

	public static void clearAll() {
		tupleTypes.clear();
	}
}
