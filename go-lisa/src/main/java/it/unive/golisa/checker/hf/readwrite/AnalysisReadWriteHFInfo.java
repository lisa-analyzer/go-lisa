package it.unive.golisa.checker.hf.readwrite;


import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

import it.unive.lisa.analysis.string.tarsis.Tarsis;
import it.unive.lisa.program.cfg.statement.call.Call;

public class AnalysisReadWriteHFInfo {

	private final Call call;
	private final ReadWriteInfo info;
	private final ArrayList<Set<Tarsis>> keyValues;
	private final Set<Tarsis> collectionValues; 
	
	public AnalysisReadWriteHFInfo(Call call, ReadWriteInfo info, ArrayList<Set<Tarsis>> keyValues) {
		this.call = call;
		this.info = info;
		this.keyValues = keyValues;
		this.collectionValues = null;
	}
	
	public AnalysisReadWriteHFInfo(Call call, ReadWriteInfo info, ArrayList<Set<Tarsis>> keyValues, Set<Tarsis> collectionValues) {
		this.call = call;
		this.info = info;
		this.keyValues = keyValues;
		this.collectionValues = collectionValues;
	}

	public Call getCall() {
		return call;
	}

	public ReadWriteInfo getInfo() {
		return info;
	}

	public ArrayList<Set<Tarsis>> getKeyValues() {
		return keyValues;
	}

	public boolean hasCollection() {
		return collectionValues != null;
	}
	
	
	
	public Set<Tarsis> getCollectionValues() {
		return collectionValues;
	}
	
	//Equals must evaluate only call and info

	@Override
	public int hashCode() {
		return Objects.hash(call, info);
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
		return Objects.equals(call, other.call) && Objects.equals(info, other.info);
	}


	
}

