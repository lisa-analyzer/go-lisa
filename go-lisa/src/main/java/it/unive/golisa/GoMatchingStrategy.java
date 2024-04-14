package it.unive.golisa;

import it.unive.golisa.cfg.VarArgsParameter;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.program.cfg.statement.call.Call.CallType;
import it.unive.lisa.program.language.resolution.ParameterMatchingStrategy;
import it.unive.lisa.type.Type;
import java.util.Set;

/**
 * The Go parameter matching strategy.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoMatchingStrategy implements ParameterMatchingStrategy {

	/**
	 * The singleton instance of this class.
	 */
	public static final GoMatchingStrategy INSTANCE = new GoMatchingStrategy();

	@Override
	public boolean matches(Call call, Parameter[] formals, Expression[] actuals, Set<Type>[] types) {
		boolean hasVarargs = formals.length > 0 && formals[formals.length - 1] instanceof VarArgsParameter;
		if (hasVarargs) {
			if (formals.length - 1 > actuals.length)
				return false;
		} else if (formals.length > actuals.length)
			return false;

		for (int i = 0; i < formals.length; i++)
			if (i == formals.length - 1 && hasVarargs)
				if (i == types.length)
					// no values passed for the varargs parameter
					return true;
				else
					return compatibleWith(formals[i].getStaticType(), i, actuals, types);
			else if (i == 0 && call.getCallType() == CallType.INSTANCE)
				return matchCallee(formals[i], types[i]);
			else if (!matches(formals[i].getStaticType(), types[i]))
				return false;
		

		return true;
	}

	private boolean compatibleWith(Type type, int i, Expression[] actuals, Set<Type>[] types) {
		for (int j = i; j < types.length; j++)
			if (!matches(type, types[i]))
				return false;
		return true;
	}

	private boolean matches(Type type, Set<Type> types) {
		/*if (types.stream()
				.anyMatch(rt -> rt.isPointerType() && rt.asPointerType().getInnerType().canBeAssignedTo(type)))
			return true;
		return types.stream().anyMatch(rt -> rt.canBeAssignedTo(type));
		*/ 
		return true; // removed type checks

	}

	private boolean matchCallee(Parameter formal, Set<Type> types) {
		if (formal.getStaticType().isPointerType()) {
			Type inner = formal.getStaticType().asPointerType().getInnerType();
			return types.stream()
					.anyMatch(rt -> rt.canBeAssignedTo(inner) || rt.canBeAssignedTo(formal.getStaticType()));
		} else if (types.stream().anyMatch(
				rt -> rt.isPointerType() && rt.asPointerType().getInnerType().canBeAssignedTo(formal.getStaticType())))
			return true;
		return types.stream().anyMatch(rt -> rt.canBeAssignedTo(formal.getStaticType()));
	}
}
