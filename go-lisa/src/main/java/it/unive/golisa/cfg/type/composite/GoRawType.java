package it.unive.golisa.cfg.type.composite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.Type;

public class GoRawType extends ArrayList<Parameter> implements GoType {

	public static final Set<GoRawType> rawTypes = new HashSet<>();

	public static GoRawType lookup(GoRawType type)  {
		if (!rawTypes.contains(type))
			rawTypes.add(type);
		return rawTypes.stream().filter(x -> x.equals(type)).findFirst().get();
	}
		
	public static boolean hasRawType(GoRawType raw) {
		return rawTypes.contains(raw);
	}
		
	public GoRawType(Parameter[] pars) {
		super();
		for (int i = 0; i < pars.length; i++)
			this.add(pars[i]);
	}
	
	@Override
	public boolean canBeAssignedTo(Type other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Type commonSupertype(Type other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression defaultValue(CFG cfg) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean isGoInteger() {
		return false;
	}
	
	@Override
	public Collection<Type> allInstances() {
		return Collections.singleton(this);
	}
}
