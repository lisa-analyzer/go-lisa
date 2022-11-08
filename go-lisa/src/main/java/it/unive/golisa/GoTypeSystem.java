package it.unive.golisa;

import it.unive.golisa.cfg.type.GoBoolType;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.lisa.type.BooleanType;
import it.unive.lisa.type.NumericType;
import it.unive.lisa.type.StringType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;

/**
 * The {@link TypeSystem} for the Go language.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoTypeSystem extends TypeSystem {

	@Override
	public BooleanType getBooleanType() {
		return GoBoolType.INSTANCE;
	}

	@Override
	public StringType getStringType() {
		return GoStringType.INSTANCE;
	}

	@Override
	public NumericType getIntegerType() {
		return GoIntType.INSTANCE;
	}

	@Override
	public boolean canBeReferenced(Type type) {
		return true;
	}

}
