package it.unive.golisa;

import it.unive.golisa.cfg.type.GoBoolType;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.lisa.type.BooleanType;
import it.unive.lisa.type.NumericType;
import it.unive.lisa.type.StringType;
import it.unive.lisa.type.TypeSystem;

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

}
