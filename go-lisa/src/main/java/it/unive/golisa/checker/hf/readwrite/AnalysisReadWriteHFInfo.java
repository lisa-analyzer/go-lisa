package it.unive.golisa.checker.hf.readwrite;

import it.unive.lisa.analysis.string.tarsis.Tarsis;
import it.unive.lisa.program.cfg.statement.call.Call;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Read-write set issue information.
 * 
 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
 */
public class AnalysisReadWriteHFInfo {

	/**
	 * The call to which this class refers to.
	 */
	private final Call call;

	/**
	 * Read-write information.
	 */
	private final ReadWriteInfo info;

	/**
	 * The list of sets of key values.
	 */
	private final List<Set<Tarsis>> keyValues;

	/**
	 * The collection values.
	 */
	private final Set<Tarsis> collectionValues;

	/**
	 * Builds the read-write analysis information object.
	 * 
	 * @param call      the call node
	 * @param info      the read-write information
	 * @param keyValues the key values
	 */
	public AnalysisReadWriteHFInfo(Call call, ReadWriteInfo info, List<Set<Tarsis>> keyValues) {
		this(call, info, keyValues, null);
	}

	/**
	 * Builds the read-write analysis information object.
	 * 
	 * @param call             the call node
	 * @param info             the read-write information
	 * @param keyValues        the key values
	 * @param collectionValues the collection values
	 */
	public AnalysisReadWriteHFInfo(Call call, ReadWriteInfo info, List<Set<Tarsis>> keyValues,
			Set<Tarsis> collectionValues) {
		this.call = call;
		this.info = info;
		this.keyValues = keyValues;
		this.collectionValues = collectionValues;
	}

	/**
	 * Yields the call node.
	 * 
	 * @return the call node
	 */
	public Call getCall() {
		return call;
	}

	/**
	 * Yields the read-write information.
	 * 
	 * @return the read-write information
	 */
	public ReadWriteInfo getInfo() {
		return info;
	}

	/**
	 * Yields the key values.
	 * 
	 * @return the key values
	 */
	public List<Set<Tarsis>> getKeyValues() {
		return keyValues;
	}

	/**
	 * Checks whether this class has collection values.
	 * 
	 * @return {@code true} if this class has collection values, {@code false}
	 *             otherwise.
	 */
	public boolean hasCollection() {
		return collectionValues != null;
	}

	/**
	 * Yields the collection values.
	 * 
	 * @return the collection values
	 */
	public Set<Tarsis> getCollectionValues() {
		return collectionValues;
	}

	// Equals must evaluate only call and info

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
