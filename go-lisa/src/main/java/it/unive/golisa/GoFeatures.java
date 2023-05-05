package it.unive.golisa;

import java.util.Set;

import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.program.language.LanguageFeatures;
import it.unive.lisa.program.language.hierarchytraversal.HierarcyTraversalStrategy;
import it.unive.lisa.program.language.hierarchytraversal.SingleInheritanceTraversalStrategy;
import it.unive.lisa.program.language.parameterassignment.OrderPreservingAssigningStrategy;
import it.unive.lisa.program.language.parameterassignment.ParameterAssigningStrategy;
import it.unive.lisa.program.language.resolution.FixedOrderMatchingStrategy;
import it.unive.lisa.program.language.resolution.ParameterMatchingStrategy;
import it.unive.lisa.program.language.validation.BaseValidationLogic;
import it.unive.lisa.program.language.validation.ProgramValidationLogic;
import it.unive.lisa.type.Type;

/**
 * Go's {@link LanguageFeatures} implementation.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoFeatures extends LanguageFeatures {

	static class RelaxedTypesMatchingStrategy implements ParameterMatchingStrategy {

		/**
		 * The singleton instance of this class.
		 */
		public static final RelaxedTypesMatchingStrategy INSTANCE = new RelaxedTypesMatchingStrategy();

		private RelaxedTypesMatchingStrategy() {
		}
		
		

		public boolean matches(Call call, int pos, Parameter formal, Expression actual, Set<Type> types) {
			return true;
		}



		@Override
		public final boolean matches(Call call, Parameter[] formals, Expression[] actuals, Set<Type>[] types) {
			if (formals.length != actuals.length) {
				if(formals.length > 0 && formals.length < actuals.length) {
					Parameter last = formals[formals.length-1];
					if(last.getStaticType() instanceof GoSliceType)
						for (int i = formals.length-1; i < actuals.length; i++)
							if (!matches(call, i, formals[formals.length-1], actuals[i], types[i]))
								return false;

						return true;
				}

					
				return false;
			}
			
			for (int i = 0; i < formals.length; i++)
				if (!matches(call, i, formals[i], actuals[i], types[i]))
					return false;

			return true;
		}
		
		
	}

	@Override
	public ParameterMatchingStrategy getMatchingStrategy() {

		return RelaxedTypesMatchingStrategy.INSTANCE;
	}

	@Override
	public HierarcyTraversalStrategy getTraversalStrategy() {
		return SingleInheritanceTraversalStrategy.INSTANCE;
	}

	@Override
	public ParameterAssigningStrategy getAssigningStrategy() {
		return OrderPreservingAssigningStrategy.INSTANCE;
	}

	@Override
	public ProgramValidationLogic getProgramValidationLogic() {
		return new BaseValidationLogic();
	}
	
	

}
