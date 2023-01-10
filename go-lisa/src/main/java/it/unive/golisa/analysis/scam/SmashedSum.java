package it.unive.golisa.analysis.scam;

import java.util.TreeSet;

import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.nonrelational.value.BaseNonRelationalValueDomain;
import it.unive.lisa.analysis.numeric.Interval;
import it.unive.lisa.analysis.representation.DomainRepresentation;
import it.unive.lisa.analysis.representation.StringRepresentation;
import it.unive.lisa.analysis.string.CharInclusion;
import it.unive.lisa.analysis.string.Prefix;
import it.unive.lisa.analysis.string.Suffix;
import it.unive.lisa.analysis.string.fsa.FSA;
import it.unive.lisa.analysis.string.tarsis.Tarsis;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.symbolic.value.operator.binary.NumericNonOverflowingAdd;
import it.unive.lisa.symbolic.value.operator.binary.StringConcat;
import it.unive.lisa.symbolic.value.operator.ternary.StringSubstring;
import it.unive.lisa.symbolic.value.operator.ternary.TernaryOperator;
import it.unive.lisa.type.Untyped;
import it.unive.lisa.util.numeric.IntInterval;

public class SmashedSum<S extends BaseNonRelationalValueDomain<S>> implements BaseNonRelationalValueDomain<SmashedSum<S>> {

	private final Interval intValue;
	private final S stringValue;
		
	public SmashedSum(Interval intValue, S stringValue) {
		this.intValue = intValue;
		this.stringValue = stringValue;
	}
	
	@Override
	public SmashedSum<S> evalNonNullConstant(Constant constant, ProgramPoint pp) throws SemanticException {
		if (constant.getValue() instanceof Integer)
			return new SmashedSum<S>(intValue.evalNonNullConstant(constant, pp), stringValue.bottom());
		else if (constant.getValue() instanceof String)
			return new SmashedSum<S>(intValue.bottom(), stringValue.evalNonNullConstant(constant, pp));
		return top();
	}
	
	@Override
	public SmashedSum<S> lubAux(SmashedSum<S> other) throws SemanticException {
		return new SmashedSum<S>(intValue.lub(other.intValue), stringValue.lub(other.stringValue));
	}
	
	@Override
	public SmashedSum<S> wideningAux(SmashedSum<S> other) throws SemanticException {
		return new SmashedSum<S>(intValue.widening(other.intValue), stringValue.widening(other.stringValue));

	}

	@Override
	public boolean lessOrEqualAux(SmashedSum<S> other) throws SemanticException {
		return intValue.lessOrEqual(other.intValue) && stringValue.lessOrEqual(other.stringValue);
	}

	@Override
	public boolean isTop() {
		return intValue.isTop() && stringValue.isTop();
	}
	
	@Override
	public SmashedSum<S> top() {
		return new SmashedSum<S>(intValue.top(), stringValue.top());
	}

	@Override
	public boolean isBottom() {
		return intValue.isBottom() && stringValue.isBottom();
	}
	
	@Override
	public SmashedSum<S> bottom() {
		return new SmashedSum<S>(intValue.bottom(), stringValue.bottom());
	}

	
	@Override
	public SmashedSum<S> evalBinaryExpression(BinaryOperator operator, SmashedSum<S> left, SmashedSum<S> right,
			ProgramPoint pp) throws SemanticException {
		if (operator == StringConcat.INSTANCE)
			return mkSmashedValue(stringValue.evalBinaryExpression(operator, left.stringValue, right.stringValue, pp));
		else if (operator == NumericNonOverflowingAdd.INSTANCE)
			return mkSmashedValue(intValue.evalBinaryExpression(operator, left.intValue, right.intValue, pp));
		return top();
	}
	
	@Override
	public SmashedSum<S> evalTernaryExpression(TernaryOperator operator, SmashedSum<S> left, SmashedSum<S> middle,
			SmashedSum<S> right, ProgramPoint pp) throws SemanticException {
		if (operator == StringSubstring.INSTANCE) {
			IntInterval begin = middle.intValue.interval;
			IntInterval end = right.intValue.interval;

			if (!begin.isFinite() || !end.isFinite())
				return mkSmashedValue(stringValue.top());
			
			S partial = null; //stringSingleton.bottom();
			S temp = null;
			outer:
			for (long b : begin)
				if (b >= 0)
					for (long e : end) { 
						if (b < e) 
							temp = partial == null ? substring(left.stringValue, b, e) : partial.lub(substring(left.stringValue, b, e));
						else if (b == e) 
							temp = partial == null ? mkEmptyString(this.stringValue) :  partial.lub(mkEmptyString(this.stringValue));

						if (temp.equals(partial))
							break outer;
						partial = temp;
						if (partial.isTop())
							break outer;
					}
			return mkSmashedValue(partial);
		}
		
		return top();
	}
	
	
	@SuppressWarnings("unchecked")
	private S mkEmptyString(S str) throws SemanticException {
		if (str instanceof Prefix)
			return (S) new Prefix("");
		else if (str instanceof Suffix)
			return (S) new Suffix("");
		else if (str instanceof CharInclusion)
			return (S) new CharInclusion(new TreeSet<>(), new TreeSet<>());
		else if (str instanceof FSA || str instanceof Tarsis)
			return (S) str.evalNonNullConstant(new Constant(Untyped.INSTANCE, "", null), null);

		
		throw new RuntimeException("Unsupported string domain");
	}

	@SuppressWarnings("unchecked")
	private S substring(S str, long begin, long end) {
		if (str instanceof Prefix)
			return (S) ((Prefix) str).substring(begin, end);
		else if (str instanceof Suffix)
			return (S) ((Suffix) str).substring(begin, end);
		else if (str instanceof CharInclusion)
			return (S) ((CharInclusion) str).substring(begin, end);
		else if (str instanceof FSA)
			return (S) ((FSA) str).substring(begin, end);
		else if (str instanceof Tarsis)
			return (S) ((Tarsis) str).substring(begin, end);
		
		throw new RuntimeException("Unsupported string domain");
	}
	
	@Override
	public DomainRepresentation representation() {
		if (isBottom())
			return Lattice.bottomRepresentation();
		else if (isTop())
			return Lattice.topRepresentation();
		else if (intValue.isBottom())
			return stringValue.representation();
		else if (stringValue.isBottom())
			return intValue.representation();
		
		return new StringRepresentation("(" + intValue.representation().toString() +  ", " + stringValue.representation().toString() + ")");	
	}
		
	private SmashedSum<S> mkSmashedValue(S stringValue) {
		return new SmashedSum<>(intValue.bottom(), stringValue);
	}
	
	private SmashedSum<S> mkSmashedValue(Interval intValue) {
		return new SmashedSum<>(intValue, stringValue.bottom());
	}
}
