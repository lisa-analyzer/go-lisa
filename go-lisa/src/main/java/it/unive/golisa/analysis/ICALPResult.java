package it.unive.golisa.analysis;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ICALPResult {
	
	String filePath;
	String unsupportedOp;
	String error;
	boolean parsed;
	boolean cfgCreated;
	boolean analyzedByTarsis;
	boolean analyzedByRSub;
	long time;

	public String getFilePath() {
		return filePath;
	}

	@XmlElement
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}


	public String getError() {
		return error;
	}
	
	@XmlElement
	public void setError(String error) {
		this.error = error;
	}

	public boolean isCfgCreated() {
		return cfgCreated;
	}

	@XmlElement
	public void setCfgCreated(boolean cfgCreated) {
		this.cfgCreated = cfgCreated;
	}

	public boolean isAnalyzedByTarsis() {
		return analyzedByTarsis;
	}
	
	@XmlElement
	public void setAnalyzedByTarsis(boolean analyzedByTarsis) {
		this.analyzedByTarsis = analyzedByTarsis;
	}

	public boolean isAnalyzedByRSub() {
		return analyzedByRSub;
	}

	@XmlElement
	public void setAnalyzedByRSub(boolean analyzedByRSub) {
		this.analyzedByRSub = analyzedByRSub;
	}

	public boolean isParsed() {
		return parsed;
	}

	@XmlAttribute
	public void setParsed(boolean parsed) {
		this.parsed = parsed;
	}

	public String getUnsupportedOp() {
		return unsupportedOp;
	}

	public long getTime() {
		return time;
	}
	
	@XmlElement
	public void setTime(long time) {
		this.time = time;
	}

	@XmlElement
	public void setUnsupportedOp(String unsupportedOp) {
		this.unsupportedOp = unsupportedOp;
	}
}
