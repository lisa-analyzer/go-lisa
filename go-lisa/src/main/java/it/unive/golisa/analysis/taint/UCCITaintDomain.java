package it.unive.golisa.analysis.taint;

import java.util.Set;

import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.informationFlow.BaseTaint;
import it.unive.lisa.analysis.informationFlow.ThreeLevelsTaint;
import it.unive.lisa.analysis.nonrelational.value.BaseNonRelationalValueDomain;
import it.unive.lisa.lattices.informationFlow.ThreeTaint;
import it.unive.lisa.program.annotations.Annotations;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.symbolic.value.Identifier;

public class UCCITaintDomain extends ThreeLevelsTaint {
	
	Set<Call> uccis;
	
	public UCCITaintDomain(Set<Call> uccis) {
		this.uccis = uccis;
	}

	
	@Override
	public ThreeTaint fixedVariable(Identifier id, ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
		Annotations annots = id.getAnnotations();
		
		if (!annots.isEmpty()){
			if (annots.contains(BaseTaint.TAINTED_MATCHER) ) {
				if(isProgramPointWithUCCI(pp))
					return tainted();
				else {
					return clean();
				}
			}
	
			if (annots.contains(BaseTaint.CLEAN_MATCHER))
				return clean();
		}
		return super.fixedVariable(id, pp, oracle);
	}


	@Override
	protected ThreeTaint defaultApprox(Identifier id, ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
		Annotations annots = id.getAnnotations();
		
		if (!annots.isEmpty()){
			if (annots.contains(BaseTaint.TAINTED_MATCHER) ) {
				if(isProgramPointWithUCCI(pp))
					return tainted();
				else {
					return clean();
				}
			}
	
			if (annots.contains(BaseTaint.CLEAN_MATCHER))
				return clean();
		}

		return super.defaultApprox(id, pp, oracle);
	}

	private boolean isProgramPointWithUCCI(ProgramPoint pp) {
		for(Call call : uccis) {
			if(call.equals(pp))
				return true;
		}
		return false;
	}
	


}
