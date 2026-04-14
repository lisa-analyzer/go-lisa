package it.unive.golisa.analysis;

import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.nonrelational.value.BaseNonRelationalValueDomain;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

public class DummyDomain implements BaseNonRelationalValueDomain<DummyDomain>, Comparable<DummyDomain>{

	private static final DummyDomain dummy = new DummyDomain();
	private final byte id;
	
	
	public DummyDomain() {
		id = 0;
	}

	@Override
	public DummyDomain lubAux(DummyDomain other) throws SemanticException {
		return dummy;
	}

	@Override
	public boolean lessOrEqualAux(DummyDomain other) throws SemanticException {
		return false;
	}

	@Override
	public DummyDomain top() {
		return dummy;
	}

	@Override
	public DummyDomain bottom() {
		return dummy;
	}

	@Override
	public StructuredRepresentation representation() {
		return new StringRepresentation("dummy");
	}

	@Override
	public int compareTo(DummyDomain o) {
		return 0;
	}

}
