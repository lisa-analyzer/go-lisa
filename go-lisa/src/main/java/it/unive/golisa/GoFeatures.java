package it.unive.golisa;

import it.unive.lisa.program.language.LanguageFeatures;
import it.unive.lisa.program.language.hierarchytraversal.HierarcyTraversalStrategy;
import it.unive.lisa.program.language.hierarchytraversal.SingleInheritanceTraversalStrategy;
import it.unive.lisa.program.language.parameterassignment.OrderPreservingAssigningStrategy;
import it.unive.lisa.program.language.parameterassignment.ParameterAssigningStrategy;
import it.unive.lisa.program.language.resolution.ParameterMatchingStrategy;
import it.unive.lisa.program.language.resolution.RuntimeTypesMatchingStrategy;
import it.unive.lisa.program.language.validation.BaseValidationLogic;
import it.unive.lisa.program.language.validation.ProgramValidationLogic;

/**
 * Go's {@link LanguageFeatures} implementation.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoFeatures extends LanguageFeatures {

	@Override
	public ParameterMatchingStrategy getMatchingStrategy() {
		return GoMatchingStrategy.INSTANCE;
	}

	@Override
	public HierarcyTraversalStrategy getTraversalStrategy() {
		return SingleInheritanceTraversalStrategy.INSTANCE;
	}

	@Override
	public ParameterAssigningStrategy getAssigningStrategy() {
		return GoAssigningStrategy.INSTANCE;
	}

	@Override
	public ProgramValidationLogic getProgramValidationLogic() {
		return new BaseValidationLogic();
	}

}
