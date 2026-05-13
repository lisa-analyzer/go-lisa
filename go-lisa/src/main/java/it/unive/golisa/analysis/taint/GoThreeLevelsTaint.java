package it.unive.golisa.analysis.taint;

import it.unive.golisa.cfg.expression.instrumented.TaintPlaceholder;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.informationFlow.ThreeLevelsTaint;
import it.unive.lisa.lattices.informationFlow.ThreeTaint;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.symbolic.value.TernaryExpression;
import it.unive.lisa.symbolic.value.UnaryExpression;

/**
 * Three levels taint domain tailored for Go programs.
 */
public class GoThreeLevelsTaint extends ThreeLevelsTaint {

	@Override
	public ThreeTaint evalConstant(Constant c, ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
		if (c instanceof TaintPlaceholder)
			return tainted();
		else
			return clean();
	}

	@Override
	public ThreeTaint evalBinaryExpression(BinaryExpression arg0, ThreeTaint arg1, ThreeTaint arg2, ProgramPoint arg3,
			SemanticOracle arg4) throws SemanticException {

		if (arg1 == ThreeTaint.BOTTOM || arg2 == ThreeTaint.BOTTOM)
			return ThreeTaint.BOTTOM;

		if (arg1 == ThreeTaint.TAINTED || arg2 == ThreeTaint.TAINTED)
			return ThreeTaint.TAINTED;

		if (arg1 == ThreeTaint.TOP || arg2 == ThreeTaint.TOP)
			return ThreeTaint.TOP;

		return ThreeTaint.CLEAN;

	}

	@Override
	public ThreeTaint evalPushAny(PushAny arg0, ProgramPoint arg1, SemanticOracle arg2) throws SemanticException {
		return top();
	}

	@Override
	public ThreeTaint evalTernaryExpression(TernaryExpression arg0, ThreeTaint arg1, ThreeTaint arg2, ThreeTaint arg3,
			ProgramPoint arg4, SemanticOracle arg5) throws SemanticException {

		if (arg1 == ThreeTaint.BOTTOM || arg2 == ThreeTaint.BOTTOM || arg3 == ThreeTaint.BOTTOM)
			return ThreeTaint.BOTTOM;

		if (arg1 == ThreeTaint.TAINTED || arg2 == ThreeTaint.TAINTED || arg3 == ThreeTaint.TAINTED)
			return ThreeTaint.TAINTED;

		if (arg1 == ThreeTaint.TOP || arg2 == ThreeTaint.TOP || arg3 == ThreeTaint.TOP)
			return ThreeTaint.TOP;

		return ThreeTaint.CLEAN;
	}

	@Override
	public ThreeTaint evalTypeCast(BinaryExpression arg0, ThreeTaint arg1, ThreeTaint arg2, ProgramPoint arg3,
			SemanticOracle arg4) throws SemanticException {
		return arg2;
	}

	@Override
	public ThreeTaint evalTypeConv(BinaryExpression arg0, ThreeTaint arg1, ThreeTaint arg2, ProgramPoint arg3,
			SemanticOracle arg4) throws SemanticException {
		return arg2;
	}

	@Override
	public ThreeTaint evalUnaryExpression(UnaryExpression arg0, ThreeTaint arg1, ProgramPoint arg2, SemanticOracle arg3)
			throws SemanticException {
		return arg1;
	}

}
