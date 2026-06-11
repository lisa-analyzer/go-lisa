package it.unive.golisa;

import it.unive.golisa.program.cfg.VarArgsParameter;
import it.unive.lisa.program.cfg.CodeMember;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.program.cfg.statement.call.Call.CallType;
import it.unive.lisa.program.cfg.statement.call.UnresolvedCall;
import it.unive.lisa.program.language.resolution.ParameterMatchingStrategy;
import it.unive.lisa.type.PointerType;
import it.unive.lisa.type.ReferenceType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
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
		if (types.stream()
				.anyMatch(rt -> rt.isPointerType() && rt.asPointerType().getInnerType().canBeAssignedTo(type)))
			return true;
		return types.stream().anyMatch(rt -> rt.canBeAssignedTo(type));

	}

	private boolean matchCallee(Parameter formal, Set<Type> types) {
		if (formal.getStaticType().isPointerType()) {
			Type inner = formal.getStaticType().asPointerType().getInnerType();
			if (types.stream()
					.anyMatch(rt -> rt.canBeAssignedTo(inner) || rt.canBeAssignedTo(formal.getStaticType())
							|| canBeAssignedPointRef(rt, formal.getStaticType())))
				return true;
		} else if (types.stream().anyMatch(
				rt -> rt.isPointerType() && rt.asPointerType().getInnerType().canBeAssignedTo(formal.getStaticType())))
			return true;
		return types.stream().anyMatch(rt -> rt.canBeAssignedTo(formal.getStaticType()));
	}

	private boolean canBeAssignedPointRef(Type rt, Type formal) {
		if (rt.isReferenceType() && formal.isPointerType()) {
			ReferenceType ref = (ReferenceType) rt;
			PointerType point = (PointerType) formal;
			if (ref.getInnerType().canBeAssignedTo(point.getInnerType()))
				return true;
		}
		return false;
	}

	@Override
	public int distanceFromPerfectTarget(UnresolvedCall call, Set<Type>[] types, CodeMember cm, boolean instance) {
		int distance = 0;
		int startIdx = instance ? 1 : 0;
		Expression[] params = call.getParameters();
		CodeMemberDescriptor descriptor = cm.getDescriptor();
		boolean hasVarargs = descriptor.getFormals().length > 0
				&& descriptor.getFormals()[descriptor.getFormals().length - 1] instanceof VarArgsParameter;
		for (int i = startIdx; i < params.length; i++) {
			if (i == descriptor.getFormals().length - 1 && hasVarargs)
				if (i == types.length)
					// no values passed for the varargs parameter
					return distance;
			Expression parameter = params[i];
			Type paramType = parameter.getStaticType();
			Type formalType;
			if (hasVarargs && i > descriptor.getFormals().length - 1)
				formalType = descriptor.getFormals()[descriptor.getFormals().length - 1].getStaticType();
			else
				formalType = descriptor.getFormals()[i].getStaticType();
			if (formalType instanceof Untyped)
				return 0;
			if (paramType instanceof Untyped) {
				boolean allIncomparable = true;
				for (Type runtimeType : types[i]) {
					int dist = call.getProgram().getTypes().distanceBetweenTypes(formalType, runtimeType);
					if (dist >= 0) {
						allIncomparable = false;
						distance += dist;
					}
				}
				if (allIncomparable)
					return -1;
				continue;
			}
			int dist = call.getProgram().getTypes().distanceBetweenTypes(formalType, paramType);
			if (dist < 0)
				return -1;
			distance += dist;
		}
		return distance;
	}

}
