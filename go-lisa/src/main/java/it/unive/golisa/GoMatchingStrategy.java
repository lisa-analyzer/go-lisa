package it.unive.golisa;

import java.util.Set;

import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.program.cfg.statement.call.Call.CallType;
import it.unive.lisa.program.language.resolution.FixedOrderMatchingStrategy;
import it.unive.lisa.type.Type;

public class GoMatchingStrategy extends FixedOrderMatchingStrategy {

	/**
	 * The singleton instance of this class.
	 */
	public static final GoMatchingStrategy INSTANCE = new GoMatchingStrategy();

	@Override
	public boolean matches(Call call, int pos, Parameter formal, Expression actual, Set<Type> types) {
		if (pos == 0 && call.getCallType() == CallType.INSTANCE)
			return matchCallee(formal, types);
		else if (types.stream().anyMatch(rt -> rt.isPointerType() && rt.asPointerType().getInnerType().canBeAssignedTo(formal.getStaticType())))
				return true;
		return types.stream().anyMatch(rt -> rt.canBeAssignedTo(formal.getStaticType()));

	}

	private boolean matchCallee(Parameter formal, Set<Type> types) {
		if (formal.getStaticType().isPointerType()) {
			Type inner = formal.getStaticType().asPointerType().getInnerType();
			return types.stream().anyMatch(rt -> rt.canBeAssignedTo(inner) || rt.canBeAssignedTo(formal.getStaticType()));
		} else if (types.stream().anyMatch(rt -> rt.isPointerType() && rt.asPointerType().getInnerType().canBeAssignedTo(formal.getStaticType())))
			return true;
		return types.stream().anyMatch(rt -> rt.canBeAssignedTo(formal.getStaticType()));
	}
}
