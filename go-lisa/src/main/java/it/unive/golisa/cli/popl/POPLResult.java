package it.unive.golisa.cli.popl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "POPLResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class POPLResult {
	
    @XmlElement(name = "analyzedByTarsis")   
	boolean analyzedByTarsis;
	
    @XmlElement(name = "analyzedByRSub")   
	boolean analyzedByRSub;
    	
    @XmlElement(name = "cfgCreated")
	boolean cfgCreated;

	String errorReturned;
	
    @XmlElement(name = "locs")
	int locs;
    
	public void setAnalyzedByTarsis(boolean analyzedByTarsis) {
		this.analyzedByTarsis = analyzedByTarsis;
	}

	public void setAnalyzedByRSubs(boolean analyzedByRSubs) {
		this.analyzedByRSub = analyzedByRSubs;
	}


	public void setCfgCreated(boolean cfgCreated) {
		this.cfgCreated = cfgCreated;
	}
	
	public void setErrorReturned(String errorReturned) {
		this.errorReturned = errorReturned;
	}
	
	public String getErrorReturned() {
		return errorReturned;
	}
	
	public void setLocs(int locs) {
		this.locs = locs;
	}
}
