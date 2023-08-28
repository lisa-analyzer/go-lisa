package it.unive.golisa.checker.readwrite;


import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

import it.unive.lisa.analysis.string.tarsis.Tarsis;
import it.unive.lisa.program.cfg.statement.call.UnresolvedCall;

public class AnalysisReadWriteHFInfo {

	private final UnresolvedCall call;
	private final ReadWriteInfo info;
	private final ArrayList<Set<Tarsis>> keyValues;
	
	public AnalysisReadWriteHFInfo(UnresolvedCall call, ReadWriteInfo info, ArrayList<Set<Tarsis>> keyValues) {
		this.call = call;
		this.info = info;
		this.keyValues = keyValues;
	}

	public UnresolvedCall getCall() {
		return call;
	}

	public ReadWriteInfo getInfo() {
		return info;
	}

	public ArrayList<Set<Tarsis>> getKeyValues() {
		return keyValues;
	}

	@Override
	public int hashCode() {
		return Objects.hash(call, info, keyValues);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AnalysisReadWriteHFInfo other = (AnalysisReadWriteHFInfo) obj;
		return call.equals(other.call) && info.equals(other.info)
				&& keyValues.equals(other.keyValues);
	}
	
	
}

