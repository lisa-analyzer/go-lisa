package it.unive.golisa.cfg.type;

import it.unive.golisa.cfg.type.numeric.floating.GoFloat32Type;
import it.unive.golisa.cfg.type.numeric.floating.GoFloat64Type;
import it.unive.golisa.cfg.type.untyped.GoUntypedFloat;
import it.unive.golisa.cfg.type.untyped.GoUntypedInt;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.Type;

public interface GoType extends Type {

	public Expression defaultValue(CFG cfg);
		
	public default boolean isGoUntyped() {
		return this instanceof GoUntypedFloat || this instanceof GoUntypedInt;
	}
	
	public default boolean isGoFloat() {
		return this instanceof GoFloat32Type || this instanceof GoFloat64Type || this instanceof GoUntypedFloat;
	}
	
	public default boolean isGoUnsignedInteger() {
		return isNumericType() && asNumericType().isIntegral() && asNumericType().isUnsigned();
	}
	
	public default boolean isGoSignedInteger() {
		return isNumericType() && asNumericType().isIntegral() && asNumericType().isSigned();
	}
}
