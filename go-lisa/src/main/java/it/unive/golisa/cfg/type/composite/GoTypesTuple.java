package it.unive.golisa.cfg.type.composite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;

public class GoTypesTuple extends ArrayList<Parameter> implements GoType {

	public static final Set<GoTypesTuple> tupleTypes = new HashSet<>();

	public static GoTypesTuple lookup(GoTypesTuple type)  {
		if (!tupleTypes.contains(type))
			tupleTypes.add(type);
		return tupleTypes.stream().filter(x -> x.equals(type)).findFirst().get();
	}

	public static boolean hasTupleType(GoTypesTuple raw) {
		return tupleTypes.contains(raw);
	}

	public GoTypesTuple(Parameter[] pars) {
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Type> allInstances() {
		return Collections.singleton(this);
	}
	
	@Override
	public boolean isPointerType() {
		return true;
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
