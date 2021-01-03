package it.unive.golisa.cfg.type;

import it.unive.golisa.cfg.type.numeric.floating.GoFloat32Type;
import it.unive.golisa.cfg.type.numeric.floating.GoFloat64Type;
import it.unive.golisa.cfg.type.untyped.GoUntypedFloat;
import it.unive.golisa.cfg.type.untyped.GoUntypedInt;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.type.NumericType;
import it.unive.lisa.cfg.type.Type;

public interface GoType extends Type {

	public Expression defaultValue(CFG cfg);
	
	public boolean isGoInteger();
	
	public default boolean isGoUntyped() {
		return this instanceof GoUntypedFloat || this instanceof GoUntypedInt;
	}
	
	public default boolean isGoFloat() {
		return this instanceof GoFloat32Type || this instanceof GoFloat64Type || this instanceof GoUntypedFloat;
	}
	
	public default boolean isGoUnsignedInteger() {
		return this.isGoInteger() && ((NumericType) this).isUnsigned();
	}
	
	public default boolean isGoSignedInteger() {
		return this.isGoInteger() && ((NumericType) this).isSigned();
	}
}
