package it.unive.golisa.cfg.type;

import it.unive.golisa.cfg.type.numeric.floating.GoFloat32Type;
import it.unive.golisa.cfg.type.numeric.floating.GoFloat64Type;
import it.unive.golisa.cfg.type.untyped.GoUntypedFloat;
import it.unive.golisa.cfg.type.untyped.GoUntypedInt;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.Type;

/**
 * The Go type interface.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public interface GoType extends Type {

	/**
	 * Yields the default value of {@code this} type.
	 * 
	 * @param cfg      the {@link CFG} where the default value lies
	 * @param location the location where the default value is defined
	 * 
	 * @return the default value of {@code this} type
	 */
	public Expression defaultValue(CFG cfg, SourceCodeLocation location);

	/**
	 * Checks whether {@code this} type is untyped.
	 * 
	 * @return whether {@code this} type is untyped
	 */
	public default boolean isGoUntyped() {
		return this instanceof GoUntypedFloat || this instanceof GoUntypedInt;
	}

	/**
	 * Checks whether {@code this} type is a Go float type.
	 * 
	 * @return whether {@code this} type is a Go float type
	 */
	public default boolean isGoFloat() {
		return this instanceof GoFloat32Type || this instanceof GoFloat64Type || this instanceof GoUntypedFloat;
	}

	/**
	 * Checks whether {@code this} type is a Go unsigned integer type.
	 * 
	 * @return whether {@code this} type is a Go unsigned integer type
	 */
	public default boolean isGoUnsignedInteger() {
		return isNumericType() && asNumericType().isIntegral() && asNumericType().isUnsigned();
	}

	/**
	 * Checks whether {@code this} type is a Go signed integer type.
	 * 
	 * @return whether {@code this} type is a Go signed integer type
	 */
	public default boolean isGoSignedInteger() {
		return isNumericType() && asNumericType().isIntegral() && asNumericType().isSigned();
	}
}
