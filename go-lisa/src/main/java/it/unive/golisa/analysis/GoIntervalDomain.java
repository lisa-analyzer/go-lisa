package it.unive.golisa.analysis;

import java.math.BigDecimal;

import it.unive.golisa.cfg.type.numeric.signed.GoInt16Type;
import it.unive.golisa.cfg.type.numeric.signed.GoInt32Type;
import it.unive.golisa.cfg.type.numeric.signed.GoInt64Type;
import it.unive.golisa.cfg.type.numeric.signed.GoInt8Type;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt16Type;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt32Type;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt64Type;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt8Type;
import it.unive.golisa.cfg.type.untyped.GoUntypedInt;
import it.unive.lisa.analysis.BaseLattice;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.nonrelational.value.BaseNonRelationalValueDomain;
import it.unive.lisa.analysis.numeric.Interval;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.type.Type;
import it.unive.lisa.util.numeric.IntInterval;
import it.unive.lisa.util.numeric.MathNumber;

/**
 * The overflow-insensitive interval abstract domain customized for Go
 * 
 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
 */
public class GoIntervalDomain extends Interval {

	/**
	 * 
	 * uint8       the set of all unsigned  8-bit integers (0 to 255)
	 * uint16      the set of all unsigned 16-bit integers (0 to 65535)
	 * uint32      the set of all unsigned 32-bit integers (0 to 4294967295)
	 * uint64      the set of all unsigned 64-bit integers (0 to 18446744073709551615)
	 * 
	 * int8        the set of all signed  8-bit integers (-128 to 127)
	 * int16       the set of all signed 16-bit integers (-32768 to 32767)
	 * int32       the set of all signed 32-bit integers (-2147483648 to 2147483647)
	 * int64       the set of all signed 64-bit integers (-9223372036854775808 to 9223372036854775807)
	 * 
	 */
	@Override
	public Interval evalPushAny(PushAny pushAny, ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
		
		Type type = pushAny.getStaticType();
		
		if (type == GoInt8Type.INSTANCE)
			return new Interval(new IntInterval(-128, 127));

		if (type == GoInt16Type.INSTANCE)
			return new Interval(new IntInterval(-32768, 32767));

		if (type == GoInt32Type.INSTANCE)
			return new Interval(new IntInterval(-2147483648, 2147483647));

		if (type == GoInt64Type.INSTANCE || type == GoIntType.INSTANCE || type == GoUntypedInt.INSTANCE)
			return new Interval(new IntInterval(new MathNumber(-9223372036854775808L), new MathNumber(9223372036854775807L)));

		if (type == GoUInt8Type.INSTANCE)
			return new Interval(new IntInterval(0, 255));

		if (type == GoUInt16Type.INSTANCE)
			return new Interval(new IntInterval(0, 65535));

		if (type == GoUInt32Type.INSTANCE)
			return new Interval(new IntInterval(new MathNumber(0), new MathNumber(4294967295L)));

		if (type == GoUInt64Type.INSTANCE)
			return new Interval(new IntInterval(new MathNumber(0), new MathNumber(new BigDecimal("18446744073709551615"))));
		
		return super.evalPushAny(pushAny, pp, oracle);
	}

}
