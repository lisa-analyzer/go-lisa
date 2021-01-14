package it.unive.golisa.analysis;

import it.unive.golisa.analysis.rsubs.RelationalSubstringDomain;
import it.unive.lisa.analysis.BaseLattice;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.ValueDomain;
import it.unive.lisa.analysis.impl.numeric.Interval;
import it.unive.lisa.analysis.nonrelational.ValueEnvironment;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.ValueExpression;

public class RSubs extends BaseLattice<RSubs> implements ValueDomain<RSubs> {

	private static final RSubs TOP = new RSubs(true, false);
	private static final RSubs BOTTOM = new RSubs(false, false);
	
	private final RelationalSubstringDomain string;
	private final ValueEnvironment<Interval> num;
	
	private final boolean isTop;
	private final boolean isBottom;
	
	
	public RSubs() {
		this(new RelationalSubstringDomain(), new ValueEnvironment<Interval>(new Interval()), true, false);
	}
	
	private RSubs(boolean isTop, boolean isBottom) {
		this(new RelationalSubstringDomain(), new ValueEnvironment<Interval>(new Interval()), isTop, isBottom);
	}
		
	private RSubs(RelationalSubstringDomain string, ValueEnvironment<Interval> num) {
		this(string, num, false, false);
	}
	
	private RSubs(RelationalSubstringDomain string, ValueEnvironment<Interval> num, boolean isTop, boolean isBottom) {
		this.string = string;
		this.num = num;
		this.isTop = isTop;
		this.isBottom = isBottom;
	}
	
	@Override
	public RSubs assign(Identifier id, ValueExpression expression) throws SemanticException {
		ValueEnvironment<Interval> numResult = num.assign(id, expression);
		RelationalSubstringDomain stringResult = string.assign(id, expression);
		
		if (numResult.isTop() && stringResult.isTop())
			return TOP;
		if (numResult.isTop())
			return new RSubs(stringResult, num.bottom());
		else
			return new RSubs(string.bottom(), numResult);
	
	}

	private boolean processableByNumericalDomain(ValueExpression exp) {
		//TODO: look at environment for identifier case
		return false;
	}

	private boolean processableByStringDomain(ValueExpression exp) {
		//TODO: look at environment for identifier case
		return false;
	}

	private boolean isString() {
		return !string.isBottom() && num.isBottom();
	}
	
	private boolean isNumeric() {
		return string.isBottom() && !num.isBottom();
	}
	
	@Override
	public RSubs smallStepSemantics(ValueExpression expression) throws SemanticException {
		return new RSubs(string, num, isTop, isBottom);
	}

	@Override
	public RSubs assume(ValueExpression expression) throws SemanticException {
		return new RSubs(string, num, isTop, isBottom);
	}

	@Override
	public RSubs forgetIdentifier(Identifier id) throws SemanticException {
		if (isTop() || isBottom())
			return new RSubs(isTop, isBottom);
		else
			return new RSubs(string.forgetIdentifier(id), num.forgetIdentifier(id));
	}

	@Override
	public Satisfiability satisfies(ValueExpression expression) throws SemanticException {
		return Satisfiability.UNKNOWN;
	}

	@Override
	public String representation() {
		if (isTop())
			return "TOP";
		if (isBottom())
			return "BOTTOM";
		if (isNumeric())
			return num.toString();
		else
			return string.toString();
	}

	@Override
	public RSubs top() {
		return TOP;
	}

	@Override
	public RSubs bottom() {
		return BOTTOM;
	}

	@Override
	protected RSubs lubAux(RSubs other) throws SemanticException {
		if (isNumeric() && other.isNumeric())
			return new RSubs(string, num.lub(other.num));
		if (isString() && other.isString())
			return new RSubs(string.lub(other.string), num);
		
		return TOP;
	}

	@Override
	protected RSubs wideningAux(RSubs other) throws SemanticException {
		if (isNumeric() && other.isNumeric())
			return new RSubs(string, num.widening(other.num));
		if (isString() && other.isString())
			return new RSubs(string.widening(other.string), num);
		
		return TOP;
	}

	@Override
	protected boolean lessOrEqualAux(RSubs other) throws SemanticException {
		if (isNumeric() && other.isNumeric())
			return num.lessOrEqual(other.num);
		if (isString() && other.isString())
			return string.lessOrEqual(other.string);
		
		return false;
	}

	@Override
	public int hashCode() {	
		if (isTop())
			return 1;
		if (isBottom())
			return 2;
		
		final int prime = 31;
		int result = 1;
		result = prime * result + (isBottom ? 1231 : 1237);
		result = prime * result + (isTop ? 1231 : 1237);
		result = prime * result + ((num == null) ? 0 : num.hashCode());
		result = prime * result + ((string == null) ? 0 : string.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RSubs other = (RSubs) obj;
		if (isBottom != other.isBottom)
			return false;
		if (isTop != other.isTop)
			return false;
		if (num == null) {
			if (other.num != null)
				return false;
		} else if (!num.equals(other.num))
			return false;
		if (string == null) {
			if (other.string != null)
				return false;
		} else if (!string.equals(other.string))
			return false;
		return isTop && other.isTop;
	}
}
