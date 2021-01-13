package it.unive.golisa.analysis;

import it.unive.golisa.analysis.rsubs.RelationalSubstringDomain;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.ValueDomain;
import it.unive.lisa.analysis.impl.numeric.Interval;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.ValueExpression;

public class RSubs implements ValueDomain<RSubs> {

	private final RelationalSubstringDomain string;
	private final Satisfiability bool;
	private final Interval num;
	
	public RSubs() {
		this(new RelationalSubstringDomain(), Satisfiability.UNKNOWN, new Interval());
	}
	
	private RSubs(RelationalSubstringDomain string, Satisfiability bool, Interval num) {
		this.string = string;
		this.bool = bool;
		this.num = num;
	}
	
	@Override
	public RSubs assign(Identifier id, ValueExpression expression) throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RSubs smallStepSemantics(ValueExpression expression) throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RSubs assume(ValueExpression expression) throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RSubs forgetIdentifier(Identifier id) throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Satisfiability satisfies(ValueExpression expression) throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String representation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RSubs lub(RSubs other) throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RSubs widening(RSubs other) throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean lessOrEqual(RSubs other) throws SemanticException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public RSubs top() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RSubs bottom() {
		// TODO Auto-generated method stub
		return null;
	}

}
