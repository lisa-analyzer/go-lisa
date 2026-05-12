package it.unive.golisa.analysis.taint;

import it.unive.golisa.cfg.expression.unary.GoRange;
import it.unive.golisa.cfg.expression.unary.GoRangeGetNextIndex;
import it.unive.golisa.cfg.expression.unary.instrumented.GoRangeGetNextValue;
import it.unive.golisa.cfg.type.composite.GoMapType;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.informationFlow.ThreeLevelsTaint;
import it.unive.lisa.lattices.informationFlow.ThreeTaint;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.type.Untyped;

/**
 * Taint analysis domain specific for non-determinism issues.
 * 
 * 
 */
public class NonDetTaintDomain extends ThreeLevelsTaint {

	@Override
	protected ThreeTaint defaultApprox(Identifier id, ProgramPoint pp, SemanticOracle oracle) throws SemanticException {

		boolean isAssignedFromMapIteration = 
				
				pp.getCFG().getDescriptor().getControlFlowStructures().stream().anyMatch(g -> {
					Statement condition = g.getCondition();
					if (condition instanceof GoRange && isMapRange((GoRange) condition)
							&& matchMapRangeIds((GoRange) condition, id))
						return true;
					return false;
				});

	if (isAssignedFromMapIteration) // map iterations lead to non-determinism
		return tainted();
	
	if(pp.getProgram().getGlobal(id.getName()) != null) // global vars lead to non-determinism
		return tainted();

	return super.defaultApprox(id, pp, oracle);
	}
	

	private boolean matchMapRangeIds(GoRange range, Identifier id) {
		return matchMapRangeId(range.getIdxRange(), id) || matchMapRangeId(range.getValRange(), id);
	}

	private boolean matchMapRangeId(Statement st, Identifier id) {

		if (st instanceof VariableRef) {
			VariableRef vRef = (VariableRef) st;
			if (vRef.getVariable().equals(id)) {
				Statement pred = st.getEvaluationPredecessor();
				if (pred != null) {
					if (pred instanceof GoRangeGetNextIndex
							|| pred instanceof GoRangeGetNextValue) {
						return true;
					}
				}
			}
		}

		return false;
	}

	private boolean isMapRange(GoRange range) {

		if (range.getCollectionTypes() == null) {
			// range not evaluated yet
			return false;
		}

		return range.getCollectionTypes().stream()
				.anyMatch(type -> type instanceof GoMapType || type == Untyped.INSTANCE);
	}

}
